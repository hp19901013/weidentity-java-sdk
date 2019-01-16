/*
 *       Copyright© (2018) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.full.credential;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.common.PasswordKey;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.RegisterCptArgs;
import com.webank.weid.protocol.request.VerifyCredentialArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;

/**
 * verifyCredentialWithSpecifiedPubKey method for testing CredentialService.
 * 
 * @author v_wbgyang
 *
 */
@Test(groups = "all")
public class TestVerifyCredentialWithSpecifiedPubKey extends TestBaseServcie {

    private static final Logger logger =
        LoggerFactory.getLogger(TestVerifyCredentialWithSpecifiedPubKey.class);

    private static CptBaseInfo cptBaseInfo = null;
    private static CreateWeIdDataResult createWeIdResultWithSetAttr = null;
    private static RegisterCptArgs registerCptArgs = null;

    @Override
    public synchronized void testInit() {
        super.testInit();
        if (null == createWeIdResultWithSetAttr) {
            createWeIdResultWithSetAttr = this.createWeIdWithSetAttr();
        }
        if(null == cptBaseInfo || null == registerCptArgs){
            registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeIdResultWithSetAttr);
            cptBaseInfo = super.registerCpt(createWeIdResultWithSetAttr, registerCptArgs);
        }
    }

    /**
     * case: verifyCredentialWithSpecifiedPubKey success.
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase1() {
        PasswordKey newPasswordKey = TestBaseUtil.createEcKeyPair();

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(newPasswordKey.getPrivateKey());

        Credential credential = super.createCredential(createCredentialArgs);

        VerifyCredentialArgs verifyCredentialArgs =
            TestBaseUtil.buildVerifyCredentialArgs(credential, newPasswordKey.getPublicKey());

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(verifyCredentialArgs);
        logger.info("verifyCredentialWithSpecifiedPubKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: verifyCredentialWithSpecifiedPubKey fail.
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase2() {
        PasswordKey newPasswordKey = TestBaseUtil.createEcKeyPair();

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(newPasswordKey.getPrivateKey());

        Credential credential = super.createCredential(createCredentialArgs);

        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();

        VerifyCredentialArgs verifyCredentialArgs =
            TestBaseUtil.buildVerifyCredentialArgs(credential, passwordKey.getPublicKey());

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(verifyCredentialArgs);
        logger.info("verifyCredentialWithSpecifiedPubKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_SIGNATURE_BROKEN.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: verifyCredentialWithSpecifiedPubKey publicKey is null.
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase3() {
        PasswordKey newPasswordKey = TestBaseUtil.createEcKeyPair();

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(newPasswordKey.getPrivateKey());

        Credential credential = super.createCredential(createCredentialArgs);

        VerifyCredentialArgs verifyCredentialArgs =
            TestBaseUtil.buildVerifyCredentialArgs(credential, null);

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(verifyCredentialArgs);
        logger.info("verifyCredentialWithSpecifiedPubKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: verifyCredentialArgs is null.
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase4() {

        VerifyCredentialArgs verifyCredentialArgs = null;

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(verifyCredentialArgs);
        logger.info("verifyCredentialWithSpecifiedPubKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: credential is null.
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase5() {
        PasswordKey newPasswordKey = TestBaseUtil.createEcKeyPair();

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(newPasswordKey.getPrivateKey());

        Credential credential = super.createCredential(createCredentialArgs);

        VerifyCredentialArgs verifyCredentialArgs =
            TestBaseUtil.buildVerifyCredentialArgs(credential, newPasswordKey.getPublicKey());
        verifyCredentialArgs.setCredential(null);

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(verifyCredentialArgs);
        logger.info("verifyCredentialWithSpecifiedPubKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: weIdPublicKey is null.
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase6() {
        PasswordKey newPasswordKey = TestBaseUtil.createEcKeyPair();

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(newPasswordKey.getPrivateKey());

        Credential credential = super.createCredential(createCredentialArgs);

        VerifyCredentialArgs verifyCredentialArgs =
            TestBaseUtil.buildVerifyCredentialArgs(credential, newPasswordKey.getPublicKey());
        verifyCredentialArgs.setWeIdPublicKey(null);

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(verifyCredentialArgs);
        logger.info("verifyCredentialWithSpecifiedPubKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: verifyCredentialWithSpecifiedPubKey publicKey is "xxxxxxxxxxxxx".
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase7() {
        PasswordKey newPasswordKey = TestBaseUtil.createEcKeyPair();

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(newPasswordKey.getPrivateKey());

        Credential credential = super.createCredential(createCredentialArgs);

        VerifyCredentialArgs verifyCredentialArgs =
            TestBaseUtil.buildVerifyCredentialArgs(credential, "xxxxxxxxxxxxx");

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(verifyCredentialArgs);
        logger.info("verifyCredentialWithSpecifiedPubKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
