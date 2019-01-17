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

package com.webank.weid.testcase.credential;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.RegisterCptArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.impl.CredentialServiceImpl;
import com.webank.weid.service.impl.WeIdServiceImpl;
import com.webank.weid.testcase.TestBaseServcie;
import com.webank.weid.testcase.TestBaseUtil;
import com.webank.weid.testcase.TestData;
import com.webank.weid.utils.PasswordKey;

import mockit.Mock;
import mockit.MockUp;
import org.junit.Assert;
import org.testng.annotations.Test;

/**
 * verifyCredential method for testing CredentialService.
 *
 * @author v_wbgyang
 */
@Test(groups = "all")
public class TestVerifyCredential extends TestBaseServcie {

    private static CreateCredentialArgs createCredentialArgs = null;
    private static CptBaseInfo cptBaseInfo = null;
    private static CreateWeIdDataResult createWeIdResultWithSetAttr = null;
    private static Credential credentialInfo = null;
    private static RegisterCptArgs registerCptArgs = null;

    public synchronized void testInit() {
        super.testInit();
        if (null == createWeIdResultWithSetAttr) {
            createWeIdResultWithSetAttr = super.createWeIdWithSetAttr();
        }
        if (null == cptBaseInfo || null == createCredentialArgs || null == credentialInfo
            || null == registerCptArgs) {
            registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeIdResultWithSetAttr);
            createCredentialArgs = TestBaseUtil
                .buildCreateCredentialArgs(createWeIdResultWithSetAttr);
            cptBaseInfo = super.registerCpt(createWeIdResultWithSetAttr, registerCptArgs);
            createCredentialArgs.setCptId(cptBaseInfo.getCptId());
            credentialInfo = super.createCredential(createCredentialArgs);
        }
    }

    /**
     * case: verifyCredential success.
     */
    @Test
    public void testVerifyCredentialCase1() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);
        ResponseData<Boolean> response = super.verifyCredential(credential);
        System.out.println("testVerifyCredentialCase1实例" + response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: context is null.
     */
    @Test
    public void testVerifyCredentialCase2() {

        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setContext(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CONTEXT_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: context is other string.
     */
    @Test
    public void testVerifyCredentialCase3() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setContext("xxx");

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: cptId is null.
     */
    @Test
    public void testVerifyCredentialCase4() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setCptId(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: cptId is minus number.
     */
    @Test
    public void testVerifyCredentialCase5() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setCptId(-1);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: cptId is not exists.
     */
    @Test
    public void testVerifyCredentialCase6() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setCptId(10000);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: cptId is another.
     */
    @Test
    public void testVerifyCredentialCase7() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        CptBaseInfo cpt = super.registerCpt(createWeIdResultWithSetAttr, registerCptArgs);
        System.out.println("cpt结果:" + cpt);
        credential.setCptId(cpt.getCptId());

        ResponseData<Boolean> response = super.verifyCredential(credential);
        System.out.println("credential参数:" + credential);
        System.out.println("verifyCredential结果:" + response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: id is null.
     */
    @Test
    public void testVerifyCredentialCase8() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setId(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ID_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: id is another.
     */
    @Test
    public void testVerifyCredentialCase9() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setId("xxxxxxxxx");

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuer is null.
     */
    @Test
    public void testVerifyCredentialCase10() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setIssuer(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuer is xxxxx.
     */
    @Test
    public void testVerifyCredentialCase11() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setIssuer("xxxxxxxxxxx");

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuer is another.
     */
    @Test
    public void testVerifyCredentialCase12() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setIssuer(createWeIdNew.getWeId());

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuranceDate < = 0.
     */
    @Test
    public void testVerifyCredentialCase13() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setIssuranceDate(-1L);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuranceDate > now.
     */
    @Test
    public void testVerifyCredentialCase14() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setIssuranceDate(System.currentTimeMillis() + 100000);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: expirationDate <= 0.
     */
    @Test
    public void testVerifyCredentialCase15() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setExpirationDate(-1L);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: expirationDate <= now.
     */
    @Test
    public void testVerifyCredentialCase16() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setExpirationDate(System.currentTimeMillis() - 10000);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: expirationDate is null.
     */
    @Test
    public void testVerifyCredentialCase17() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setExpirationDate(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuranceDate is null.
     */
    @Test
    public void testVerifyCredentialCase18() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setIssuranceDate(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CREATE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: claim is null.
     */
    @Test
    public void testVerifyCredentialCase19() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setClaim(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: claim is xxxxxxxxxx.
     */
    @Test
    public void testVerifyCredentialCase20() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setClaim("xxxxxxxxxxxxxx");

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: claim does not match jsonSchema.
     */
    @Test
    public void testVerifyCredentialCase21() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setClaim(TestData.schemaDataInvalid);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: signature is null.
     */
    @Test
    public void testVerifyCredentialCase22() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setSignature(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_SIGNATURE_BROKEN.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: signature is xxxxxxxxxxxxxxxxxxx.
     */
    @Test
    public void testVerifyCredentialCase23() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setSignature("xxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: signature by 122324324324.
     */
    @Test
    public void testVerifyCredentialCase24() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);

        CreateCredentialArgs createCredentialArgsNew =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr);
        createCredentialArgsNew.setCptId(cptBaseInfo.getCptId());

        createCredentialArgsNew.getWeIdPrivateKey().setPrivateKey("122324324324");
        Credential credential = super.createCredential(createCredentialArgsNew);
        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: signature by non WeIdentity DID publickeys.
     */
    @Test
    public void testVerifyCredentialCase25() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        CreateCredentialArgs createCredentialArgsNew =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr);
        createCredentialArgsNew.setCptId(cptBaseInfo.getCptId());
        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();

        createCredentialArgsNew.getWeIdPrivateKey().setPrivateKey(passwordKey.getPrivateKey());
        Credential credential = super.createCredential(createCredentialArgsNew);

        ResponseData<Boolean> response = super.verifyCredential(credential);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Sing through another private key in publickeys of WeIdentity DID.
     */
    public void testVerifyCredentialCase26() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();

        super.setPublicKey(
            createWeIdResultWithSetAttr,
            passwordKey.getPublicKey(),
            createWeIdResultWithSetAttr.getWeId());
        super.setAuthentication(
            createWeIdResultWithSetAttr,
            passwordKey.getPublicKey(),
            createWeIdResultWithSetAttr.getWeId());

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());
        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(passwordKey.getPrivateKey());

        Credential credential = super.createCredential(createCredentialArgs);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: issuranceDate < now && expirationDate < now  && issuranceDate < expirationDate.
     */
    @Test
    public void testVerifyCredentialCase27() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setIssuranceDate(System.currentTimeMillis() - 12000);
        credential.setExpirationDate(System.currentTimeMillis() - 10000);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: mock CREDENTIAL_WEID_DOCUMENT_ILLEGAL.
     */
    @Test(groups = "MockUp")
    public void testVerifyCredentialCase28() {

        MockUp<WeIdServiceImpl> mockTest = new MockUp<WeIdServiceImpl>() {
            @Mock
            public ResponseData<WeIdDocument> getWeIdDocument(String weId) {
                ResponseData<WeIdDocument> response = new ResponseData<WeIdDocument>();
                response.setErrorCode(ErrorCode.CREDENTIAL_WEID_DOCUMENT_ILLEGAL.getCode());
                return response;
            }
        };
        Credential credential = copyCredential(credentialInfo);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.CREDENTIAL_WEID_DOCUMENT_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: mock NullPointerException.
     */
    @Test(groups = "MockUp")
    public void testVerifyCredentialCase29() {
        Credential credential = copyCredential(credentialInfo);

        credential.setIssuranceDate(System.currentTimeMillis() - 12000);
        credential.setExpirationDate(System.currentTimeMillis() - 10000);

        MockUp<CredentialServiceImpl> mockTest = new MockUp<CredentialServiceImpl>() {
            @Mock
            private ResponseData<Boolean> checkCreateCredentialArgsValidity(
                CreateCredentialArgs args,
                boolean privateKeyRequired) {
                return null;
            }
        };

        ResponseData<Boolean> response = super.verifyCredential(credential);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuer is not exists.
     */
    @Test
    public void testVerifyCredentialCase30() {
        System.out.println("-----" + createCredentialArgs);
        System.out.println("-----" + cptBaseInfo);
        System.out.println("-----" + createWeIdResultWithSetAttr);
        System.out.println("-----" + credentialInfo);
        Credential credential = copyCredential(credentialInfo);

        credential.setIssuer("did:weid:0x111111111111111");

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    public Credential copyCredential(Credential credential) {
        Credential copyCredential = new Credential();
        copyCredential.setId(credential.getId());
        copyCredential.setIssuer(credential.getIssuer());
        copyCredential.setExpirationDate(credential.getExpirationDate());
        copyCredential.setCptId(credential.getCptId());
        copyCredential.setIssuranceDate(credential.getIssuranceDate());
        copyCredential.setClaim(credential.getClaim());
        copyCredential.setContext(credential.getContext());
        copyCredential.setSignature(credential.getSignature());
        return copyCredential;
    }

}
