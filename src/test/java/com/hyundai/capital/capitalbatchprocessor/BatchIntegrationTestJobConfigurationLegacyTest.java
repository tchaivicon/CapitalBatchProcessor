package com.hyundai.capital.capitalbatchprocessor;

import com.hyundai.capital.capitalbatchprocessor.config.tutorials.test.BatchJpaTestConfiguration;
import com.hyundai.capital.capitalbatchprocessor.config.tutorials.test.TestBatchLegacyConfig;
import com.hyundai.capital.capitalbatchprocessor.entity.tutorials.sales.Sales;
import com.hyundai.capital.capitalbatchprocessor.entity.tutorials.sales.SalesRepository;
import com.hyundai.capital.capitalbatchprocessor.entity.tutorials.sales.SalesSum;
import com.hyundai.capital.capitalbatchprocessor.entity.tutorials.sales.SalesSumRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static com.hyundai.capital.capitalbatchprocessor.config.tutorials.test.BatchJpaTestConfiguration.FORMATTER;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={BatchJpaTestConfiguration.class, TestBatchLegacyConfig.class})
public class BatchIntegrationTestJobConfigurationLegacyTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private SalesRepository salesRepository;
    @Autowired
    private SalesSumRepository salesSumRepository;

    //private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    @After
    public void tearDown() throws Exception{
        salesRepository.deleteAllInBatch();
        salesSumRepository.deleteAllInBatch();
    }

    @Test
    public void 기간내_Sales가_집계되어_SalesSum_이된다() throws Exception{

        //given
        LocalDate orderDate = LocalDate.of(2019,12,10);
        Long amount1 = 1000L;
        Long amount2 = 500L;
        Long amount3 = 100L;

        salesRepository.save(new Sales(orderDate, amount1,"1"));
        salesRepository.save(new Sales(orderDate, amount2,"2"));
        salesRepository.save(new Sales(orderDate, amount3,"3"));

        JobParameters jobParameter = new JobParametersBuilder()
                .addString("orderDate",orderDate.format(FORMATTER))
                .toJobParameters();
        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameter);

        //then
        assert(jobExecution.getStatus().isLessThanOrEqualTo(BatchStatus.COMPLETED));
        List<SalesSum> salesSumList = salesSumRepository.findAll();
        assert(salesSumList.size()==1);
        assert(salesSumList.get(0).getOrderDate().isEqual(orderDate));
        assert(salesSumList.get(0).getAmountSum()==(amount1+amount2+amount3));

    }
}

