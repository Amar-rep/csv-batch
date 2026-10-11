package com.example.batch.config;

import javax.sql.DataSource;

import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.batch.model.FailedUserRecord;
import com.example.batch.model.UserRecord;

@Configuration
public class BatchWriterConfig {

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
    public JdbcBatchItemWriter<FailedUserRecord> failedUserWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<FailedUserRecord>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO failed_user_record
                            (job_execution_id, first_name, last_name, email, error_message, created_at)
                        VALUES
                            (:jobExecutionId, :firstName, :lastName, :email, :errorMessage, :createdAt)
                        """)
                .beanMapped()
                .build();
    }
}
