package com.example.batch;

import com.example.batch.repository.UserRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest
class BatchJobIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private UserRecordRepository userRecordRepository;

    @Test
    void importsValidRowsAndFiltersInvalidRows() throws Exception {
        userRecordRepository.deleteAll();

        String inputFile = new ClassPathResource("user_details_500.csv").getFile().getAbsolutePath();
        JobExecution execution = jobLauncherTestUtils.launchJob(new JobParametersBuilder()
                .addString("inputFile", inputFile)
                .addString("launchId", "integration-test")
                .toJobParameters());

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        StepExecution step = execution.getStepExecutions().iterator().next();
        assertThat(step.getReadCount()).isEqualTo(5);
        assertThat(step.getFilterCount()).isEqualTo(2);
        assertThat(step.getWriteCount()).isEqualTo(3);
        assertThat(userRecordRepository.count()).isEqualTo(3);
        assertThat(userRecordRepository.findAll())
                .extracting("firstName")
                .containsExactlyInAnyOrder("ALICE", "BOB", "EVAN");
    }
}
