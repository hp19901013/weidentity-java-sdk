package com.webank.weid;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.webank.weid.base.JmhBase;
import com.webank.weid.base.JmhUtil;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 10)
@Measurement(iterations = 1, time = 300)
public class JmhTestSetServiceMethod extends JmhBase {

    private List<CreateWeIdDataResult> createWeIdDataResultList = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(JmhTestSetServiceMethod.class);
    JmhUtil util = new JmhUtil();
    private int index = 0;

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime})
    public void setService() {
        int i;
        i = (int) (Math.random() * 20000);
        try {
            CreateWeIdDataResult createWeIdResult = createWeIdDataResultList.get(i);
            SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
            ResponseData<Boolean> responseData = weIdService.setService(setServiceArgs);
            logger.info(responseData.toString());
            if (responseData.getErrorCode() != 0) {
                System.out.println(responseData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Setup
    public void initData() {
        BufferedReader br = null;
        try {

            String filePath = "E:\\works\\weidentity-java-sdk\\src\\jmh\\resources\\setServiceData.txt";
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String jsonStr = br.readLine();

            ObjectMapper objectMapper = new ObjectMapper();
            JavaType javaType = objectMapper.getTypeFactory()
                .constructParametricType(ArrayList.class, CreateWeIdDataResult.class);
            createWeIdDataResultList = objectMapper.readValue(jsonStr, javaType);
            System.out.println("init finsh. size:" + createWeIdDataResultList.size());
            System.out.println("test getWeIdDocument start,current block:" + util.getBlockNumber());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @TearDown
    public void tearDown() {
        System.out.println("test getWeIdDocument end,current block:" + util.getBlockNumber());
    }
}
