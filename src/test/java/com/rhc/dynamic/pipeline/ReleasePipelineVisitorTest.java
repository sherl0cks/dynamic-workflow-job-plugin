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

import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rhc.dynamic.pipeline.utils.TestUtils;

/**
 * We're using mockito to stub out Jenkins interactions. These links should
 * explain the mechanism in play:
 * {@link http://site.mockito.org/mockito/docs/current/org/mockito/junit/MockitoRule.html}
 * {@link http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#15}
 */
public class ReleasePipelineVisitorTest {

	private static final Logger LOGGER = LoggerFactory.getLogger("ReleasePipelineVisitorTest");

	@Mock
	private CpsScript mockScript;

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Test
	public void shouldFailWhenNoConfigurationIsProvided() throws IOException {
		// given
		DynamicPipelineFactory factory = new DynamicPipelineFactory(mockScript).withReleaseType().withApplicationName(TestUtils.APPLICATION_NAME);

		// when
		try {
			factory.generatePipelineScript();
			Assert.fail("An exception should have been thrown");
		} catch (RuntimeException e) {
			// then
			Assert.assertEquals("You must provide a configuration on the classpath, or with HTTP, using withConfiguration()", e.getMessage());
		}
	}

	@Test
	public void shouldFailWhenNoApplicationNameIsProvided() throws IOException {
		// given
		DynamicPipelineFactory factory = new DynamicPipelineFactory(mockScript).withReleaseType().withConfigurationFile(TestUtils.NO_BUILD_TOOL_FILE);

		// when
		try {
			factory.generatePipelineScript();
			Assert.fail("An exception should have been thrown");
		} catch (RuntimeException e) {
			// then
			Assert.assertEquals("You must provide a name for this application using withApplicationName()", e.getMessage());
		}
	}

	@Test
	public void shouldCorrectlyCreateSingleClusterMultiProjectScriptNoBuildTool() throws IOException {
		// given
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
		DynamicPipelineFactory factory = new DynamicPipelineFactory(mockScript).withReleaseType().withConfigurationFile(TestUtils.NO_BUILD_TOOL_FILE)
				.withApplicationName(TestUtils.APPLICATION_NAME);

		// when
		factory.generateAndExecutePipelineScript();

		// then
		verify(mockScript).evaluate(argument.capture());
		Assert.assertEquals(TestUtils.getPipelineScriptFromFileWithoutWhitespace("singleClusterScriptNoBuildTool.groovy"), TestUtils.removeWhiteSpace(argument.getValue()));
	}

	@Test
	public void shouldCorrectlyCreateSingleClusterMultiProjectScriptWithCustomBuildImageAndCustomDeployCommands() throws IOException {
		// given
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
		DynamicPipelineFactory factory = new DynamicPipelineFactory(mockScript).withReleaseType().withConfigurationFile(TestUtils.CUSTOM_BUILD_IMAGE_FILE)
				.withApplicationName(TestUtils.APPLICATION_NAME);

		// when
		factory.generateAndExecutePipelineScript();

		// then
		verify(mockScript).evaluate(argument.capture());
		Assert.assertEquals(TestUtils.getPipelineScriptFromFileWithoutWhitespace("singleClusterScriptCustomCommands.groovy"), TestUtils.removeWhiteSpace(argument.getValue()));
	}

	@Test
	public void shouldCorrectlyCreateSingleClusterMultiProjectScriptWithMvn() throws IOException {
		// given
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
		DynamicPipelineFactory factory = new DynamicPipelineFactory(mockScript).withReleaseType().withConfigurationFile(TestUtils.MVN_BUILD_FILE)
				.withApplicationName(TestUtils.APPLICATION_NAME);

		// when
		factory.generateAndExecutePipelineScript();

		// then
		verify(mockScript).evaluate(argument.capture());
		Assert.assertEquals(TestUtils.getPipelineScriptFromFileWithoutWhitespace("singleClusterScriptMvn3.groovy"), TestUtils.removeWhiteSpace(argument.getValue()));
	}

	@Test
	public void shouldThrowExceptionForUnsupportedBuildTool() throws IOException {
		// given
		DynamicPipelineFactory factory = new DynamicPipelineFactory(mockScript).withReleaseType().withConfigurationFile(TestUtils.UNSUPPORTED_BUILD_TOOL_FILE)
				.withApplicationName(TestUtils.APPLICATION_NAME);

		// when
		try {
			factory.generatePipelineScript();
			Assert.fail("did not throw error");
		} catch (RuntimeException e) {
			// then
			if (e.getMessage() != null && e.getMessage().contains("gradle-3 is currently unsupported")) {
				// do nothing, this is desired behavior
			} else {
				Assert.fail("this is the wrong exception " + e.getMessage());
			}
		}
	}

	@Test
	public void shouldThrowExceptionBecauseFirstProjectIsNotABuildEnv() throws IOException {
		// given
		DynamicPipelineFactory factory = new DynamicPipelineFactory(mockScript).withReleaseType().withConfigurationFile(TestUtils.PROMOTION_ENV_FIRST_FILE)
				.withApplicationName(TestUtils.APPLICATION_NAME);

		// when
		try {
			factory.generatePipelineScript();
			Assert.fail("did not throw error");
		} catch (RuntimeException e) {
			// then
			if (e.getMessage() != null && e.getMessage().equals(VisitPlanner.BUILD_ENV_ERR)) {
				// do nothing, this is desired behavior
			} else {
				Assert.fail("this is the wrong exception " + e.getMessage());
			}
		}
	}


}
