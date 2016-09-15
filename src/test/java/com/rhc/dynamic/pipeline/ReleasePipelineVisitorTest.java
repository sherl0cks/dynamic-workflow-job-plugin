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

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.rhc.automation.model.Engagement;

public class ReleasePipelineVisitorTest {

	private Visitor visitor = new ReleasePipelineVisitor();

	@Test
	public void shouldCorrectlyCreateInitializedAndCompletedScript() throws IOException {
		// given
		Engagement engagement = buildEngagement();

		// when
		visitor.visit(engagement);

		// then
		String script = visitor.getPipelineScript();
		System.out.println( script );
		Assert.assertEquals(getPipelineScriptFromFileWithoutWhitespace("initializedAndCompletedScript.groovy"), removeWhiteSpace(script));
	}

	private Engagement buildEngagement() {
		return new Engagement();
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
