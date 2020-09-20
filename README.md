# portfolio-tracking-api

The API is written in java (Spring) and the database used is H2(in-memory database). 

Reason for using this database was it can be coupled into a java jar and no need for manual setup.

Command to run jar :-

java -jar -Dserver.port=8080 portfolio-tracking-api.jar

Base Documentation can be found at :- http://localhost:8000/swagger-ui.html

Health of the API can be checked at :- http://localhost:8000/actuator/health

Database can be found at :- http://localhost:8000/h2-console

Base URL :-  http://localhost:8000/api/portfolio-tracking

