package com.treeleaf.suchi.activities.credit;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.SuchiProto;
import com.treeleaf.suchi.realm.models.Credit;
import com.treeleaf.suchi.realm.models.Creditors;
import com.treeleaf.suchi.rpc.SuchiRpcProto;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.CallbackWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Response;

public class CreditHisotryPresenterImpl implements CreditHisotryPresenter {
    private static final String TAG = "CreditHisotryPresenterI";
    private Endpoints endpoints;
    private CreditHistory activity;

    public CreditHisotryPresenterImpl(Endpoints endpoints, CreditHistory activity) {
        this.endpoints = endpoints;
        this.activity = activity;
    }

    @Override
    public void syncCreditors(String token, List<Creditors> creditors) {

        List<SuchiProto.Creditor> creditorPbList = mapCreditorsToProto(creditors);

        SuchiProto.SyncRequest creditorsSyncRequest = SuchiProto.SyncRequest.newBuilder()
                .addAllCreditors(creditorPbList)
                .build();

        endpoints.addCreditors(token, creditorsSyncRequest).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<SuchiRpcProto.SuchiBaseResponse>() {
            @Override
            public void onSuccessResult(Response<SuchiRpcProto.SuchiBaseResponse> response) {
                SuchiRpcProto.SuchiBaseResponse baseResponse = response.body();

                if (baseResponse == null) {
                    activity.showMessage("sync creditors failed");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.showMessage(baseResponse.getMsg());
                }

                AppUtils.showLog(TAG, "SyncCreditorsResponse: " + baseResponse);
                activity.syncCreditorsSuccess();
            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.syncCreditorsFail("creditors sync failed");
            }
        }));

    }

    private List<SuchiProto.Creditor> mapCreditorsToProto(List<Creditors> creditors) {

        List<SuchiProto.Creditor> creditorPbList = new ArrayList<>();
        for (Creditors creditorModel : creditors
        ) {
            SuchiProto.Creditor creditorPb = SuchiProto.Creditor.newBuilder()
                    .setCreditorId(creditorModel.getId())
                    .setName(creditorModel.getName())
                    .setAddress(creditorModel.getAddress())
                    .setPhone(creditorModel.getPhone())
                    .setPicture(creditorModel.getPic())
                    .setUserId(creditorModel.getUserId())
                    .setCreatedAt(creditorModel.getCreatedAt())
                    .setUpdatedAt(creditorModel.getUpdatedAt())
                    .build();

            creditorPbList.add(creditorPb);
        }

        return creditorPbList;
    }


    @Override
    public void syncCredits(String token, List<Credit> credits) {
//        List<SuchiProto.CreditDetail> creditDetails = mapCreditDetailsToProto(credits);

    }

 /*   private List<SuchiProto.CreditDetail> mapCreditDetailsToProto(List<Credit> credits) {
        List<SuchiProto.CreditDetail> creditDetails = new ArrayList<>();
        for (Credit creditModel : credits
        ) {

            SuchiProto.CreditDetail creditDetail = SuchiProto.CreditDetail.newBuilder()
                    .setCreditId(creditModel.getCreditorId())
                    .setCreditorId(creditModel.getCreditorId())
                    .setPaidAmount(Double.valueOf(creditModel.getPaidAmount()))
                    .setDueAmount(Double.valueOf(creditModel.getDueAmount()))
                    .build();
        }
    }*/
}
