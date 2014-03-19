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

import java.util.concurrent.Future;

/**
 * @author Thomas Darimont
 */
public interface AnalyticController<M extends Analytic<?, ?>> {

	/**
	 * Returns the current {@link Analytic} with the given {@code name} or {@literal null} if not available.
	 *
	 * @param name must not be {@iteral null}.
	 * @return
	 */
	M getCurrent(String name);

	/**
	 * Triggers an update for the {@link Analytic} with the given {@code name}.
	 *
	 * @param name must not be {@iteral null}.
	 * @return a {@link Future} that will eventually hold the updated {@code Analytic} instance.
	 */
	Future<M> updateAsync(String name);

	/**
	 * Returns an array of the support {@link Analytic} types.
	 *
	 * @return
	 */
	Class<? extends M>[] getSupportedAnalyticTypes();
}
