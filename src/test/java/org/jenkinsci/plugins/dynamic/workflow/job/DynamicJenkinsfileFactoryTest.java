package org.jenkinsci.plugins.dynamic.workflow.job;

import org.junit.Assert;
import org.junit.Test;

public class DynamicJenkinsfileFactoryTest {

	DynamicPipelineFactory factory = new DynamicPipelineFactory(null);
	
	@Test
	public void helloWorld(){
		PipelineConfiguration config = factory.getPipelineConfigurationFromURL("file://a");
		Assert.assertEquals( "booger", config.getBuildTool() );
		
		
	}
}
