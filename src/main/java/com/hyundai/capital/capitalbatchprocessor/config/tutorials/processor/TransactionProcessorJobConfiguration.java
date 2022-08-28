package com.hyundai.capital.capitalbatchprocessor.config.tutorials.processor;

import com.hyundai.capital.capitalbatchprocessor.entity.tutorials.student.ClassInformation;
import com.hyundai.capital.capitalbatchprocessor.entity.tutorials.student.Teacher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TransactionProcessorJobConfiguration {
    public static final String JOB_NAME = "transactionProcessorBatch";
    public static final String BEAN_PREFIX = JOB_NAME + "_";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Value("${chunkSize:1000}")
    private int chunkSize;

    @Bean(JOB_NAME)
    public Job job(){
        return jobBuilderFactory.get(JOB_NAME)
                .preventRestart()
                .start(step())
                .build();
    }
    @Bean(BEAN_PREFIX+"step")
    public Step step() {
        return stepBuilderFactory.get(BEAN_PREFIX+"step")
                .<Teacher, ClassInformation>chunk(chunkSize)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();


    }

    private JpaPagingItemReader<Teacher> reader() {
        return new JpaPagingItemReaderBuilder <Teacher>()
                .name(BEAN_PREFIX+"reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT t FROM Teacher t")
                .build();
    }

    private ItemProcessor<Teacher,ClassInformation> processor() {
        return teacher -> new ClassInformation(teacher.getName(),teacher.getStudents().size());
    }

    private ItemWriter<ClassInformation> writer() {
        return items->{
            log.info(">>>>>>>>Item WRITE!!");
            for(ClassInformation item: items){
                log.info("반 정보 = {}", item);
            }
        };
    }
}
