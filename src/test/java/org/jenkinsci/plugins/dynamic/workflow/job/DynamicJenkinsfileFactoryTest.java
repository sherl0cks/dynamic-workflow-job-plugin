package org.jenkinsci.plugins.dynamic.workflow.job;

import org.junit.Assert;
import org.junit.Test;

public class DynamicJenkinsfileFactoryTest {

	DynamicJenkinsfileFactory factory = new DynamicJenkinsfileFactory();
	
	@Test
	public void helloWorld(){
		PipelineConfiguration config = factory.getPipelineConfigurationFromURL("file://a");
		Assert.assertEquals( "booger", config.getBuildTool() );
	}
}
