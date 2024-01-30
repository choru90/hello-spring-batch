package com.example.hello.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class HelloSpringBatchConfig {

    private static final String STEP_NAME = "step1";
    private static final String JOB_NAME ="job1";
    private static final String MESSAGE = "message";

    @Bean
    public Tasklet testTasklet(){
        return ((contribution, chunkContext) -> {
            log.info("!!!!!!!!! task let!!!!!!!");
            return RepeatStatus.FINISHED;
        });
    }

    @Bean
    public Step step(JobRepository jobRepository, Tasklet testTasklet, PlatformTransactionManager transactionManager){
        return new StepBuilder(STEP_NAME, jobRepository)
                .tasklet(testTasklet, transactionManager)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step){
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(step)
                .build();
    }
}
