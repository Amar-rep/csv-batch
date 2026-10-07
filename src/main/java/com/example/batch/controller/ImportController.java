package com.example.batch.controller;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/imports")
public class ImportController {

    private final JobLauncher jobLauncher;
    private final Job importUserJob;
    private final Path uploadDirectory;

    public ImportController(
            JobLauncher jobLauncher,
            Job importUserJob,
            @Value("${batch.upload-directory:./data/uploads}") String uploadDirectory) {
        this.jobLauncher = jobLauncher;
        this.importUserJob = importUserJob;
        this.uploadDirectory = Path.of(uploadDirectory).toAbsolutePath().normalize();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ImportResponse importCsv(@RequestParam("file") MultipartFile file) {
        validate(file);
        Path savedFile = saveUpload(file);

        try {
            JobExecution execution = jobLauncher.run(importUserJob, new JobParametersBuilder()
                    .addString("inputFile", savedFile.toString())
                    .addString("launchId", UUID.randomUUID().toString())
                    .toJobParameters());

            if (execution.getStatus() == BatchStatus.COMPLETED) {
                Files.deleteIfExists(savedFile);
            }

            return new ImportResponse(execution.getId(), execution.getStatus().name());
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CSV import failed", exception);
        }
    }

    private void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSV file is required");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".csv")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only CSV files are accepted");
        }
    }

    private Path saveUpload(MultipartFile file) {
        try {
            Files.createDirectories(uploadDirectory);
            Path destination = uploadDirectory.resolve(UUID.randomUUID() + ".csv").normalize();
            if (!destination.startsWith(uploadDirectory)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid upload path");
            }
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return destination;
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store uploaded CSV", exception);
        }
    }

    public record ImportResponse(Long executionId, String status) {
    }
}
