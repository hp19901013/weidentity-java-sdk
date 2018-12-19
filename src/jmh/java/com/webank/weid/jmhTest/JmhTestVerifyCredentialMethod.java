package com.webank.weid.jmhTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.base.JmhBase;
import com.webank.weid.utils.JmhUtil;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.utils.PropertiesUtils;

@State(value = Scope.Benchmark)
public class JmhTestVerifyCredentialMethod extends JmhBase {

    private static final Logger logger = LoggerFactory.getLogger(JmhTestVerifyCredentialMethod.class);

    private List<Credential> credentialList = new ArrayList<Credential>();
    private int index = 0;
    JmhUtil util = new JmhUtil();

    @Benchmark
    public void verifyCredential() throws Exception {
        int maxSize = Integer.parseInt(
            PropertiesUtils.getProperty("setServiceDataSize", "1000"));
        int i = (int) (Math.random() * maxSize);
        Credential credential = credentialList.get(i);
        ResponseData<Boolean> response = credentialService.verifyCredential(credential);
        logger.info(response.toString());
        if (!response.getResult()) {
            System.out.println(response);
        }
    }

    @Setup
    public void initData() {
        BufferedReader br = null;
        try {
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("../jmhTestData/createGetWeIdAndVerifyData.json");
            br = new BufferedReader(new InputStreamReader(resourceAsStream));
            br = new BufferedReader(new InputStreamReader(resourceAsStream));
            String jsonStr = br.readLine();

            ObjectMapper objectMapper = new ObjectMapper();
            JavaType javaType = objectMapper.getTypeFactory()
                .constructParametricType(ArrayList.class, Credential.class);
            credentialList = objectMapper.readValue(jsonStr, javaType);
            System.out.println("test verifyCredential start,current block:" + util.getBlockNumber());
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
