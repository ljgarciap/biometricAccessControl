package com.softclass.accessControl.biometric;

import com.digitalpersona.onetouch.*;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.DPFPCaptureFactory;
import com.digitalpersona.onetouch.capture._impl.DPFPCaptureFactoryImpl;
import com.digitalpersona.onetouch.capture.event.*;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPTemplateStatus;
import com.digitalpersona.onetouch.processing._impl.DPFPEnrollmentFactoryImpl;
import com.digitalpersona.onetouch.processing._impl.DPFPFeatureExtractionFactoryImpl;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import com.digitalpersona.onetouch.verification._impl.DPFPVerificationFactoryImpl;
import com.digitalpersona.onetouch._impl.DPFPTemplateFactoryImpl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DigitalPersonaDevice implements BiometricDevice {

    private boolean initialized = false;

    private DPFPCapture capture;
    private byte[] lastTemplateData;
    private CountDownLatch captureLatch;

    private final DPFPFeatureExtractionFactoryImpl featureExtractionFactory;
    private final DPFPTemplateFactoryImpl templateFactory;
    private final DPFPVerificationFactoryImpl verificationFactory;
    private final DPFPEnrollmentFactoryImpl enrollmentFactory;

    public DigitalPersonaDevice() {
        this.featureExtractionFactory = new DPFPFeatureExtractionFactoryImpl();
        this.templateFactory = new DPFPTemplateFactoryImpl();
        this.verificationFactory = new DPFPVerificationFactoryImpl();
        this.enrollmentFactory = new DPFPEnrollmentFactoryImpl();
    }

    @Override
    public void initialize() {
        try {
            DPFPCaptureFactory captureFactory = new DPFPCaptureFactoryImpl();
            capture = captureFactory.createCapture();

            capture.addDataListener(new DPFPDataListener() {
                @Override
                public void dataAcquired(DPFPDataEvent e) {
                    processSample(e.getSample());
                    if (captureLatch != null) {
                        captureLatch.countDown();
                    }
                }
            });

            capture.addErrorListener(new DPFPErrorListener() {
                @Override
                public void errorOccured(DPFPErrorEvent e) {
                    System.err.println("Error ocurrido: " + e.getError());
                }

                @Override
                public void exceptionCaught(DPFPErrorEvent e) {
                    System.err.println("Excepción captura: " + e.getError());
                    if (captureLatch != null) {
                        captureLatch.countDown();
                    }
                }
            });

            capture.startCapture();
            System.out.println("Lector DigitalPersona inicializado.");
        } catch (Exception ex) {
            System.err.println("Error inicializando dispositivo: " + ex.getMessage());
            ex.printStackTrace();
        }
        initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    private void processSample(DPFPData sample) {
        try {
            DPFPFeatureSet features = featureExtractionFactory.createFeatureExtraction()
                    .createFeatureSet((DPFPSample) sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

            // Agregar las features al último enrollment si existe
            if (lastTemplateData != null) {
                DPFPEnrollment enrollment = enrollmentFactory.createEnrollment();
                enrollment.addFeatures(features);

                if (enrollment.getTemplateStatus() == DPFPTemplateStatus.TEMPLATE_STATUS_READY) {
                    lastTemplateData = enrollment.getTemplate().serialize();
                }
            } else {
                DPFPEnrollment enrollment = enrollmentFactory.createEnrollment();
                enrollment.addFeatures(features);

                if (enrollment.getTemplateStatus() == DPFPTemplateStatus.TEMPLATE_STATUS_READY) {
                    lastTemplateData = enrollment.getTemplate().serialize();
                }
            }
        } catch (Exception ex) {
            System.err.println("Error procesando muestra: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public byte[] captureTemplate(String contextKey) {
        try {
            lastTemplateData = null;
            captureLatch = new CountDownLatch(1);
            System.out.println("Coloque el dedo en el lector...");

            boolean captured = captureLatch.await(30, TimeUnit.SECONDS);

            if (captured && lastTemplateData != null) {
                System.out.println("Huella capturada exitosamente");
                return lastTemplateData;
            } else {
                System.out.println("Tiempo de espera agotado o huella no capturada");
                return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Captura interrumpida");
            return null;
        } finally {
            captureLatch = null;
        }
    }

    @Override
    public boolean verify(byte[] templateCaptured, byte[] templateInDB) {
        if (templateCaptured == null || templateInDB == null) return false;

        try {
            DPFPTemplate capturedTemplate = templateFactory.createTemplate(templateCaptured);
            DPFPTemplate storedTemplate = templateFactory.createTemplate(templateInDB);

            DPFPVerification verifier = verificationFactory.createVerification();
            DPFPVerificationResult result = verifier.verify((DPFPFeatureSet) capturedTemplate, storedTemplate);

            return result.isVerified();
        } catch (Exception ex) {
            System.err.println("Error en verificación: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public String format() {
        return "DigitalPersona U.are.U 4500";
    }

    public void stopCapture() {
        if (capture != null) {
            try {
                capture.stopCapture();
                System.out.println("Captura detenida");
            } catch (Exception e) {
                System.err.println("Error deteniendo captura: " + e.getMessage());
            }
        }
    }

    public byte[] enroll(String contextKey, int samplesRequired) {
        try {
            DPFPEnrollment enrollment = enrollmentFactory.createEnrollment();

            for (int i = 0; i < samplesRequired; i++) {
                System.out.println("Captura " + (i + 1) + " de " + samplesRequired);
                byte[] templateData = captureTemplate(contextKey);
                if (templateData == null) {
                    System.out.println("Registro cancelado");
                    return null;
                }

                DPFPTemplate template = templateFactory.createTemplate(templateData);
                DPFPFeatureSet features = featureExtractionFactory.createFeatureExtraction()
                        .createFeatureSet((DPFPSample) template, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);
                enrollment.addFeatures(features);
            }

            if (enrollment.getTemplateStatus() == DPFPTemplateStatus.TEMPLATE_STATUS_READY) {
                DPFPTemplate finalTemplate = enrollment.getTemplate();
                return finalTemplate.serialize();
            } else {
                System.out.println("Registro incompleto");
                return null;
            }
        } catch (Exception ex) {
            System.err.println("Error en enrolamiento: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public boolean isDeviceReady() {
        return capture != null;
    }

    public void cleanup() {
        stopCapture();
    }
}
