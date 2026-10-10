package com.example.batch.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.batch.dto.ImportResponse;
import com.example.batch.service.JobLauncherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/imports")
@RequiredArgsConstructor
public class ImportController {

	private final JobLauncherService jobLauncherService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportResponse> importCsv(@RequestPart("file") MultipartFile file) {
    	return ResponseEntity.ok(jobLauncherService.launchCsvProcessing(file));
    }

}
