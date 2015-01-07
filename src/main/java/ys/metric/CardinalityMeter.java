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

import org.HdrHistogram.WriterReaderPhaser;

import ys.probabilistic.util.LinearCounter;

/**
 * An object that meters the cardinality of the data set over a period of time using the linear
 * counters. Each time the cardinality is requested the metric is reset. The implementation is
 * thread-safe if the underlying {@link LinearCounter} supports concurrent writing (counting
 * values). Reading phase is guarded by the {@link WriterReaderPhaser} synchronization primitive.
 *
 * @param <V> the type of values being counted
 *
 * @author Yauheni Shahun
 */
public class CardinalityMeter<V> {

  private final WriterReaderPhaser phaser = new WriterReaderPhaser();
  private final LinearCounterFactory<V> factory;

  private volatile LinearCounter<V> activeCounter;
  private volatile LinearCounter<V> inactiveCounter;

  /**
   * Constructs a cardinality meter.
   *
   * @param factory the factory of the write-concurrent linear counters
   */
  public CardinalityMeter(LinearCounterFactory<V> factory) {
    this.factory = factory;
    this.activeCounter = factory.create();
    this.inactiveCounter = factory.create();
  }

  /**
   * Counts the given value.
   */
  public void count(V value) {
    long criticalValue = phaser.writerCriticalSectionEnter();
    try {
      activeCounter.count(value);
    } finally {
      phaser.writerCriticalSectionExit(criticalValue);
    }
  }

  /**
   * Returns the cardinality of the observed values since the last call of either
   * {@link #getCardinality()} or {@link #getCounter()}.
   *
   * @return expected cardinality as {@code double}
   */
  public double getCardinality() {
    phaser.readerLock();
    try {
      inactiveCounter.clear();

      // Swap counters.
      LinearCounter<V> tempCounter = activeCounter;
      activeCounter = inactiveCounter;
      inactiveCounter = tempCounter;

      phaser.flipPhase();

      return inactiveCounter.getCardinality();
    } finally {
      phaser.readerUnlock();
    }
  }

  /**
   * Returns the linear counter that accumulates info about the observed values since the last call
   * of either {@link #getCardinality()} or {@link #getCounter()}.
   *
   * @return an instance of {@link LinearCounter} that can be safely used
   */
  public LinearCounter<V> getCounter() {
    phaser.readerLock();
    try {
      inactiveCounter.clear();

      // Swap counters.
      LinearCounter<V> snapshot = activeCounter;
      activeCounter = inactiveCounter;
      inactiveCounter = factory.create();

      phaser.flipPhase();

      return snapshot;
    } finally {
      phaser.readerUnlock();
    }
  }
}
