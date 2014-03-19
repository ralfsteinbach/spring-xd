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

import javax.xml.transform.sax.SAXSource;
import java.io.StringReader;
import java.util.Locale;
import java.util.regex.Pattern;

import org.dmg.pmml.PMML;
import org.jpmml.model.ImportFilter;
import org.jpmml.model.JAXBUtil;
import org.springframework.util.Assert;
import org.springframework.xd.analytics.rproject.RprojectCallback;
import org.springframework.xd.analytics.rproject.RprojectConnection;
import org.springframework.xd.analytics.rproject.RprojectOperations;
import org.xml.sax.InputSource;

/**
 * @author Thomas Darimont
 */
public class RprojectPmmlProvider implements PmmlProvider {

	private static final Pattern EXTRACT_PMML_PATTERN = Pattern.compile(".*(<PMML.*</PMML>).*", Pattern.DOTALL | Pattern.MULTILINE);

	private static final String ANALYTICS_MODELS_LOCATION = "analytics/models/";

	private static final String REBUILD_MODEL_COMMAND = "rebuildModel(\"%s\",\"%s\")";

	private final RprojectOperations rprojectOperations;

	/**
	 * Creates a {@link RprojectPmmlProvider}.
	 * @param rprojectOperations must not be {@literal null}.
	 */
	public RprojectPmmlProvider(RprojectOperations rprojectOperations) {

		Assert.notNull(rprojectOperations,"rprojectOperations");

		this.rprojectOperations = rprojectOperations;
	}

	@Override
	public PMML getPmml(final String name, final String modelId) {

		String pmmlText = rprojectOperations.executeScript(name, new RprojectCallback() {
			public String executeInR(RprojectConnection connection) {
				String cmd = String.format(Locale.ENGLISH, REBUILD_MODEL_COMMAND, name, modelId);
				return connection.eval(cmd);
			}
		});

		try {
			String pmmlSource = EXTRACT_PMML_PATTERN.matcher(pmmlText).replaceFirst("$1");

			SAXSource transformedSource = ImportFilter.apply(new InputSource(new StringReader(pmmlSource)));
			PMML pmml = JAXBUtil.unmarshalPMML(transformedSource);

			return pmml;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
