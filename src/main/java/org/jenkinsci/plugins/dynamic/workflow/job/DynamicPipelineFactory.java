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
package org.jenkinsci.plugins.dynamic.workflow.job;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import org.jenkinsci.plugins.workflow.cps.CpsScript;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhc.automation.model.Engagement;
/**
 * TODO set binding here for Engagement
 * TODO set binding here for config not in engagement
 */
public class DynamicPipelineFactory implements Serializable{
	
	private static final long serialVersionUID = -7772221801921220616L;
	
	private final CpsScript script;
	
	public DynamicPipelineFactory(CpsScript script) {
		this.script = script;
	}

	public String buildJenkinsFile( Engagement engagement ){
		return "foo";
	}
	
	public String helloWorld(){
		return "echo 'this is a hello from the factory'";
	}
	
	public void sayHello(){
		script.evaluate( "echo 'hello, world!'");
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
