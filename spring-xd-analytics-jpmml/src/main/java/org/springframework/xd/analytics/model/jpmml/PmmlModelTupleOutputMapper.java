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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.FieldName;
import org.springframework.xd.tuple.Tuple;
import org.springframework.xd.tuple.TupleBuilder;

/**
 * @author Thomas Darimont
 */
public class PmmlModelTupleOutputMapper implements PmmlModelOutputMapper<Tuple, Tuple> {

	private final List<String> outputFieldsNames;

	private final List<FieldName> outputFields;

	/**
	 * Creates a new {@link PmmlModelTupleOutputMapper}.
	 *
	 * @param outputFieldsNames
	 */
	public PmmlModelTupleOutputMapper(List<String> outputFieldsNames) {

		if (outputFieldsNames == null) {
			this.outputFieldsNames = null;
			this.outputFields = null;
			return;
		}

		this.outputFieldsNames = outputFieldsNames;
		this.outputFields = new ArrayList<FieldName>(outputFieldsNames.size());
		for (String fieldName : outputFieldsNames) {
			this.outputFields.add(new FieldName(fieldName));
		}
	}

	/**
	 * @param definition the {@link AnalyticModelDefinition} that can be used to retrieve mapping information.
	 * @param result
	 * @param input the input for this {@link AnalyticModel} that could be used to build the model {@code O}.
	 * @return
	 */
	@Override
	public Tuple mapOutput(PmmlModelDefinition definition, Map<FieldName, Object> result, Tuple input) {

		List<String> outputNames = new ArrayList(input.getFieldNames());
		List<Object> outputValues = new ArrayList<Object>(input.getValues());

		enhanceResultIfNecessary(definition, outputFields, result);

		addEntriesFromResult(result, outputNames, outputValues);

		return TupleBuilder.tuple().ofNamesAndValues(outputNames, outputValues);
	}

	/**
	 * @param definition
	 * @param outputFields
	 * @param result
	 */
	protected void enhanceResultIfNecessary(PmmlModelDefinition definition, List<FieldName> outputFields, Map<FieldName, ? super Object> result) {
		//noop
	}

	/**
	 * @param result
	 * @param outputNames
	 * @param outputValues
	 */
	protected void addEntriesFromResult(Map<FieldName, ? super Object> result, List<String> outputNames, List<Object> outputValues) {

		Collection<FieldName> outputFieldNames = outputFields == null ? result.keySet() : outputFields;

		for (FieldName field : outputFieldNames) {

			Object outputValue = result.get(field);

			int fieldIndex = outputNames.indexOf(field.getValue());
			if (fieldIndex != -1) {
				outputValues.set(fieldIndex, outputValue);
			} else {
				outputNames.add(field.getValue());
				outputValues.add(outputValue);
			}
		}
	}
}
