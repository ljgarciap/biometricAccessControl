package com.softclass.accessControl.service.impl;

import com.softclass.accessControl.service.BiometricService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Arrays;

@Primary
@Service
public class BiometricSimulatedServiceImpl implements BiometricService {

    private final SecureRandom rnd = new SecureRandom();

    @Override
    public byte[] captureTemplate() {
        byte[] tpl = new byte[256];
        rnd.nextBytes(tpl);
        return tpl;
    }

    @Override
    public boolean verify(byte[] templateCaptured, byte[] templateInBD) {
    // Simulación: "match" si comparten los primeros 8 bytes
        return Arrays.equals(Arrays.copyOf(templateCaptured, 8),
                Arrays.copyOf(templateInBD, 8)
        );
    }

    @Override
    public String format() {
        return "SIMULATED";
    }
}
