package com.dandreev.recognition;

import android.app.Application;

public class RecognitionApplication extends Application {
    private IRecognitionComponent recognitionComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        recognitionComponent = createRecognitionComponent();
    }

    public IRecognitionComponent recognitionComponent() {
        return recognitionComponent;
    }

    private IRecognitionComponent createRecognitionComponent() {
        return DaggerIRecognitionComponent
                .builder()
                .recognitionModule(new RecognitionModule())
                .build();
    }
}
