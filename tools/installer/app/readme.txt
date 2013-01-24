


For complete details, plans, functionality description and roadmap check
the wiki link

http://community.project.net/trac/pnet-community/wiki/feature-product-installer


The vision for this installer to make so functional that it should handle settings up production systems.

Technical objective is -
User should only need to deploy WAR application by simply copying WAR to webapps or using tomcat manager. When deployed and accessing deployed application using browser, it should display the first page of installer. It should then guide user with wizard UI and setup required configurations** automatically for successful working of project.net.

** currently following activities are done automatically
- copy mail/activation.jar
- copy endorsed jars
- copy jdbc jars
- change context.xml - database configuration
- change context.xml - email notifications configuration
- Execution of all project.net scripts to populate the existing blank database
- verification of database connection
- verification of email sending
- stop tomcat as and when the config files (context.xml, web.xml) are changed


possible enhancements
- check the deployment server configuration and suggest the optimal jvm/tomcat tuning parameters
- post installation redirect user to register (sales/support) screen on project.net site, OR before installing collect customer details and submit them using ajax to project.net server
- support for additional servers, such as widely used resin


