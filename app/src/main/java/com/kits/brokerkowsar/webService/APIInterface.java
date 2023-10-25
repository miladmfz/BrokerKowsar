package com.kits.brokerkowsar.webService;

import com.kits.brokerkowsar.model.RetrofitResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {



    String Kits_Url="kits/";
    String kowsar_Url="kowsar/";



    String Broker_Url="Broker/";
    String Company_Url="Company/";
    String Ocr_Url="Ocr/";
    String order_Url="order/";





    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetAppPrinter(
            @Field("tag") String tag
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> AppBrokerPrint(
            @Field("tag") String tag,
            @Field("Image") String Image,
            @Field("Code") String Code,
            @Field("PrinterName") String PrinterName,
            @Field("PrintCount") String PrintCount
    );

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

//
//    @POST("index.php")
//    @FormUrlEncoded
//    Call<RetrofitResponse> Activation(
//            @Field("tag") String tag
//            , @Field("ActivationCode") String ActivationCode
//    );


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
    @FormUrlEncoded
    Call<RetrofitResponse> GetSellBroker(@Field("tag") String tag);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> UpdateLocation(@Field("tag") String tag, @Field("GpsLocations") String GpsLocations);

















































    @GET(Kits_Url+"Activation")
    Call<RetrofitResponse> Activation(
            @Query("ActivationCode") String ActivationCode
    );



    /*






    @GET(Kits_Url+"Activation")
    Call<RetrofitResponse> Activation(
            @Query("ActivationCode") String ActivationCode
    );




    @GET(Kits_Url+"Kowsar_log")
    Call<RetrofitResponse> Kowsar_log(
            @Query("Device_Id") String Device_Id,
            @Query("Address_Ip") String Address_Ip,
            @Query("Server_Name") String Server_Name,
            @Query("Factor_Code") String Factor_Code,
            @Query("StrDate") String StrDate,
            @Query("Broker") String Broker,
            @Query("Explain") String Explain
    );

    @GET(Kits_Url+"Errorlog")
    Call<RetrofitResponse> Errorlog(
            @Query("ErrorLog") String ErrorLog,
            @Query("Broker") String Broker,
            @Query("DeviceId") String DeviceId,
            @Query("ServerName") String ServerName,
            @Query("StrDate") String StrDate,
            @Query("VersionName") String VersionName
    );







    @GET(kowsar_Url+"GetImageFromKsr")
    Call<RetrofitResponse> GetImageFromKsr(
            @Query("KsrImageCode") String KsrImageCode
    );

    @GET(kowsar_Url+"GetImageCustom")
    Call<RetrofitResponse> GetImageCustom(
            @Query("ClassName") String ClassName,
            @Query("ObjectRef") String ObjectRef,
            @Query("Scale") String Scale
    );

    @GET(kowsar_Url+"DbSetupvalue")
    Call<RetrofitResponse> DbSetupvalue(
            @Query("Where") String Where
    );

    @GET(kowsar_Url+"customer_insert")
    Call<RetrofitResponse> customer_insert(
            @Query("BrokerRef") String BrokerRef,
            @Query("CityCode") String CityCode,
            @Query("KodeMelli") String KodeMelli,
            @Query("FName") String FName,
            @Query("LName") String LName,
            @Query("Address") String Address,
            @Query("Phone") String Phone,
            @Query("Mobile") String Mobile,
            @Query("EMail") String EMail,
            @Query("PostCode") String PostCode,
            @Query("ZipCode") String ZipCode
    );

    @GET(kowsar_Url+"GetGoodType")
    Call<RetrofitResponse> GetGoodType();



    @GET(kowsar_Url+"GetSellBroker")
    Call<RetrofitResponse> GetSellBroker();





    @GET(Broker_Url+"GetColumnList")
    Call<RetrofitResponse> GetColumnList(
            @Query("Type") String Type,
            @Query("AppType") String AppType,
            @Query("IncludeZero") String IncludeZero
    );
    @GET(Broker_Url+"BrokerStack")
    Call<RetrofitResponse> BrokerStack(@Query("BrokerRef") String brokerRef);

    @GET(Broker_Url+"GetMenuBroker")
    Call<RetrofitResponse> GetMenuBroker();

    @GET(Broker_Url+"GetMaxRepLog")
    Call<RetrofitResponse> GetMaxRepLog();

    @GET(Broker_Url+"BrokerOrder")
    Call<String> BrokerOrder(
            @Query("HeaderDetail") String HeaderDetail,
            @Query("RowDetail") String RowDetail
    );
    @POST(Broker_Url+"BrokerOrder")
    Call<String> BrokerOrder1(
            @Field("HeaderDetail") String HeaderDetail,
            @Field("RowDetail") String RowDetail
    );

    @POST(Broker_Url+"BrokerOrder") // Replace with your actual API endpoint
    Call<ResponseBody> sendData(@Body RequestBody requestBody);



    @GET(Broker_Url+"repinfo")
    Call<RetrofitResponse> repinfo(
            @Query("code") String code,
            @Query("table") String table,
            @Query("reptype") String reptype,
            @Query("Reprow") String Reprow
    );

    @GET(Broker_Url+"UpdateLocation")
    Call<RetrofitResponse> UpdateLocation(
            @Query("GpsLocations") String GpsLocations
    );



    @GET(Broker_Url+"GetAppPrinter")
    Call<RetrofitResponse> GetAppPrinter();

     @GET(Broker_Url+"AppBrokerPrint")
    Call<RetrofitResponse> AppBrokerPrint(
            @Query("Image") String Image,
            @Query("Code") String Code,
            @Query("PrinterName") String PrinterName,
            @Query("PrintCount") String PrintCount
    );
*/

}

