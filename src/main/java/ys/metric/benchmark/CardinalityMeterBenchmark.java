package ys.metric.benchmark;

import java.util.Random;
import java.util.UUID;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import ys.metric.CardinalityMeter;
import ys.metric.StringLinearCounterFactory;
import ys.probabilistic.util.MurmurHashers;

public class CardinalityMeterBenchmark {

  private static final int COUNTER_SIZE = 8 * 1024 * 8; // 8 Kb

  @State(Scope.Group)
  public static class GroupState {
    final CardinalityMeter<String> meter = new CardinalityMeter<>(
        new StringLinearCounterFactory(COUNTER_SIZE, MurmurHashers.stringHasher()));
  }

  @State(Scope.Thread)
  public static class Data {
    final String[] data = generateData(10000, 1000);
    int index;

    public String nextValue() {
      return data[index++ % data.length];
    }
  }

  @Benchmark
  @Group("one")
  @GroupThreads(1)
  public void count_1(GroupState state, Data data) {
    state.meter.count(data.nextValue());
  }

  @Benchmark
  @Group("two")
  @GroupThreads(2)
  public void count_2(GroupState state, Data data) {
    state.meter.count(data.nextValue());
  }

  /*@Benchmark
  @Group("two")
  @GroupThreads(1)
  public double cardinality_2(GroupState state) {
    return state.meter.getCardinality();
  }*/

  @Benchmark
  @Group("four")
  @GroupThreads(4)
  public void count_4(GroupState state, Data data) {
    state.meter.count(data.nextValue());
  }

  private static String[] generateData(int count, int uniqueCount) {
    String[] uniqueData = new String[uniqueCount];
    for (int i = 0; i < uniqueCount; i++) {
      uniqueData[i] = UUID.randomUUID().toString();
    }

    String[] data = new String[count];

    Random random = new Random();
    for (int i = 0; i < count; i++) {
      data[i] = uniqueData[random.nextInt(uniqueCount)];
    }

    return data;
  }
}
