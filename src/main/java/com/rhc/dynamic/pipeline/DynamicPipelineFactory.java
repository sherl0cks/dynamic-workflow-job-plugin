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
package com.rhc.dynamic.pipeline;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.io.IOUtils;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhc.automation.model.Engagement;

public class DynamicPipelineFactory implements Serializable {

	private static final long serialVersionUID = -7772221801921220616L;
	private static final Logger LOGGER = LoggerFactory.getLogger("DynamicPipelineFactory");
	public static final String AUTOMATION_API_VERSION = "0.1.0";

	private final CpsScript script;
	private String configFile;
	private String applicationName;
	private transient Engagement engagement;
	private String pipelineType;

	public DynamicPipelineFactory(CpsScript script) {
		this.script = script;

	}

	public DynamicPipelineFactory withConfigurationFile(String fileName) throws IOException {

		InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
		if (is == null) {
			throw new RuntimeException("Could not find the specified configuration file: " + fileName);
		}
		this.configFile = IOUtils.toString(is);
		engagement = new ObjectMapper().readValue(this.configFile, Engagement.class);

		return this;
	}

	public DynamicPipelineFactory withApplicationName(String appName) {
		this.applicationName = appName;
		return this;
	}

	public DynamicPipelineFactory withReleaseType() {
		this.pipelineType = "Release";
		return this;
	}

	public DynamicPipelineFactory withDevelopmentType() {
		this.pipelineType = "Development";
		return this;
	}

	public String generatePipelineScript() {
		checkConfiguration();
		Visitor visitor;
		if (pipelineType.equalsIgnoreCase("Release")) {
			visitor = new ReleasePipelineVisitor(applicationName);
		} else if (pipelineType.equalsIgnoreCase("Development")) {
			visitor = new DevelopmentPipelineVisitor(applicationName);
		} else {
			throw new RuntimeException("You must set the pipelineType to either Release or Development");
		}

		VisitPlanner.orchestrateVisit(visitor, engagement);
		String pipelineScript = visitor.getPipelineScript();
		LOGGER.debug("\n\n" + pipelineScript + "\n\n");
		return pipelineScript;
	}

	public void generateAndExecutePipelineScript() {
		String pipelineScript = generatePipelineScript();
		script.evaluate(pipelineScript);
	}

	private void checkConfiguration() {
		if (script == null) {
			throw new RuntimeException("The CpsScript cannot be null. Mock it if you are unit testing.");
		}
		if (engagement == null) {
			throw new RuntimeException("You must provide a configuration on the classpath, or with HTTP, using withConfiguration()");
		}
		if (applicationName == null || applicationName.isEmpty()) {
			throw new RuntimeException("You must provide a name for this application using withApplicationName()");
		}
		if (pipelineType == null || pipelineType.isEmpty() || (!pipelineType.equalsIgnoreCase("Release") && !pipelineType.equalsIgnoreCase("Development"))) {
			throw new RuntimeException("You must set the pipelineType to either Release or Development");
		}

	}

}
