package com.softclass.accessControl.biometric;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class DigitalPersonaDevice implements BiometricDevice {

    private boolean initialized = false;

    @Override
    public void initialize() {
        log.info("[DigitalPersonaDevice] Inicializando lector biométrico DigitalPersona...");
        // Aquí iría el código real de inicialización del SDK DigitalPersona.
        initialized = true;
        log.info("[DigitalPersonaDevice] Inicialización completada.");
    }

    @Override
    public byte[] captureTemplate(String contextKey) {
        if (!initialized) {
            throw new IllegalStateException("[DigitalPersonaDevice] El dispositivo no está inicializado.");
        }
        log.info("[DigitalPersonaDevice] Capturando plantilla para contexto: {}", contextKey);
        // En stub devolvemos un arreglo dummy, en real se conecta al SDK DigitalPersona.
        return ("digitalpersona-template-" + contextKey).getBytes();
    }

    @Override
    public boolean verify(byte[] templateCaptured, byte[] templateInDB) {
        if (!initialized) {
            throw new IllegalStateException("[DigitalPersonaDevice] El dispositivo no está inicializado.");
        }
        boolean match = java.util.Arrays.equals(templateCaptured, templateInDB);
        log.info("[DigitalPersonaDevice] Verificación realizada: {}", match ? "MATCH" : "NO MATCH");
        return match;
    }

    @Override
    public String format() {
        return "DigitalPersona";
    }
}
