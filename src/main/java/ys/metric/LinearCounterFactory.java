/*
 * Copyright 2014 Yauheni Shahun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ys.metric;

import ys.probabilistic.util.LinearCounter;

/**
 * A factory of the linear counters of the same size.
 *
 * @param <V> the type of values counted by the linear counters
 *
 * @author Yauheni Shahun
 */
public interface LinearCounterFactory<V> {

  /**
   * Creates an instance of {@link LinearCounter}.
   */
  LinearCounter<V> create();
}
