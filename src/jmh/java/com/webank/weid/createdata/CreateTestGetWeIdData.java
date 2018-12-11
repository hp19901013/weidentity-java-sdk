package com.webank.weid.createdata;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.webank.weid.base.JmhBase;
import com.webank.weid.base.JmhUtil;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class CreateTestGetWeIdData extends JmhBase {

    private List<CreateWeIdDataResult> createWeIdDataResultList = new ArrayList<>();
    private static int maxSize = 2000;
    private int initThreadNum = 10;

    @Test
    public void initstart() {
        JmhUtil util = new JmhUtil();
        System.out.println("init data start,current block:" + util.getBlockNumber());
        initCreateWeIdData();
        System.out.println("init data end,current block:" + util.getBlockNumber());
    }

    public void initCreateWeIdData() {
        for (int i = 0; i <= initThreadNum; i++) {
            new Thread(() -> {
                while (createWeIdDataResultList.size() < maxSize) {
                    try {
                        CreateWeIdDataResult data = createData();
                        createWeIdDataResultList.add(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        while (createWeIdDataResultList.size() < maxSize) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter file = new FileWriter("E:\\getWeIdData.txt");
            ObjectMapper mapper = new ObjectMapper();
            String s = mapper.writeValueAsString(createWeIdDataResultList);
            file.write(s);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public CreateWeIdDataResult createData() throws Exception {
        CreateWeIdDataResult createWeIdResult1 = weIdService.createWeId().getResult();
        CreateWeIdDataResult createWeIdResult2 = weIdService.createWeId().getResult();

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult2);
        setPublicKeyArgs.setPublicKey(createWeIdResult2.getUserWeIdPublicKey().getPublicKey());
        setPublicKeyArgs.setOwner(createWeIdResult2.getWeId());
        weIdService.setPublicKey(setPublicKeyArgs);

        setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult2);
        setPublicKeyArgs.setPublicKey(TestBaseUtil.createEcKeyPair()[0]);
        setPublicKeyArgs.setOwner(createWeIdResult2.getWeId());
        weIdService.setPublicKey(setPublicKeyArgs);

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult2);
        setAuthenticationArgs.setOwner(createWeIdResult2.getWeId());
        setAuthenticationArgs.setPublicKey(createWeIdResult2.getUserWeIdPublicKey().getPublicKey());
        weIdService.setAuthentication(setAuthenticationArgs);

        setAuthenticationArgs = TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult1);
        setAuthenticationArgs.setOwner(createWeIdResult1.getWeId());
        setAuthenticationArgs.setPublicKey(TestBaseUtil.createEcKeyPair()[0]);
        weIdService.setAuthentication(setAuthenticationArgs);

        SetServiceArgs setServiceArgs1 = TestBaseUtil.buildSetServiceArgs(createWeIdResult1);
        SetServiceArgs setServiceArgs2 = TestBaseUtil.buildSetServiceArgs(createWeIdResult2);

        weIdService.setService(setServiceArgs1);
        weIdService.setService(setServiceArgs2);

        return createWeIdResult2;
    }
}
