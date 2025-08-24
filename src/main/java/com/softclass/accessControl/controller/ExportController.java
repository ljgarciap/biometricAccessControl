package com.softclass.accessControl.controller;

import com.opencsv.CSVWriter;
import com.softclass.accessControl.domain.Access;
import com.softclass.accessControl.repo.AccessRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/export")
public class ExportController {
    private final AccessRepository ingresoRepo;

    @GetMapping("/menu")
    public String menu() {
        return "reportes";
    }

    @GetMapping("/csv")
    public void exportCsv(HttpServletResponse resp,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) throws Exception {
        LocalDateTime d = from != null ? from.atStartOfDay() : LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime h = to != null ? to.atTime(23,59,59) : LocalDateTime.now();
        List<Access> rows = ingresoRepo.findByDateBetween(d, h);

        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=ingresos.csv");
        try (var writer = new OutputStreamWriter(resp.getOutputStream(), StandardCharsets.UTF_8);
             var csv = new CSVWriter(writer)) {
            csv.writeNext(new String[]{"ID","Fecha","Documento","Nombre","Resultado","Score","Dispositivo"});
            for (var i : rows) {
                csv.writeNext(new String[]{
                        String.valueOf(i.getId()),
                        String.valueOf(i.getDate()),
                        i.getPersona()!=null? i.getPersona().getDocument():"",
                        i.getPersona()!=null? i.getPersona().getName():"",
                        i.getResult(),
                        String.valueOf(i.getScore()),
                        i.getDevice()
                });
            }
        }
    }

    @GetMapping("/excel")
    public void exportExcel(HttpServletResponse resp,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) throws Exception {
        LocalDateTime d = from != null ? from.atStartOfDay() : LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime h = to != null ? to.atTime(23,59,59) : LocalDateTime.now();
        List<Access> rows = ingresoRepo.findByDateBetween(d, h);

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Ingresos");
            int r = 0;
            Row header = sh.createRow(r++);
            String[] cols = {"ID","Fecha","Documento","Nombre","Resultado","Score","Dispositivo"};
            for (int c=0;c<cols.length;c++) header.createCell(c).setCellValue(cols[c]);
            for (var i : rows) {
                Row row = sh.createRow(r++);
                int c=0;
                row.createCell(c++).setCellValue(i.getId());
                row.createCell(c++).setCellValue(String.valueOf(i.getDate()));
                row.createCell(c++).setCellValue(i.getPersona()!=null? i.getPersona().getDocument():"");
                row.createCell(c++).setCellValue(i.getPersona()!=null? i.getPersona().getName():"");
                row.createCell(c++).setCellValue(i.getResult());
                row.createCell(c++).setCellValue(i.getScore()!=null? i.getScore():0);
                row.createCell(c++).setCellValue(i.getDevice());
            }
            for (int c=0;c<7;c++) sh.autoSizeColumn(c);
            resp.setHeader("Content-Disposition", "attachment; filename=ingresos.xlsx");
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            wb.write(resp.getOutputStream());
        }
    }
}

