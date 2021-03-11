package com.bancar.services.processor;

/**
 * Created on: Apr 8, 2019
 * @author santiagobernal
 */
public class DefaultProcessor implements Processor<Object>{

    @Override
    public void processImages(Object newImage, Object oldImage) {
        System.out.println("No processor was found, be sure to setup the methods in the handler");
        
    }

}
