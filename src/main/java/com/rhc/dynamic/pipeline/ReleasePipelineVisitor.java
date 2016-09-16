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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rhc.automation.model.Application;
import com.rhc.automation.model.Engagement;
import com.rhc.automation.model.OpenshiftCluster;
import com.rhc.automation.model.Project;

/**
 * This class maintain state. Create a new instance of all implementations for
 * each use.
 */
public class ReleasePipelineVisitor implements Visitor {

	private static final Logger LOGGER = LoggerFactory.getLogger("ReleasePipelineVisitor");
	private static final Set<String> SUPPORTED_BUILD_TOOLS = new HashSet<String>(Arrays.asList("node-0.10", "node-4", "mvn-3"));
	private final String applicationName;
	private StringBuilder script;

	public ReleasePipelineVisitor(String applicationName) {
		initializeScript();
		this.applicationName = applicationName;
	}

	@Override
	public void visit(Engagement engagement) {
		LOGGER.debug("visiting engagement: " + engagement.getName());
	}

	@Override
	public void visit(OpenshiftCluster cluster) {
		LOGGER.debug("visiting openShiftCluster: " + cluster.getId());

		// Login to OpenShift and it's docker image registry
		script.append("  oc.login( '").append(cluster.getOpenshiftHostEnv()).append("' )\n");
		script.append("  docker.login( '").append(cluster.getImageRegistry().getHost()).append("', oc.getTrimmedUserToken() )\n\n");
	}

	@Override
	public void visit(Project project) {
		LOGGER.debug("visiting project: " + project.getName());

		if (project.getBuildEnvironment() == true) {
			createBuildAppScript(project);
			createBuildAndDeployImageScript(project);
		}
	}

	/**
	 * HACK ALERT: using baseImageTage for buildImageCommands with , delimiter
	 * 
	 * @param project
	 */
	private void createBuildAndDeployImageScript(Project project) {

		script.append("\n  stage 'Build Image and Deploy to Dev'\n");
		Application app = getSelectedApplication(project);
		if (app.getBaseImageTag() == null || app.getBaseImageTag().isEmpty()) {
			script.append("  echo 'No buildImageCommands, using default OpenShift image build and deploy'\n");
			createDefaultOpenShiftBuildAndDeployScript(project);
		} else {
			script.append("  echo 'Found buildImageCommands, executing in shell'\n");
			String[] commands = app.getBaseImageTag().split(",");
			for (int i=0; i<commands.length; i++){
				script.append("  sh '").append( commands[i] ).append("' \n");
			}
		}
	}

	private void createBuildAppScript(Project project) {

		Application app = getSelectedApplication(project);
		if (app.getContextDir() != null && !app.getContextDir().isEmpty()) {
			script.append("  dir( '").append(app.getContextDir()).append("' ) {\n  ");
			createBuildCommands(app);
			script.append("  }\n");
		} else {
			createBuildCommands(app);
		}

	}

	private Application getSelectedApplication(Project project) {
		for (Application app : project.getApps()) {
			if (app.getName().equals(applicationName)) {
				return app;
			}
		}
		// we don't find it, throw an error
		throw new RuntimeException("No apps in project " + project.getName() + " have the name " + applicationName);
	}
	

	private void createDefaultOpenShiftBuildAndDeployScript(Project project) {
		script.append( "  oc.startBuildAndWaitUntilComplete( '").append( applicationName ).append("', '").append( project.getName() ).append("' )\n");
	}

	/**
	 * Hack Alert!!! I'm using scmType in lieu of build tool for the time being
	 * TODO: fix this
	 */
	private void createBuildCommands(Application app) {

		if (app.getScmType() == null || app.getScmType().isEmpty()) {
			script.append("  echo 'No build tool declared. Any commands will execute directly in the shell.'\n");
			createListOfShellCommandsScript(app, null);
		} else if (SUPPORTED_BUILD_TOOLS.contains(app.getScmType())) {
			script.append("  echo 'Using build tool: ").append(app.getScmType()).append("'\n");
			createListOfShellCommandsScript(app, app.getScmType());
		} else {
			throw new RuntimeException(app.getScmType() + " is currently unsupported. Please select one of " + SUPPORTED_BUILD_TOOLS);
		}
	}

	/**
	 * Hack alert: scmRef for buildCommands
	 * 
	 * @param app
	 * @param tool
	 */
	private void createListOfShellCommandsScript(Application app, String tool) {
		if (tool != null) {
			script.append("  def toolHome = tool '").append(tool).append("'\n");
		}
		if (app.getScmRef() == null || app.getScmRef().isEmpty()) {
			throw new RuntimeException(
					"app.buildCommands cannot be empty. we are currently using scmRef, with comma delimited string for buildCommands. it's a hack and will be fixed soon.");
		} else {
			String[] commands = app.getScmRef().split(",");
			for (int i = 0; i < commands.length; i++) {
				script.append("  sh \"");
				if (tool != null) {
					script.append("${toolHome}/bin/");
				}
				script.append(commands[i]).append("\"\n");
			}

		}
	}

	@Override
	public String getPipelineScript() {
		completeScript();
		return script.toString();
	}

	private void initializeScript() {
		script = new StringBuilder();
		// TODO replace with OpenShift plugin
		script.append("OpenShiftClient oc = new com.rhc.automation.clients.OpenShiftClient()\n");
		// TODO replace with Fabric8 Docker plugin
		script.append("DockerClient docker = new com.rhc.automation.clients.DockerClient()\n");
		script.append("\n");
		script.append("node {\n");
		script.append("  stage 'Code Checkout'\n");
		script.append("  checkout scm\n\n");
		script.append("  stage 'Build App'\n");
	}

	private void completeScript() {
		script.append("}");
	}

}
