package com.webank.weid;

import org.openjdk.jmh.annotations.*;

@State(value = Scope.Benchmark)
public class JmhTest2Base {

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    public void test() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("2");
    }
}
