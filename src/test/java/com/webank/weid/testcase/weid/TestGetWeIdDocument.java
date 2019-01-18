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

package com.webank.weid.testcase.weid;

import java.util.concurrent.Future;

import mockit.Mock;
import mockit.MockUp;
import org.bcos.web3j.abi.datatypes.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.contract.WeIdContract;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.testcase.TestBaseServcie;
import com.webank.weid.utils.BeanUtil;
import com.webank.weid.utils.TestBaseUtil;
import com.webank.weid.utils.TestMockException;

/**
 * getWeIdDocument method for testing WeIdService.
 *
 * @author v_wbgyang
 */
@Test(groups = "all")
public class TestGetWeIdDocument extends TestBaseServcie {

    private static final Logger logger = LoggerFactory.getLogger(TestGetWeIdDocument.class);

    protected static CreateWeIdDataResult createWeIdForGetDoc = null;

    @Test(groups = "ignore")
    public synchronized void testInit() {
        super.testInit();
        if (null == createWeIdForGetDoc) {
            createWeIdForGetDoc = super.createWeIdWithSetAttr();
        }
    }

    /**
     * case: set and get weIdDom.
     */
    @Test
    public void testGetWeIdDocumentCase1() {
        CreateWeIdDataResult weIdWithSetAttr = super.createWeIdWithSetAttr();
        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(weIdWithSetAttr.getWeId());
        logger.info("getWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(1, weIdDoc.getResult().getService().size());
        Assert.assertEquals(1, weIdDoc.getResult().getAuthentication().size());
        Assert.assertEquals(1, weIdDoc.getResult().getPublicKey().size());
    }

    /**
     * case: set many times and get the weIdDom.
     */
    @Test
    public void testGetWeIdDocumentCase2() {

        super.setPublicKey(createWeIdForGetDoc,
            TestBaseUtil.createEcKeyPair().getPublicKey(),
            createWeIdNew.getWeId());
        logger.info("testGetWeIdDocumentCase2 setPublicKey" + createWeIdForGetDoc);
        super.setAuthentication(createWeIdForGetDoc,
            TestBaseUtil.createEcKeyPair().getPublicKey(),
            createWeIdForGetDoc.getWeId());
        logger.info("testGetWeIdDocumentCase2 setAuthentication" + createWeIdForGetDoc);

        super.setService(createWeIdForGetDoc,
            "drivingCardServic1",
            "https://weidentity.webank.com/endpoint/8377465");
        logger.info("testGetWeIdDocumentCase2 setService" + createWeIdForGetDoc);

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(createWeIdForGetDoc.getWeId());
        System.out.println("testGetWeIdDocumentCase2结果：" + weIdDoc);

        logger.info("getWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(2, weIdDoc.getResult().getService().size());
        Assert.assertEquals(2, weIdDoc.getResult().getAuthentication().size());
        Assert.assertEquals(3, weIdDoc.getResult().getPublicKey().size());
    }

    /**
     * case: WeIdentity DID is invalid.
     */
    @Test
    public void testGetWeIdDocumentCase3() {

        ResponseData<WeIdDocument> weIdDoc = weIdService
            .getWeIdDocument("xxxxxxxxxx");

        logger.info("getWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertNull(weIdDoc.getResult());
    }

    /**
     * case: WeIdentity DID is null.
     */
    @Test
    public void testGetWeIdDocumentCase4() {

        ResponseData<WeIdDocument> weIdDoc = weIdService.getWeIdDocument(null);
        logger.info("getWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertNull(weIdDoc.getResult());
    }

    /**
     * case: WeIdentity DID is not exists.
     */
    @Test
    public void testGetWeIdDocumentCase5() {

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument("did:weid:0xa1c93e93622c6a0b2f52c90741e0b98ab77385a9");
        logger.info("getWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.WEID_DOES_NOT_EXIST.getCode(),
            weIdDoc.getErrorCode().intValue());
        Assert.assertNull(weIdDoc.getResult());
    }

    /**
     * case: Simulation throws an InterruptedException when calling the getLatestRelatedBlock
     * method.
     */
    @Test(groups = "MockUp")
    public void testGetWeIdDocumentCase6() {

        MockUp<Future<?>> mockFuture = TestMockException.mockInterruptedFuture();

        ResponseData<WeIdDocument> weIdDoc = getWeIdDocument(mockFuture);

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            weIdDoc.getErrorCode().intValue());
        Assert.assertNull(weIdDoc.getResult());
    }

    /**
     * case: Simulation throws an TimeoutException when calling the getLatestRelatedBlock method.
     */
    @Test(groups = "MockUp")
    public void testGetWeIdDocumentCase7() {

        MockUp<Future<?>> mockFuture = TestMockException.mockTimeoutFuture();

        ResponseData<WeIdDocument> weIdDoc = getWeIdDocument(mockFuture);

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            weIdDoc.getErrorCode().intValue());
        Assert.assertNull(weIdDoc.getResult());
    }

    private ResponseData<WeIdDocument> getWeIdDocument(MockUp<Future<?>> mockFuture) {

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public Future<?> getLatestRelatedBlock(Address identity) {
                return mockFuture.getMockInstance();
            }
        };

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(createWeIdForGetDoc.getWeId());
        logger.info("getWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        mockTest.tearDown();
        mockFuture.tearDown();
        return weIdDoc;
    }
}
