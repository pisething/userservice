package com.pisethjavaschool.userservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Configuration
public class TransactionManagementConfig implements TransactionManagementConfigurer {

    private final TransactionManager reactiveTransactionManager;

    public TransactionManagementConfig(
            @Qualifier("connectionFactoryTransactionManager") TransactionManager reactiveTransactionManager
    ) {
        this.reactiveTransactionManager = reactiveTransactionManager;
    }

    @Override
    public TransactionManager annotationDrivenTransactionManager() {
        return reactiveTransactionManager;
    }
}