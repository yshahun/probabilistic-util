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

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * An implementation of {@link LinearCounter} that is lock-free for writers (they count values).
 * It's not thread-safe for reading though. It's supposed that reading occurs exclusively when
 * writing has finished.
 *
 * @param <T> the type of values being counted
 *
 * @author Yauheni Shahun
 */
public class WriteConcurrentLinearCounter<T> implements LinearCounter<T> {

  private final int size;
  private final Hasher<T> hasher;
  private final AtomicIntegerArray bitmap;

  /**
   * Constructs a linear counter of the given size.
   *
   * @param size the number of bits in the bitmap
   * @param hasher the hash function used to map the values
   */
  public WriteConcurrentLinearCounter(int size, Hasher<T> hasher) {
    this.size = size;
    this.hasher = hasher;

    int blockCount = (int) (((long) size + BLOCK_SIZE - 1) / BLOCK_SIZE);
    this.bitmap = new AtomicIntegerArray(blockCount);
  }

  @Override
  public void count(T value) {
    if (value == null) {
      throw new NullPointerException("Value is null");
    }

    int index = hasher.hash(value) % size;
    // Get the absolute value.
    int t = index >> 31;
    index = (index ^ t) - t;

    int blockIndex = index / BLOCK_SIZE;
    int mask = 1 << (index % BLOCK_SIZE);

    int oldBlock;
    int newBlock;
    do {
      oldBlock = bitmap.get(blockIndex);
      newBlock = oldBlock | mask;
    } while (oldBlock != newBlock && !bitmap.compareAndSet(blockIndex, oldBlock, newBlock));
  }

  @Override
  public double getCardinality() {
    int oneBitCount = 0;
    for (int i = 0; i < bitmap.length(); i++) {
      oneBitCount += Integer.bitCount(bitmap.get(i));
    }

    return CountUtil.calculateCardinality(size, oneBitCount);
  }

  @Override
  public void clear() {
    for (int i = 0; i < bitmap.length(); i++) {
      bitmap.set(i, 0);
    }
  }

  @Override
  public int[] getBitmap() {
    int[] copy = new int[bitmap.length()];
    for (int i = 0; i < copy.length; i++) {
      copy[i] = bitmap.get(i);
    }
    return copy;
  }

  @Override
  public void mergeTo(int[] otherBitmap) {
    if (otherBitmap.length != bitmap.length()) {
      throw new IllegalArgumentException("Bitmaps have different lengths.");
    }

    for (int i = 0; i < otherBitmap.length; i++) {
      otherBitmap[i] |= bitmap.get(i);
    }
  }
}
