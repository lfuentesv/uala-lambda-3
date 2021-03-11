package com.bancar.services.example.processor;

import java.util.HashMap;
import com.bancar.services.processor.Processor;


import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;



/**
 * Created on: Apr 11, 2019
 * @author santiagobernal
 */
@SuppressWarnings("rawtypes")
public class ExampleInsertProcessor implements Processor<HashMap>{

    @Override
    public void processImages(HashMap newImage, HashMap oldImage) {
        System.out.println("Got insert of new image: " + newImage);
        
       SnsClient sns = SnsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        pubTopic(sns, newImage.get("id").toString(), "arn:aws:sns:us-east-1:161142984839:Contacto-Topic-LFV");
        sns.close();
        
    }
    
    public static void pubTopic(SnsClient snsClient, String message, String topicArn) {

        try {
            PublishRequest request = PublishRequest.builder()
                .message(message)
                .topicArn(topicArn)
                .build();

            PublishResponse result = snsClient.publish(request);
            System.out.println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());

         } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
              System.exit(1);
         }

    }
}
