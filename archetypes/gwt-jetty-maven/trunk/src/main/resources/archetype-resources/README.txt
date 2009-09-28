Eclipse setup:

1. Execute $ mvn package eclipse:eclipse

2. Enable the checkbox "Use Google Web Toolkit" by clicking Properties->Google->Web Toolkit

3. Edit the .project file and append the ff on the <natures> element:
   <nature>com.google.gdt.eclipse.core.webAppNature</nature>

Rapid development setup:
   If google-eclipse-plugin:
     - open run configurations and uncheck "Run built-in server"
     - always execute $ mvn -Dgwt.compiler.skip=true jetty:run before running hosted mode.

   
   Else a compile-launch step can be done via $ mvn jetty:run

