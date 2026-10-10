package com.example.batch.service;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Component
@StepScope
public class FileValidationTasklet implements Tasklet {

    private static final String HEADER = "employee_id,firstName,lastName,email,salary";

    @Value("#{jobParameters['inputFile']}")
    private String filePath;

    @Value("${csv.max-file-size}")
    private DataSize maxFileSize;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<String> errorList = new ArrayList<>();

        if (filePath == null || filePath.isBlank()) {
            errorList.add("File path missing");
        } else {
            Path path = Path.of(filePath);

            if (!path.toString().toLowerCase(Locale.ROOT).endsWith(".csv")) {

                errorList.add("Not a CSV file");
            }

            if (Files.size(path) == 0) {

                errorList.add("FIle is empty");
            }
            if (Files.size(path) >= maxFileSize.toBytes()) {
                errorList.add("Invalid file size too large");
            }
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String header = reader.readLine();

                if (!HEADER.equals(header)) {
                    errorList.add("invalid header");
                }
            }

        }

        if (!errorList.isEmpty()) {
            contribution.setExitStatus(new ExitStatus("VALIDATION_FAILED", String.join(",", errorList)));
        }

        return RepeatStatus.FINISHED;
    }
}
