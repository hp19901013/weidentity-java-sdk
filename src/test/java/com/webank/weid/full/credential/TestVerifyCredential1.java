package com.webank.weid.full.credential;

import com.webank.weid.full.TestBaseServcie;

public class TestVerifyCredential1 extends TestBaseServcie {

    @Override
    public void testInit() {
        super.testInit();
        if (cptBaseInfo == null) {
            cptBaseInfo = super.registerCpt(createWeIdResultWithSetAttr);
        }
    }
}
