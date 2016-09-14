package org.jenkinsci.plugins.dynamic.workflow.job;

public class PipelineConfiguration {

	private String[] buildCommands;
	private String[] buildImageCommands;
	private String unitTestCommand;
	private String buildTool;
	private String ocHost;
	private String appName;
	private String qualityScanCommand;
	private String dockerRegistry;
	private ApplicationEnvironment[] applicationEnvironments;

	public String[] getBuildCommands() {
		return buildCommands;
	}

	public void setBuildCommands(String[] buildCommands) {
		this.buildCommands = buildCommands;
	}

	public String[] getBuildImageCommands() {
		return buildImageCommands;
	}

	public void setBuildImageCommands(String[] buildImageCommands) {
		this.buildImageCommands = buildImageCommands;
	}

	public String getUnitTestCommand() {
		return unitTestCommand;
	}

	public void setUnitTestCommand(String unitTestCommand) {
		this.unitTestCommand = unitTestCommand;
	}

	public String getBuildTool() {
		return buildTool;
	}

	public void setBuildTool(String buildTool) {
		this.buildTool = buildTool;
	}

	public String getOcHost() {
		return ocHost;
	}

	public void setOcHost(String ocHost) {
		this.ocHost = ocHost;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getQualityScanCommand() {
		return qualityScanCommand;
	}

	public void setQualityScanCommand(String qualityScanCommand) {
		this.qualityScanCommand = qualityScanCommand;
	}

	public String getDockerRegistry() {
		return dockerRegistry;
	}

	public void setDockerRegistry(String dockerRegistry) {
		this.dockerRegistry = dockerRegistry;
	}

	public ApplicationEnvironment[] getApplicationEnvironments() {
		return applicationEnvironments;
	}

	public void setApplicationEnvironments(ApplicationEnvironment[] applicationEnvironments) {
		this.applicationEnvironments = applicationEnvironments;
	}

}
