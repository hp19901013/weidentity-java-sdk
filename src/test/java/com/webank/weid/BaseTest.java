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

package com.webank.weid;

import java.math.BigInteger;

import org.bcos.contract.tools.ToolConf;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import com.webank.weid.rpc.AuthorityIssuerService;
import com.webank.weid.rpc.CptService;
import com.webank.weid.rpc.CredentialService;
import com.webank.weid.rpc.WeIdService;

/**
 * Test base class.
 *
 * @author v_wbgyang
 */
@ContextConfiguration(
    locations = {"classpath:SpringApplicationContext-test.xml", "classpath:applicationContext.xml"})
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

    @Autowired
    protected AuthorityIssuerService authorityIssuerService;

    @Autowired
    protected CptService cptService;

    @Autowired
    protected WeIdService weIdService;

    @Autowired
    protected CredentialService credentialService;

    @Autowired
    protected ToolConf toolConf;

    /**
     * the private key of sdk is a BigInteger,which needs to be used
     * when registering authority.
     *
     */
    protected static String privateKey;

    /**
     *  initialization some for test.
     *
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        privateKey = new BigInteger(toolConf.getPrivKey(), 16).toString();
        testInit();
    }

    /**
     *  tearDown some for test.
     *
     */
//    @AfterMethod
    public void tearDown() {

        authorityIssuerService = null;
        cptService = null;
        weIdService = null;
        credentialService = null;
        toolConf = null;

        testFinalize();
    }

    public void testInit() {
        Assert.assertTrue(true);
    }

    public void testFinalize() {
        Assert.assertTrue(true);
    }

/*    public int getBlockNumber() throws IOException {
        return super.getWeb3j().ethBlockNumber().send().getBlockNumber().intValue();
    }*/
}