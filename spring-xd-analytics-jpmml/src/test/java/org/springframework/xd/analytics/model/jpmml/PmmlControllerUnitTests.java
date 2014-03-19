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

import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.xd.analytics.rproject.RprojectCliConnection;
import org.springframework.xd.analytics.rproject.RprojectTemplate;

/**
 * @author Thomas Darimont
 */
public class PmmlControllerUnitTests {

	PmmlAnalyticModelBuilder modelBuilder;
	PmmlModelRepository modelRepository;


	@Before
	public void setup() throws Exception {
		PmmlProvider pp = new RprojectPmmlProvider(new RprojectTemplate(new RprojectCliConnection(new ClassPathResource("analytics/models").getFile().getAbsolutePath())));
		modelBuilder = new PmmlAnalyticModelBuilder(pp, new PmmlModelTupleInputMapper(null), new PmmlModelTupleOutputMapper(null));
		modelRepository = new PmmlModelRepository() {
		};
	}

	@Test
	public void requestModelUpdate() throws Exception {

		PmmlController pmm = new PmmlController(modelBuilder, modelRepository);

		Future<PmmlAnalyticModel> modelFuture = pmm.updateAsync("simple-linear-regression-4-iris-model.r");

		PmmlAnalyticModel model = modelFuture.get();

		assertThat(model, is(notNullValue()));
	}

	@Test
	public void getCurrentModel() throws Exception {

		PmmlController pmp = new PmmlController(modelBuilder, modelRepository);

		String modelName = "simple-linear-regression-4-iris-model.r";
		PmmlAnalyticModel model = pmp.getCurrent(modelName);

		assertThat(model, is(nullValue()));

		Future<PmmlAnalyticModel> modelFuture = pmp.updateAsync(modelName);

		model = modelFuture.get();
		assertThat(model, is(notNullValue()));

		PmmlAnalyticModel currentModel = pmp.getCurrent(modelName);
		assertThat(currentModel, is(notNullValue()));

		assertSame(model, currentModel);
	}
}
