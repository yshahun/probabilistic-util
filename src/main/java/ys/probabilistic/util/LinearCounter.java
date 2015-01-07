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
 * A probabilistic data structure to estimate the cardinality of the data set in a linear time and
 * using a small amount of memory. It's implemented by using the bitmap and the hash function.
 *
 * @param <T> the type of values being counted
 *
 * @author Yauheni Shahun
 */
public interface LinearCounter<T> {

  /**
   * Size in bits of the {@code int} block that the bitmap consists of.
   */
  int BLOCK_SIZE = 32;

  /**
   * Counts the given value.
   */
  void count(T value);

  /**
   * Gets the cardinality of the observed values.
   *
   * @return estimated cardinality as {@code double}
   */
  double getCardinality();

  /**
   * Returns a copy of the underlying bitmap (if other isn't mentioned) represented as {@code int}
   * array. This can be used for calculating aggregate cardinalities.
   */
  int[] getBitmap();

  /**
   * Merges the internal bitmap into the given bitmap represented as {@code int} array. This can be
   * used for calculating aggregate cardinalities.
   */
  void mergeTo(int[] otherBitmap);

  /**
   * Clears the counter's state (bitmap).
   */
  void clear();
}
