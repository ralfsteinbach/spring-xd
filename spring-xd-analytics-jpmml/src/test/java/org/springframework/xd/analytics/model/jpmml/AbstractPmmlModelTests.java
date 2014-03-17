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

import static org.springframework.xd.tuple.TupleBuilder.tuple;

import javax.xml.transform.sax.SAXSource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dmg.pmml.PMML;
import org.jpmml.model.ImportFilter;
import org.jpmml.model.JAXBUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ReflectionUtils;
import org.springframework.xd.tuple.Tuple;
import org.xml.sax.InputSource;

/**
 * Author: Thomas Darimont
 */
public abstract class AbstractPmmlModelTests {

	static final String ANALYTICS_MODELS_LOCATION = "analytics/models/";


	protected PmmlModel getModel(String modelName, Set<String> inputFieldNames, List<String> outputFieldNames) throws Exception {

		PmmlModelTupleInputMapper inputMapper = new PmmlModelTupleInputMapper(inputFieldNames);
		PmmlModelTupleOutputMapper outputMapper = getPmmlModelTupleOutputMapper(outputFieldNames);
		PmmlModelDescription pmmlModelDescription = new PmmlModelDescription(modelName,"model-id-4711",loadPmmlModel(modelName));

		PmmlModel evaluator = new PmmlModel(pmmlModelDescription, inputMapper,outputMapper);

		return evaluator;
	}

	protected PmmlModelTupleOutputMapper getPmmlModelTupleOutputMapper(List<String> outputFieldNames) {
		return new PmmlModelTupleOutputMapper(outputFieldNames);
	}

	protected static PMML loadPmmlModel(String modelName) throws Exception {

		InputSource pmmlStream = new InputSource(new ClassPathResource(ANALYTICS_MODELS_LOCATION + modelName).getInputStream());
		SAXSource transformedSource = ImportFilter.apply(pmmlStream);

		return JAXBUtil.unmarshalPMML(transformedSource);
	}

	protected static Tuple objectToTuple(final Object o) {

		final List<String> fieldNames = new ArrayList<String>();
		final List<Object> fieldValues = new ArrayList<Object>();

		ReflectionUtils.doWithFields(o.getClass(), new ReflectionUtils.FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

				if(field.isSynthetic()){
					return;
				}

				//We abuse the @Value annotation here to declare the attribute name that is used inside the PMML model.
				//We should use another annotation here.
				Value value = field.getAnnotation(Value.class);
				fieldNames.add(value == null ? field.getName() : value.value());
				fieldValues.add(field.get(o));
			}
		});

		return tuple().ofNamesAndValues(fieldNames,fieldValues);
	}
}
