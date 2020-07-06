package com.dandreev.recognition.text;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

public class TextRecognitionImpl implements ITextRecognition {
    @Override
    public void runTextRecognition(Bitmap bitmap, Button mRecognizeTextBtn, ProgressBar mProgressSpinner, TextView mRecognizedTextView, Activity activity) {
        mRecognizeTextBtn.setEnabled(false);
        mProgressSpinner.setVisibility(View.VISIBLE);

        FirebaseVisionImage visionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        detector.processImage(visionImage)
                .addOnSuccessListener((firebaseVisionText) -> {
                    if (firebaseVisionText.getText().equals("")) {
                        Toast.makeText(activity, "No text found", Toast.LENGTH_LONG).show();
                    }
                    mRecognizedTextView.setText(firebaseVisionText.getText());
                    mRecognizeTextBtn.setEnabled(true);
                    mProgressSpinner.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                    mRecognizeTextBtn.setEnabled(true);
                    mProgressSpinner.setVisibility(View.GONE);
                });
    }
}
