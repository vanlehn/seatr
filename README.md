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
- The Hello World application can be accessed on amazon ec2 using the following URL : [http://ec2-52-35-118-9.us-west-2.compute.amazonaws.com:8080/seatr-0.0.1-SNAPSHOT/rest/hello](http://ec2-52-35-118-9.us-west-2.compute.amazonaws.com:8080/seatr-0.0.1-SNAPSHOT/rest/hello)

## JMeter
- JMeter tests cases for testing on aws ec2 server can be found at jmeter/tests/aws_ec2/.
- JMeter tests cases for testing on local server can be found at jmeter/tests/local/.
- The output of the testc can be found at jmeter/log/.
- The tests can either be run using the JMeter GUI or on Command Line.

  <b>JMeter GUI</b>
- In apache-jmeter-2.13\bin\ folder open JMeter.bat to access the JMeter GUI. 
- Click on the file button(top left corner of Menu bar) and then click on open and browse the test case.
- Now run the test using the Run button(centre of Menu bar) and then click on start.

  <b>Command Line</b>  
  jmeter -n -t  "testcase location"   "output log file location"
  
  eg.  jmeter -n -t ../../Test_Plan1.jmx -l ../../log.jtl
