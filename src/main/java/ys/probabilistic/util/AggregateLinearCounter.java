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

import java.util.Arrays;

/**
 * A {@link LinearCounter} that aggregates results from other linear counters. It doesn't count
 * values itself. The implementation is not thread safe.
 *
 * @param <T> the type of values being counted
 *
 * @author Yauheni Shahun
 */
public class AggregateLinearCounter<T> implements LinearCounter<T> {

  private final int size;
  private final int[] bitmap;

  /**
   * Constructs an aggregate counter.
   *
   * @param size the number of bits in the bitmap
   */
  public AggregateLinearCounter(int size) {
    this.size = size;
    int blockCount = (int) (((long) size + BLOCK_SIZE - 1) / BLOCK_SIZE);
    this.bitmap = new int[blockCount];
  }

  /**
   * @throws UnsupportedOperationException
   */
  @Override
  public void count(T value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public double getCardinality() {
    return CountUtil.calculateCardinality(size, CountUtil.getBitCount(bitmap));
  }

  @Override
  public void clear() {
    Arrays.fill(bitmap, 0);
  }

  /**
   * Returns the internal bitmap.
   */
  @Override
  public int[] getBitmap() {
    return bitmap;
  }

  @Override
  public void mergeTo(int[] otherBitmap) {
    if (otherBitmap.length != bitmap.length) {
      throw new IllegalArgumentException("Bitmaps have different lengths.");
    }

    for (int i = 0; i < otherBitmap.length; i++) {
      otherBitmap[i] |= bitmap[i];
    }
  }
}
