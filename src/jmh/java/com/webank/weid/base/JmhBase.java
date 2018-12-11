package com.webank.weid.base;

import com.webank.weid.rpc.AuthorityIssuerService;
import com.webank.weid.rpc.CptService;
import com.webank.weid.rpc.CredentialService;
import com.webank.weid.rpc.WeIdService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JmhBase {

    public static WeIdService weIdService;
    public static AuthorityIssuerService authorityIssuerService;
    public static CptService cptService;
    public static CredentialService credentialService;

    static {
        ApplicationContext context = new ClassPathXmlApplicationContext(
            "applicationContext-jmh.xml");
        weIdService = context.getBean(WeIdService.class);
        authorityIssuerService = context.getBean(AuthorityIssuerService.class);
        cptService = context.getBean(CptService.class);
        credentialService = context.getBean(CredentialService.class);
    }
}
