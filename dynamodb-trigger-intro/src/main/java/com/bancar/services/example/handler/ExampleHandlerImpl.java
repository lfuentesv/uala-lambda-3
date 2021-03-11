package com.bancar.services.example.handler;

import java.util.HashMap;

import com.bancar.services.example.processor.ExampleInsertProcessor;
import com.bancar.services.example.processor.ExampleRemoveProcessor;
import com.bancar.services.example.processor.ExampleUpdateProcessor;
import com.bancar.services.handler.DynamoDBTriggerHandler;
import com.bancar.services.processor.Processor;

/**
 * Created on: Apr 11, 2019
 * @author santiagobernal
 */
@SuppressWarnings("rawtypes")
public class ExampleHandlerImpl extends DynamoDBTriggerHandler<HashMap>{

    @Override
    public Processor<HashMap> getInsertProcessor() {
        return new ExampleInsertProcessor();
    }

    @Override
    public Processor<HashMap> getUpdateProcessor() {
        return new ExampleUpdateProcessor();
    }

    @Override
    public Processor<HashMap> getRemoveProcessor() {
        return new ExampleRemoveProcessor();
    }

    @Override
    public Class<HashMap> getClazz() {
        return HashMap.class;
    }

}
