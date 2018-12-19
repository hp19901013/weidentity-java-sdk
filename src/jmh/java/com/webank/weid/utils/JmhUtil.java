package com.webank.weid.utils;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.webank.weid.service.BaseService;

import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.core.Request;
import org.bcos.web3j.protocol.core.methods.response.EthBlockNumber;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

public class JmhUtil extends BaseService {

    public int getBlockNumber() {
        int blockNumber = 0;
        try {
            blockNumber = super.getWeb3j().ethBlockNumber().send().getBlockNumber().intValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blockNumber;
    }
}
