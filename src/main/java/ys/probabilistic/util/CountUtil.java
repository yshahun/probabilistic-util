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
 * Utilities for counting unique objects (cardinality).
 *
 * @author Yauheni Shahun
 */
public final class CountUtil {

  private CountUtil() {}

  /**
   * Returns the count of 1s bits in the given bitmap.
   */
  public static int getBitCount(int[] bitmap) {
    int bitCount = 0;
    for (int block : bitmap) {
      bitCount += Integer.bitCount(block);
    }
    return bitCount;
  }

  /**
   * Calculates the cardinality of the data set.
   *
   * @param bitCount the total number of bits allocated for counting
   * @param oneBitCount the number of 1's in the bitmap
   * @return estimated cardinality as {@code double}
   */
  public static double calculateCardinality(int bitCount, int oneBitCount) {
    return -bitCount * Math.log((bitCount - oneBitCount) / (double) bitCount);
  }
}
