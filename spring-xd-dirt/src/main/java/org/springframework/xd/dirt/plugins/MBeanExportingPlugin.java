/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.springframework.xd.dirt.plugins;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.xd.dirt.util.ConfigLocations;
import org.springframework.xd.module.core.Module;

/**
 * Exports MBeans from a module using a unique domain name xd.[group].[module]
 * 
 * @author David Turanski
 * @author Gary Russell
 */
public class MBeanExportingPlugin extends AbstractPlugin {

	private static final String CONTEXT_CONFIG_ROOT = ConfigLocations.XD_CONFIG_ROOT + "plugins/jmx/";

	@Value("${XD_JMX_ENABLED}")
	private boolean jmxEnabled;

	@Override
	public void preProcessModule(Module module) {
		module.addComponents(new ClassPathResource(CONTEXT_CONFIG_ROOT + "mbean-exporters.xml"));
		Properties objectNameProperties = new Properties();
		objectNameProperties.put("xd.module.name", module.getName());
		objectNameProperties.put("xd.module.index", module.getDeploymentMetadata().getIndex());

		module.addProperties(objectNameProperties);
	}

	@Override
	public boolean supports(Module module) {
		return jmxEnabled;
	}
}
