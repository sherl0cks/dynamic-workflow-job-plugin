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

public class VisitPlanner {

	public static final String BUILD_ENV_ERR = "The first project in a cluster MUST be a buildEnvironment. This restriction will be resolved by TODO https://github.com/rht-labs/api-design/issues/23";

	public static void orchestrateVisit( Visitor visitor, Engagement engagement){
		visitor.visit( engagement );
		for ( OpenShiftCluster cluster : engagement.getOpenShiftClusters() ){
			visitor.visit( cluster );
			for ( int i=0; i < cluster.getOpenShiftResources().getProjects().size(); i++ ){
				Project project = cluster.getOpenShiftResources().getProjects().get( i );
				
				// HACK ALERT
				if ( i == 0 && project.getBuildEnvironment() == false ){
					throw new RuntimeException( BUILD_ENV_ERR );
				}
				visitor.visit( project );
			}
		}
	}
}
