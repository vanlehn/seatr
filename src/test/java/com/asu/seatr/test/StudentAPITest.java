package com.asu.seatr.test;

import static org.junit.Assert.assertEquals;
import org.glassfish.jersey.test.JerseyTest;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.asu.seatr.api.StudentAPI;

public class StudentAPITest extends JerseyTest {
	@Override
    protected Application configure() {
        return new ResourceConfig(StudentAPI.class);
    }
	
	@Test
    public void test() {
        final String hello = target("hello/plain").request().get(String.class);
        assertEquals("Hello World!", hello);
    }
}
