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

import java.util.ArrayList;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhc.automation.model.Application;
import com.rhc.automation.model.Engagement;
import com.rhc.automation.model.OpenShiftCluster;
import com.rhc.automation.model.OpenShiftResources;
import com.rhc.automation.model.Project;

/**
 * This is a simple utility for test data. The methods here will provide a
 * configured Engagement, which you can use in the test to generate JSON for use
 * elsewhere in the project. All of the files in the requests folder came from here
 */
public class ObjectMother {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("ObjectMother");

	/**
	 * Helper method to generate json to be used in test 
	 * @throws JsonProcessingException
	 */
	@Test
	public void shouldGenerateSomeStuff() throws JsonProcessingException {
		String applicationName = "cool-application-name";
		Engagement engagement = buildSingleClusterMultiProjectEngagementNoBuildTool(applicationName );
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		String output = mapper.writeValueAsString(engagement);
		LOGGER.info("\n\n" + output + "\n\n");
	}
	
	public static Engagement buildEmptyEngagement() {
		return new Engagement();
	}

	public static Engagement buildSingleClusterEngagement() {
		Engagement engagement = buildEmptyEngagement();
		String registry = "registry.apps.redhat.com";
		OpenShiftCluster cluster = new OpenShiftCluster().id(1l).openShiftHostEnv("master.openshift.redhat.com").imageRegistry(registry);

		engagement.openShiftClusters(new ArrayList<OpenShiftCluster>()).addOpenShiftClustersItem(cluster);

		return engagement;
	}

	/**
	 * Hack Alert: scpTpe for buildTool, scmRef for buildAppCommands
	 * 
	 * @return
	 */
	public static Engagement buildSingleClusterMultiProjectEngagementNoBuildTool(String applicationName) {
		Engagement engagement = buildSingleClusterMultiProjectEngagement(applicationName);
		Application app = engagement.getOpenShiftClusters().get(0).getOpenShiftResources().getProjects().get(0).getApps().get(0);
		app.scmType("").scmRef("customBuildAppCommand,customBuildAppCommand with arguments");
		return engagement;
	}

	/**
	 * Hack alert: using baseImageRef for buildImageCommand
	 * 
	 * @return
	 */
	public static Engagement buildSingleClusterMultiProjectEngagementWithCustomBuildImageCommands(String applicationName) {
		Engagement engagement = buildSingleClusterMultiProjectEngagement(applicationName);
		Application app = engagement.getOpenShiftClusters().get(0).getOpenShiftResources().getProjects().get(0).getApps().get(0);
		app.scmType("").scmRef("customBuildAppCommand,customBuildAppCommand with arguments").baseImageTag(
				"customBuildImageCommand,customBuildImageCommand with arguments:customDeployImageCommand,customDeployImageCommand with arguments");
		return engagement;
	}

	public static Engagement buildSingleClusterMultiProjectEngagementWithMvn(String applicationName) {
		Engagement engagement = buildSingleClusterMultiProjectEngagement(applicationName);
		Application app = engagement.getOpenShiftClusters().get(0).getOpenShiftResources().getProjects().get(0).getApps().get(0);
		app.scmType("mvn-3").scmRef("mvn clean deploy").contextDir("");
		return engagement;
	}

	public static Engagement buildSingleClusterMultiProjectEngagementWithUnsupportedBuildTool(String applicationName) {
		Engagement engagement = buildSingleClusterMultiProjectEngagement(applicationName);
		Application app = engagement.getOpenShiftClusters().get(0).getOpenShiftResources().getProjects().get(0).getApps().get(0);
		app.scmType("gradle-3");
		return engagement;
	}

	public static Engagement buildSingleClusterMultiProjectEngagement(String applicationName) {
		Engagement engagement = buildSingleClusterEngagement();
		Application devApp = new Application().name(applicationName).contextDir("build-home-dir");
		Application stageApp = new Application().name(applicationName);
		Application prodApp = new Application().name(applicationName);
		Project dev = new Project().buildEnvironment(true).name("dev-project").addAppsItem(devApp);
		Project stage = new Project().promotionEnvironment(true).name("stage-project").addAppsItem(stageApp);
		Project prod = new Project().promotionEnvironment(true).name("prod-project").addAppsItem(prodApp);
		OpenShiftResources resources = new OpenShiftResources().addProjectsItem(dev).addProjectsItem(stage).addProjectsItem(prod);
		engagement.getOpenShiftClusters().get(0).openShiftResources(resources);

		return engagement;
	}

	public static Engagement buildSingleClusterEngagementWithPromotionEnvironmentFirst(String applicationName) {
		Engagement engagement = buildSingleClusterEngagement();
		Project project = new Project().buildEnvironment(false);
		OpenShiftResources resources = new OpenShiftResources().addProjectsItem(project);
		engagement.getOpenShiftClusters().get(0).openShiftResources(resources);

		return engagement;
	}
}
