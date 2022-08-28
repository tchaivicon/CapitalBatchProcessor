package com.hyundai.capital.capitalbatchprocessor.config.tutorials.test;

import com.hyundai.capital.capitalbatchprocessor.entity.tutorials.sales.SalesSum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ofPattern;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class BatchJpaTestConfiguration {
    public static final DateTimeFormatter FORMATTER = ofPattern("yyyy-MM-dd");
    public static final String JOB_NAME = "batchJpaUnitTestJob";
    
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    
    @Value("${chunkSize:1000}")
    private int chunkSize;
    
    @Bean
    public Job job(){
        return jobBuilderFactory.get(JOB_NAME)
                .start(batchJpaUnitTestJobStep())
                .build();
    }
    
    @Bean
    public Step batchJpaUnitTestJobStep() {
        return stepBuilderFactory.get("batchJpaUnitTestJobStep")
                .<SalesSum, SalesSum>chunk(chunkSize)
                .reader(batchJpaUnitTestJobReader(null))
                .writer(batchJpaUnitTestJobWriter())
                .build();
                
    }

    private ItemReader<SalesSum> batchJpaUnitTestJobReader(@Value("#{jobParameters[orderDate]}")String orderDate) {

        Map<String, Object> params = new HashMap<>();
        params.put("orderDate", LocalDate.parse(orderDate, FORMATTER));

        String className = SalesSum.class.getName();
        String queryString = String.format(
                        "SELECT new %s(s.orderDate, SUM(s.amount))"+
                        "FROM Sales s" +
                        "WHERE s.orderDate = :orderDate"+
                        "GROUP BY s.orderDate", className);
        return new JpaPagingItemReaderBuilder<SalesSum>()
                .name("batchJpaUnitTestJobReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString(queryString)
                .parameterValues(params)
                .build();
    }

    private ItemWriter<SalesSum> batchJpaUnitTestJobWriter() {
        JpaItemWriter<SalesSum>jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

}


