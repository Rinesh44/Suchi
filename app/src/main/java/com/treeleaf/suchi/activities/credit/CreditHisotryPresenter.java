package com.treeleaf.suchi.activities.credit;

import com.treeleaf.suchi.realm.models.Credit;
import com.treeleaf.suchi.realm.models.Creditors;

import java.util.List;

public interface CreditHisotryPresenter {

    void syncCreditors(String token, List<Creditors> creditors);

    void syncCredits(String token, List<Credit> credits);
}
