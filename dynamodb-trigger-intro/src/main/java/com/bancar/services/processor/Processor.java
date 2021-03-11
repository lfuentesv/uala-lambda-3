package com.bancar.services.processor;

/**
 *
 * @author santiagobernal
 */
public interface Processor<IMAGE> {

    public abstract void processImages(IMAGE newImage, IMAGE oldImage);
}
