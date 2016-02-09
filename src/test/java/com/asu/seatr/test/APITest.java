package com.asu.seatr.test;

import com.asu.seatr.API;
import com.asu.seatr.common.Demo;

import static org.junit.Assert.*;

import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Test;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

public class APITest extends JerseyTest{

	@Override
    protected Application configure() {
        return new ResourceConfig(API.class);
    }
	
	@Test
    public void test() {
        final String hello = target("hello/plain").request().get(String.class);
        assertEquals("Hello World!", hello);
    }

}
