package com.softclass.accessControl.biometric;

public interface BiometricDevice {
    void initialize();

    /**
     * Captura la plantilla desde el lector.
     * @param contextKey puede ser el documento u otro identificador lógico.
     */
    byte[] captureTemplate(String contextKey);

    /**
     * Verifica si dos plantillas coinciden.
     */
    boolean verify(byte[] templateCaptured, byte[] templateInDB);

    /**
     * Nombre del dispositivo (DigitalPersona, Hamster, Mock...).
     */
    String format();

    boolean isInitialized();
}
