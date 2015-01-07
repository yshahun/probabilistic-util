package ys.metric;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import ys.probabilistic.util.MurmurHashers;

/**
 * Tests for {@link CardinalityMeter}.
 *
 * @author Yauheni Shahun
 */
public class CardinalityMeterTest {
  /*
   * Number of bits for counting.
   */
  private static final int M = 8 * 1024 * 4;
  /*
   * Error of estimation as fraction of the count of distinct values.
   */
  private static final double ERROR = 0.07d;

  private CardinalityMeter<String> meter;

  @Before
  public void setUp() {
    meter = new CardinalityMeter<>(
        new StringLinearCounterFactory(M, MurmurHashers.stringHasher()));
  }

  @Test
  public void testGetCardinality() {
    observe(1000, 100);
    assertEquals(100d, meter.getCardinality(), 100 * ERROR);

    assertEquals(0d, meter.getCardinality(), 0d);

    observe(1000, 300);
    assertEquals(300d, meter.getCardinality(), 300 * ERROR);
  }

  @Test
  public void testGetCounter() {
    observe(1000, 200);
    assertEquals(200d, meter.getCounter().getCardinality(), 200 * ERROR);

    assertEquals(0d, meter.getCounter().getCardinality(), 0d);

    observe(1000, 450);
    assertEquals(450d, meter.getCounter().getCardinality(), 450 * ERROR);
  }

  private void observe(int count, int uniqueCount) {
    for (String value : generateData(count, uniqueCount)) {
      meter.count(value);
    }
  }

  private static List<String> generateData(int count, int uniqueCount) {
    String[] uniqueData = new String[uniqueCount];
    for (int i = 0; i < uniqueCount; i++) {
      uniqueData[i] = UUID.randomUUID().toString();
    }

    List<String> result = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      result.add(uniqueData[i % uniqueCount]);
    }
    return result;
  }
}
