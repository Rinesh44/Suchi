package com.treeleaf.suchi.api;

import com.treeleaf.suchi.entities.AccountProto;
import com.treeleaf.suchi.entities.ReqResProto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Endpoints {
    String API_BASE_URL = "http://192.168.0.60:9000/";

    String CONTENT_TYPE = "Content-Type: application/protobuf";
    String AUTHORIZATION = "authorization";

    String LOGIN = "account/login";
    String REGISTER = "user/register";
    String LOGOUT = "account/logout";
    String GET_ALL_DATA = "inventory/data/all";
    String ADD_TO_INVENTORY = "inventory";


    @Headers({CONTENT_TYPE})
    @POST(API_BASE_URL + LOGIN)
    Call<ReqResProto.Response> login(@Body AccountProto.LoginRequest loginRequest);

    @POST(API_BASE_URL + REGISTER)
    Call<ReqResProto.Response> register(@Body AccountProto.User user);

    @DELETE(API_BASE_URL + LOGOUT)
    Call<ReqResProto.Response> logout(@Header(AUTHORIZATION) String authorization);

    @GET(API_BASE_URL + GET_ALL_DATA)
    Call<ReqResProto.Response> getAllData(@Header(AUTHORIZATION) String authorization);

    @POST(API_BASE_URL + ADD_TO_INVENTORY)
    Call<ReqResProto.Response> addInventory(@Header(AUTHORIZATION) String authorization,
                                            @Body ReqResProto.Request addInventoryRequest);
}
