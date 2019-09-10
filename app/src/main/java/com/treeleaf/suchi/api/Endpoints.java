package com.treeleaf.suchi.api;

import com.treeleaf.suchi.entities.SuchiProto;
import com.treeleaf.suchi.entities.TreeleafProto;
import com.treeleaf.suchi.rpc.SuchiRpcProto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface Endpoints {
    //    String API_BASE_URL = "http://192.168.0.173:9020/";
//    String API_BASE_URL = "http://192.168.0.191:9020/";
//    String API_BASE_URL = "http://192.168.0.60:9000/";
    String API_BASE_URL = "https://suchi-api.treeleaf.ai/";

    String CONTENT_TYPE = "Content-Type: application/protobuf";
    String AUTHORIZATION = "authorization";

    String LOGIN = "account/login";
    String REGISTER = "user/register";
    String LOGOUT = "account/logout";
    String GET_ALL_DATA = "inventory/data/all";
    String INVENTORY = "inventory";
    String ADD_SKU = "sku";
    String SALES = "sale/sync";

    String ADD_UNSYNCED_INVENTORIES = "inventory/sync";


    @Headers({CONTENT_TYPE})
    @POST(API_BASE_URL + LOGIN)
    Call<SuchiRpcProto.SuchiBaseResponse> login(@Body TreeleafProto.LoginRequest loginRequest);

    @POST(API_BASE_URL + REGISTER)
    Call<SuchiRpcProto.SuchiBaseResponse> register(@Body TreeleafProto.UserAccount user);

    @DELETE(API_BASE_URL + LOGOUT)
    Call<SuchiRpcProto.SuchiBaseResponse> logout(@Header(AUTHORIZATION) String authorization);

    @GET(API_BASE_URL + GET_ALL_DATA)
    Call<SuchiRpcProto.SuchiBaseResponse> getAllData(@Header(AUTHORIZATION) String authorization);

    @POST(API_BASE_URL + INVENTORY)
    Call<SuchiRpcProto.SuchiBaseResponse> addInventory(@Header(AUTHORIZATION) String authorization,
                                                       @Body SuchiProto.Inventory inventory);

    @PATCH(API_BASE_URL + INVENTORY)
    Call<SuchiRpcProto.SuchiBaseResponse> updateInventory(@Header(AUTHORIZATION) String authorization,
                                                          @Body SuchiProto.Inventory inventory);

    @POST(API_BASE_URL + ADD_UNSYNCED_INVENTORIES)
    Call<SuchiRpcProto.SuchiBaseResponse> addUnSyncedInventories(@Header(AUTHORIZATION) String auth,
                                                                 @Body SuchiProto.SyncRequest syncRequest);

    @GET(API_BASE_URL + INVENTORY)
    Call<SuchiRpcProto.SuchiBaseResponse> getInventory(@Header(AUTHORIZATION) String authorization);

    @POST(API_BASE_URL + ADD_SKU)
    Call<SuchiRpcProto.SuchiBaseResponse> addSku(@Header(AUTHORIZATION) String auth,
                                                 @Body SuchiProto.StockKeepingUnit stockKeepingUnit);

    @POST(API_BASE_URL + SALES)
    Call<SuchiRpcProto.SuchiBaseResponse> addSales(@Header(AUTHORIZATION) String auth,
                                                   @Body SuchiProto.SyncRequest syncRequest);

    @GET(API_BASE_URL + SALES)
    Call<SuchiRpcProto.SuchiBaseResponse> getSales(@Header(AUTHORIZATION) String auth);

}
