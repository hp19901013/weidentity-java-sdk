package com.webank.weid;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.base.JmhBase;
import com.webank.weid.base.JmhUtil;
import com.webank.weid.common.BeanUtil;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.impl.CredentialServiceImpl;

@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 10)
@Measurement(iterations = 1, time = 300)
public class JmhTestVerifyCredentialMethod extends JmhBase {

    private static final Logger logger = LoggerFactory.getLogger(JmhTestVerifyCredentialMethod.class);

    private List<Credential> credentialList = new ArrayList<Credential>();
    private int index = 0;
    JmhUtil util = new JmhUtil();

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime})
//    @Test
    public void verifyCredential() throws Exception {

        int i = (int) (Math.random() * 2000);
        Credential credential = credentialList.get(i);
        ResponseData<Boolean> response = credentialService.verifyCredential(credential);
        logger.info(response.toString());
        if (!response.getResult()) {
            BeanUtil.print(credential);
            BeanUtil.print(
                "randomIndex:" + i + "result:" + response.getResult() + ",message:"
                    + response.getErrorMessage() + ",code:" + response.getErrorCode());
            System.out.println("-------------------");
        }
    }

    @Setup
    public void initData() {
        BufferedReader br = null;
        try {
            String filePath = "E:\\works\\jmh\\weidentity-java-sdk-master\\src\\jmh\\resources\\verifyCredentialData.txt";
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String jsonStr = br.readLine();

            ObjectMapper objectMapper = new ObjectMapper();
            JavaType javaType = objectMapper.getTypeFactory()
                .constructParametricType(ArrayList.class, Credential.class);
            credentialList = objectMapper.readValue(jsonStr, javaType);
//            System.out.println(credentialList.get(0));
            System.out.println("init finsh. size:" + credentialList.size());
            System.out
                .println("test verifyCredential start,current block:" + util.getBlockNumber());
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
        System.out.println("test verifyCredential end,current block:" + util.getBlockNumber());
    }
}
