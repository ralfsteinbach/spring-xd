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
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.xd.analytics.model.AnalyticalModelManager;

/**
 * Author: Thomas Darimont
 */
public class PmmlModelManager implements AnalyticalModelManager<PmmlModel> {

	private final PmmlModelBuilder modelBuilder;

	private final PmmlModelRepository modelRepository;

	private final AsyncListenableTaskExecutor asyncTaskExecutor;

	private final ConcurrentMap<String,PmmlModel> currentModels;

	private final ConcurrentMap<String,ListenableFuture<PmmlModel>> pendingModels;

	public PmmlModelManager(PmmlModelBuilder modelBuilder, PmmlModelRepository modelRepository) {

		this.modelBuilder = modelBuilder;
		this.modelRepository = modelRepository;
		this.asyncTaskExecutor = new SimpleAsyncTaskExecutor();
		this.currentModels = new ConcurrentHashMap<String, PmmlModel>();
		this.pendingModels = new ConcurrentHashMap<String, ListenableFuture<PmmlModel>>();
	}

	@Override
	public PmmlModel getCurrentModel(String name) {
		return this.currentModels.get(name);
	}

	@Override
	public Future<PmmlModel> requestModelUpdate(final String name) {

		ListenableFuture<PmmlModel> modelFuture = pendingModels.get(name);
		if(modelFuture == null){

			modelFuture = asyncTaskExecutor.submitListenable(new Callable<PmmlModel>(){

				@Override
				public PmmlModel call() throws Exception {
					return modelBuilder.buildModel(name);
				}
			});

			ListenableFuture<PmmlModel> tmp = pendingModels.putIfAbsent(name,modelFuture);
			if(tmp != null){
				modelFuture = tmp;
			}else{
				modelFuture.addCallback(registerUpdatedModel(name));
			}
		}

		return modelFuture;
	}

	private ListenableFutureCallback<PmmlModel> registerUpdatedModel(final String name) {

		return new ListenableFutureCallback<PmmlModel>() {
			@Override
			public void onSuccess(PmmlModel result) {

				currentModels.put(name,result);
				pendingModels.remove(name);
			}

			@Override
			public void onFailure(Throwable t) {

				pendingModels.remove(name);
				t.printStackTrace();
			}
		};
	}
}
