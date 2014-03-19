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

import java.util.UUID;

import org.dmg.pmml.PMML;
import org.springframework.util.Assert;
import org.springframework.xd.analytics.model.AnalyticModelBuilder;

/**
 * @author Thomas Darimont
 */
public class PmmlAnalyticModelBuilder implements AnalyticModelBuilder<PmmlAnalyticModel<?, ?>, PmmlModelDefinition> {

	private final PmmlModelInputMapper<?> inputMapper;

	private final PmmlModelOutputMapper<?, ?> outputMapper;

	private final PmmlProvider pmmlProvider;

	/**
	 * Creates a new {@link PmmlAnalyticModelBuilder}.
	 *
	 * @param pmmlProvider must not be {@literal null}.
	 * @param inputMapper must not be {@literal null}.
	 * @param outputMapper must not be {@literal null}.
	 */
	public PmmlAnalyticModelBuilder(PmmlProvider pmmlProvider, PmmlModelInputMapper<?> inputMapper, PmmlModelOutputMapper<?, ?> outputMapper) {

		Assert.notNull(pmmlProvider, "pmmlProvider");
		Assert.notNull(inputMapper, "inputMapper");
		Assert.notNull(outputMapper, "outputMapper");

		this.pmmlProvider = pmmlProvider;
		this.inputMapper = inputMapper;
		this.outputMapper = outputMapper;
	}

	/**
	 * @param name must not be {@literal null}
	 * @return
	 */
	@Override
	public PmmlAnalyticModel<?, ?> buildModel(String name) {

		Assert.notNull(name, "name");

		PmmlModelDefinition modelDefinition = buildModelDefinition(name);

		return new PmmlAnalyticModel(modelDefinition, this.inputMapper, this.outputMapper);
	}

	/**
	 * @param name must not be {@literal null}
	 * @return
	 */
	private PmmlModelDefinition buildModelDefinition(String name) {

		Assert.notNull(name, "name");

		String modelId = generateModelId(name);

		PMML pmml = pmmlProvider.getPmml(name, modelId);

		return new PmmlModelDefinition(name, modelId, pmml);
	}

	/**
	 * @param name
	 * @return
	 */
	protected String generateModelId(String name) {
		return UUID.randomUUID().toString();
	}
}
