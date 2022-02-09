package com.vmware.accessmanagement.util;

import org.springframework.context.ApplicationContext;

public enum AppContext {

    INSTANCE;

    public static AppContext getInstance() {
        return INSTANCE;
    }

    private ApplicationContext applicationContext;

    /**
     * Default constructor
     */
    private AppContext() {
    }

    /**
     *
     */
    public void setContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     *
     * @return
     */
    public ApplicationContext getContext() {
        return applicationContext;
    }
}