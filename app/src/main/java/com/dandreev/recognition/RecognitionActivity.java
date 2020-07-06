package com.dandreev.recognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dandreev.recognition.face.IFaceRecognition;
import com.dandreev.recognition.text.ITextRecognition;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import static com.dandreev.recognition.AppConstants.CAMERA_PERMISSION_CODE;
import static com.dandreev.recognition.AppConstants.CAMERA_REQUEST;

public class RecognitionActivity extends AppCompatActivity {
    private ImageView mSelectedImage;
    private Button mRecognizeTextBtn, mCameraBtn, mFaceRecognizeBtn;
    private TextView mRecognizedTextView, mWelcomeText;
    private ProgressBar mProgressSpinner;
    private Uri selectedImageUri;

    @Inject
    ITextRecognition textRecognition;
    @Inject
    IFaceRecognition faceRecognition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);
        init();

        ((RecognitionApplication)getApplication())
                .recognitionComponent()
                .inject(this);

        mRecognizeTextBtn.setOnClickListener(view -> textRecognition.runTextRecognition(getSelectedDrawableImage(), mRecognizeTextBtn, mProgressSpinner, mRecognizedTextView, this));
        mFaceRecognizeBtn.setOnClickListener(view -> faceRecognition.runFaceRecognition(getSelectedDrawableImage(), mProgressSpinner, mSelectedImage, this));

        mCameraBtn.setOnClickListener(view -> {
            if (mRecognizedTextView.getText() != null) {
                mRecognizedTextView.setText(null);
            }
            try {
                checkCameraPermissions();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Uri photoURI = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID,
                    photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            this.selectedImageUri = photoURI;

            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Picasso.get()
                    .load(this.selectedImageUri)
                    .fit().centerInside()
                    .rotate(90)
                    .into(mSelectedImage);
            mWelcomeText.setVisibility(View.GONE);
            mFaceRecognizeBtn.setEnabled(true);
            mRecognizeTextBtn.setEnabled(true);
        }
    }

    private void init() {
        mRecognizeTextBtn = findViewById(R.id.textRecognizerBtn);
        mSelectedImage = findViewById(R.id.selectedImage);
        mRecognizedTextView = findViewById(R.id.recognizedTextView);
        mCameraBtn = findViewById(R.id.cameraBtn);
        mProgressSpinner = findViewById(R.id.progressBar);
        mFaceRecognizeBtn = findViewById(R.id.faceRecognitionBtn);
        mWelcomeText = findViewById(R.id.welcomeText);
    }

    private void checkCameraPermissions() throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{ Manifest.permission.CAMERA }, CAMERA_PERMISSION_CODE);
            }
            else
            {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = createImageFile();
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID,
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                this.selectedImageUri = photoURI;
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }
    }

    private Bitmap getSelectedDrawableImage() {
        BitmapDrawable drawable = (BitmapDrawable) mSelectedImage.getDrawable();
        Bitmap copyBitmap = drawable.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        mSelectedImage.setImageBitmap(copyBitmap);
        return copyBitmap;
    }


    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }
}