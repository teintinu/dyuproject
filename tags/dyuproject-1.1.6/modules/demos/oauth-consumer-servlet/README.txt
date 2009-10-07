To run the webapp, execute:
 mvn jetty:run

To test with google, edit src/main/resources/oauth_consumer.properties.
Replace the following with your keys:
 www.google.com.consumer_key = your_consumer_key
 www.google.com.consumer_secret = your_consumer_secret
 
To test the local oauth service provider, run the oauth-serviceprovider-servlet webapp.
See its README.txt
