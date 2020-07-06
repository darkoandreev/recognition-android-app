package com.dandreev.recognition.face;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.util.List;

public class FaceRecognitionImpl implements IFaceRecognition {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void runFaceRecognition(Bitmap bitmap, ProgressBar mProgressSpinner, ImageView mSelectedImage, Activity activity) {
        mProgressSpinner.setVisibility(View.VISIBLE);
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .build();

        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);

        detector.detectInImage(image)
                .addOnSuccessListener(firebaseVisionFaces -> {
                    Canvas canvas = new Canvas(bitmap);
                    processFaceContourDetectionResult(firebaseVisionFaces, mProgressSpinner, mSelectedImage, canvas, activity);
                })
                .addOnFailureListener(
                        Throwable::printStackTrace);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void processFaceContourDetectionResult(List<FirebaseVisionFace> faces, ProgressBar mProgressSpinner, ImageView mSelectedImage, Canvas canvas, Activity activity) {
        if (faces.size() == 0) {
            Toast.makeText(activity, "No faces found", Toast.LENGTH_LONG).show();
            mProgressSpinner.setVisibility(View.GONE);
            return;
        }
        mSelectedImage.invalidate();
        Paint rectPaint = getPaint(Color.BLACK, Paint.Style.STROKE);
        Paint facePaint = getPaint(Color.RED, Paint.Style.FILL);

        for (FirebaseVisionFace face : faces) {
            Rect bounds = face.getBoundingBox();
            canvas.drawRect(bounds, rectPaint);

            FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
            FirebaseVisionFaceLandmark rightEar = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR);
            FirebaseVisionFaceLandmark nose = face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE);
            FirebaseVisionFaceLandmark leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE);
            FirebaseVisionFaceLandmark rightEye = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE);
            FirebaseVisionFaceLandmark mouth = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM);

            if (leftEar != null && rightEar != null && nose != null && leftEye != null && rightEye != null && mouth != null) {
                canvas.drawText("Ear1", leftEar.getPosition().getX(), leftEar.getPosition().getY(), facePaint);
                canvas.drawText("Ear2", rightEar.getPosition().getX(), rightEar.getPosition().getY(), facePaint);
                canvas.drawText("N", nose.getPosition().getX(), nose.getPosition().getY(), facePaint);
                canvas.drawText("Eye1", leftEye.getPosition().getX(), leftEye.getPosition().getY(), facePaint);
                canvas.drawText("Eye2", rightEye.getPosition().getX(), rightEye.getPosition().getY(), facePaint);
                canvas.drawText("M", mouth.getPosition().getX(), mouth.getPosition().getY(), facePaint);
            }
        }
        mProgressSpinner.setVisibility(View.GONE);
    }

    private Paint getPaint(int color, Paint.Style style) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(style);
        paint.setStrokeWidth(5);
        paint.setTextSize(50);
        return paint;
    }
}
