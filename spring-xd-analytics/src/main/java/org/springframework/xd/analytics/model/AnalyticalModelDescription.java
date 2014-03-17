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
 * Represents a description of a parameterized algorithm to perform analytics, e.g. a logistic regression function for classification
 * or a linear regression function to predict a new value based on a given input.
 *
 * An {@code AnalyticalModelDescription} has a {@code name} and an {@code id}.
 * The id represents the identity of a model and must be unique.
 *
 * An {@code AnalyticalModelDescription} that evolves over time due to new data will always have the same {@code name}
 * but a new instance will have a new {@code id}.
 *
 * Author: Thomas Darimont
 */
public interface AnalyticalModelDescription {

	/**
	 * The id of a model must be unique.
	 * @return
	 */
	String getId();

	/**
	 * The name of a model must not be {@literal null}.
	 * @return
	 */
	String getName();
}
