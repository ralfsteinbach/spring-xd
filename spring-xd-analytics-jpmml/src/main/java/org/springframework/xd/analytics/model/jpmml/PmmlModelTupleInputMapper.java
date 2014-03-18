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

import java.util.*;

import org.dmg.pmml.FieldName;
import org.springframework.xd.tuple.Tuple;

/**
 * Author: Thomas Darimont
 */
public class PmmlModelTupleInputMapper implements PmmlModelInputMapper<Tuple> {

	private final Set<String> inputFieldNames;

	public PmmlModelTupleInputMapper(Set<String> inputFieldNames) {
		this.inputFieldNames = inputFieldNames == null ? null : Collections.unmodifiableSet(inputFieldNames);
	}

	public Set<String> getInputFields() {
		return this.inputFieldNames;
	}

	@Override
	public Map<FieldName, Object> mapInput(PmmlModelDefinition modelDefinition, Tuple input) {

		Map<FieldName, Object> inputData = new HashMap<FieldName, Object>();
		for (String fieldName : input.getFieldNames()) {

			if (inputFieldNames == null || inputFieldNames.contains(fieldName)) {
				inputData.put(new FieldName(fieldName), input.getValue(fieldName));
			}
		}

		return inputData;
	}
}
