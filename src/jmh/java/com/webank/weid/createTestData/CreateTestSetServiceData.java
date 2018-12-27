package com.webank.weid.createTestData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.webank.weid.base.JmhBase;
import com.webank.weid.utils.FileUtil;
import com.webank.weid.utils.JmhUtil;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.utils.PropertiesUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateTestSetServiceData extends JmhBase {

    private static List<CreateWeIdDataResult> createWeIdDataResultList = new ArrayList<>();
    private static int maxSize;
    private static int initThreadNum;

    public static void main(String args[]) {

        JmhUtil util = new JmhUtil();
        System.out.println("init data start,current block:" + util.getBlockNumber());
        maxSize = Integer.parseInt(
            PropertiesUtils.getProperty("setServiceDataSize", "1000"));
        initThreadNum = Integer
            .parseInt(PropertiesUtils.getProperty("createSetServiceThread", "10"));

        initsetServiceData();
        System.out.println("init data end,current block:" + util.getBlockNumber());
        System.exit(0);
    }

    public static void initsetServiceData() {
        for (int i = 0; i <= initThreadNum; i++) {
            new Thread(() -> {
                while (true) {
                    createWeIdDataResultList.add(weIdService.createWeId().getResult());
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
            String projectPath = FileUtil.getProjectPath();
            File fileDir = new File(projectPath + "../jmhTestData");
            if(!fileDir.exists() && !fileDir.isDirectory()){
                fileDir.mkdir();
            }
            FileWriter file = new FileWriter(projectPath + "../jmhTestData/setServiceData.json");
            ObjectMapper mapper = new ObjectMapper();
            String s = mapper.writeValueAsString(createWeIdDataResultList);
            file.write(s);
            System.out.println(createWeIdDataResultList.size());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
