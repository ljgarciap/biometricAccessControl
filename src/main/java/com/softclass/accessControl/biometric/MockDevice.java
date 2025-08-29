package com.softclass.accessControl.biometric;

import java.util.concurrent.atomic.AtomicBoolean;

public class MockDevice implements BiometricDevice {

    private boolean initialized = false;
    @Override
    public void initialize() {
        initialized=true;
        System.out.println("[MOCK] Device initialized");
    }

    @Override
    public byte[] captureTemplate(String contextKey) {
        if (!initialized) {
            throw new IllegalStateException("Device not initialized");
        }
        // Simula un template único por contexto
        return ("TPL_" + contextKey).getBytes();
    }

    @Override
    public boolean verify(byte[] templateCaptured, byte[] templateInDB) {
        if (templateCaptured == null || templateInDB == null) return false;
        return java.util.Arrays.equals(templateCaptured, templateInDB);
    }

    @Override
    public String format() {
        return "MOCK_DEVICE";
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }
}
