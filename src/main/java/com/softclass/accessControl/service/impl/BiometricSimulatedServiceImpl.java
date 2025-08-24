package com.softclass.accessControl.service.impl;

import com.softclass.accessControl.service.BiometricService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class BiometricSimulatedServiceImpl implements BiometricService {

    // Genera siempre el mismo template para la misma clave (p.ej. userId)
    @Override
    public byte[] captureTemplate(String contextKey) {
        try {
            String key = (contextKey == null || contextKey.isBlank()) ? "ANON" : contextKey;
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] seed = sha.digest(key.getBytes(StandardCharsets.UTF_8));

            // "Template" de 256 bytes repitiendo el hash
            byte[] tpl = new byte[256];
            for (int i = 0; i < tpl.length; i++) {
                tpl[i] = seed[i % seed.length];
            }
            return tpl;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    @Override
    public boolean verify(byte[] templateCaptured, byte[] templateInDB) {
        // Mock: coincide si los primeros 16 bytes son iguales
        return Arrays.equals(
                Arrays.copyOf(templateCaptured, 16),
                Arrays.copyOf(templateInDB, 16)
        );
    }

    @Override
    public String format() {
        return "SIMULATED";
    }
}
