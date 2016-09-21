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

import javax.annotation.Nonnull;

import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.jenkinsci.plugins.workflow.cps.GlobalVariable;

import com.rhc.OpenShiftClient;

import groovy.lang.Binding;
import hudson.Extension;

@Extension
public class OpenShiftClientJenkinsDSL extends GlobalVariable{

    private static final String OPENSHIFT_CLIENT = "oc";

    @Nonnull
    @Override
    public String getName() {
        return OPENSHIFT_CLIENT;
    }

    @Nonnull
    @Override
    public Object getValue(CpsScript script) throws Exception {
        Binding binding = script.getBinding();
        Object oc;
        if (binding.hasVariable(getName())) {
        	oc = binding.getVariable(getName());
        } else {
            // Note that if this were a method rather than a constructor, we would need to mark it @NonCPS lest it throw CpsCallableInvocation.
        	oc = new OpenShiftClient();
            binding.setVariable(getName(), oc);
        }
        return oc;
    }
}
