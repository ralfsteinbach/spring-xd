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
import java.io.File;
import java.io.StringReader;
import java.util.Scanner;
import java.util.UUID;

import org.dmg.pmml.PMML;
import org.jpmml.model.ImportFilter;
import org.jpmml.model.JAXBUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.xd.analytics.model.AnalyticalModelBuilder;
import org.xml.sax.InputSource;

/**
 * Author: Thomas Darimont
 */
public class PmmlModelBuilder implements AnalyticalModelBuilder<PmmlModel, PmmlModelDefinition> {

	private static final String ANALYTICS_MODELS_LOCATION = "analytics/models/";
	public static final String REBUILD_MODEL = "rebuildModel";

	@Override
	public PmmlModel buildModel(String name) {

		PmmlModelDefinition modelDefinition = buildModelDefinition(name);

		return buildModel(modelDefinition);
	}

	private PmmlModelDefinition buildModelDefinition(String name) {

		String modelId = UUID.randomUUID().toString();

		PMML pmml = generatePmmlByExecutingRScriptWith(name, modelId);

		return new PmmlModelDefinition(name, modelId, pmml);
	}

	/**
	 * Dummy Implementation
	 */
	private PMML generatePmmlByExecutingRScriptWith(String name, String modelId) {

		try {

			ProcessBuilder pb = new ProcessBuilder("Rscript", new ClassPathResource(ANALYTICS_MODELS_LOCATION + name).getFile().getAbsolutePath(), REBUILD_MODEL, name, modelId);

			int exit = pb.start().waitFor();

			Scanner scanner = new Scanner(new File(System.getProperties().get("java.io.tmpdir") + "model.pmml.xml"));
			StringBuilder sb = new StringBuilder();
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
			}
			scanner.close();

			String pmmlSource = sb.toString().replace(".*(<PMML>.*</PMML>).*", "$1");

			SAXSource transformedSource = ImportFilter.apply(new InputSource(new StringReader(pmmlSource)));
			PMML pmml = JAXBUtil.unmarshalPMML(transformedSource);

			return pmml;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PmmlModel buildModel(PmmlModelDefinition modelDefinition) {
		return new PmmlModel(modelDefinition, null, null);
	}
}
