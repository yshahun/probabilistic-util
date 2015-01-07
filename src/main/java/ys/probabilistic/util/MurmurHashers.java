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

import java.nio.charset.StandardCharsets;

import com.google.common.hash.Hashing;
import com.google.common.hash.PrimitiveSink;

/**
 * A factory of the collision-resistant {@link Hasher}s that are based on the {@code murmur3}
 * algorithm.
 *
 * @author Yauheni Shahun
 */
public final class MurmurHashers {

  private MurmurHashers() {}

  /**
   * An abstract {@link Hasher} that uses {@code murmur3}.
   */
  private static abstract class AbstractMurmurHasher<T> implements Hasher<T> {
    @Override
    public int hash(T value) {
      com.google.common.hash.Hasher hasher = Hashing.murmur3_32().newHasher();
      put(value, hasher);
      return hasher.hash().asInt();
    }

    /**
     * Puts the value into the {@link PrimitiveSink} as the underlying primitive(s).
     */
    protected abstract void put(T value, PrimitiveSink sink);
  }

  /**
   * Returns a hasher for string values.
   */
  public static Hasher<String> stringHasher() {
    return new AbstractMurmurHasher<String>() {
      @Override
      protected void put(String value, PrimitiveSink sink) {
        sink.putString(value, StandardCharsets.UTF_8);
      }
    };
  }

  /**
   * Returns a hasher for integer values.
   */
  public static Hasher<Integer> integerHasher() {
    return new AbstractMurmurHasher<Integer>() {
      @Override
      protected void put(Integer value, PrimitiveSink sink) {
        sink.putInt(value);
      }
    };
  }
}
