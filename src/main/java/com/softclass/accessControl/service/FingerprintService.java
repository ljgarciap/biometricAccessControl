package com.softclass.accessControl.service;


import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.processing.DPFPTemplateStatus;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.softclass.accessControl.biometric.DigitalPersonaDevice;
import org.springframework.stereotype.Service;

@Service
public class FingerprintService {

    private final DPFPCapture capture;
    private final DPFPEnrollment enrollment;
    private final DPFPVerification verification;

    public FingerprintService(DigitalPersonaDevice device) {
        this.capture = DPFPGlobal.getCaptureFactory().createCapture();
        this.enrollment = DPFPGlobal.getEnrollmentFactory().createEnrollment();
        this.verification = DPFPGlobal.getVerificationFactory().createVerification();

        // Listener para capturas
        capture.addDataListener(new DPFPDataAdapter() {
            @Override
            public void dataAcquired(DPFPDataEvent e) {
                processCapture(e.getSample());
            }
        });
    }

    private void processCapture(com.digitalpersona.onetouch.DPFPSample sample) {
        try {
            DPFPFeatureSet features = DPFPGlobal.getFeatureExtractionFactory()
                    .createFeatureExtraction()
                    .createFeatureSet(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

            if (features != null) {
                enrollment.addFeatures(features);
                System.out.println("Features agregadas. Restantes: " + enrollment.getFeaturesNeeded());
            }

        } catch (Exception ex) {
            System.err.println("Error procesando muestra: " + ex.getMessage());
        }
    }

    /** Inicia captura de huella */
    public void startCapture() {
        capture.startCapture();
    }

    /** Detiene captura */
    public void stopCapture() {
        capture.stopCapture();
    }

    /** Devuelve el template final enrolado */
    public DPFPTemplate getTemplate() {
        if (enrollment.getTemplateStatus() == DPFPTemplateStatus.TEMPLATE_STATUS_READY) {
            return enrollment.getTemplate();
        }
        return null;
    }

    /** Verifica una huella contra un template guardado */
    public boolean verify(com.digitalpersona.onetouch.DPFPSample sample, byte[] storedTemplateBytes) throws DPFPImageQualityException {
        DPFPTemplate storedTemplate = DPFPGlobal.getTemplateFactory().createTemplate(storedTemplateBytes);

        DPFPFeatureSet features = DPFPGlobal.getFeatureExtractionFactory()
                .createFeatureExtraction()
                .createFeatureSet(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

        return verification.verify(features, storedTemplate).isVerified();
    }
}

