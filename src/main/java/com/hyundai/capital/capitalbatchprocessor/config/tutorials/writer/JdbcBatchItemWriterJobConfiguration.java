package com.hyundai.capital.capitalbatchprocessor.config.tutorials.writer;

import com.hyundai.capital.capitalbatchprocessor.entity.tutorials.Pay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JdbcBatchItemWriterJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    private static final int chunksize = 1;

    @Bean
    public Job jdbcBatchItemWriterJob(){
        return jobBuilderFactory.get("jdbcBathItemWriterJob")
                .start(jdbcBatchItemWriterStep())
                .build();
    }

    @Bean
    public Step jdbcBatchItemWriterStep() {
        return stepBuilderFactory.get("jdbcBatchItemWriterSetup")
                .<Pay, Pay>chunk(chunksize)
                .reader(jdbcBatchItemReader())
                .writer(jdbcBatchItemWriter())
                .build();
    }
    @Bean
    public JdbcBatchItemWriter<Pay> jdbcBatchItemWriter() {
        return new JdbcBatchItemWriterBuilder<Pay>()
                .dataSource(dataSource)
                .sql("insert into pay2(amount, tx_name, tx_date_time) " +
                        "values(:amount, :txName, :txDateTime)")
                .beanMapped()
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Pay> jdbcBatchItemReader() {
        return new JdbcCursorItemReaderBuilder<Pay>()
                .fetchSize(chunksize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Pay.class))
                .sql("SELECT id, amount, tx_name, tx_date_time from pay")
                .name("jdbcBatchItemWriter")
                .build();

    }
}
