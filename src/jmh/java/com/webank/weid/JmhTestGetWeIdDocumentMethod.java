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
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 10)
@Measurement(iterations = 1, time = 300)
public class JmhTestGetWeIdDocumentMethod extends JmhBase {

    private List<CreateWeIdDataResult> createWeIdDataResultList = new ArrayList<>();
    private static final Logger logger = LoggerFactory
        .getLogger(JmhTestGetWeIdDocumentMethod.class);
    private List<Credential> credentialList = new ArrayList<Credential>();
    private int index = 0;
    JmhUtil util = new JmhUtil();

    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    public void getWeIdDocument() throws Exception {
        int i = (int) (Math.random() * 2000);
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
            String filePath = "E:\\works\\jmh\\weidentity-java-sdk-master\\src\\jmh\\resources\\verifyCredentialData.txt";
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
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
//            System.out.println(credentialList.get(0));
            System.out.println("init finsh. size:" + credentialList.size());
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

/*    @Setup
    public void initData() {
        BufferedReader br = null;
        try {

            String filePath = "E:\\works\\jmh\\weidentity-java-sdk-master\\src\\jmh\\resources\\getWeIdData.txt";
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String jsonStr = br.readLine();

            ObjectMapper objectMapper = new ObjectMapper();
            JavaType javaType = objectMapper.getTypeFactory()
                .constructParametricType(ArrayList.class, CreateWeIdDataResult.class);
            createWeIdDataResultList = objectMapper.readValue(jsonStr, javaType);
            System.out.println("init finsh. size:" + createWeIdDataResultList.size());
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
        System.out.println("test getWeIdDocument start,current block:" + util.getBlockNumber());
    }*/

    @TearDown
    public void tearDown() {
        System.out.println("test getWeIdDocument end,current block:" + util.getBlockNumber());
    }
}
