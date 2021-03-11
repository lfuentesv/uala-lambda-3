package com.bancar.services.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.OperationType;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.bancar.services.processor.DefaultProcessor;
import com.bancar.services.processor.Processor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * Base handler for learning how to work with dynamodb triggers. 
 * The handler captures the event, and gets the objects associated with the table
 * based on the class used in the IMAGE parameter. 
 * For more information on lambda dynamodb triggers and the dynamodbEvent format
 * see {@link https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Streams.Lambda.Tutorial.html}
 * 
 * This class should be extended to use in your application.
 * The IMAGE parameter would be the class that you want to capture events from
 * The following methods should be implemented:
 * - getInsertProcessor -> processes the INSERT operation on the table
 * - getUpdateProcessor -> processes the MODIFY operation on the table
 * - getRemoveProcessor -> processes the REMOVE operation on the table
 * All of these methods return a {@link Processor} that should be implemented for each case
 * If no processor is found, the Handler will execute the {@link DefaultProcessor}.
 * 
 * Study this handler to see how streams work and the Items that are obtained
 * when the trigger is executed
 * 
 * @author santiagobernal
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class DynamoDBTriggerHandler<IMAGE> implements RequestHandler<DynamodbEvent, Void>{
    
    Map<OperationType, Processor> processors;
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * The handler receives a DynamodbEvent class which has a list of records
     * An example of this event can be seen at:
     * {@link https://github.com/aws/aws-lambda-dotnet/blob/master/Libraries/test/EventsTests/dynamodb-event.json}
     * Each record contains a newImage and/or an oldImage, depending on the type of operation
     * Operations include: INSERT, MODIFY and REMOVE
     */
    @Override
    public Void handleRequest(DynamodbEvent ddbEvent, Context context) {
        int successCounter = 0;
        if (ddbEvent.getRecords() == null || ddbEvent.getRecords().isEmpty()) {
            System.out.println("No records to process!");
            return null;
        }
        //A dynamodbEvent may contain multiple records of operations executed
        //The amount of batches can be controlled when configuring the trigger in AWS
        for (DynamodbStreamRecord record : ddbEvent.getRecords()) {
            System.out.println("EventID [" + record.getEventID() + "] EventName [" + record.getEventName() + "]");
            System.out.println("StreamRecord [" + record.getDynamodb().toString() + "]");
            try {
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                
                Optional<Map<String, AttributeValue>> optNewImage = Optional.ofNullable(record.getDynamodb().getNewImage());
                Optional<Map<String, AttributeValue>> optOldImage = Optional.ofNullable(record.getDynamodb().getOldImage());
                System.out.println("optNewImage " + optNewImage);
                System.out.println("optOldImage " + optOldImage);
                System.out.println("streamRecord.getOldImage() " + record.getDynamodb().getOldImage());
                System.out.println(record.getDynamodb().toString() + ", ok");
                
                //Convert the images in the input to the desired object
                IMAGE newImage = optNewImage.map(x -> getImage(x)).orElse(null);
                IMAGE oldImage = optOldImage.map(x -> getImage(x)).orElse(null);
                
                //Get the processor from the map, and execute it
                this.getProcessors().getOrDefault(OperationType.fromValue(record.getEventName()), new DefaultProcessor()).processImages(newImage, oldImage);
                successCounter++;
            } catch (Exception e) {
                System.out.println("Error processing record " + record.getDynamodb().toString());
                e.printStackTrace();
            }
        }
        System.out.println("Totally processed " + ddbEvent.getRecords().size() + " records");
        System.out.println(successCounter + " records finished successfully, " + (ddbEvent.getRecords().size() - successCounter) + " records finished with errors");
        return null;
    }
    
    /**
     * A map of processors is defined, specifying the one used for each operation
     * @return
     */
    public Map<OperationType, Processor> getProcessors() {
        if(this.processors == null) {
            this.processors = new HashMap<>();
            this.processors.put(OperationType.INSERT, getInsertProcessor());
            this.processors.put(OperationType.MODIFY, getUpdateProcessor());
            this.processors.put(OperationType.REMOVE, getRemoveProcessor());
        }
        return this.processors;
    }
    
    public abstract Processor<IMAGE> getInsertProcessor();
    public abstract Processor<IMAGE> getUpdateProcessor();
    public abstract Processor<IMAGE> getRemoveProcessor();
    
    /**
     * DynamoDB objects are stored using a AWS syntax using AttributeValues
     * AWS gives us the {@link ItemUtils} which allows us to convert it to 
     * simple json. We then use a jackson mapper to turn it into the object.
     * @param item
     * @return
     */
    public IMAGE getImage(Map<String, AttributeValue> item) {
        try {
            return mapper.readValue(ItemUtils.toItem(item).toJSON(), getClazz());
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public abstract Class<IMAGE> getClazz();
   

}
