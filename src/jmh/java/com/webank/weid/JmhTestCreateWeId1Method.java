package com.webank.weid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.webank.weid.base.JmhBase;
import com.webank.weid.base.JmhUtil;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 1, time = 10)
@Measurement(iterations = 1, time = 10 , timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class JmhTestCreateWeId1Method extends JmhBase {

    JmhUtil util = new JmhUtil();

    @Setup
    public void setup() throws Exception {
        System.out.println("test createWeId start,current block:" + util.getBlockNumber());
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    public void testCreateWeIdMethod1() {
        ResponseData<CreateWeIdDataResult> weId = weIdService.createWeId();
        if (weId.getErrorCode() != 0) {
            System.out.println(weId);
        }
    }

    @TearDown
    public void tearDown() {
        System.out.println("test createWeId end,current block:" + util.getBlockNumber());
    }



}
