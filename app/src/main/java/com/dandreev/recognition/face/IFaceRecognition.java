package com.dandreev.recognition.face;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.ProgressBar;

public interface IFaceRecognition {
    void runFaceRecognition(Bitmap bitmap, ProgressBar mProgressSpinner, ImageView mSelectedImage, Activity activity);
}
