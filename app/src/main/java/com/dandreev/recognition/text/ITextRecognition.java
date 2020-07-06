package com.dandreev.recognition.text;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public interface ITextRecognition {
    void runTextRecognition(Bitmap bitmap, Button mRecognizeTextBtn, ProgressBar mProgressSpinner, TextView mRecognizedTextView, Activity activity);
}
