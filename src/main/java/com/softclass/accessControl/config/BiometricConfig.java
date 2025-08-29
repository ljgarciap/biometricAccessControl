package com.softclass.accessControl.config;

import com.softclass.accessControl.biometric.BiometricDevice;
import com.softclass.accessControl.biometric.DigitalPersonaDevice;
import com.softclass.accessControl.biometric.HamsterDevice;
import com.softclass.accessControl.biometric.MockDevice;
import com.softclass.accessControl.service.BiometricService;
import com.softclass.accessControl.service.FingerprintService;
import com.softclass.accessControl.service.impl.BiometricSimulatedServiceImpl;
// (en Fase 2 agregarás DigitalPersonaServiceImpl / SecuGenServiceImpl)
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BiometricConfig {

    @Bean
    @ConditionalOnProperty(name = "biometric.provider",
                           havingValue = "hamster")
    public BiometricDevice hamsterDevice() {
        return new HamsterDevice();
    }

    @Bean
    @ConditionalOnProperty(name = "biometric.provider",
                           havingValue = "digitalpersona")
    public BiometricDevice digitalPersonaDevice() {
        return new DigitalPersonaDevice();
    }

    // ⚡ Para test: se crean los dos beans juntos
    @Bean
    @ConditionalOnProperty(name = "biometric.provider",
                           havingValue = "digitalpersonaTest")
    public BiometricDevice digitalPersonaDeviceForTest() {
        return new DigitalPersonaDevice();
    }

    @Bean
    @ConditionalOnProperty(name = "biometric.provider",
                           havingValue = "digitalpersonaTest")
    public FingerprintService fingerprintService(BiometricDevice device) {
        return new FingerprintService((DigitalPersonaDevice) device);
    }

    @Bean
    @ConditionalOnProperty(name = "biometric.provider",
                           havingValue = "mock", matchIfMissing = true)
    public BiometricDevice mockDevice() {
        return new MockDevice();
    }
}

