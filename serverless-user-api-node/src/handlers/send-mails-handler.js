const AWS = require('aws-sdk');
const sns = new AWS.SNS();

exports.handleRequest = async (event) => {
  for (const record of event.Records) {
    const user = JSON.parse(record.body);

    const snsParams = {
      Message: `A new user has been created: ${user.name}, Email: ${user.email}`,
      Subject: 'New User Created',
      TopicArn: process.env.EMAIL_TOPIC_ARN, 
    };

    try {
      await sns.publish(snsParams).promise();
    } catch (error) {
      console.error('Error sending email:', error);
    }
  }
};