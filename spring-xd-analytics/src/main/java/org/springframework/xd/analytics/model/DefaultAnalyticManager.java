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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * @author Thomas Darimont
 */
public class DefaultAnalyticManager implements AnalyticManager, InitializingBean {

	private Set<? extends AnalyticController> analyticControllers = new HashSet<AnalyticController>();

	private Map<Class<? extends Analytic<?, ?>>, AnalyticController<? extends Analytic<?, ?>>> analyticTypeToControllers = new HashMap<Class<? extends Analytic<?, ?>>, AnalyticController<? extends Analytic<?, ?>>>();

	/**
	 * Creates a new {@link DefaultAnalyticManager}.
	 *
	 * @param analyticControllers
	 */
	public DefaultAnalyticManager(Set<? extends AnalyticController> analyticControllers) {
		this.analyticControllers = analyticControllers;
	}

	/**
	 * @param name must not be {@iteral null}.
	 * @param analyticType must not be {@iteral null}.
	 * @param <I>
	 * @param <O>
	 * @param <M>
	 * @return
	 */
	@Override
	public <I, O, M extends Analytic<I, O>> M getCurrentAnalytic(String name, Class<M> analyticType) {
		return (M) getAnalyticController(analyticType).getCurrent(name);
	}

	/**
	 * @param name must not be {@iteral null}.
	 * @param analyticType must not be {@iteral null}.
	 * @param <I>
	 * @param <O>
	 * @param <M>
	 * @return
	 */
	@Override
	public <I, O, M extends Analytic<I, O>> Future<M> updateAsync(String name, Class<M> analyticType) {
		return (Future<M>) getAnalyticController(analyticType).updateAsync(name);
	}

	/**
	 * @param analyticType
	 * @param <M>
	 * @return
	 */
	private <M extends Analytic<?, ?>> AnalyticController<? extends Analytic<?, ?>> getAnalyticController(Class<M> analyticType) {
		return analyticTypeToControllers.get(analyticType);
	}

	/**
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

		for (AnalyticController<?> analyticController : analyticControllers) {

			register(analyticController);
		}
	}

	/**
	 * @param controller
	 */
	private void register(AnalyticController<?> controller) {

		Assert.notNull(controller, "provider");

		for (Class<? extends Analytic<?, ?>> type : controller.getSupportedAnalyticTypes()) {
			analyticTypeToControllers.put(type, controller);
		}
	}
}
