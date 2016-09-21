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
import com.rhc.automation.model.OpenShiftCluster;
import com.rhc.automation.model.Project;

/**
 * All implementations of this class maintain state. Create a new instance of all implementations for each use.
 * 
 * http://butunclebob.com/ArticleS.UncleBob.IuseVisitor
 */
public interface Visitor {

	public void visit( Engagement engagement );
	public void visit( OpenShiftCluster cluster );
	public void visit( Project project );
	
	public String getPipelineScript();
	
}
