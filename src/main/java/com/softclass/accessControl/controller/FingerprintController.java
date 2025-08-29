package com.softclass.accessControl.controller;

import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.softclass.accessControl.service.FingerprintService;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/fingerprint")
public class FingerprintController {

    private final FingerprintService fingerprintService;
    private final ConcurrentHashMap<String, byte[]> userTemplates = new ConcurrentHashMap<>();

    public FingerprintController(FingerprintService fingerprintService) {
        this.fingerprintService = fingerprintService;
    }

    @PostMapping("/enroll/{userId}")
    public String enroll(@PathVariable String userId) {
        fingerprintService.startCapture();
        // Aquí el usuario pone el dedo varias veces
        // Simulación: después de varias capturas
        DPFPTemplate template = fingerprintService.getTemplate();
        fingerprintService.stopCapture();

        if (template != null) {
            byte[] templateBytes = template.serialize();
            userTemplates.put(userId, templateBytes);
            return "Usuario " + userId + " enrolado correctamente.";
        }
        return "Error: huella no completó enrolamiento.";
    }

    @PostMapping("/verify/{userId}")
    public String verify(@PathVariable String userId, @RequestBody String base64Sample) {
        byte[] storedTemplate = userTemplates.get(userId);
        if (storedTemplate == null) {
            return "Usuario no enrolado.";
        }

        try {
            // Decodificar el sample en Base64
            byte[] sampleBytes = Base64.getDecoder().decode(base64Sample);

            // Crear el sample usando el factory
            DPFPSample sample = com.digitalpersona.onetouch.DPFPGlobal
                    .getSampleFactory()
                    .createSample(sampleBytes);

            // Verificar contra el template almacenado
            boolean ok = fingerprintService.verify(sample, storedTemplate);
            return ok ? "Verificación exitosa" : "Verificación fallida";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error en verificación: " + e.getMessage();
        }
    }

}

