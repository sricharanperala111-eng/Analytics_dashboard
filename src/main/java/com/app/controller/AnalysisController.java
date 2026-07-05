package com.app.controller;

import com.app.model.AnalysisResult;
import com.app.service.CsvAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class AnalysisController {

    private final CsvAnalysisService analysisService;

    public AnalysisController(CsvAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a non-empty CSV file");
        }
        try {
            AnalysisResult result = analysisService.analyze(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to process CSV: " + e.getMessage());
        }
    }
}
