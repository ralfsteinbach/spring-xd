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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.springframework.xd.tuple.Tuple;

/**
 * @author Thomas Darimont
 */
public class AssociationPmmlModelTests extends AbstractPmmlModelTests {

	@Test
	@SuppressWarnings("unchecked")
	public void testEvaluateAssociationRules1shopping() throws Exception {

		PmmlAnalyticModel<Tuple, Tuple> model = getAnalytic("association-rules-1-shopping", null, Arrays.asList("Predicted_item"));

		Tuple output = model.evaluate(objectToTuple(new Object() {
			Collection<String> item = Arrays.asList("Choclates");
		}));

		Collection<String> predicted = (Collection<String>) output.getValue("Predicted_item");
		assertThat(predicted, hasItems("Pencil"));
	}

	@Override
	protected PmmlModelTupleOutputMapper getPmmlModelTupleOutputMapper(List<String> outputFieldNames) {
		return new PmmlModelTupleAssociationOutputMapper(outputFieldNames);
	}
}
