package com.softclass.accessControl.service.impl;

import com.softclass.accessControl.biometric.BiometricDevice;
import com.softclass.accessControl.service.BiometricService;
import org.springframework.stereotype.Service;

@Service
public class BiometricServiceImpl implements BiometricService {

    private final BiometricDevice device;

    public BiometricServiceImpl(BiometricDevice device) {
        this.device = device;
        this.device.initialize();
    }

    @Override
    public byte[] captureTemplate(String contextKey) {
        return device.captureTemplate(contextKey);
    }

    @Override
    public boolean verify(byte[] templateCaptured, byte[] templateInDB) {
        return device.verify(templateCaptured, templateInDB);
    }

    @Override
    public String format() {
        return device.format();
    }
}

