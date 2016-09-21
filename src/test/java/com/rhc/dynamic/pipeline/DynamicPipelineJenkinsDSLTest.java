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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.annotation.Nonnull;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.jenkinsci.plugins.workflow.cps.GlobalVariable;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;

/**
 * These tests show the basic DSL glue to Jenkins. The dynamic pipeline is
 * tested at {@link ReleasePipelineVisitorWithConfigFileTest} and
 * {@link DevelopmentPipelineVisitorTest}
 */

public class DynamicPipelineJenkinsDSLTest {

	@ClassRule
	public static BuildWatcher buildWatcher = new BuildWatcher();
	@Rule
	public JenkinsRule r = new JenkinsRule();


	@Test
	public void shouldPrintAutomationApiVersionFromDynamicPipelineFactory() throws Exception {
		WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");
		p.setDefinition(new CpsFlowDefinition("println(dynamicPipeline.AUTOMATION_API_VERSION)"));
		WorkflowRun b1 = r.assertBuildStatusSuccess(p.scheduleBuild2(0));
		
		assertFalse(b1.isBuilding());
		assertFalse(b1.isLogUpdated());
		assertTrue(b1.getDuration() > 0);
		WorkflowRun b2 = r.assertBuildStatusSuccess(p.scheduleBuild2(0));
		assertEquals(b1, b2.getPreviousBuild());
		assertEquals(null, b1.getPreviousBuild());
		r.assertLogContains("0.2.0\n", b1);
	}
	
	@TestExtension
	public static class DynamicPipelineJenkinsTestDSL extends GlobalVariable {

	    private final DynamicPipelineJenkinsDSL dsl = new DynamicPipelineJenkinsDSL();

	    @Nonnull
	    @Override
	    public String getName() {
	        return dsl.getName();
	    }

	    @Nonnull
	    @Override
	    public Object getValue(CpsScript script) throws Exception {
	    	return dsl.getValue(script);
	    }
	}
	
}
