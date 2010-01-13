To run the webapp, execute:
 mvn jetty:run

Testing locally with google:
 The "anonymous" keys found in src/main/resources/oauth_consumer.properties will work.
 
 When you are ready to deploy your webapp, see to it that your site's domain is registered with google. 
 See https://www.google.com/accounts/ManageDomains
 After registration, replace the following with your keys:
  www.google.com.consumer_key = your_consumer_key
  www.google.com.consumer_secret = your_consumer_secret
 
To test the local oauth service provider, run the oauth-serviceprovider-servlet webapp.
See its README.txt
