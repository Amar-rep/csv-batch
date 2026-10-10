package com.example.batch.config;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.example.batch.model.UserRecord;

@Configuration
public class CsvReaderConfig {

	@Bean
	@StepScope
	public FlatFileItemReader<UserRecord> csvReader(
			@Value("#{jobParameters['inputFile']}") String inputFilePath) {
		return new FlatFileItemReaderBuilder<UserRecord>()
				.name("userReader")
				.resource(new FileSystemResource(inputFilePath))
				.delimited()
				.names("employee_id", "firstName", "lastName", "email", "salary")
				.targetType(UserRecord.class)
				.linesToSkip(1)
				.strict(true)
				.saveState(false)
				.build();
	}

	@Bean
	@StepScope
	public SynchronizedItemStreamReader<UserRecord> syncReader(
			FlatFileItemReader<UserRecord> csvReader) {
		SynchronizedItemStreamReader<UserRecord> reader = new SynchronizedItemStreamReader<>();
		reader.setDelegate(csvReader);
		return reader;
	}
}
