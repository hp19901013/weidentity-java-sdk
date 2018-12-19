package com.webank.weid.jmhTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.webank.weid.base.JmhBase;
import com.webank.weid.utils.JmhUtil;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.utils.PropertiesUtils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@State(value = Scope.Benchmark)
public class JmhTestGetWeIdDocumentMethod extends JmhBase {
    int maxSize = Integer.parseInt(
        PropertiesUtils.getProperty("getWeIdAndVerifyDataSize", "1000"));
    private List<CreateWeIdDataResult> createWeIdDataResultList = new ArrayList<>();
    private static final Logger logger = LoggerFactory
        .getLogger(JmhTestGetWeIdDocumentMethod.class);
    private List<Credential> credentialList = new ArrayList<Credential>();
    private int index = 0;
    JmhUtil util = new JmhUtil();

    @Benchmark
    public void getWeIdDocument() throws Exception {
        int i = (int) (Math.random() * maxSize);
        ResponseData<WeIdDocument> weIdDocument = weIdService
            .getWeIdDocument(createWeIdDataResultList.get(i).getWeId());
        logger.info(weIdDocument.toString());
        if (weIdDocument.getErrorCode() != 0) {
            System.out.println(weIdDocument);
        }
    }

    @Setup
    public void initData() {
        BufferedReader br = null;
        try {
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("../jmhTestData/createGetWeIdAndVerifyData.json");
            br = new BufferedReader(new InputStreamReader(resourceAsStream));
            String jsonStr = br.readLine();

            ObjectMapper objectMapper = new ObjectMapper();
            JavaType javaType = objectMapper.getTypeFactory()
                .constructParametricType(ArrayList.class, Credential.class);
            credentialList = objectMapper.readValue(jsonStr, javaType);
            for (Credential c : credentialList) {
                String issuer = c.getIssuer();
                CreateWeIdDataResult createWeIdDataResult = new CreateWeIdDataResult();
                createWeIdDataResult.setWeId(issuer);
                createWeIdDataResultList.add(createWeIdDataResult);
            }
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
