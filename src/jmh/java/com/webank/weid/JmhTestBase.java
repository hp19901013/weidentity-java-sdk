package com.webank.weid;

import java.io.IOException;

import com.webank.weid.base.JmhBase;

import org.openjdk.jmh.annotations.*;

@State(value = Scope.Benchmark)
public class JmhTestBase {

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    public void test() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("1");
    }
}
