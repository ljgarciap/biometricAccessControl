package com.softclass.accessControl.config;

import com.softclass.accessControl.service.BiometricService;
import com.softclass.accessControl.service.impl.BiometricSimulatedServiceImpl;
// (en Fase 2 agregarás DigitalPersonaServiceImpl / SecuGenServiceImpl)
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BiometricConfig {

    @Value("${biometric.provider:mock}")
    private String provider;

    @Bean
    public BiometricService biometricService() {
        switch (provider.toLowerCase()) {
            case "mock":
                return new BiometricSimulatedServiceImpl();

            // case "digitalpersona":
            //     return new BiometricDigitalPersonaServiceImpl(...);

            // case "secugen":
            //     return new BiometricSecuGenServiceImpl(...);

            default:
                throw new IllegalArgumentException("Proveedor biométrico no soportado: " + provider);
        }
    }
}
