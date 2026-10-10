package com.example.batch.service;

import java.time.LocalDateTime;

import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.batch.model.FailedUserRecord;
import com.example.batch.model.UserRecord;
import com.example.batch.repository.FailedRecordRepository;

@Component
@StepScope
public class UserSkipListener implements SkipListener<UserRecord, UserRecord> {
	private final FailedRecordRepository failedRecordRepository;
	private final Long jobExecutionId;

	public UserSkipListener(FailedRecordRepository failedRecordRepository,
			@Value("#{stepExecution.jobExecution.id}") Long jobExecutionId) {
		this.failedRecordRepository = failedRecordRepository;
		this.jobExecutionId = jobExecutionId;
	}

	@Override
	public void onSkipInProcess(UserRecord record, Throwable t) {
		FailedUserRecord failedUserRecord = new FailedUserRecord();
		failedUserRecord.setJobExecutionId(jobExecutionId);
		failedUserRecord.setCreatedAt(LocalDateTime.now());
		failedUserRecord.setEmail(record.getEmail());
		failedUserRecord.setErrorMessage(t.getMessage());
		failedUserRecord.setFirstName(record.getFirstName());
		failedUserRecord.setLastName(record.getLastName());
		failedRecordRepository.save(failedUserRecord);
	}
}
