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
import java.util.Scanner;

import org.springframework.xd.tuple.Tuple;


/**
 *
 * @author Ralf Steinbach
 */
public class CsvSplitter {

	public List<Tuple> split(String payload) {

		List<Tuple> result = new ArrayList<Tuple>();
		String content = payload; // .getString(0);
		Scanner scanner = null;
		try {
			scanner = new Scanner(content);
			scanner.useDelimiter(",");
			List<String> header = processLine(scanner.nextLine());
			while (scanner.hasNextLine()) {
				List<String> line = processLine(scanner.nextLine());
				result.add(createTuple(header, line));
			}
		}
		finally {
			if (null != scanner)
				scanner.close();
		}

		return result;
	}

	/**
	 * @param header
	 * @param line
	 * @return
	 */
	private Tuple createTuple(List<String> header, List<String> line) {

		Object[] tupleValues = new Object[header.size()];
		for (int i = 0; i < header.size(); i++) {
			tupleValues[i] = line.get(i);
		}
		return tuple().ofNamesAndValues(header, Arrays.asList(tupleValues));
	}

	/**
	 * @param line
	 * @return
	 */
	private List<String> processLine(String line) {

		List<String> lineValues = new ArrayList<String>();
		Scanner lineScanner = null;
		try {
			lineScanner = new Scanner(line);
			lineScanner.useDelimiter(",");
			while (lineScanner.hasNext()) {
				lineValues.add(lineScanner.next().trim());
			}
		}
		finally {
			lineScanner.close();
		}

		return lineValues;
	}
}
