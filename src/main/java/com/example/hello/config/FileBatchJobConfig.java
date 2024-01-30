package com.example.hello.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FileBatchJobConfig {

    private static final int CHUNK_SIZE = 2;
    private static final int ADD_PRICE = 1000;
    private static final String JOB_NAME = "fileJob";
    private static final String STEP_NAME = "fileStep";

    private Resource inputFileResource = new FileSystemResource("input/sample-product.csv");
    private Resource outputFileResource = new FileSystemResource("output/output-product.csv");

    @Bean
    public Job fileJob(JobRepository jobRepository, Step fileStep){
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fileStep).build();
    }

    @Bean
    public Step fileStep(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder(STEP_NAME, jobRepository)
                .chunk(CHUNK_SIZE)
                .reader(fileItemReader())
                .processor(fileItemProcessor())
                .writer(fileItemWriter())
                .build();
    }

    @Bean
    public FlatFileItemReader fileItemReader(){
        FlatFileItemReader<Product> productFlatFileItemReader = new FlatFileItemReader<>();
        productFlatFileItemReader.setResource(this.inputFileResource);

        DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
        lineMapper.setFieldSetMapper(new ProductFieldSetMapper());

        productFlatFileItemReader.setLineMapper(lineMapper);
        return productFlatFileItemReader;
    }

    @Bean
    public ItemProcessor<Product, Product> fileItemProcessor(){
        return product -> {
            BigDecimal updatePrice = product.getPrice().add(new BigDecimal(ADD_PRICE));
            log.info("update product price : {}", updatePrice);
            product.updatePrice(updatePrice);
            return product;
        };
    }

    @Bean
    public FlatFileItemWriter<Product> fileItemWriter(){
        FlatFileItemWriter flatFileItemWriter = new FlatFileItemWriter();
        flatFileItemWriter.setResource((WritableResource) outputFileResource);
        flatFileItemWriter.setAppendAllowed(true);

        DelimitedLineAggregator<Product> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setFieldExtractor(new BeanWrapperFieldExtractor<>(){
            {
                setNames(new String[]{"id", "name", "price"});
            }
        });
        flatFileItemWriter.setLineAggregator(lineAggregator);
        return flatFileItemWriter;
    }
}
