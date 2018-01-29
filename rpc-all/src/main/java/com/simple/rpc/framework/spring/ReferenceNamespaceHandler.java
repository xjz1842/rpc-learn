package com.simple.rpc.framework.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class ReferenceNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("reference", new RevokerFactoryBeanDefinitionParser());
    }
}
