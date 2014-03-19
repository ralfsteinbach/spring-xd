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
import org.springframework.util.Assert;
import org.springframework.xd.analytics.model.DefaultAnalyticModelDefinition;

/**
 * An {@link org.springframework.xd.analytics.model.AnalyticModelDefinition} that is backed by a {@link PMML} model definition.
 *
 * @author Thomas Darimont
 */
public class PmmlModelDefinition extends DefaultAnalyticModelDefinition {

	private final PMML pmml;

	/**
	 * Creates a new {@link PmmlModelDefinition}.
	 *
	 * @param name
	 * @param id
	 * @param pmml
	 */
	public PmmlModelDefinition(String name, String id, PMML pmml) {
		super(name, id);

		Assert.notNull(pmml, "pmml");

		this.pmml = pmml;
	}

	/**
	 * Returns the default {@link Model} of the wrapped {@link PMML} object.
	 * According to the PMML specification, this is the first {@code Model} in the {@code PMML} structure.
	 *
	 * @return
	 */
	Model getDefaultModel() {
		return this.pmml.getModels().get(0);
	}

	/**
	 * @return
	 */
	public PMML getPmml() {
		return pmml;
	}
}
