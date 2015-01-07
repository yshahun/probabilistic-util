package ys.probabilistic.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for {@link WriteConcurrentLinearCounter}.
 *
 * @author Yauheni Shahun
 */
public class WriteConcurrentLinearCounterTest {
  /*
   * Number of unique values.
   */
  private static final int N = 8000;
  /*
   * Number of bits for counting.
   */
  private static final int M = 8 * 1024 * 8;
  /*
   * Standard deviation of the cardinality (normal) distribution with given N and M.
   */
  private static final double SD = 22.5;
  /*
   * Confidence interval 99.99%.
   */
  private static final double CONFIDENCE_9999 = 3.9 * SD;

  private WriteConcurrentLinearCounter<String> counter;

  @Before
  public void setUp() {
    counter = new WriteConcurrentLinearCounter<String>(M, new SimpleHasher<String>());
  }

  @Test
  public void testCardinality() {
    int k = 100;

    for (int i = 0; i < k; i++) {
      LinearCounter<String> counter = new WriteConcurrentLinearCounter<>(M, new SimpleHasher<String>());
      CountTestUtil.observe(counter, N * 2, N);
      assertEquals(N, counter.getCardinality(), CONFIDENCE_9999);
    }
  }

  @Test
  @Ignore
  public void testCardinality_distribution() throws Exception {
    int k = 10000;
    double e = 0d;

    try (PrintWriter writer = new PrintWriter(new FileWriter("cardinality-dist.txt"))) {
      for (int i = 0; i < k; i++) {
        LinearCounter<String> counter = new WriteConcurrentLinearCounter<>(M, new SimpleHasher<String>());
        //LinearCounter<String> counter = new WriteConcurrentLinearCounter<>(M, MurmurHashers.stringHasher());
        CountTestUtil.observeUnique(counter, N);
        double c = counter.getCardinality();

        e += Math.pow(c - N, 2);
        writer.println(String.format("%.2f", c));
      }
    }

    e = Math.sqrt(e / (k - 1));
    System.out.println("Cardinality distribution's std.dev: " + e);
  }

  @Test
  public void testCardinality_withMultipleWriters() throws Exception {
    ExecutorService executor = Executors.newFixedThreadPool(2);
    List<Future<?>> futures = new ArrayList<>();

    futures.add(executor.submit(new Observer(counter, 2000)));
    futures.add(executor.submit(new Observer(counter, 2000)));
    futures.add(executor.submit(new Observer(counter, 2000)));
    futures.add(executor.submit(new Observer(counter, 2000)));

    executor.shutdown();

    for (Future<?> future : futures) {
      future.get();
    }

    assertEquals(N, counter.getCardinality(), CONFIDENCE_9999);
  }

  @Test
  public void testClear() {
    assertEquals(0d, counter.getCardinality(), 0d);

    CountTestUtil.observeUnique(counter, N);
    assertTrue(counter.getCardinality() > 0);

    counter.clear();
    assertEquals(0d, counter.getCardinality(), 0d);
  }

  @Test
  public void testGetBitmap() {
    CountTestUtil.observeUnique(counter, 100);

    int[] bitmap = counter.getBitmap();
    assertTrue(CountUtil.getBitCount(bitmap) > 0);

    Arrays.fill(bitmap, 0);
    assertEquals(0, CountUtil.getBitCount(bitmap));

    // Test that modified copy of the bitmap doesn't affect the counter's state.
    assertTrue(CountUtil.getBitCount(counter.getBitmap()) > 0);
  }

  @Test
  public void testMergeTo() {
    int[] bitmap = new int[M / LinearCounter.BLOCK_SIZE];

    CountTestUtil.observeUnique(counter, 100);
    counter.mergeTo(bitmap);

    assertEquals(CountUtil.getBitCount(counter.getBitmap()), CountUtil.getBitCount(bitmap));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMergeTo_bitmapWithDifferentLength() {
    counter.mergeTo(new int[M / 64]);
  }

  /**
   * An object that imitates the given count of unique observations.
   */
  private static class Observer implements Runnable {
    final LinearCounter<String> counter;
    final int count;

    public Observer(LinearCounter<String> counter, int count) {
      this.counter = counter;
      this.count = count;
    }

    @Override
    public void run() {
      CountTestUtil.observe(counter, count * 2, count);
    }
  }
}
