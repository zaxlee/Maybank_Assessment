package com.zax.maybank_assessment.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StepMetricsListener implements StepExecutionListener {
    @Override public void beforeStep(StepExecution s) {
        log.info("[Batch] Step '{}' starting…", s.getStepName());
    }
    @Override public ExitStatus afterStep(StepExecution s) {
        log.info("[Batch] Step '{}' done. readCount={}, writeCount={}, skipCount={}, filterCount={}",
                s.getStepName(), s.getReadCount(), s.getWriteCount(), s.getSkipCount(), s.getFilterCount());
        return s.getExitStatus();
    }
}