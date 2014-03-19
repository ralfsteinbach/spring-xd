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

package org.springframework.xd.analytics.model;

/**
 * @author Thomas Darimont
 */
public interface AnalyticModelOutputMapper<O, I, MO, MD extends AnalyticModelDefinition> {

	/**
	 * Maps the model-output {@code MO} to an appropriate output {@code O}.
	 *
	 * @param modelDefinition the {@link AnalyticModelDefinition} that can be used to retrieve mapping information.
	 * @param output the raw unmapped model output.
	 * @param input the input for this {@link AnalyticModel} that could be used to build the model {@code O}.
	 * @return
	 */
	O mapOutput(MD modelDefinition, MO output, I input);
}
