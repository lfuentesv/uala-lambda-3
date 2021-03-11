package com.bancar.services.example.processor;

import java.util.HashMap;

import com.bancar.services.processor.Processor;

/**
 * Created on: Apr 11, 2019
 * @author santiagobernal
 */
@SuppressWarnings("rawtypes")
public class ExampleUpdateProcessor implements Processor<HashMap>{

    @Override
    public void processImages(HashMap newImage, HashMap oldImage) {
        System.out.println("Got update of old image: " + oldImage + " to new image: " + newImage);
        
    }

}
