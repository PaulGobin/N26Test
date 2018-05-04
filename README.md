# N26Test

You can clone or import this service in eclipse. This service is created using Spring Boot and maven. To build it, use mcn clean install. A self contained app.jar file will be created in the target folder. To run it, you can use maven or java -jar app.jar. The service, once started, will bind to the default 8088 port. You can access the swagger ui at http://localhost:8088/swagger-ui.html.
To execute any API endpoints, you must provide x-account and x-authtoken http headers, you can use any values for these headers since ther are not enforced or verified.


