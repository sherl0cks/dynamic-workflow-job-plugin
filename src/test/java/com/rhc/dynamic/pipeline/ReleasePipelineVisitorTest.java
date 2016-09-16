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
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rhc.automation.model.Application;
import com.rhc.automation.model.Engagement;
import com.rhc.automation.model.ImageRegistry;
import com.rhc.automation.model.OpenshiftCluster;
import com.rhc.automation.model.OpenshiftResources;
import com.rhc.automation.model.Project;

public class ReleasePipelineVisitorTest {

	private static final Logger LOGGER = LoggerFactory.getLogger("ReleasePipelineVisitorTest");
	private static final String APPLICATION_NAME = "cool-application-name";

	@Test
	public void shouldCorrectlyCreateInitializedAndCompletedScript() throws IOException {
		// given
		Engagement engagement = buildEmptyEngagement();
		Visitor visitor = new ReleasePipelineVisitor(APPLICATION_NAME);

		// when
		VisitPlanner.orchestrateVisit(visitor, engagement);

		// then
		String script = visitor.getPipelineScript();
		LOGGER.debug("shouldCorrectlyCreateInitializedAndCompletedScript() \n\n" + script);
		Assert.assertEquals(getPipelineScriptFromFileWithoutWhitespace("initializedAndCompletedScript.groovy"), removeWhiteSpace(script));
	}

	@Test
	public void shouldCorrectlyCreateSingleClusterMultiProjectScriptNoBuildTool() throws IOException {
		// given
		Engagement engagement = buildSingleClusterMultiProjectEngagementNoBuildTool();
		Visitor visitor = new ReleasePipelineVisitor(APPLICATION_NAME);

		// when
		VisitPlanner.orchestrateVisit(visitor, engagement);

		// then
		String script = visitor.getPipelineScript();
		LOGGER.debug("shouldCorrectlyCreateSingleClusterMultiProjectScriptNoBuildTool() \n\n" + script);
		Assert.assertEquals(getPipelineScriptFromFileWithoutWhitespace("singleClusterScriptNoBuildTool.groovy"), removeWhiteSpace(script));
	}
	
	@Test
	public void shouldCorrectlyCreateSingleClusterMultiProjectScriptWithMvn() throws IOException {
		// given
		Engagement engagement = buildSingleClusterMultiProjectEngagementWithMvn();
		Visitor visitor = new ReleasePipelineVisitor(APPLICATION_NAME);

		// when
		VisitPlanner.orchestrateVisit(visitor, engagement);

		// then
		String script = visitor.getPipelineScript();
		LOGGER.debug("shouldCorrectlyCreateSingleClusterMultiProjectScriptWithMvn() \n\n" + script);
		Assert.assertEquals(getPipelineScriptFromFileWithoutWhitespace("singleClusterScriptMvn3.groovy"), removeWhiteSpace(script));
	}

	@Test
	public void shouldThrowExceptionForUnsupportedBuildTool() throws IOException {
		// given
		Engagement engagement = buildSingleClusterMultiProjectEngagementWithUnsupportedBuildTool();
		Visitor visitor = new ReleasePipelineVisitor(APPLICATION_NAME);

		// when
		try {
			VisitPlanner.orchestrateVisit(visitor, engagement);
			Assert.fail("did not throw error");
		} catch (RuntimeException e) {
			// then
			if (e.getMessage() != null && e.getMessage().contains("gradle-3 is currently unsupported") ) {
				// do nothing, this is desired behavior
			} else {
				Assert.fail("this is the wrong exception " + e.getMessage());
			}
		}
	}

	@Test
	public void shouldThrowExceptionBecauseFirstProjectIsNotABuildEnv() throws IOException {
		// given
		Engagement engagement = buildSingleClusterEngagementWithPromotionEnvironmentFirst();
		Visitor visitor = new ReleasePipelineVisitor(APPLICATION_NAME);

		// when
		try {
			VisitPlanner.orchestrateVisit(visitor, engagement);
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

	private Engagement buildEmptyEngagement() {
		return new Engagement();
	}

	private Engagement buildSingleClusterEngagement() {
		Engagement engagement = buildEmptyEngagement();
		ImageRegistry registry = new ImageRegistry().host("registry.apps.redhat.com");
		OpenshiftCluster cluster = new OpenshiftCluster().id(1l).openshiftHostEnv("master.openshift.redhat.com").imageRegistry(registry);

		engagement.openshiftClusters(new ArrayList<OpenshiftCluster>()).addOpenshiftClustersItem(cluster);

		return engagement;
	}

	/**
	 * Hack Alert: scpTpe for buildTool, scmRef for buildCommands
	 * 
	 * @return
	 */
	private Engagement buildSingleClusterMultiProjectEngagementNoBuildTool() {
		Engagement engagement = buildSingleClusterMultiProjectEngagement();
		Application app = engagement.getOpenshiftClusters().get(0).getOpenshiftResources().getProjects().get(0).getApps().get(0);
		app.scmType("").scmRef("customCommand,customCommand with arguments");
		return engagement;
	}
	
	private Engagement buildSingleClusterMultiProjectEngagementWithMvn() {
		Engagement engagement = buildSingleClusterMultiProjectEngagement();
		Application app = engagement.getOpenshiftClusters().get(0).getOpenshiftResources().getProjects().get(0).getApps().get(0);
		app.scmType("mvn-3").scmRef("mvn clean deploy").contextDir("");
		return engagement;
	}

	private Engagement buildSingleClusterMultiProjectEngagementWithUnsupportedBuildTool() {
		Engagement engagement = buildSingleClusterMultiProjectEngagement();
		Application app = engagement.getOpenshiftClusters().get(0).getOpenshiftResources().getProjects().get(0).getApps().get(0);
		app.scmType("gradle-3");
		return engagement;
	}

	private Engagement buildSingleClusterMultiProjectEngagement() {
		Engagement engagement = buildSingleClusterEngagement();
		Application devApp = new Application().name(APPLICATION_NAME).contextDir("build-home-dir");
		Project dev = new Project().buildEnvironment(true).name("dev-project").addAppsItem(devApp);
		Project stage = new Project().buildEnvironment(false).name("stage-project");
		Project prod = new Project().buildEnvironment(false).name("prod-project");
		OpenshiftResources resources = new OpenshiftResources().addProjectsItem(dev).addProjectsItem(stage).addProjectsItem(prod);
		engagement.getOpenshiftClusters().get(0).openshiftResources(resources);

		return engagement;
	}

	private Engagement buildSingleClusterEngagementWithPromotionEnvironmentFirst() {
		Engagement engagement = buildSingleClusterEngagement();
		Project project = new Project().buildEnvironment(false);
		OpenshiftResources resources = new OpenshiftResources().addProjectsItem(project);
		engagement.getOpenshiftClusters().get(0).openshiftResources(resources);

		return engagement;
	}

	private String getPipelineScriptFromFileWithoutWhitespace(String fileName) throws IOException {
		return removeWhiteSpace(getPipelineScriptFromFile(fileName));
	}

	private String getPipelineScriptFromFile(String fileName) throws IOException {
		return IOUtils.toString(getClass().getClassLoader().getResourceAsStream("com/rhc/dynamic/pipeline/" + fileName));
	}

	private String removeWhiteSpace(String input) {
		return input.replaceAll("\\s+", "");
	}
}
