package serverless.challenge.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class UpdateUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LogManager.getLogger(UpdateUserHandler.class);
    private static final String TABLE_NAME = "UsersTable";
    public static final String EMAIL_FIELD = "email";
    public static final String NAME_FIELD = "name";
    public static final String ID_FIELD = "id";
    public static final String ERROR_MESSAGE = "{\"error\": \"Failed to update user\", \"message\": \"%s\"}";

    private final AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String userId = input.getPathParameters().get(ID_FIELD);
        logger.info("Updating user with ID: {}", userId);

        try {
            JsonNode body = objectMapper.readTree(input.getBody());
            String name = body.has(NAME_FIELD) ? body.get(NAME_FIELD).asText() : null;
            String email = body.has(EMAIL_FIELD) ? body.get(EMAIL_FIELD).asText() : null;

            Map<String, AttributeValue> key = new HashMap<>();
            key.put(ID_FIELD, new AttributeValue(userId));

            Map<String, String> expressionAttributeNames = new HashMap<>();
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();

            if (name != null) {
                expressionAttributeNames.put("#name", NAME_FIELD);
                expressionAttributeValues.put(":name", new AttributeValue(name));
            }
            if (email != null) {
                expressionAttributeNames.put("#email", EMAIL_FIELD);
                expressionAttributeValues.put(":email", new AttributeValue(email));
            }

            String updateExpression = "SET ";
            if (name != null) updateExpression += "#name = :name, ";
            if (email != null) updateExpression += "#email = :email, ";
            updateExpression = updateExpression.replaceAll(", $", ""); // Remove trailing comma

            logger.debug("Update expression: {}", updateExpression);

            UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                    .withTableName(TABLE_NAME)
                    .withKey(key)
                    .withUpdateExpression(updateExpression)
                    .withExpressionAttributeNames(expressionAttributeNames)
                    .withExpressionAttributeValues(expressionAttributeValues)
                    .withReturnValues(ReturnValue.ALL_NEW);

            UpdateItemResult updateItemResult = dynamoDB.updateItem(updateItemRequest);
            Map<String, String> updatedUser = getUserMap(updateItemResult, userId);

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(objectMapper.writeValueAsString(updatedUser));

        } catch (Exception e) {
            logger.error("Error updating user", e);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody(String.format(ERROR_MESSAGE, e.getMessage()));
        }
    }

    private static Map<String, String> getUserMap(UpdateItemResult updateItemResult, String userId) {
        Map<String, AttributeValue> updatedAttributes = updateItemResult.getAttributes();

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
