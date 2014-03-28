/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.xd.csv;

import static org.springframework.xd.tuple.TupleBuilder.tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.springframework.xd.tuple.Tuple;


/**
 *
 * @author Ralf Steinbach
 */
public class CsvSplitterTest {

	@Test
	public void testTransform() {

		String testData = createSourceText().toString();
		CsvSplitter splitter = new CsvSplitter();
		List<Tuple> splitTupples = splitter.split(testData);
		for (Tuple splitTuple : splitTupples) {
			Assert.assertNotNull(splitTuple.getString("sepalLength"));
			Assert.assertNotNull(splitTuple.getString("sepalWidth"));
			Assert.assertNotNull(splitTuple.getString("petalLength"));
			Assert.assertNotNull(splitTuple.getString("petalWidth"));
		}
	}

	/**
	 * @return
	 */
	private Tuple createTestTuple() {

		List<Object> transformerInput = new ArrayList<Object>();
		StringBuilder sourceText = createSourceText();
		transformerInput.add(sourceText.toString());
		Tuple testTuple = tuple().ofNamesAndValues(Arrays.asList("payload"), transformerInput);
		return testTuple;
	}

	private StringBuilder createSourceText() {

		StringBuilder sourceText = new StringBuilder();
		sourceText.append("sepalLength, sepalWidth, petalLength, petalWidth\n");
		sourceText.append("6.4, 3.2, 4.5, 1.9\n");
		sourceText.append("6.2, 3.1, 4.0, 1.8\n");
		sourceText.append("6.4, 3.2, 4.1, 1.7\n");
		sourceText.append("6.4, 3.1, 4.2, 1.6\n");
		sourceText.append("6.0, 3.2, 4.3, 1.5\n");
		sourceText.append("6.3, 3.1, 4.4, 1.4\n");
		sourceText.append("6.6, 3.2, 4.5, 1.3\n");
		return sourceText;
	}
}
