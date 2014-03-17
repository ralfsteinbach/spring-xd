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
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Author: Thomas Darimont
 */
public class PmmlModelBuilderTests {

	@Test
	public void buildModel(){

		PmmlModelBuilder pmb = new PmmlModelBuilder();
		PmmlModel model = pmb.buildModel("simple-linear-regression-3-iris-model.r");

		assertThat(model,is(notNullValue()));
	}
}
