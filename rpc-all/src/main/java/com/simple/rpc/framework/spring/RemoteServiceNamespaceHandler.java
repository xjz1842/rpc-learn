package com.simple.rpc.framework.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class RemoteServiceNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("service", new ProviderFactoryBeanDefinitionParser());
    }

}
