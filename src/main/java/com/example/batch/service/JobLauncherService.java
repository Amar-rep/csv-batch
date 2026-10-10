package com.example.batch.service;

import java.nio.file.Path;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.batch.dto.ImportResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobLauncherService {

	private final JobLauncher jobLauncher;
	private final Job importUserJob;
	private final ValidationService validationService;

	public ImportResponse launchCsvProcessing(MultipartFile file) {
		Path inputFile = Path.of("src", "main", "resources", "employees_50_invalid.csv")
				.toAbsolutePath().normalize();
		try {
			JobExecution execution = jobLauncher.run(importUserJob,
					new JobParametersBuilder().addString("inputFile", inputFile.toString())
							.addString("launchId", "500").toJobParameters());
			return new ImportResponse(execution.getId(), execution.getStatus().name());
		} catch (Exception exception) {
			throw new RuntimeException("CSV import failed", exception);
		}
	}
}
