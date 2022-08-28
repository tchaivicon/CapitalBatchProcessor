package com.hyundai.capital.capitalbatchprocessor.config.tutorials;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeciderJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job deciderJob(){
        return jobBuilderFactory.get("deciderJob")
                .start(startStep())
                .next(decider())//홀.짝 구분
                .from(decider())//결과로 판단하는데..
                    .on("ODD")//홀수일때
                    .to(oddStep())//oddStep
                .from(decider())//결과로 판단하는데
                    .on("EVEN")//짝수일때
                    .to(evenStep())//evenStep
                .end()
                .build();
    }

    @Bean
    public Step startStep(){
        return stepBuilderFactory.get("startStep")
                .tasklet((stepContribution, chunkContext) -> {
                    log.info(">>>>>>>>>>>START!");
                    return RepeatStatus.FINISHED;
                }).build();
    }
    @Bean
    public Step evenStep(){
        return stepBuilderFactory.get("evenStep")
                .tasklet((stepContribution, chunkContext) -> {
                    log.info(">>>>>>>짝수입니다.");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step oddStep(){
        return stepBuilderFactory.get("oddStep")
                .tasklet((stepContribution, chunkContext) -> {
                    log.info(">>>>>>>>홀수입니다.");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public JobExecutionDecider decider(){
        return new OddDecider();
    }
    public static class OddDecider implements JobExecutionDecider{
        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution){
            Random rand = new Random();

            int randomNumber = rand.nextInt(50)+1;
            log.info("랜덤숫자 : {}", randomNumber);

            if(randomNumber % 2 == 0){
                return new FlowExecutionStatus("EVEN");
            }else{
                return new FlowExecutionStatus("ODD");
            }
        }
    }
}
