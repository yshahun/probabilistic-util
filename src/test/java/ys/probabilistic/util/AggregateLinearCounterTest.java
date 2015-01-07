package ys.probabilistic.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link AggregateLinearCounter}.
 *
 * @author Yauheni Shahun
 */
public class AggregateLinearCounterTest {
  /*
   * Number of bits for counting.
   */
  private static final int M = 8 * 1024 * 8;
  /*
   * Error of estimation as fraction of the count of distinct values.
   */
  private static final double ERROR = 0.07d;

  private AggregateLinearCounter<String> counter;

  @Before
  public void setUp() {
    counter = new AggregateLinearCounter<>(M);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testCount() {
    counter.count("foo");
  }

  @Test
  public void testCardinality() {
    LinearCounter<String> c1 = new WriteConcurrentLinearCounter<>(M, new SimpleHasher<String>());
    LinearCounter<String> c2 = new WriteConcurrentLinearCounter<>(M, new SimpleHasher<String>());

    CountTestUtil.observe(c1, 500, 200);
    CountTestUtil.observe(c2, 810, 450);

    c1.mergeTo(counter.getBitmap());
    c2.mergeTo(counter.getBitmap());

    assertEquals(650d, counter.getCardinality(), 650 * ERROR);
  }

  @Test
  public void testClear() {
    assertEquals(0d, counter.getCardinality(), 0d);

    LinearCounter<String> c1 = new WriteConcurrentLinearCounter<>(M, new SimpleHasher<String>());
    CountTestUtil.observeUnique(c1, 100);
    c1.mergeTo(counter.getBitmap());

    assertTrue(counter.getCardinality() > 0);

    counter.clear();
    assertEquals(0d, counter.getCardinality(), 0d);
  }

  @Test
  public void testGetBitmap() {
    assertEquals(0, CountUtil.getBitCount(counter.getBitmap()));

    LinearCounter<String> c1 = new WriteConcurrentLinearCounter<>(M, new SimpleHasher<String>());
    CountTestUtil.observeUnique(c1, 100);
    c1.mergeTo(counter.getBitmap());

    assertEquals(CountUtil.getBitCount(c1.getBitmap()), CountUtil.getBitCount(counter.getBitmap()));
  }

  @Test
  public void testMergeTo() {
    LinearCounter<String> c1 = new WriteConcurrentLinearCounter<>(M, new SimpleHasher<String>());
    CountTestUtil.observeUnique(c1, 100);
    c1.mergeTo(counter.getBitmap());

    int[] bitmap = new int[M / LinearCounter.BLOCK_SIZE];
    counter.mergeTo(bitmap);

    assertEquals(CountUtil.getBitCount(counter.getBitmap()), CountUtil.getBitCount(bitmap));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMergeTo_bitmapWithDifferentLength() {
    counter.mergeTo(new int[0]);
  }
}
