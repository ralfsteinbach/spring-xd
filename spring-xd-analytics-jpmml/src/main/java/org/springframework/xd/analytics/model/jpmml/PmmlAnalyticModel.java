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

import java.util.Map;

import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.manager.PMMLManager;
import org.springframework.util.Assert;
import org.springframework.xd.analytics.model.AnalyticModel;

/**
 * @author Thomas Darimont
 */
public class PmmlAnalyticModel<I, O> implements AnalyticModel<I, O, PmmlModelDefinition> {

	private final PmmlModelDefinition definition;

	private final PmmlModelInputMapper<I> inputMapper;

	private final PmmlModelOutputMapper<O, I> outputMapper;

	private final PMMLManager pmmlManager;

	private volatile Evaluator pmmlEvaluator;

	/**
	 * Creates a new {@link PmmlAnalyticModel}.
	 *
	 * @param definition
	 * @param inputMapper
	 * @param outputMapper
	 */
	public PmmlAnalyticModel(PmmlModelDefinition definition, PmmlModelInputMapper<I> inputMapper, PmmlModelOutputMapper<O, I> outputMapper) {
		this(definition, inputMapper, outputMapper, null);
	}

	/**
	 * Creates a new {@link PmmlAnalyticModel}.
	 *
	 * @param definition
	 * @param inputMapper
	 * @param outputMapper
	 * @param pmmlEvaluator
	 */
	public PmmlAnalyticModel(PmmlModelDefinition definition, PmmlModelInputMapper<I> inputMapper, PmmlModelOutputMapper<O, I> outputMapper, Evaluator pmmlEvaluator) {

		Assert.notNull(definition, "definition");
		Assert.notNull(inputMapper, "definition");
		Assert.notNull(outputMapper, "definition");

		this.definition = definition;
		this.inputMapper = inputMapper;
		this.outputMapper = outputMapper;

		this.pmmlManager = new PMMLManager(definition.getPmml());
		this.pmmlEvaluator = pmmlEvaluator != null ? pmmlEvaluator : (Evaluator) this.pmmlManager.getModelManager(null, ModelEvaluatorFactory.getInstance());
	}

	/**
	 * @param input must not be {@literal null}
	 * @return
	 */
	public O evaluate(I input) {

		Map<FieldName, Object> inputData = inputMapper.mapInput(definition, input);
		Map<FieldName, Object> outputData = (Map<FieldName, Object>) this.pmmlEvaluator.evaluate(inputData);

		O output = outputMapper.mapOutput(definition, outputData, input);

		return output;
	}

	public PmmlModelDefinition getDefinition() {
		return definition;
	}
}
