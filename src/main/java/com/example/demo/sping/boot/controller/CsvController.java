package com.example.demo.sping.boot.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.sping.boot.service.csv.CsvService;

import io.swagger.v3.oas.annotations.tags.Tag;



@RestController
@Tag(name = "Csv Controller", description = "create edit file csv")
@RequestMapping("/csv")
public class CsvController {
    private final CsvService csvService;
    public CsvController (
        CsvService csvService
    ) {
        this.csvService = csvService;
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile() {
        var csvStream = csvService.generateCsv();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(csvStream));
    }

}
