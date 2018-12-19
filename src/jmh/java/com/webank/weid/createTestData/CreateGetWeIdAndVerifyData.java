package com.webank.weid.createTestData;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.webank.weid.base.JmhBase;
import com.webank.weid.utils.FileUtil;
import com.webank.weid.utils.JmhUtil;
import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.RegisterCptArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.utils.PropertiesUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateGetWeIdAndVerifyData extends JmhBase {

    private static List<Credential> credentialList = new ArrayList<Credential>();
    private static int maxSize;
    private static int initThreadNum;

    public static void main(String args[]) {
        JmhUtil util = new JmhUtil();
        System.out.println("init data start,current block:" + util.getBlockNumber());

        maxSize = Integer.parseInt(PropertiesUtils.getProperty("getWeIdAndVerifyDataSize", "1000"));
        initThreadNum = Integer
            .parseInt(PropertiesUtils.getProperty("createGetWeIdAndVerifyThread", "10"));
        System.out.println(maxSize +"----"+ initThreadNum);
        initVerifyCredentialData();
        System.out.println("init data end,current block:" + util.getBlockNumber());
        System.exit(0);

    }

    public static void initVerifyCredentialData() {
        for (int i = 0; i < initThreadNum; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (credentialList.size() < maxSize) {
                        try {
                            credentialList.add(createCerifyData());
                        } catch (Exception e) {
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
            String projectPath = FileUtil.getProjectPath();
            File fileDir = new File(projectPath + "../jmhTestData");
            if(!fileDir.exists() && !fileDir.isDirectory()){
                fileDir.mkdir();
            }
            FileWriter file = new FileWriter(projectPath + "../jmhTestData/createGetWeIdAndVerifyData.json");
            file.write(writeValueAsString);
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Credential createCerifyData() throws Exception {

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
