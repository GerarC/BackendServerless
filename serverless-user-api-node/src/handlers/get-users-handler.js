const AWS = require("aws-sdk");

const dynamoDB = new AWS.DynamoDB.DocumentClient();
const TABLE_NAME = "UsersTable";

exports.handleRequest = async (event) => {
  try {
    const params = {
      TableName: TABLE_NAME,
    };

    const data = await dynamoDB.scan(params).promise();

    return {
      statusCode: 200,
      body: JSON.stringify(data.Items),
    };
  } catch (error) {
    console.error("DynamoDB Error:", error);

    return {
      statusCode: 500,
      body: JSON.stringify({ error: "Could not fetch users" }),
    };
  }
};
