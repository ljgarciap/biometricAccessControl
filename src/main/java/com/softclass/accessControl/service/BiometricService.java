package com.softclass.accessControl.service;

public interface BiometricService {
    default byte[] captureTemplate() {
        return captureTemplate(null);
    }

    // Permite que el mock genere siempre lo mismo para un userId
    byte[] captureTemplate(String contextKey);

    boolean verify(byte[] templateCaptured, byte[] templateInDB);

    String format();
}

