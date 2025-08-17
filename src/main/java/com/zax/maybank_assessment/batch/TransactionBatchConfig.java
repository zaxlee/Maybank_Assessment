package com.zax.maybank_assessment.batch;

import com.zax.maybank_assessment.domain.Transaction;
import com.zax.maybank_assessment.repo.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class TransactionBatchConfig {

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("[HH:mm][:ss]");

    @Bean
    FlatFileItemReader<String[]> txnReader() {
        return new FlatFileItemReaderBuilder<String[]>()
                .name("txnReader")
                .resource(new ClassPathResource("data/transactions.txt")) // ← hardcoded for stability
                .encoding("UTF-8")
                .strict(true)           // fail fast if file missing
                .linesToSkip(1)         // header present
                .delimited().delimiter("|")
                .names("ACCOUNT_NUMBER","TRX_AMOUNT","DESCRIPTION","TRX_DATE","TRX_TIME","CUSTOMER_ID")
                .fieldSetMapper(fs -> new String[]{
                        fs.readString("ACCOUNT_NUMBER"),
                        fs.readString("TRX_AMOUNT"),
                        fs.readString("DESCRIPTION"),
                        fs.readString("TRX_DATE"),
                        fs.readString("TRX_TIME"),
                        fs.readString("CUSTOMER_ID")
                })
                .build();
    }

    @Bean
    ItemProcessor<String[], Transaction> txnProcessor() {
        return row -> Transaction.builder()
                .accountNumber(row[0].trim())
                .trxAmount(new BigDecimal(row[1].trim()))
                .description(row[2].trim())
                .trxDate(LocalDate.parse(row[3].trim()))
                .trxTime(LocalTime.parse(row[4].trim(), TIME_FMT))
                .customerId(row[5].trim())
                .build();
    }

    @Bean
    ItemWriter<Transaction> txnWriter(TransactionRepository repo) {
        Logger log = LoggerFactory.getLogger("BatchDuplicates");

        return items -> {
            Map<String, Transaction> unique = new LinkedHashMap<>();

            for (Transaction t : items) {
                String key = t.getAccountNumber() + "|" + t.getTrxAmount() + "|" +
                        t.getDescription()   + "|" + t.getTrxDate()   + "|" +
                        t.getTrxTime()       + "|" + t.getCustomerId();

                if (unique.containsKey(key)) {
                    log.warn("[Duplicate] Skipping duplicate in chunk: {}", key);
                } else {
                    unique.put(key, t);
                }
            }

            repo.saveAll(unique.values());
        };
    }

    @Bean
    public Step importStep(JobRepository jobRepository,
                           org.springframework.transaction.PlatformTransactionManager txManager,
                           ItemReader<String[]> reader,
                           ItemProcessor<String[], Transaction> processor,
                           ItemWriter<Transaction> writer) {
        return new StepBuilder("importStep", jobRepository)
                .<String[], Transaction>chunk(200, txManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skipLimit(1_000_000)
                .skip(org.springframework.dao.DataIntegrityViolationException.class)
                .skip(org.hibernate.exception.ConstraintViolationException.class)
                .noRollback(org.springframework.dao.DataIntegrityViolationException.class)
                .noRollback(org.hibernate.exception.ConstraintViolationException.class)
                .build();
    }

    @Bean
    public Job importJob(JobRepository jobRepository, Step importStep) {
        return new JobBuilder("importJob", jobRepository).start(importStep).build();
    }
}