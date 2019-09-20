package com.treeleaf.suchi.activities.credit;

public interface CreditHistoryView {

    void syncCreditorsSuccess();

    void syncCreditorsFail(String msg);

    void syncCreditsSuccess();

    void syncCreditsFail(String msg);


}
