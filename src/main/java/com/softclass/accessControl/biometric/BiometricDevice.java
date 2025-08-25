package com.softclass.accessControl.biometric;

public interface BiometricDevice {
    /**
     * Enrolar la huella asociada a un documento.
     * Devuelve true si se enrola correctamente.
     */
    boolean enroll(String document);

    /**
     * Verificar huella y devolver el documento asociado.
     * Devuelve null si no se reconoce.
     */
    String verify();
}
