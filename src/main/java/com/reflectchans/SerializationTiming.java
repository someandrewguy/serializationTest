package com.reflectchans;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import com.reflectchans.TestValueJava.TestValueJavaType;
import com.reflectchans.generated.TestValueProtos.TestValue;
import com.reflectchans.generated.TestValueProtos.TestValue.TestType;

/*
java -jar target/benchmarks.jar -f 1 -wi 3 -i 10 -t 1

# Run complete. Total time: 00:00:27

Benchmark                               Mode  Cnt   Score   Error  Units
SerializationTiming.serializeAProtobuf  avgt   10  80.300 ± 3.637  ns/op
SerializationTiming.serializeBJava      avgt   10  45.940 ± 2.438  ns/op
*/
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SerializationTiming {

	private static final int VALUES_COUNT = 1_000_000;
	private static final int CREATED_COUNT = 1_000;
	private static final List<TestValueJava> TEST_VALUE_JAVAS = new ArrayList<>();
	private static final List<TestValue> TEST_VALUES = new ArrayList<>();

	@Setup
	public void setup() {
		// pre populate object lists so that creation is not part of
		// serialization
		TEST_VALUE_JAVAS.clear();
		for (int i = 0; i < CREATED_COUNT; i++) {
			TestValueJava tvj = new TestValueJava();
			tvj.setId(i);
			tvj.setName(Integer.toString(i));
			tvj.setType(getJavaTypeFromNumber(i));
			TEST_VALUE_JAVAS.add(tvj);
		}
		TEST_VALUES.clear();
		for (int i = 0; i < CREATED_COUNT; i++) {
			TestValue.Builder tvp = TestValue.newBuilder();
			tvp.setId(i);
			tvp.setName(Integer.toString(i));
			tvp.setType(getProtoTypeFromNumber(i));
			TEST_VALUES.add(tvp.build());
		}
	}

	@Benchmark
	@OperationsPerInvocation(VALUES_COUNT)
	public void serializeBJava() {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(baos);) {
			for (int i = 0; i < VALUES_COUNT; i++) {
				out.writeObject(TEST_VALUE_JAVAS.get(i % CREATED_COUNT));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Benchmark
	@OperationsPerInvocation(VALUES_COUNT)
	public void serializeAProtobuf() {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(baos);) {
			for (int i = 0; i < VALUES_COUNT; i++) {
				TEST_VALUES.get(i % CREATED_COUNT).writeTo(out);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private TestValueJavaType getJavaTypeFromNumber(int i) {
		switch (i % 3) {
		case 0:
			return TestValueJavaType.ZERO;
		case 1:
			return TestValueJavaType.ONE;
		default:
			return TestValueJavaType.TWO;
		}
	}

	private TestType getProtoTypeFromNumber(int i) {
		switch (i % 3) {
		case 0:
			return TestType.ZERO;
		case 1:
			return TestType.ONE;
		default:
			return TestType.TWO;
		}
	}

}
