package com.example.hello.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.logging.log4j2.SpringBootConfigurationFactory;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Long id;
    private String name;
    private BigDecimal price;

    public void updatePrice(BigDecimal price){
        this.price = price;
    }
}
