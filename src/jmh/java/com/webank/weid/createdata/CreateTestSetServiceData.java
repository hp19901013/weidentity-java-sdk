package com.webank.weid.createdata;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.webank.weid.base.JmhBase;
import com.webank.weid.base.JmhUtil;
import com.webank.weid.protocol.response.CreateWeIdDataResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class CreateTestSetServiceData extends JmhBase {

    private List<CreateWeIdDataResult> createWeIdDataResultList = new ArrayList<>();
    private static int maxSize = 20;
    private int initThreadNum = 10;

    @Test
    public void initstart(){

        JmhUtil util = new JmhUtil();
        System.out.println("init data start,current block:" + util.getBlockNumber());
        initsetServiceData();
        System.out.println("init data end,current block:" + util.getBlockNumber());

    }

    public void initsetServiceData() {
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
            FileWriter file = new FileWriter("E:\\setServiceData.txt");
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
