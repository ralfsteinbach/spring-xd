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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Thomas Darimont
 */
public class DefaultAnalyticManagerTests {

	DefaultAnalyticManager analyticManager;

	@Before
	public void setUp() throws Exception {
		analyticManager = new DefaultAnalyticManager(Collections.singleton(new SimpleOrderFraudDetectionAnalyticController()));
		analyticManager.afterPropertiesSet();
	}

	@Test
	public void testGetCurrentAnalytic() throws Exception {

		Order input = new Order();
		input.totalAmount = 200000;

		Analytic<Order,Order> analytic = analyticManager.getCurrentAnalytic("orderFraudDetection", SimpleOrderFraudDetectionAnalytic.class);

		Order output = analytic.evaluate(input);

		assertThat(output.fraudulent,is(true));
	}

	@Test
	public void testComputeUpdatedAnalytic() throws Exception {

		Order input = new Order();
		input.totalAmount = 200000;

		Future<SimpleOrderFraudDetectionAnalytic> analytic = analyticManager.updateAsync("orderFraudDetection", SimpleOrderFraudDetectionAnalytic.class);

		Order output = analytic.get().evaluate(input);

		assertThat(output.fraudulent,is(true));

	}

	static class SimpleOrderFraudDetectionAnalyticController implements AnalyticController<SimpleOrderFraudDetectionAnalytic>{

		@Override
		public SimpleOrderFraudDetectionAnalytic getCurrent(String name) {
			return new SimpleOrderFraudDetectionAnalytic();
		}

		@Override
		public Future<SimpleOrderFraudDetectionAnalytic> updateAsync(String name) {
			return new AsyncResult<SimpleOrderFraudDetectionAnalytic>(new SimpleOrderFraudDetectionAnalytic());
		}

		@Override
		public Class<? extends SimpleOrderFraudDetectionAnalytic>[] getSupportedAnalyticTypes() {
			return new Class[]{SimpleOrderFraudDetectionAnalytic.class};
		}
	}

	static class SimpleOrderFraudDetectionAnalytic implements Analytic<Order,Order>{

		@Override
		public Order evaluate(Order input) {

			Order output = new Order();
			output.totalAmount = input.totalAmount;
			output.fraudulent = input.totalAmount > 10000;

			return output;
		}
	}

	static class Order{

		boolean fraudulent;
		double totalAmount;
	}
}
