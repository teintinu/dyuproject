Eclipse setup:
   
   Execute $ mvn compile eclipse:eclipse


Rapid development setup:
   
   Using google-eclipse-plugin:
      1. Enable the checkbox "Use Google Web Toolkit" by right-clicking ${rootArtifactId}-client and selecting Properties->Google->Web Toolkit

      2. Edit the .project file and append the ff on the <natures> element:
         <nature>com.google.gdt.eclipse.core.webAppNature</nature>
     
      When testing against the server backend, disable hosted-mode by opening run configurations and uncheck "Run built-in server".
      Before running, execute $ mvn -Dgwt.compiler.skip=true jetty:run

   
   Using maven-jetty-plugin:
      $ mvn jetty:run
      This will do a "gwt-compile -> run-webapp" step with "overlays" from ${rootArtifactId}-server
      
