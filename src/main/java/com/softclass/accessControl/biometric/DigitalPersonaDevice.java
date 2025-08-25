package com.softclass.accessControl.biometric;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "biometric.provider", havingValue = "digitalpersona")
public class DigitalPersonaDevice implements BiometricDevice {

    @Override
    public boolean enroll(String document) {
        // TODO: Integración real con SDK DigitalPersona
        System.out.println("Simulando enrolamiento DigitalPersona para: " + document);
        return true;
    }

    @Override
    public String verify() {
        // TODO: Integración real con SDK DigitalPersona
        System.out.println("Simulando verificación DigitalPersona");
        // Retorna documento reconocido o null
        return "123456789";
    }
}
