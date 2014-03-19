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

package org.springframework.xd.analytics.rproject;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * @author Thomas Darimont
 */
public class RprojectTemplateTests {

	@Test
	public void foo(){


		String scriptRootLocation = "/Users/tom/Documents/dev/repos/thomasdarimont/spring-xd/spring-xd-analytics-jpmml/src/test/resources/analytics/models/";
		RprojectConnection con = new RprojectCliConnection(scriptRootLocation);

		RprojectTemplate tpl = new RprojectTemplate(con);

		String result = tpl.executeScript("simple-linear-regression-4-iris-model.r", new RprojectCallback() {
			@Override
			public String executeInR(RprojectConnection connection) {
				return connection.eval("rebuildModel(\"model-name-bubu\",\"model-id-42\")");
			}
		});

		assertThat(result,is(notNullValue()));
	}
}
