package com.dandreev.recognition;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = RecognitionModule.class)
public interface IRecognitionComponent {
    void inject (RecognitionActivity recognitionActivity);
}
