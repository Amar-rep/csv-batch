package com.example.batch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batch.Exception.InvalidRecordException;
import com.example.batch.model.UserRecord;
import com.example.batch.processor.UserProcessor;
import com.example.batch.service.FileValidationTasklet;
import com.example.batch.service.UserSkipListener;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {
	private final UserSkipListener userSkipListener;

	@Bean
	public Step validateFileStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			FileValidationTasklet fileValidationTasklet) {
		return new StepBuilder("validateFileStep", jobRepository)
				.tasklet(fileValidationTasklet, transactionManager)
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public JdbcBatchItemWriter<UserRecord> userWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<UserRecord>()
				.dataSource(dataSource)
				.sql("""
						INSERT INTO user_records
						    (employee_id, first_name, last_name, email, salary)
						VALUES
						    (:employee_id, :firstName, :lastName, :email, :salary)
						ON CONFLICT DO NOTHING
						""")
				.beanMapped()
				.assertUpdates(false)
				.build();
	}

	@Bean
	public Step importUserStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			SynchronizedItemStreamReader<UserRecord> syncReader, UserProcessor userProcessor,
			JdbcBatchItemWriter<UserRecord> userWriter,
			@Qualifier("batchTaskExecutor") TaskExecutor batchTaskExecutor) {
		return new StepBuilder("importUserStep", jobRepository).<UserRecord, UserRecord>chunk(100, transactionManager)
				.reader(syncReader).processor(userProcessor).writer(userWriter).faultTolerant()
				.skip(InvalidRecordException.class).skipLimit(50).listener(userSkipListener)
				.taskExecutor(batchTaskExecutor).build();
	}

	@Bean
	public Job importUserJob(JobRepository jobRepository,
			@Qualifier("validateFileStep") Step validateFileStep,
			@Qualifier("importUserStep") Step importUserStep) {
		return new JobBuilder("importUserJob", jobRepository)
				.start(validateFileStep)
				.on("VALIDATION_FAILED").fail()
				.from(validateFileStep)
				.on("COMPLETED").to(importUserStep)
				.end()
				.build();
	}

}
