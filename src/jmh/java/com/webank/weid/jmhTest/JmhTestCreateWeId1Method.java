package com.webank.weid.jmhTest;

import com.webank.weid.base.JmhBase;
import com.webank.weid.utils.JmhUtil;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(value = Scope.Benchmark)
public class JmhTestCreateWeId1Method extends JmhBase {

    JmhUtil util = new JmhUtil();

    @Setup
    public void setup() throws Exception {
        System.out.println("test createWeId start,current block:" + util.getBlockNumber());
    }

    @Benchmark
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
