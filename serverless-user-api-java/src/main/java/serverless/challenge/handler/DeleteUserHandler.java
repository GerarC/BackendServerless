package serverless.challenge.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DeleteUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LogManager.getLogger(DeleteUserHandler.class);
    private static final String TABLE_NAME = "UsersTable";
    public static final String ID_FIELD = "id";
    public static final String EMAIL_FIELD = "email";
    public static final String NAME_FIELD = "name";
    public static final String ERROR_MESSAGE = "{\"error\": \"Failed to update user\", \"message\": \"%s\"}";

    private final AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String userId = input.getPathParameters().get(ID_FIELD);
        logger.info("Updating user with ID: {}", userId);
        try {

            Map<String, AttributeValue> key = new HashMap<>();
            key.put(ID_FIELD, new AttributeValue(userId));

            DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
                    .withTableName(TABLE_NAME)
                    .withKey(key)
                    .withReturnValues(ReturnValue.ALL_OLD);

            DeleteItemResult deleteItemResult = dynamoDB.deleteItem(deleteItemRequest);

            Map<String, String> deletedUser = getUserMap(deleteItemResult, userId);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(objectMapper.writeValueAsString(deletedUser));

        } catch (Exception e){
            logger.error("Error updating user", e);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody(String.format(ERROR_MESSAGE, e.getMessage()));
        }
    }

    private static Map<String, String> getUserMap(DeleteItemResult deleteItemResult, String userId) {
        Map<String, AttributeValue> updatedAttributes = deleteItemResult.getAttributes();

        // Convert updated attributes to JSON response
        Map<String, String> updatedUser = new HashMap<>();
        updatedUser.put(ID_FIELD, userId);
        if (updatedAttributes.containsKey(NAME_FIELD)) {
            updatedUser.put(NAME_FIELD, updatedAttributes.get(NAME_FIELD).getS());
        }
        if (updatedAttributes.containsKey(EMAIL_FIELD)) {
            updatedUser.put(EMAIL_FIELD, updatedAttributes.get(EMAIL_FIELD).getS());
        }
        return updatedUser;
    }
}