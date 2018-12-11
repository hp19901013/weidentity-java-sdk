package com.webank.weid;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class JmhMain {

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        Options opt = new OptionsBuilder()
            .include("JmhTestCreateWeId1Method")
            .warmupIterations(1) //预热次数
            .threads(1)
            .measurementIterations(1) //真正执行次数
            .measurementTime(new TimeValue(10, TimeUnit.SECONDS))
            .build();

        new Runner(opt).run();
    }
}
