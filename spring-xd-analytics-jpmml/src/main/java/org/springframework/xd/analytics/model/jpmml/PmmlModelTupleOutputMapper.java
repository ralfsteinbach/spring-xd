/*
 *
 *  * Copyright 2014 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
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
 * Author: Thomas Darimont
 */
public class PmmlModelTupleOutputMapper implements PmmlModelOutputMapper<Tuple, Tuple> {

	private final List<String> outputFieldsNames;

	private final List<FieldName> outputFields;

	public PmmlModelTupleOutputMapper(List<String> outputFieldsNames) {

		this.outputFieldsNames = outputFieldsNames;
		this.outputFields = new ArrayList<FieldName>(outputFieldsNames.size());
		for(String fieldName : outputFieldsNames){
			this.outputFields.add(new FieldName(fieldName));
		}
	}

	@Override
	public Tuple mapOutput(PmmlModel model, Map<FieldName, ? super Object> result, Tuple input) {

		List<String> outputNames = new ArrayList(input.getFieldNames());
		List<Object> outputValues = new ArrayList<Object>(input.getValues());

		enhanceResultIfNecessary(model, outputFields, result);

		addEntriesFromResult(result, outputNames, outputValues);

		return TupleBuilder.tuple().ofNamesAndValues(outputNames, outputValues);
	}

	protected void enhanceResultIfNecessary(PmmlModel model, List<FieldName> outputFields, Map<FieldName, ? super Object> result) {
		//noop
	}

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
