package com.webank.weid;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class JmhMain {

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
            .include("JmhTestGetWeIdDocumentMethod")
            .warmupIterations(1)
            .threads(1)
            .forks(1)
            .measurementIterations(1)
            .measurementTime(new TimeValue(10, TimeUnit.SECONDS))
            .mode(Mode.AverageTime)
            .mode(Mode.Throughput)
            .build();

        new Runner(opt).run();
    }
}
