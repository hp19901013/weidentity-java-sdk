/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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

import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import mockit.Mock;
import mockit.MockUp;
import org.bcos.web3j.crypto.Sign;
import org.junit.Assert;
import org.junit.Test;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.common.PasswordKey;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.CredentialWrapper;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.impl.WeIdServiceImpl;
import com.webank.weid.util.JsonUtil;
import com.webank.weid.util.SignatureUtils;

/**
 * verifyCredential method for testing CredentialService.
 *
 * @author v_wbpenghu
 */
public class TestVerifyCredential1 extends TestBaseServcie {

    protected PasswordKey passwordKey = null;

    private static CredentialWrapper credentialWrapper = null;

    @Override
    public void testInit() {
        super.testInit();
        passwordKey = TestBaseUtil.createEcKeyPair();
        credentialWrapper = super.createCredential(createCredentialArgs);
    }

    /**
     * case: verifyCredential success.
     */
    @Test
    public void testVerifyCredentialCase1() {

        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: context is null.
     */
    @Test
    public void testVerifyCredentialCase2() {

        credentialWrapper.getCredential().setContext(null);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_CONTEXT_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: context is other string.
     */
    @Test
    public void testVerifyCredentialCase3() {

        credentialWrapper.getCredential().setContext("xxx");
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: cptId is another.
     */
    @Test
    public void testVerifyCredentialCase4() {

        CptBaseInfo cpt = super.registerCpt(createWeIdResultWithSetAttr, registerCptArgs);
        credentialWrapper.getCredential().setCptId(cpt.getCptId());
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: id is null.
     */
    @Test
    public void testVerifyCredentialCase5() {

        credentialWrapper.getCredential().setId(null);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ID_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: id is another.
     */
    @Test
    public void testVerifyCredentialCase6() {

        credentialWrapper.getCredential().setId("xxxxxxxxx");
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ID_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuer is null.
     */
    @Test
    public void testVerifyCredentialCase7() {

        credentialWrapper.getCredential().setIssuer(null);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuer is xxxxx.
     */
    @Test
    public void testVerifyCredentialCase8() {

        credentialWrapper.getCredential().setIssuer("xxxxxxxxxxx");

        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuer is another.
     */
    @Test
    public void testVerifyCredentialCase9() {

        credentialWrapper.getCredential().setIssuer(createWeIdNew.getWeId());
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuranceDate < = 0.
     */
    @Test
    public void testVerifyCredentialCase10() {

        credentialWrapper.getCredential().setIssuranceDate(-1L);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuranceDate > now.
     */
    @Test
    public void testVerifyCredentialCase11() {

        credentialWrapper.getCredential().setIssuranceDate(System.currentTimeMillis() + 100000);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: expirationDate <= 0.
     */
    @Test
    public void testVerifyCredentialCase12() {

        credentialWrapper.getCredential().setExpirationDate(-1L);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: expirationDate <= now.
     */
    @Test
    public void testVerifyCredentialCase13() {

        credentialWrapper.getCredential().setExpirationDate(System.currentTimeMillis() - 10000);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: expirationDate is null.
     */
    @Test
    public void testVerifyCredentialCase14() {

        credentialWrapper.getCredential().setExpirationDate(null);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuranceDate is null.
     */
    @Test
    public void testVerifyCredentialCase15() {

        credentialWrapper.getCredential().setIssuranceDate(null);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_CREATE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: claim is null.
     */
    // @Test
    public void testVerifyCredentialCase16() {

        credentialWrapper.getCredential().setClaim(null);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }


    /**
     * case: signature is null.
     */
    @Test
    public void testVerifyCredentialCase17() {

        credentialWrapper.getCredential().setSignature(null);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_SIGNATURE_BROKEN.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: signature is xxxxxxxxxxxxxxxxxxx.
     */
    @Test
    public void testVerifyCredentialCase18() {

        credentialWrapper.getCredential().setSignature("xxxxxxxxxxxxxxx");
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: signature by 122324324324.
     */
    @Test
    public void testVerifyCredentialCase19() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());
        createCredentialArgs.getWeIdPrivateKey().setPrivateKey("122324324324");
        CredentialWrapper credentialWrapper = super.createCredential(createCredentialArgs);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuranceDate < now && expirationDate < now  && issuranceDate < expirationDate.
     */
    @Test
    public void testVerifyCredentialCase20() {

        credentialWrapper.getCredential().setIssuranceDate(System.currentTimeMillis() - 12000);
        credentialWrapper.getCredential().setExpirationDate(System.currentTimeMillis() - 10000);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuer is not exists.
     */
    @Test
    public void testVerifyCredentialCase21() {

        credentialWrapper.getCredential().setIssuer("did:weid:0x111111111111111");
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: modify Disclosur
     */
    @Test
    public void testVerifyCredentialCase22() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", 0);
        paramMap.put("gender", 0);
        paramMap.put("age", 1);
        credentialWrapper.setDisclosure(paramMap);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
    }

    /**
     * case:  Disclosur is null
     */
    @Test
    public void testVerifyCredentialCase23() {
        credentialWrapper.setDisclosure(null);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertFalse(response.getResult());
    }

    /**
     * case: credential is null
     */
    @Test
    public void testVerifyCredentialCase24() {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", 0);
        paramMap.put("gender", 0);
        paramMap.put("age", 1);
        credentialWrapper.setCredential(null);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);

        Assert.assertFalse(response.getResult());
    }

    /**
     * case: credentialWrapper is null
     */
    @Test
    public void testVerifyCredentialCase25() {
        credentialWrapper = null;
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertFalse(response.getResult());
    }
}
