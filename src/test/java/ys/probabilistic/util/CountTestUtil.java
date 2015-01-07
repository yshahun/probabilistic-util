package ys.probabilistic.util;

import java.util.UUID;

/**
 * Test utilities for counting cardinality.
 *
 * @author Yauheni Shahun
 */
public final class CountTestUtil {

  private CountTestUtil() {}

  public static void observe(LinearCounter<String> counter, int count, int uniqueCount) {
    String[] uniqueData = new String[uniqueCount];
    for (int i = 0; i < uniqueCount; i++) {
      uniqueData[i] = UUID.randomUUID().toString();
    }

    for (int i = 0; i < count; i++) {
      counter.count(uniqueData[i % uniqueCount]);
    }
  }

  public static void observeUnique(LinearCounter<String> counter, int count) {
    for (int i = 0; i < count; i++) {
      counter.count(UUID.randomUUID().toString());
    }
  }
}
