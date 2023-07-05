package com.kits.brokerkowsar.webService;

import com.kits.brokerkowsar.model.RetrofitResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIInterface {


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetImageFromKsr(
            @Field("tag") String tag
            , @Field("KsrImageCode") String KsrImageCode
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetImageCustom(
            @Field("tag") String tag
            , @Field("ClassName") String ClassName
            , @Field("ObjectRef") String ObjectRef
            , @Field("Scale") String Scale
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> Activation(
            @Field("tag") String tag
            , @Field("ActivationCode") String ActivationCode
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> SetLocation(
            @Field("tag") String tag
            , @Field("Longitude") String Altitude
            , @Field("Latitude") String Latitude
            , @Field("BrokerRef") String BrokerRef
            , @Field("GpsDate") String GpsDate
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetLocation(
            @Field("tag") String tag
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> BrokerStack(
            @Field("tag") String tag
            , @Field("BrokerRef") String BrokerRef
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> MenuBroker(
            @Field("tag") String tag
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> info(
            @Field("tag") String tag,
            @Field("Where") String Where
    );




    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> MaxRepLogCode(
            @Field("tag") String tag
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> Notification(
            @Field("tag") String tag,
            @Field("Condition") String Condition
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> Kowsar_log(
            @Field("tag") String tag
            , @Field("Device_Id") String Device_Id
            , @Field("Address_Ip") String Address_Ip
            , @Field("Server_Name") String Server_Name
            , @Field("Factor_Code") String Factor_Code
            , @Field("StrDate") String StrDate
            , @Field("Broker") String Broker
            , @Field("Explain") String Explain
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> Errorlog(
            @Field("tag") String tag
            , @Field("ErrorLog") String ErrorLog
            , @Field("Broker") String Broker
            , @Field("DeviceId") String DeviceId
            , @Field("ServerName") String ServerName
            , @Field("StrDate") String StrDate
            , @Field("VersionName") String VersionName
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> customer_insert(
            @Field("tag") String tag
            , @Field("BrokerRef") String BrokerRef
            , @Field("CityCode") String CityCode
            , @Field("KodeMelli") String KodeMelli
            , @Field("FName") String FName
            , @Field("LName") String LName
            , @Field("Address") String Address
            , @Field("Phone") String Phone
            , @Field("Mobile") String Mobile
            , @Field("EMail") String EMail
            , @Field("PostCode") String PostCode
            , @Field("ZipCode") String ZipCode
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetGoodType(
            @Field("tag") String tag
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetColumnList(
            @Field("tag") String tag
            , @Field("Type") String Type
            , @Field("AppType") String AppType
            , @Field("IncludeZero") String IncludeZero
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> RetrofitReplicate(
            @Field("tag") String tag
            , @Field("code") String code
            , @Field("table") String table
            , @Field("Where") String Where
            , @Field("reptype") String reptype
            , @Field("Reprow") String Reprow
    );


    @POST("index.php")
    Call<RetrofitResponse> sendfactor(
            @Field("tag") String tag,
            @Field("PFHDQASW") String pfhdqasw,
            @Field("PFDTQASW") String pfdtqasw
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetSellBroker(@Field("tag") String tag);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> UpdateLocation(@Field("tag") String tag, @Field("GpsLocations") String GpsLocations);




}

