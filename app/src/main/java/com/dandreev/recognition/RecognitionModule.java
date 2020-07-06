package com.dandreev.recognition;

import com.dandreev.recognition.face.FaceRecognitionImpl;
import com.dandreev.recognition.face.IFaceRecognition;
import com.dandreev.recognition.text.ITextRecognition;
import com.dandreev.recognition.text.TextRecognitionImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RecognitionModule {
    @Provides
    @Singleton
    static ITextRecognition provideTextRecognition() {
        return new TextRecognitionImpl();
    }

    @Provides
    @Singleton
    static IFaceRecognition provideFaceRecognition() {
        return new FaceRecognitionImpl();
    }
}
