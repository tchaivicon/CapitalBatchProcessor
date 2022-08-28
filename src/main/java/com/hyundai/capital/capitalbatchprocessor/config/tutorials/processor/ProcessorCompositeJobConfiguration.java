package com.hyundai.capital.capitalbatchprocessor.config.tutorials.processor;

import com.hyundai.capital.capitalbatchprocessor.entity.tutorials.student.Teacher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ProcessorCompositeJobConfiguration {
    private static final String JOB_NAME = "processorCompositeBatch";
    private static final String BEAN_PREFIX= JOB_NAME + "_";

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
    @JobScope
    public Step step(){
        return stepBuilderFactory.get(BEAN_PREFIX+"step")
                .<Teacher, String>chunk(chunkSize)
                .reader(reader())
                .processor(compositeProcessor())
                .writer(writer())
                .build();
    }

    @Bean(BEAN_PREFIX+"reader")
    public JpaPagingItemReader<Teacher> reader(){
        return new JpaPagingItemReaderBuilder<Teacher>().name(BEAN_PREFIX+"reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("Select t from Teacher t")
                .build();
    }

    @Bean
    public CompositeItemProcessor<Teacher, String> compositeProcessor() {
        List<ItemProcessor<?, String>> delegates = new ArrayList<>(2);
        delegates.add(processor1());
        delegates.add(processor2());

        CompositeItemProcessor<Teacher, String> processor = new CompositeItemProcessor<>();
        processor.setDelegates(delegates);
        return processor;
    }

    private ItemProcessor<Teacher,String> processor1() {
        return teacher -> teacher.getName();
    }

    private ItemProcessor<String, String> processor2() {
        return name -> "안녕하세요 저는 "+name+"입니다.";
    }

    private ItemWriter<String> writer() {
        return items->{
          for(String item : items){
              log.info("TeacherName = {}",item);
          }
        };
    }
}
