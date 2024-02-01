package com.example.hello.config;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class ProductFieldSetMapper implements FieldSetMapper<Product> {

    @Override
    public Product mapFieldSet(FieldSet fieldSet) throws BindException {
        return new Product(fieldSet.readLong(0),
                           fieldSet.readString(1),
                           fieldSet.readBigDecimal(2));
    }
}
