# Student Embedded Assessment and Task Recommender

## Prerequisites
- Java 1.7
- Eclipse (Mars)
- Maven (eclipse plugin)
- Tomcat server (version 8)
- JMeter 2.13

## Run
- Checkout the project from git 
- Build the project with maven, Right click on project and select 'Run as' -> Maven clean, install
- 'Run as' -> Run on server -> Select Tomcat 8
- The following URL should display a sample json: [http://localhost:8080/seatr/rest/hello](http://localhost:8080/seatr/rest/hello)

## JMeter
- Either Load the JMeter test plans in JMeter GUI or run it on command line using command:  
  jmeter -n -t  "testcase location"   "output log file location"
  
  eg.  jmeter -n -t ../../Test_Plan1.jmx -l ../../log.jtl
