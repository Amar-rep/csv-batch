package com.example.batch.writer;

import com.example.batch.model.UserRecord;
import com.example.batch.repository.UserRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

// @Component
// @Slf4j
// @RequiredArgsConstructor
// public class UserWriter implements ItemWriter<UserRecord> {

// private final UserRecordRepository repository;

// @Override
// public void write(Chunk<? extends UserRecord> chunk) {
// repository.saveAll(chunk.getItems());

// }
// }
