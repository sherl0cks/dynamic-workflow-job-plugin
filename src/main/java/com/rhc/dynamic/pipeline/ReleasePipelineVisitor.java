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

import com.rhc.automation.model.Engagement;

/**
 * This class maintain state. Create a new instance of all implementations for each use.
 */
public class ReleasePipelineVisitor implements Visitor {

	private StringBuilder pipelineScript;

	public ReleasePipelineVisitor() {
		initializeScript();
	}

	@Override
	public void visit(Engagement engagement) {
	}

	@Override
	public String getPipelineScript() {
		completeScript();
		return pipelineScript.toString();
	}

	private void initializeScript() {
		pipelineScript = new StringBuilder();
		pipelineScript.append("OpenShiftClient oc = new com.rhc.automation.clients.OpenShiftClient()\n"); // TODO replace with OpenShift plugin
		pipelineScript.append("DockerClient docker = new com.rhc.automation.clients.DockerClient()\n"); // TODO replace with Fabric8
		pipelineScript.append("\n");
		pipelineScript.append("node {\n");
		pipelineScript.append("  stage 'Code Checkout'\n");
		pipelineScript.append("  checkout scm\n");
		pipelineScript.append("  stage 'Build App'\n");
	}

	private void completeScript() {
		pipelineScript.append("}");
	}
}
