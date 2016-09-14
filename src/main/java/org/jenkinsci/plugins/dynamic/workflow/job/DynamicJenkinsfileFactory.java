package org.jenkinsci.plugins.dynamic.workflow.job;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhc.automation.model.Engagement;

public class DynamicJenkinsfileFactory {

	public String buildJenkinsFile( Engagement engagement ){
		return "foo";
	}
	
	public String helloWorld(){
		return "hello from new plugin";
	}
	
	public PipelineConfiguration getPipelineConfigurationFromURL( String string ){
		ObjectMapper mapper = new ObjectMapper();
		PipelineConfiguration obj = null;
		try {
			URL url = new URL(string);
			if ( url.getProtocol().equals("file")){
				obj = mapper.readValue( "{\"buildTool\": \"booger\"}", PipelineConfiguration.class);
			}
			//url.
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
}
