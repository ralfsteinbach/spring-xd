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
import org.springframework.xd.analytics.model.AnalyticalModel;
import org.springframework.xd.tuple.Tuple;

/**
 * Author: Thomas Darimont
 */
public class PmmlModel implements AnalyticalModel<PmmlModelDefinition> {

	private final PmmlModelDefinition modelDefinition;
	private final PmmlModelInputMapper<Tuple> inputMapper;
	private final PmmlModelOutputMapper<Tuple, Tuple> outputMapper;

	private volatile Evaluator pmmlEvaluator;

	public PmmlModel(PmmlModelDefinition modelDefinition, PmmlModelInputMapper<Tuple> inputMapper, PmmlModelOutputMapper<Tuple, Tuple> outputMapper) {
		this(modelDefinition, inputMapper, outputMapper, null);
	}

	public PmmlModel(PmmlModelDefinition modelDefinition, PmmlModelInputMapper<Tuple> inputMapper, PmmlModelOutputMapper<Tuple, Tuple> outputMapper, Evaluator pmmlEvaluator) {

		this.modelDefinition = modelDefinition;
		this.inputMapper = inputMapper == null ? new PmmlModelTupleInputMapper(null) : inputMapper;
		this.outputMapper = outputMapper == null ? new PmmlModelTupleOutputMapper(null) : outputMapper;
		this.pmmlEvaluator = pmmlEvaluator;
	}

	public Tuple evaluate(Tuple input) {

		Map<FieldName, Object> inputData = inputMapper.mapInput(modelDefinition, input);
		Map<FieldName, Object> outputData = (Map<FieldName, Object>) getPmmlEvaluator().evaluate(inputData);

		Tuple output = outputMapper.mapOutput(modelDefinition, outputData, input);

		return output;
	}

	protected Evaluator getPmmlEvaluator() {

		if(this.pmmlEvaluator == null){
			this.pmmlEvaluator = (Evaluator) new PMMLManager(modelDefinition.getPmml()).getModelManager(null, ModelEvaluatorFactory.getInstance());
		}

		return pmmlEvaluator;
	}

	public PmmlModelDefinition getModelDefinition() {
		return modelDefinition;
	}
}
