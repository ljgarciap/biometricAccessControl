package com.softclass.accessControl.biometric;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class HamsterDevice implements BiometricDevice {

    private boolean initialized = false;

    @Override
    public void initialize() {
        log.info("[HamsterDevice] Inicializando lector biométrico Hamster...");
        // Aquí iría el código real de inicialización del SDK Hamster.
        initialized = true;
        log.info("[HamsterDevice] Inicialización completada.");
    }

    @Override
    public byte[] captureTemplate(String contextKey) {
        if (!initialized) {
            throw new IllegalStateException("[HamsterDevice] El dispositivo no está inicializado.");
        }
        log.info("[HamsterDevice] Capturando plantilla para contexto: {}", contextKey);
        // En stub devolvemos un arreglo dummy, en real se conecta al SDK Hamster.
        return ("hamster-template-" + contextKey).getBytes();
    }

    @Override
    public boolean verify(byte[] templateCaptured, byte[] templateInDB) {
        if (!initialized) {
            throw new IllegalStateException("[HamsterDevice] El dispositivo no está inicializado.");
        }
        boolean match = java.util.Arrays.equals(templateCaptured, templateInDB);
        log.info("[HamsterDevice] Verificación realizada: {}", match ? "MATCH" : "NO MATCH");
        return match;
    }

    @Override
    public String format() {
        return "Hamster";
    }
}
