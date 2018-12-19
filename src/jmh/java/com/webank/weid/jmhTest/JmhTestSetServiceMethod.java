package com.webank.weid.jmhTest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.webank.weid.base.JmhBase;
import com.webank.weid.utils.JmhUtil;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.utils.PropertiesUtils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjdk.jmh.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@State(value = Scope.Benchmark)
public class JmhTestSetServiceMethod extends JmhBase {

    private List<CreateWeIdDataResult> createWeIdDataResultList = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(JmhTestSetServiceMethod.class);
    JmhUtil util = new JmhUtil();
    private int index = 0;

    @Benchmark
    public void setService() {
        int maxSize = Integer.parseInt(
            PropertiesUtils.getProperty("setServiceDataSize", "1000"));
        int i = (int) (Math.random() * maxSize);
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
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("../jmhTestData/setServiceData.json");
            br = new BufferedReader(new InputStreamReader(resourceAsStream));
            String jsonStr = br.readLine();
            ObjectMapper objectMapper = new ObjectMapper();
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, CreateWeIdDataResult.class);
            createWeIdDataResultList = objectMapper.readValue(jsonStr, javaType);
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
