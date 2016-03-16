package com.asu.seatr.api.test.jmeter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jorphan.collections.HashTree;
import org.json.*;

public class LoadTest {
	public void readRequestFrom(String path) throws IOException{
		File f=new File(path);
		InputStream is=new FileInputStream(f);
		String jsonTxt=IOUtils.toString(is);
		JSONArray jsonArray=new JSONArray(jsonTxt);
		
		// JMeter Engine
		StandardJMeterEngine jmeter = new StandardJMeterEngine();
		
		 // Initialize Properties, logging, locale, etc.
		JMeterUtils.setJMeterHome("C:\\apache-jmeter-2.11\\");
        JMeterUtils.loadJMeterProperties("C:\\apache-jmeter-2.11\\bin\\jmeter.properties");
        //JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
        JMeterUtils.initLocale();
        
        // JMeter Test Plan, basic all u JOrphan HashTree
        HashTree testPlanTree = new HashTree();
        
        // HTTP Sampler
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy ();
        httpSampler.setDomain("ec2-52-35-118-9.us-west-2.compute.amazonaws.com");
        httpSampler.setPort(8080);
        httpSampler.setPath("/seatr/rest/analyzer/1/students?external_student_id=22&external_course_id=37");
        httpSampler.setMethod("GET");
        httpSampler.setName("Test 1");
        httpSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
        
        // Loop Controller
        LoopController loopController = new LoopController();
        loopController.setLoops(1);
        loopController.setFirst(true);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.initialize();
        
        // Thread Group
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Sample Thread Group");
        threadGroup.setNumThreads(1);
        threadGroup.setRampUp(1);
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
        
        // Test Plan
        TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        
        // Construct Test Plan from previously initialized elements
        testPlanTree.add("testPlan", testPlan);
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(httpSampler);
        // save generated test plan to JMeter's .jmx file format
        SaveService.saveTree(testPlanTree, new FileOutputStream(".\\jmeter\\jmeter_api_sample.jmx"));
        
        //add Summarizer output to get test progress in stdout like:
        // summary =      2 in   1.3s =    1.5/s Avg:   631 Min:   290 Max:   973 Err:     0 (0.00%)
        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }
        
        // Store execution results into a .jtl file, we can save file as csv also
        String reportFile = ".\\report\\report.jtl";
        String csvFile = ".\\report\\report.csv";
        ResultCollector logger = new ResultCollector(summer);
        logger.setFilename(reportFile);
        ResultCollector csvlogger = new ResultCollector(summer);
        csvlogger.setFilename(csvFile);
        testPlanTree.add(testPlanTree.getArray()[0], logger);
        testPlanTree.add(testPlanTree.getArray()[0], csvlogger);

        
        // Run Test Plan
        jmeter.configure(testPlanTree);
        jmeter.run();
		
		/*for(int i=0;i<jsonArray.length();i++){
			JSONObject json=jsonArray.getJSONObject(i);
			System.out.println(json);
		}*/
	}
	
	public static void main(String[] args) throws IOException{
		LoadTest t=new LoadTest();
		t.readRequestFrom("C:\\Users\\lzhang90\\Desktop\\STEAR\\ope_tutor_sample_responses\\student_response_sample.json");
	}
}
