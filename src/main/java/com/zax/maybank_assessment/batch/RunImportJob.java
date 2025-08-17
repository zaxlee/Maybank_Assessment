package com.zax.maybank_assessment.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RunImportJob implements ApplicationRunner {
    private final JobLauncher jobLauncher;
    private final Job importJob;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("[Runner] Launching importJob…");
        var params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();
        var exec = jobLauncher.run(importJob, params);
        log.info("[Runner] Finished. status={}, exit={}", exec.getStatus(), exec.getExitStatus());
    }
}