package com.framework.context;

public final class ContextKeys {
    private ContextKeys() {}

    public static final String BILLPAY_PAYEE_NAME    = "BILLPAY_PAYEE_NAME";
    public static final String BILLPAY_AMOUNT         = "BILLPAY_AMOUNT";
    public static final String BILLPAY_FROM_ACCOUNT   = "BILLPAY_FROM_ACCOUNT";
    public static final String TX_AMOUNT              = "TX_AMOUNT";
    public static final String TX_FROM                = "TX_FROM";
    public static final String TX_TO                  = "TX_TO";
    public static final String LOAN_EXPECTED_STATUS   = "LOAN_EXPECTED_STATUS";
    public static final String SHARED_ACCOUNT_ID      = "SHARED_ACCOUNT_ID";
    public static final String ACTUAL_FUNDING_ACCOUNT = "ACTUAL_FUNDING_ACCOUNT";
    public static final String USER_DATA              = "USER_DATA";
    public static final String LOAN_APPROVAL_AMOUNT       = "LOAN_APPROVAL_AMOUNT";
    public static final String LOAN_APPROVAL_DOWN_PAYMENT = "LOAN_APPROVAL_DOWN_PAYMENT";
    public static final String LOAN_DENIAL_AMOUNT         = "LOAN_DENIAL_AMOUNT";
    public static final String LOAN_DENIAL_DOWN_PAYMENT   = "LOAN_DENIAL_DOWN_PAYMENT";
}