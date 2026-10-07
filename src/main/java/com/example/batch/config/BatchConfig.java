package com.example.batch.config;

import com.example.batch.model.UserRecord;
import com.example.batch.processor.UserProcessor;
import com.example.batch.writer.UserWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    @StepScope
    public FlatFileItemReader<UserRecord> userReader(
            @Value("#{jobParameters['inputFile']}") String inputFilePath) {
        return new FlatFileItemReaderBuilder<UserRecord>()
                .name("userReader")
                .resource(new FileSystemResource(inputFilePath))
                .delimited()
                .names("firstName", "lastName", "zipcode", "phone1", "phone2", "email", "web")
                .targetType(UserRecord.class)
                .linesToSkip(1)
                .strict(true)
                .build();
    }

    @Bean
    public Step importUserStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<UserRecord> userReader,
            UserProcessor userProcessor,
            UserWriter userWriter) {
        return new StepBuilder("importUserStep", jobRepository)
                .<UserRecord, UserRecord>chunk(10, transactionManager)
                .reader(userReader)
                .processor(userProcessor)
                .writer(userWriter)
                .build();
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository, Step importUserStep) {
        return new JobBuilder("importUserJob", jobRepository)
                .start(importUserStep)
                .build();
    }
}
