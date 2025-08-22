package com.softclass.accessControl.service;

public interface BiometricService {
        byte[] captureTemplate();
        boolean verify(byte[] templateCaptured, byte[] templateInBD);
        default int score(byte[] a, byte[] b) {
            return verify(a,b) ? 100 : 0;
        }
        String format(); // ej. SIMULADO, DP_PROP, ANSI_378
    }
