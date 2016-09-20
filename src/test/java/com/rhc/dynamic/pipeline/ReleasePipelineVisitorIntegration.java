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

import org.junit.Test;

import com.rhc.dynamic.pipeline.utils.TestUtils;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**
 * We need an approach to run these tests in the build...
 */
public class ReleasePipelineVisitorIntegration {

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	@Test
	public void hello() throws IOException {
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder().url("http://automation-api-automation-api.apps.env2-1.etl.rht-labs.com:80/engagements").build();

		Response response = client.newCall(request).execute();
		System.out.println(response.body().string());

	}

	@Test
	public void postSomeData() throws IOException {
		OkHttpClient client = new OkHttpClient();

		RequestBody body = RequestBody.create(JSON, TestUtils.getEngagementStringFromFile(	TestUtils.MVN_BUILD_FILE) );
		System.err.println(TestUtils.getEngagementStringFromFile(	TestUtils.MVN_BUILD_FILE));
		Request request = new Request.Builder().url("http://automation-api-automation-api.apps.env2-1.etl.rht-labs.com:80/engagements").post(body).build();
		Response response = client.newCall(request).execute();
		System.out.println( response.body().string() );
	}

}
