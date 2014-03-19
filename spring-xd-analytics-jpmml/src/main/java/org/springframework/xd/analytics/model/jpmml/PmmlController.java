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

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.xd.analytics.model.AnalyticController;
import org.springframework.xd.analytics.model.AnalyticModelBuilder;

/**
 * @author Thomas Darimont
 */
public class PmmlController<A extends PmmlAnalyticModel<?, ?>> implements AnalyticController<A> {

	public static final Class[] SUPPORTED_ANALYTIC_TYPES = new Class[]{PmmlAnalyticModel.class};

	private final AnalyticModelBuilder<? extends A, ? extends PmmlModelDefinition> modelBuilder;

	private final PmmlModelRepository modelRepository;

	private final AsyncListenableTaskExecutor asyncTaskExecutor;

	private final ConcurrentMap<String, Future<A>> currentModels;

	private final ConcurrentMap<String, ListenableFuture<A>> pendingModels;

	/**
	 * Creates a new {@link PmmlController}.
	 *
	 * @param modelBuilder must not be {@literal null}.
	 * @param modelRepository must not be {@literal null}.
	 */
	public PmmlController(AnalyticModelBuilder<? extends A, ? extends PmmlModelDefinition> modelBuilder, PmmlModelRepository modelRepository) {

		Assert.notNull(modelBuilder, "modelBuilder");
		Assert.notNull(modelRepository, "modelRepository");

		this.modelBuilder = modelBuilder;
		this.modelRepository = modelRepository;

		this.asyncTaskExecutor = new SimpleAsyncTaskExecutor();
		this.currentModels = new ConcurrentHashMap<String, Future<A>>();
		this.pendingModels = new ConcurrentHashMap<String, ListenableFuture<A>>();
	}

	/**
	 * @param name must not be {@literal null}.
	 * @return
	 */
	@Override
	public A getCurrent(String name) {
		Future<A> pmmlModelFuture = this.currentModels.get(name);
		return pmmlModelFuture != null ? saveGet(pmmlModelFuture) : null;
	}

	/**
	 * @param analyticFuture
	 * @return
	 */
	private A saveGet(Future<A> analyticFuture) {
		try {
			return analyticFuture.get();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param name must not be {@iteral null}.
	 * @return
	 */
	@Override
	public ListenableFuture<A> updateAsync(final String name) {

		ListenableFuture<A> modelFuture = pendingModels.get(name);
		if (modelFuture == null) {

			modelFuture = asyncTaskExecutor.submitListenable(new Callable<A>() {

				@Override
				public A call() throws Exception {
					return modelBuilder.buildModel(name);
				}
			});
			ListenableFuture<A> tmp = pendingModels.putIfAbsent(name, modelFuture);
			if (tmp != null) {
				modelFuture = tmp;
			} else {
				modelFuture.addCallback(registerUpdatedModel(name));
			}
		}

		currentModels.putIfAbsent(name, modelFuture);

		return modelFuture;
	}

	/**
	 * @param name
	 * @return
	 */
	private ListenableFutureCallback<A> registerUpdatedModel(final String name) {

		return new ListenableFutureCallback<A>() {

			@Override
			public void onSuccess(A result) {

				Assert.notNull(result, "result");

				currentModels.put(name, new AsyncResult<A>(result));
				pendingModels.remove(name);
			}

			@Override
			public void onFailure(Throwable t) {

				pendingModels.remove(name);
				t.printStackTrace();
			}
		};
	}

	/**
	 * @return
	 */
	@Override
	public Class<? extends A>[] getSupportedAnalyticTypes() {
		return SUPPORTED_ANALYTIC_TYPES;
	}
}
