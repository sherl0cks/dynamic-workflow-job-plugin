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
package com.rhc.dynamic.pipeline.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class TestUtils {

	public static final String APPLICATION_NAME = "cool-application-name";
	public static final String NO_BUILD_TOOL_FILE = "singleClusterMultiProjectNoBuildTool.json";
	public static final String CUSTOM_BUILD_IMAGE_FILE = "singleClusterMultiProjectWithCustomBuildImageCommands.json";
	public static final String MVN_BUILD_FILE = "singleClusterMultiProjectWithMvn.json";
	public static final String UNSUPPORTED_BUILD_TOOL_FILE = "singleClusterMultiProjectWithUnsupportedBuildTool.json";
	public static final String PROMOTION_ENV_FIRST_FILE = "singleClusterWithPromotionEnvironmentFirst.json";

	
	public static String getPipelineScriptFromFileWithoutWhitespace(String fileName) throws IOException {
		return removeWhiteSpace(getPipelineScriptFromFile(fileName));
	}

	public static String getPipelineScriptFromFile(String fileName) throws IOException {
		return IOUtils.toString(TestUtils.class.getClassLoader().getResourceAsStream("com/rhc/dynamic/pipeline/scripts/" + fileName));
	}

	public static String removeWhiteSpace(String input) {
		return input.replaceAll("\\s+", "");
	}
	
	public static String getEngagementStringFromFile(String fileName) throws IOException{
		InputStream stream = TestUtils.class.getClassLoader().getResourceAsStream("com/rhc/dynamic/pipeline/engagements/" + fileName);
		if (stream == null ){
			throw new RuntimeException("could not find file: " + fileName);
		}
		return IOUtils.toString(stream);
	}
}
