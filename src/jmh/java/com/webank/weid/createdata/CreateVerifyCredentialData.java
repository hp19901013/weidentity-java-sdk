package com.webank.weid.createdata;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.webank.weid.base.JmhBase;
import com.webank.weid.base.JmhUtil;
import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.RegisterCptArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class CreateVerifyCredentialData extends JmhBase {

    private List<Credential> credentialList = new ArrayList<Credential>();
    private static int maxSize = 2000;
    private int initThreadNum = 10;

    @Test
    public void initStart() {

        JmhUtil util = new JmhUtil();
        System.out.println("init data start,current block:" + util.getBlockNumber());
        initVerifyCredentialData();
        System.out.println("init data end,current block:" + util.getBlockNumber());

    }


    public void initVerifyCredentialData() {
        for (int i = 0; i < initThreadNum; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (credentialList.size() < maxSize) {
                        try {
                            credentialList.add(createCerifyData());
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (credentialList.size() >= maxSize) {
                break;
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            String writeValueAsString = mapper.writeValueAsString(credentialList);
            FileWriter file = new FileWriter("E:\\verifyCredentialData.txt");
            file.write(writeValueAsString);
            file.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Credential createCerifyData() throws Exception {

        CreateWeIdDataResult createWeIdResult = weIdService.createWeId().getResult();

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        weIdService.setPublicKey(setPublicKeyArgs);

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        weIdService.setAuthentication(setAuthenticationArgs);

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        weIdService.setService(setServiceArgs);

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeIdResult);
        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            BeanUtil.print(registerCptArgs);
            BeanUtil.print(response);
        }
        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResult, response.getResult());
        return credentialService.createCredential(createCredentialArgs).getResult();

    }

}
