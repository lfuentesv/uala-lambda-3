package com.bancar.services.example.processor;

import java.util.HashMap;

import com.bancar.services.processor.Processor;

/**
 * Created on: Apr 11, 2019
 * @author santiagobernal
 */
@SuppressWarnings("rawtypes")
public class ExampleRemoveProcessor implements Processor<HashMap>{

    @Override
    public void processImages(HashMap newImage, HashMap oldImage) {
        System.out.println("Got remove of old image: " + oldImage);
        
    }

}
