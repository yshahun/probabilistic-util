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

import ys.probabilistic.util.Hasher;
import ys.probabilistic.util.LinearCounter;
import ys.probabilistic.util.WriteConcurrentLinearCounter;

/**
 * A factory of {@link WriteConcurrentLinearCounter}s that count {@link String} values.
 *
 * @author Yauheni Shahun
 */
public class StringLinearCounterFactory implements LinearCounterFactory<String> {

  private final int size;
  private final Hasher<String> hasher;

  /**
   * Constructs a factory of the write-concurrent linear counters.
   *
   * @param size the number of bits used by the linear counters
   * @param hasher the idempotent hash function used by the linear counters
   */
  public StringLinearCounterFactory(int size, Hasher<String> hasher) {
    this.size = size;
    this.hasher = hasher;
  }

  /**
   * Creates a write-concurrent linear counter that counts string values.
   */
  @Override
  public LinearCounter<String> create() {
    return new WriteConcurrentLinearCounter<>(size, hasher);
  }
}
