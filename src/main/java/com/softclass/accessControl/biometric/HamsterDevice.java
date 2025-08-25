package com.softclass.accessControl.biometric;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "biometric.provider", havingValue = "hamster")
public class HamsterDevice implements BiometricDevice {

    @Override
    public boolean enroll(String document) {
        // TODO: Integración real con SDK Hamster
        System.out.println("Simulando enrolamiento Hamster para: " + document);
        return true;
    }

    @Override
    public String verify() {
        // TODO: Integración real con SDK Hamster
        System.out.println("Simulando verificación Hamster");
        return "987654321"; // documento reconocido
    }
}
