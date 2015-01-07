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

package ys.probabilistic.util;

/**
 * An abstraction of the idempotent hash function.
 *
 * @param <T> the type of values whose hash codes are calculated
 *
 * @author Yauheni Shahun
 */
public interface Hasher<T> {

  /**
   * Returns a hash code of the given value.
   */
  int hash(T value);
}
