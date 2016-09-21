/*
 * Copyright (C) 2016 Original Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rhc.dynamic.pipeline.utils;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is just a simple development aid. You can run it with `mvn exec:java`
 */
public class JettyTestServer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("JettyTestServer");
	public static Server server;

	public static void main(String [] args) throws Exception{
		server = new Server(0);
		server.setStopAtShutdown(true);

		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[] { "pom.html" });

		resource_handler.setResourceBase("src/test/resources");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
		server.setHandler(handlers);

		server.start();
		server.join();
		int serverPort = ((ServerConnector) server.getConnectors()[0]).getLocalPort();
		LOGGER.info("Server port for Jetty: " + serverPort);
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	System.err.println("hi");
		    	try {
					JettyTestServer.server.stop();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		 });
		
	}

}
