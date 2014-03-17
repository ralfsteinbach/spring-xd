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

/**
 * Author: Thomas Darimont
 */
public class PmmlManagerUnitTests {

	PmmlModelBuilder modelBuilder;
	PmmlModelRepository modelRepository;


	@Before
	public void setup(){
		modelBuilder = new PmmlModelBuilder();
		modelRepository = new PmmlModelRepository(){};
	}

	@Test
	public void requestModelUpdate() throws Exception{

		PmmlModelManager pmm = new PmmlModelManager(modelBuilder,modelRepository);

		Future<PmmlModel> modelFuture = pmm.requestModelUpdate("simple-linear-regression-3-iris-model.r");

		PmmlModel model = modelFuture.get();

		assertThat(model,is(notNullValue()));
	}

	@Test
	public void getCurrentModel() throws Exception{

		PmmlModelManager pmm = new PmmlModelManager(modelBuilder,modelRepository);

		String modelName = "simple-linear-regression-3-iris-model.r";
		PmmlModel model = pmm.getCurrentModel(modelName);

		assertThat(model, is(nullValue()));

		Future<PmmlModel> modelFuture = pmm.requestModelUpdate(modelName);

		model = modelFuture.get();

		assertThat(model,is(notNullValue()));

		PmmlModel currentModel = pmm.getCurrentModel(modelName);
		assertSame(model,currentModel);

	}
}
