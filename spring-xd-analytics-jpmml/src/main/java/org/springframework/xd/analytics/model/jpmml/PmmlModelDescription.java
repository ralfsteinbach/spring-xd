/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.xd.analytics.model.jpmml;

import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.springframework.xd.analytics.model.AbstractAnalyticalModelDescription;

/**
 * An {@link org.springframework.xd.analytics.model.AnalyticalModelDescription} that is backed by a {@link PMML} model definition.
 *
 * Author: Thomas Darimont
 */
public class PmmlModelDescription extends AbstractAnalyticalModelDescription {

	private final PMML pmml;

	public PmmlModelDescription(String name, String id, PMML pmml){
		setName(name);
		setId(id);
		this.pmml = pmml;
	}

	Model getDefaultModel() {
		return this.pmml.getModels().get(0);
	}

	public PMML getPmml() {
		return pmml;
	}
}
