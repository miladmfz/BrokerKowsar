package com.kits.brokerkowsar.webService;

import com.kits.brokerkowsar.model.RetrofitResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIInterface_kowsar {


     String PostString="index.php";
   // String PostString="kits/";


    @POST(PostString)
 @FormUrlEncoded
 Call<RetrofitResponse> Activation(
         @Field("tag") String tag
         , @Field("ActivationCode") String ActivationCode
 );




}

