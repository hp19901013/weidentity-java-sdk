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

package com.webank.weid.testcase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.BaseTest;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.RegisterCptArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.WeIdUtils;
import com.webank.weid.utils.BeanUtil;
import com.webank.weid.utils.TestBaseUtil;
import com.webank.weid.utils.TestData;

/**
 * testing basic method classes.
 *
 * @author v_wbgyang
 */
public abstract class TestBaseServcie extends BaseTest {

    /**
     * log4j.
     */
    private static final Logger logger = LoggerFactory.getLogger(TestBaseServcie.class);

    protected static List<String> issuerPrivateList = new ArrayList<String>();

    protected static boolean isInitIssuer = false;

    protected static CreateWeIdDataResult createWeIdResult = null;

    protected static CreateWeIdDataResult createWeIdNew = null;

    /**
     * initializing related services.
     */
    @Override
    public synchronized void testInit() {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        if (createWeIdResult == null) {
            new Thread(() -> {
                createWeIdResult = this.createWeId();
                countDownLatch.countDown();
            }).start();
        } else {
            countDownLatch.countDown();
        }

        if (createWeIdNew == null) {
            new Thread(() -> {
                createWeIdNew = this.createWeId();
                countDownLatch.countDown();
            }).start();
        } else {
            countDownLatch.countDown();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * according to the analysis of the private key to create WeIdentity DID,and registered as an
     * authority, and its private key is recorded.
     *
     * @param fileName fileName
     */
    protected void initIssuer(String fileName) {

        String[] pk = TestBaseUtil.resolvePk(fileName);

        CreateWeIdArgs createWeIdArgs1 = TestBaseUtil.buildCreateWeIdArgs();
        createWeIdArgs1.setPublicKey(pk[0]);
        createWeIdArgs1.getWeIdPrivateKey().setPrivateKey(pk[1]);
        ResponseData<String> response1 = weIdService.createWeId(createWeIdArgs1);
        if (response1.getErrorCode().intValue() != ErrorCode.WEID_ALREADY_EXIST.getCode()
            && response1.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            Assert.assertTrue(false);
        }

        String weId = WeIdUtils.convertPublicKeyToWeId(pk[0]);

        CreateWeIdDataResult createResult = new CreateWeIdDataResult();
        createResult.setWeId(weId);
        createResult.setUserWeIdPrivateKey(new WeIdPrivateKey());
        createResult.setUserWeIdPublicKey(new WeIdPublicKey());
        createResult.getUserWeIdPrivateKey().setPrivateKey(pk[1]);
        createResult.getUserWeIdPublicKey().setPublicKey(pk[0]);

        this.setPublicKey(createResult, pk[0], createResult.getWeId());
        this.setAuthentication(createResult, pk[0], createResult.getWeId());

        CreateWeIdDataResult createWeId = new CreateWeIdDataResult();
        createWeId.setWeId(weId);

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId, privateKey);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        if (response.getErrorCode()
            .intValue() != ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_ALREADY_EXIST.getCode()
            && response.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            Assert.assertTrue(false);
        }

        issuerPrivateList.add(pk[1]);
        logger.info("initIssuer success");
    }

    /**
     * verifyCredential.
     *
     * @param credential credential
     */
    protected ResponseData<Boolean> verifyCredential(Credential credential) {

        ResponseData<Boolean> response = credentialService.verifyCredential(credential);
        logger.info("verifyCredentialWithSpecifiedPubKey result:");
        BeanUtil.print(response);

        return response;
    }

    /**
     * createCredential.
     *
     * @param createCredentialArgs createCredentialArgs
     */
    protected Credential createCredential(CreateCredentialArgs createCredentialArgs) {

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        logger.info("createCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        return response.getResult();
    }

    /**
     * cpt register.
     *
     * @param createWeId createWeId
     * @param registerCptArgs registerCptArgs
     */
    protected CptBaseInfo registerCpt(
        CreateWeIdDataResult createWeId,
        RegisterCptArgs registerCptArgs) {

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        logger.info("registerCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        return response.getResult();
    }

    /**
     * cpt register.
     *
     * @param createWeId createWeId
     */
    protected CptBaseInfo registerCpt(CreateWeIdDataResult createWeId) {

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);

        CptBaseInfo cptBaseInfo = registerCpt(createWeId, registerCptArgs);

        return cptBaseInfo;
    }

    /**
     * create WeIdentity DID and registerAuthorityIssuer.
     *
     * @return CreateWeIdDataResult
     */
    protected CreateWeIdDataResult registerAuthorityIssuer() {

        CreateWeIdDataResult createWeId = this.createWeId();

        registerAuthorityIssuer(createWeId);

        return createWeId;
    }

    /**
     * registerAuthorityIssuer default.
     */
    protected void registerAuthorityIssuer(CreateWeIdDataResult createWeId) {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId, super.privateKey);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * create WeIdentity DID and set Attribute default.
     *
     * @return CreateWeIdDataResult
     */
    protected CreateWeIdDataResult createWeIdWithSetAttr() {

        CreateWeIdDataResult createWeId = this.createWeId();

        CountDownLatch countDownLatch = new CountDownLatch(3);
        new Thread(() -> {
            this.setPublicKey(createWeId, createWeId.getUserWeIdPublicKey().getPublicKey(),
                createWeId.getWeId());
            countDownLatch.countDown();
        }).start();

        new Thread(() -> {
            this.setAuthentication(createWeId, createWeId.getUserWeIdPublicKey().getPublicKey(),
                createWeId.getWeId());
            countDownLatch.countDown();
        }).start();

        new Thread(() -> {
            this.setService(createWeId, TestData.serviceType, TestData.serviceEndpoint);
            countDownLatch.countDown();
        }).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return createWeId;
    }

    /**
     * create WeIdentity DID without set Attribute default.
     *
     * @return CreateWeIdDataResult
     */
    protected CreateWeIdDataResult createWeId() {

        ResponseData<CreateWeIdDataResult> createWeIdDataResult = weIdService.createWeId();
        logger.info("createWeId result:");
        BeanUtil.print(createWeIdDataResult);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(),
            createWeIdDataResult.getErrorCode().intValue());
        Assert.assertNotNull(createWeIdDataResult.getResult());

        return createWeIdDataResult.getResult();
    }

    /**
     * setPublicKey default.
     *
     * @param createResult createResult
     * @param publicKey publicKey
     * @param owner owner
     */
    protected void setPublicKey(
        CreateWeIdDataResult createResult,
        String publicKey,
        String owner) {

        // setPublicKey for this WeId
        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createResult);
        setPublicKeyArgs.setPublicKey(publicKey);
        setPublicKeyArgs.setOwner(owner);

        ResponseData<Boolean> responseSetPub = weIdService.setPublicKey(setPublicKeyArgs);
        BeanUtil.print(responseSetPub);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), responseSetPub.getErrorCode().intValue());
        Assert.assertEquals(true, responseSetPub.getResult());
    }

    /**
     * setService default.
     *
     * @param createResult createResult
     * @param serviceType serviceType
     * @param serviceEnpoint serviceEnpoint
     */
    protected void setService(
        CreateWeIdDataResult createResult,
        String serviceType,
        String serviceEnpoint) {

        // setService for this WeIdentity DID
        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createResult);
        setServiceArgs.setType(serviceType);
        setServiceArgs.setServiceEndpoint(serviceEnpoint);

        ResponseData<Boolean> responseSetSer = weIdService.setService(setServiceArgs);
        logger.info("setService result:");
        BeanUtil.print(responseSetSer);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), responseSetSer.getErrorCode().intValue());
        Assert.assertEquals(true, responseSetSer.getResult());
    }

    /**
     * setAuthenticate default.
     *
     * @param createResult createResult
     * @param publicKey publicKey
     * @param owner owner
     */
    protected void setAuthentication(
        CreateWeIdDataResult createResult,
        String publicKey,
        String owner) {

        // setAuthenticate for this WeIdentity DID
        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createResult);
        setAuthenticationArgs.setOwner(owner);
        setAuthenticationArgs.setPublicKey(publicKey);
        ResponseData<Boolean> responseSetAuth =
            weIdService.setAuthentication(setAuthenticationArgs);
        BeanUtil.print(responseSetAuth);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), responseSetAuth.getErrorCode().intValue());
        Assert.assertEquals(true, responseSetAuth.getResult());
    }
}
