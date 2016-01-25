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
- Tomcat Server console can be accessed using the following URL : [http://ec2-52-35-118-9.us-west-2.compute.amazonaws.com:8080/](http://ec2-52-35-118-9.us-west-2.compute.amazonaws.com:8080/)

## JMeter
- JMeter tests can be found at jmeter directory on the master branch.
- Either Load the JMeter test plans in JMeter GUI or run it on command line using command:  
  jmeter -n -t  "testcase location"   "output log file location"
  
  eg.  jmeter -n -t ../../Test_Plan1.jmx -l ../../log.jtl
