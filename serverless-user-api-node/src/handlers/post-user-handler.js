const AWS = require('aws-sdk');
const { v4: uuidv4 } = require('uuid');

const dynamoDb = new AWS.DynamoDB.DocumentClient();
const sqs = new AWS.SQS();
const TABLE_NAME = "UsersTable";
const SQS_QUEUE_URL = process.env.CREATE_USER_QUEUE_URL;

exports.handleRequest = async (event) => {
  const body = JSON.parse(event.body);
  const newItem = {
    id: uuidv4(),
    name: body.name,
    email: body.email,
  };

  const params = {
    TableName: TABLE_NAME,
    Item: newItem,
  };

  try {
    await dynamoDb.put(params).promise();

    // Send message to SQS
    const sqsParams = {
      QueueUrl: SQS_QUEUE_URL,
      MessageBody: JSON.stringify(newItem),
    };
    await sqs.sendMessage(sqsParams).promise();

    return {
      statusCode: 201,
      body: JSON.stringify(newItem),
    };
  } catch (error) {
    return {
      statusCode: 500,
      body: JSON.stringify({ error: 'Could not create user' }),
    };
  }
};
