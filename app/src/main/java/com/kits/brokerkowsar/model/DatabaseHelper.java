package com.kits.brokerkowsar.model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationResult;
import com.kits.brokerkowsar.BuildConfig;
import com.kits.brokerkowsar.application.CallMethod;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DatabaseHelper extends SQLiteOpenHelper {
    CallMethod callMethod;
    ArrayList<Column> columns;
    ArrayList<Good> goods;

    Cursor cursor;
    Column column;
    Good gooddetail;

    int limitcolumn;
    String query = "";
    String result = "";
    String Search_Condition = "";
    String SH_selloff;
    String SH_grid;
    String LimitAmount;
    String SH_delay;
    String SH_brokerstack;
    String SH_prefactor_code;
    String SH_prefactor_good;
    String SH_MenuBroker;
    boolean SH_activestack;
    boolean SH_real_amount;
    boolean SH_goodamount;
    boolean SH_ArabicText;
    int k = 0;

    String StackAmountString;
    String BrokerStackString;
    String joinDetail;
    String joinbasket;

    public DatabaseHelper(Context context, String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, 1);
        this.callMethod = new CallMethod(context);
        this.goods = new ArrayList<>();

    }


    public void GetLastDataFromOldDataBase(String tempDbPath) {
        getWritableDatabase().beginTransaction();

        getWritableDatabase().execSQL("ATTACH DATABASE '" + tempDbPath + "' AS tempDb");

        getWritableDatabase().execSQL("INSERT INTO main.Prefactor SELECT * FROM tempDb.Prefactor ");
        getWritableDatabase().execSQL("INSERT INTO main.PreFactorRow SELECT * FROM tempDb.PreFactorRow ");
        getWritableDatabase().execSQL("INSERT INTO main.Config SELECT * FROM tempDb.Config ");

        getWritableDatabase().execSQL("DETACH DATABASE 'tempDb' ");
        getWritableDatabase().close();

    }

    public void CreateActivationDb() {
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS Activation (" +
                "AppBrokerCustomerCode TEXT," +
                "ActivationCode TEXT," +
                "PersianCompanyName TEXT," +
                "EnglishCompanyName TEXT," +
                "ServerURL TEXT," +
                "SQLiteURL TEXT," +
                "MaxDevice TEXT)");
        getWritableDatabase().close();
    }
    public void InitialConfigInsert() {
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement(
                "INSERT INTO config (keyvalue, datavalue) " +
                        "SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM config WHERE keyvalue = ?)");

        stmt.bindString(1, "BrokerCode");
        stmt.bindString(2, "0");
        stmt.bindString(3, "BrokerCode");
        stmt.execute();

        stmt.bindString(1, "BrokerStack");
        stmt.bindString(2, "0");
        stmt.bindString(3, "BrokerStack");
        stmt.execute();

        stmt.bindString(1, "GroupCodeDefult");
        stmt.bindString(2, "0");
        stmt.bindString(3, "GroupCodeDefult");
        stmt.execute();

        stmt.bindString(1, "MenuBroker");
        stmt.bindString(2, "0");
        stmt.bindString(3, "MenuBroker");
        stmt.execute();

        stmt.bindString(1, "KsrImage_LastRepCode");
        stmt.bindString(2, "0");
        stmt.bindString(3, "KsrImage_LastRepCode");
        stmt.execute();

        stmt.bindString(1, "MaxRepLogCode");
        stmt.bindString(2, "0");
        stmt.bindString(3, "MaxRepLogCode");
        stmt.execute();

        stmt.bindString(1, "LastGpsLocationCode");
        stmt.bindString(2, "0");
        stmt.bindString(3, "LastGpsLocationCode");
        stmt.execute();

        stmt.bindString(1, "VersionInfo");
        stmt.bindString(2, BuildConfig.VERSION_NAME);
        stmt.bindString(3, "VersionInfo");
        stmt.execute();

        stmt.close();
        db.close();
    }


    public void DatabaseCreate() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS GpsLocation (GpsLocationCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE ,Longitude TEXT, Latitude TEXT, Speed TEXT, BrokerRef TEXT, GpsDate TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS PreFactorRow (PreFactorRowCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE ,PreFactorRef INTEGER, GoodRef INTEGER, FactorAmount INTEGER, Shortage INTEGER, PreFactorDate TEXT,  Price INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Prefactor ( PreFactorCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, PreFactorDate TEXT," +
                " PreFactorTime TEXT, PreFactorKowsarCode INTEGER, PreFactorKowsarDate TEXT, PreFactorExplain TEXT, CustomerRef INTEGER, BrokerRef INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Config (ConfigCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, KeyValue TEXT , DataValue TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS BrokerColumn ( ColumnCode INTEGER PRIMARY KEY, SortOrder TEXT, ColumnName TEXT, ColumnDesc TEXT, GoodType TEXT, ColumnDefinition TEXT, ColumnType TEXT, Condition TEXT, OrderIndex TEXT, AppType INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS GoodType ( GoodTypeCode INTEGER PRIMARY KEY, GoodType TEXT, IsDefault TEXT)");


        db.execSQL("Create Index IF Not Exists IX_GoodGroup_GoodRef on GoodGroup (GoodRef,GoodGroupRef)");

        db.execSQL("Create Index IF Not Exists IX_Prefactor_CustomerRef on Prefactor (CustomerRef)");

        db.execSQL("Create Index IF Not Exists IX_PreFactorRow_GoodRef on PreFactorRow (GoodRef,PreFactorRef)");
        db.execSQL("Create Index IF Not Exists IX_PreFactorRow_PreFactorRef on PreFactorRow (PreFactorRef)");

        db.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodStack_GoodRef_stackref ON GoodStack (GoodRef,StackRef)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodStack_GoodRef ON GoodStack (GoodRef)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodStack_StackRef ON GoodStack (StackRef)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodStack_Amount ON GoodStack (Amount)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodStack_ReservedAmount ON GoodStack (ReservedAmount)");

        db.execSQL("CREATE INDEX IF NOT EXISTS IX_KsrImage_ObjectRef ON KsrImage (ObjectRef)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_KsrImage_IsDefaultImage ON KsrImage (IsDefaultImage)");

        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Customer_PriceTip ON Customer (PriceTip)");



        db.execSQL("CREATE INDEX IF NOT EXISTS IX_JobPerson_JobRef ON JobPerson (JobRef)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_JobPerson_AddressRef ON JobPerson (AddressRef)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_JobPerson_CentralRef ON JobPerson (CentralRef)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_JobPerson_Good_JobPersonRef ON JobPerson_Good (JobPersonRef)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_JobPerson_Good_GoodRef ON JobPerson_Good (GoodRef)");

        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodName ON Good (GoodName)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodMainCode ON Good (GoodMainCode)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain1 ON Good (GoodExplain1)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain2 ON Good (GoodExplain2)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain3 ON Good (GoodExplain3)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain4 ON Good (GoodExplain4)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain5 ON Good (GoodExplain5)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain6 ON Good (GoodExplain6)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_SellPriceType ON Good (SellPriceType)");
        db.execSQL("Create Index IF Not Exists IX_Good_GoodUnitRef on Good (GoodUnitRef)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar1 ON Good (Nvarchar1)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar2 ON Good (Nvarchar2)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar3 ON Good (Nvarchar3)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar4 ON Good (Nvarchar4)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar5 ON Good (Nvarchar5)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar6 ON Good (Nvarchar6)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar7 ON Good (Nvarchar7)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar8 ON Good (Nvarchar8)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar9 ON Good (Nvarchar9)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar10 ON Good (Nvarchar10)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar11 ON Good (Nvarchar11)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar12 ON Good (Nvarchar12)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar13 ON Good (Nvarchar13)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar14 ON Good (Nvarchar14)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar15 ON Good (Nvarchar15)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar16 ON Good (Nvarchar16)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar17 ON Good (Nvarchar17)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar18 ON Good (Nvarchar18)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar19 ON Good (Nvarchar19)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar20 ON Good (Nvarchar20)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Float1 ON Good (Float1)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Float2 ON Good (Float2)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Float3 ON Good (Float3)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Float4 ON Good (Float4)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Float5 ON Good (Float5)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Date1 ON Good (Date1)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Date2 ON Good (Date2)");
        db.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Text1 ON Good (Text1)");
        db.close();

    }


    public void closedb() {
        getWritableDatabase().close();
    }

    @SuppressLint("Range")
    public ArrayList<ReplicationModel> GetReplicationTable() {
        query = "SELECT * from ReplicationTable";
        ArrayList<ReplicationModel> replicationModels = new ArrayList<>();

        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ReplicationModel ReplicationModel = new ReplicationModel();
                try {

                    ReplicationModel.setReplicationCode(cursor.getInt(cursor.getColumnIndex("ReplicationCode")));
                    ReplicationModel.setServerTable(cursor.getString(cursor.getColumnIndex("ServerTable")));
                    ReplicationModel.setClientTable(cursor.getString(cursor.getColumnIndex("ClientTable")));
                    ReplicationModel.setServerPrimaryKey(cursor.getString(cursor.getColumnIndex("ServerPrimaryKey")));
                    ReplicationModel.setClientPrimaryKey(cursor.getString(cursor.getColumnIndex("ClientPrimaryKey")));
                    ReplicationModel.setCondition(cursor.getString(cursor.getColumnIndex("Condition")));
                    ReplicationModel.setConditionDelete(cursor.getString(cursor.getColumnIndex("ConditionDelete")));
                    ReplicationModel.setLastRepLogCode(cursor.getInt(cursor.getColumnIndex("LastRepLogCode")));
                    ReplicationModel.setLastRepLogCodeDelete(cursor.getInt(cursor.getColumnIndex("LastRepLogCodeDelete")));


                } catch (Exception ignored) {
                }
                replicationModels.add(ReplicationModel);
            }
        }
        assert cursor != null;
        cursor.close();
        return replicationModels;
    }


    @SuppressLint("Range")
    public ArrayList<TableDetail> GetTableDetail(String TableName) {
        query = "PRAGMA table_info( " + TableName + " )";
        ArrayList<TableDetail> tableDetails = new ArrayList<>();

        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                TableDetail tableDetail = new TableDetail();
                try {
                    tableDetail.setCid(cursor.getInt(cursor.getColumnIndex("cid")));
                    tableDetail.setName(cursor.getString(cursor.getColumnIndex("name")));
                    tableDetail.setType(cursor.getString(cursor.getColumnIndex("type")));
                    tableDetail.setText(null);
                } catch (Exception ignored) {
                }
                tableDetails.add(tableDetail);
            }
        }
        assert cursor != null;
        cursor.close();
        return tableDetails;
    }

    @SuppressLint("Range")
    public void GetLimitColumn(String AppType) {

        try {
            query = "select Count(*) count from GoodType ";

            cursor = getWritableDatabase().rawQuery(query, null);
            cursor.moveToFirst();
            String goodtypecount = cursor.getString(cursor.getColumnIndex("count"));
            cursor.close();
            query = "select Count(*) count from BrokerColumn Where Replace(Replace(AppType,char(1740),char(1610)),char(1705),char(1603)) = Replace(Replace('" + AppType + "',char(1740),char(1610)),char(1705),char(1603))";

            cursor = getWritableDatabase().rawQuery(query, null);
            cursor.moveToFirst();
            String columnscount = cursor.getString(cursor.getColumnIndex("count"));
            cursor.close();
            limitcolumn = Integer.parseInt(columnscount) / Integer.parseInt(goodtypecount);
        } catch (Exception e) {
            callMethod.showToast("تنظیم جدول از سمت دیتابیس مشکل دارد");
            Log.e("kowsar_query", e.getMessage());
        }
    }

    public void GetPreference() {


        this.SH_brokerstack = ReadConfig("BrokerStack");
        this.SH_MenuBroker = ReadConfig("MenuBroker");
        this.SH_selloff = callMethod.ReadString("SellOff");
        this.SH_grid = callMethod.ReadString("Grid");

        this.SH_delay = callMethod.ReadString("Delay");
        this.SH_prefactor_code = callMethod.ReadString("PreFactorCode");
        this.SH_prefactor_good = callMethod.ReadString("PreFactorGood");
        this.SH_activestack = callMethod.ReadBoolan("ActiveStack");
        this.SH_real_amount = callMethod.ReadBoolan("RealAmount");
        this.SH_goodamount = callMethod.ReadBoolan("GoodAmount");
        this.SH_ArabicText = callMethod.ReadBoolan("ArabicText");
        LimitAmount = String.valueOf(Integer.parseInt(SH_grid) * 11);


        BrokerStackString = "Where StackRef in (" + SH_brokerstack + ")";
        StackAmountString = "";


        joinbasket = " FROM Good g " +
                " Join Units on UnitCode =GoodUnitRef " +
                " Left Join (Select GoodRef, Sum(FactorAmount) FactorAmount , Sum(FactorAmount*Price) Price " +
                " From PreFactorRow Where PreFactorRef = " + SH_prefactor_code + " Group BY GoodRef) pf on pf.GoodRef = g.GoodCode  " +
                " Left Join PreFactor h on h.PreFactorCode = " + SH_prefactor_code +
                " Left Join Customer c on c.CustomerCode=h.CustomerRef ";

        joinDetail = " FROM Good g ,FilterTable Join Units u on u.UnitCode = g.GoodUnitRef " +
                " Left Join CacheGoodGroup cgg on cgg.GoodRef = g.Goodcode ";


    }

    @SuppressLint("Range")
    public String GetGoodTypeFromGood(String code) {
        query = "select GoodType from good where GoodCode = " + code;
        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        result = cursor.getString(cursor.getColumnIndex("GoodType"));
        cursor.close();
        return result;
    }

    @SuppressLint("Range")
    public ArrayList<Column> GetColumns(String code, String goodtype, @NonNull String AppType) {

        switch (AppType) {
            case "0"://        0-detail

                query = "Select * from BrokerColumn where Replace(Replace(GoodType,char(1740),char(1610)),char(1705),char(1603)) = '" + GetRegionText(GetGoodTypeFromGood(code)) + "' And AppType = 0";

                break;
            case "1"://        1-list
            case "2"://        2-basket
                GetLimitColumn(AppType);
                query = "Select * from BrokerColumn where AppType = " + AppType + " limit " + limitcolumn;
                break;
            case "3"://        3-search

                query = "Select * from BrokerColumn where Replace(Replace(GoodType,char(1740),char(1610)),char(1705),char(1603)) = '" + GetRegionText(goodtype) + "' And AppType = 3";

                break;
        }

        Log.e("kowsar_query", query);
        columns = new ArrayList<>();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Column column = new Column();
                try {
                    column.setColumnCode(cursor.getString(cursor.getColumnIndex("ColumnCode")));
                    column.setSortOrder(cursor.getString(cursor.getColumnIndex("SortOrder")));
                    column.setColumnName(cursor.getString(cursor.getColumnIndex("ColumnName")));
                    column.setColumnDesc(cursor.getString(cursor.getColumnIndex("ColumnDesc")));
                    column.setGoodType(cursor.getString(cursor.getColumnIndex("GoodType")));
                    column.setColumnType(cursor.getString(cursor.getColumnIndex("ColumnType")));
                    column.setColumnDefinition(cursor.getString(cursor.getColumnIndex("ColumnDefinition")));
                    column.setCondition(cursor.getString(cursor.getColumnIndex("Condition")));
                    column.setOrderIndex(cursor.getString(cursor.getColumnIndex("OrderIndex")));
                } catch (Exception ignored) {
                }
                columns.add(column);
            }
        }
        assert cursor != null;
        cursor.close();
        return columns;
    }

    @SuppressLint("Range")
    public String GetColumnscount() {

        query = "Select Count(*) result from BrokerColumn ";
        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        int result = cursor.getInt(cursor.getColumnIndex("result"));
        cursor.close();

        return String.valueOf(result);
    }

    @SuppressLint("Range")
    public String GetRegionText(String String) {


        query = "Select Replace(Replace(Cast('" + String + "' as nvarchar(500)),char(1740),char(1610)),char(1705),char(1603)) result  ";

        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        String result = cursor.getString(cursor.getColumnIndex("result"));
        cursor.close();

        return result;
    }


    @SuppressLint("Range")
    public ArrayList<Column> GetAllGoodType() {
        query = "Select * from GoodType ";
        columns = new ArrayList<>();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                column = new Column();
                try {
                    column.setGoodType(cursor.getString(cursor.getColumnIndex("GoodType")));
                    column.setIsDefault(cursor.getString(cursor.getColumnIndex("IsDefault")));
                } catch (Exception ignored) {
                }
                columns.add(column);
            }
        }
        assert cursor != null;
        cursor.close();
        return columns;
    }


    @SuppressLint({"Recycle", "Range"})
    public ArrayList<Good> getAllGood(String search_target, String aGroupCode, String MoreCallData) {
        goods.clear();
        GetPreference();


        columns = GetColumns("", "", "1");

        String search = GetRegionText(search_target);
        search = search.replaceAll(" ", "%").replaceAll("'", "%");

        Search_Condition = " '%" + search + "%' ";

        query = " With FilterTable As (Select 0 as SecondField) SELECT ";

        k = 0;

        for (Column column : columns) {

            if (column.getColumnDefinition().indexOf("Sum") > 0) {
                StackAmountString = column.getColumnDefinition().substring(
                        column.getColumnDefinition().indexOf("Sum"),
                        column.getColumnDefinition().indexOf(")") + 1
                );
            }

            if (!column.getColumnName().equals("")) {
                if (k != 0) {
                    query = query + " , ";
                }
                if (!column.getColumnDefinition().equals("")) {
                    query = query + column.getColumnDefinition() + " as " + column.getColumnName();
                } else {
                    query = query + column.getColumnName();
                }
                k++;
            }
        }

        query = query + " FROM Good g , FilterTable ";
        k = 0;
        boolean digitsOnly = TextUtils.isDigitsOnly(search);
        if (!search.equals("")) {
            for (Column column : columns) {


                if (!(!column.getColumnType().equals("0") && !digitsOnly)) {
                    if (Integer.parseInt(column.getColumnFieldValue("SortOrder")) > 0 && Integer.parseInt(column.getColumnFieldValue("SortOrder")) < 10) {
                        if (k == 0) {
                            query = query + " Where (";
                        } else {
                            query = query + " or ";
                        }
                        if (column.getColumnType().equals("0")) {
                            query = query + "Replace(Replace(" + column.getColumnName() + ",char(1740),char(1610)),char(1705),char(1603)) Like '%" + search + "%' ";
                        } else {
                            query = query + column.getColumnName() + " Like '%" + search + "%' ";
                        }
                        k++;
                    }
                }

            }

            for (Column column : columns) {
                if (column.getColumnType().equals("")) {
                    query = query + " or " + column.getColumnDefinition();
                }
            }

            query = query + " )";
        } else {
            query = query + "where 1=1 ";
        }

        query = query + " And Exists(Select 1 From GoodStack stackCondition ActiveCondition And GoodRef=GoodCode AmountCondition)";


        if (SH_activestack) {
            query = query.replaceAll("ActiveCondition", " And ActiveStack = 1 ");
        } else {
            query = query.replaceAll("ActiveCondition", " ");
        }

        if (SH_goodamount) {
            query = query.replaceAll("AmountCondition", " GROUP BY GoodRef HAVING " + StackAmountString + " > 0 ");
        } else {
            query = query.replaceAll("AmountCondition", " ");
        }

        query = query.replaceAll("stackCondition", BrokerStackString);

        query = query.replaceAll("SearchCondition", Search_Condition);


        if (Integer.parseInt(aGroupCode) > 0) {
            query = query + " And GoodCode in(Select GoodRef From GoodGroup p "
                    + "Join GoodsGrp s on p.GoodGroupRef = s.GroupCode "
                    + "Where s.GroupCode = " + aGroupCode + " or s.L1 = " + aGroupCode
                    + " or s.L2 = " + aGroupCode + " or s.L3 = " + aGroupCode
                    + " or s.L4 = " + aGroupCode + " or s.L5 = " + aGroupCode + ")";
        }
        query = query + " order by ";
        int k = 0;
        for (Column column : columns) {
            if (!column.getOrderIndex().equals("0")) {
                if (k != 0) {
                    query = query + " , ";
                }
                if (Integer.parseInt(column.getOrderIndex()) > 0) {
                    if (column.getColumnName().equals("Date")) {
                        String newSt = column.getColumnDefinition().substring(column.getColumnDefinition().indexOf("Then") + 5, column.getColumnDefinition().indexOf("Then") + 12);
                        query = query + newSt;
                        //Case When SecondField=1 Then g.Date2 Else g.Date2 End
                        //query = query + column.getColumnName();
                    } else {
                        query = query + column.getColumnName();
                    }
                } else {
                    if (column.getColumnName().equals("Date")) {
                        String newSt = column.getColumnDefinition().substring(column.getColumnDefinition().indexOf("Then") + 5, column.getColumnDefinition().indexOf("Then") + 12);
                        query = query + newSt + " DESC ";
                    } else {
                        query = query + column.getColumnName() + " DESC ";
                    }
                }
                k++;
            }
        }

        query = query + " LIMIT  " + LimitAmount;
        query = query + " OFFSET " + (Integer.parseInt(LimitAmount) * Integer.parseInt(MoreCallData));
        cursor = getWritableDatabase().rawQuery(query, null);
        Log.e("kowsar_query", query);

        if (cursor != null) {

            while (cursor.moveToNext()) {
                gooddetail = new Good();
                for (Column column : columns) {

                    try {
                        switch (column.getColumnType()) {
                            case "0":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        cursor.getString(cursor.getColumnIndex(column.getColumnName()))
                                );
                                break;
                            case "1":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(cursor.getInt(cursor.getColumnIndex(column.getColumnName())))
                                );
                                break;
                            case "2":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(cursor.getFloat(cursor.getColumnIndex(column.getColumnName())))
                                );
                                break;
                        }
                    } catch (Exception ignored) {
                    }
                }
                gooddetail.setCheck(false);
                gooddetail.setGoodFieldValue("ActiveStack", String.valueOf(cursor.getInt(cursor.getColumnIndex("ActiveStack"))));

                goods.add(gooddetail);
            }
        }
        assert cursor != null;
        cursor.close();


        return goods;
    }

    @SuppressLint("Range")
    public ArrayList<Good> getAllGood_Extended(String searchbox_result, String aGroupCode, String MoreCallData) {
        goods.clear();
        GetPreference();
        columns = GetColumns("", "", "1");


        query = "With FilterTable As (Select 0 as SecondField) SELECT ";
        k = 0;
        for (Column column : columns) {
            if (column.getColumnDefinition().indexOf("Sum") > 0) {
                StackAmountString = column.getColumnDefinition().substring(
                        column.getColumnDefinition().indexOf("Sum"),
                        column.getColumnDefinition().indexOf(")") + 1
                );
            }
            if (!column.getColumnName().equals("")) {
                if (k != 0) {
                    query = query + " , ";
                }
                if (!column.getColumnDefinition().equals("")) {
                    query = query + column.getColumnDefinition() + " as " + column.getColumnName();
                } else {
                    query = query + column.getColumnName();
                }
                k++;
            }
        }


        query = query + " FROM Good g , FilterTable ";

        query = query + " Where  1=1 ";

        query = query + searchbox_result;

        query = query + " And Exists(Select 1 From GoodStack stackCondition ActiveCondition And GoodRef=GoodCode AmountCondition)";


        if (SH_activestack) {
            query = query.replaceAll("ActiveCondition", " And ActiveStack = 1 ");
        } else {
            query = query.replaceAll("ActiveCondition", " ");
        }

        if (SH_goodamount) {
            query = query.replaceAll("AmountCondition", " GROUP BY GoodRef HAVING " + StackAmountString + " > 0 ");
        } else {
            query = query.replaceAll("AmountCondition", " ");
        }

        query = query.replaceAll("stackCondition", BrokerStackString);

        query = query.replaceAll("SearchCondition", Search_Condition);


        if (Integer.parseInt(aGroupCode) > 0) {
            query = query + " And GoodCode in(Select GoodRef From GoodGroup p "
                    + "Join GoodsGrp s on p.GoodGroupRef = s.GroupCode "
                    + "Where s.GroupCode = " + aGroupCode + " or s.L1 = " + aGroupCode
                    + " or s.L2 = " + aGroupCode + " or s.L3 = " + aGroupCode
                    + " or s.L4 = " + aGroupCode + " or s.L5 = " + aGroupCode + ")";
        }


        query = query + " order by ";
        int k = 0;
        for (Column column : columns) {
            if (!column.getOrderIndex().equals("0")) {
                if (k != 0) {
                    query = query + " , ";
                }
                if (Integer.parseInt(column.getOrderIndex()) > 0) {
                    if (column.getColumnName().equals("Date")) {
                        String newSt = column.getColumnDefinition().substring(column.getColumnDefinition().indexOf("Then") + 5, column.getColumnDefinition().indexOf("Then") + 12);
                        query = query + newSt;
                    } else {
                        query = query + column.getColumnName();
                    }
                } else {
                    if (column.getColumnName().equals("Date")) {
                        String newSt = column.getColumnDefinition().substring(column.getColumnDefinition().indexOf("Then") + 5, column.getColumnDefinition().indexOf("Then") + 12);
                        query = query + newSt + " DESC ";
                    } else {
                        query = query + column.getColumnName() + " DESC ";
                    }
                }
                k++;
            }
        }
        query = query + " LIMIT  " + LimitAmount;
        query = query + " OFFSET " + (Integer.parseInt(LimitAmount) * Integer.parseInt(MoreCallData));

        Log.e("kowsar_query", query);
        cursor = getWritableDatabase().rawQuery(query, null);

        Log.e("kowsar_query", query);
        if (cursor != null) {

            while (cursor.moveToNext()) {
                gooddetail = new Good();
                for (Column column : columns) {
                    Log.e("kowsar_query", column.getColumnName());

                    try {
                        switch (column.getColumnType()) {
                            case "0":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        cursor.getString(cursor.getColumnIndex(column.getColumnName()))
                                );
                                break;
                            case "1":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(cursor.getInt(cursor.getColumnIndex(column.getColumnName())))
                                );
                                break;
                            case "2":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(cursor.getFloat(cursor.getColumnIndex(column.getColumnName())))
                                );
                                break;
                        }
                    } catch (Exception ignored) {
                    }
                }
                gooddetail.setCheck(false);
                gooddetail.setGoodFieldValue("ActiveStack", cursor.getString(cursor.getColumnIndex("ActiveStack")));
                goods.add(gooddetail);
            }
        }
        assert cursor != null;
        cursor.close();
        return goods;
    }

    @SuppressLint("Range")
    public ArrayList<Good> getAllGood_ByDate(String xDayAgo, String MoreCallData) {

        goods.clear();
        GetPreference();
        columns = GetColumns("", "", "1");
        query = "  With FilterTable As (Select 1 as SecondField) SELECT ";
        k = 0;
        for (Column column : columns) {
            if (column.getColumnDefinition().indexOf("Sum") > 0) {
                StackAmountString = column.getColumnDefinition().substring(
                        column.getColumnDefinition().indexOf("Sum"),
                        column.getColumnDefinition().indexOf(")") + 1
                );
            }
            if (!column.getColumnName().equals("")) {
                if (k != 0) {
                    query = query + " , ";
                }
                if (!column.getColumnDefinition().equals("")) {
                    query = query + column.getColumnDefinition() + " as " + column.getColumnName();
                } else {
                    query = query + column.getColumnName();
                }
                k++;
            }
        }

        String newSt = "Date";
        for (Column column : columns) {

            if (column.getColumnName().equals("Date")) {
                newSt = column.getColumnDefinition().substring(column.getColumnDefinition().indexOf("Else") + 4, column.getColumnDefinition().indexOf("Else") + 12);
            }
        }


        query = query + " FROM Good g , FilterTable Where " + newSt + ">='" + xDayAgo + "' ";

        query = query + " And Exists(Select 1 From GoodStack stackCondition ActiveCondition And GoodRef=GoodCode AmountCondition)";


        if (SH_activestack) {
            query = query.replaceAll("ActiveCondition", " And ActiveStack = 1 ");
        } else {
            query = query.replaceAll("ActiveCondition", " ");
        }

        if (SH_goodamount) {
            query = query.replaceAll("AmountCondition", " GROUP BY GoodRef HAVING " + StackAmountString + " > 0 ");
        } else {
            query = query.replaceAll("AmountCondition", " ");
        }

        query = query.replaceAll("stackCondition", BrokerStackString);


        query = query + " order by ";
        int k = 0;
        for (Column column : columns) {
            if (!column.getOrderIndex().equals("0")) {
                if (k != 0) {
                    query = query + " , ";
                }
                if (Integer.parseInt(column.getOrderIndex()) > 0) {
                    if (column.getColumnName().equals("Date")) {
                        newSt = column.getColumnDefinition().substring(column.getColumnDefinition().indexOf("Else") + 4, column.getColumnDefinition().indexOf("Else") + 12);
                        query = query + newSt;
                    } else {
                        query = query + column.getColumnName();
                    }
                } else {
                    if (column.getColumnName().equals("Date")) {
                        newSt = column.getColumnDefinition().substring(column.getColumnDefinition().indexOf("Else") + 4, column.getColumnDefinition().indexOf("Else") + 12);
                        query = query + newSt + " DESC ";
                    } else {
                        query = query + column.getColumnName() + " DESC ";
                    }
                }
                k++;
            }
        }
        query = query + " LIMIT  " + LimitAmount;
        query = query + " OFFSET " + (Integer.parseInt(LimitAmount) * Integer.parseInt(MoreCallData));

        Log.e("kowsar_query", query);
        goods = new ArrayList<>();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                gooddetail = new Good();
                for (Column column : columns) {
                    try {
                        switch (column.getColumnType()) {
                            case "0":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        cursor.getString(cursor.getColumnIndex(column.getColumnName()))
                                );
                                break;
                            case "1":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(cursor.getInt(cursor.getColumnIndex(column.getColumnName())))
                                );
                                break;
                            case "2":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(cursor.getFloat(cursor.getColumnIndex(column.getColumnName())))
                                );
                                break;
                        }
                    } catch (Exception ignored) {
                    }
                }

                gooddetail.setCheck(false);
                gooddetail.setGoodFieldValue("ActiveStack", cursor.getString(cursor.getColumnIndex("ActiveStack")));
                goods.add(gooddetail);
            }
        }

        assert cursor != null;
        cursor.close();
        return goods;
    }


    @SuppressLint("Range")
    public Good getGoodByCode(String code) {
        GetPreference();
        columns = GetColumns(code, "", "0");

        query = "With FilterTable As (Select 0 as SecondField) SELECT ";
        k = 0;
        for (Column column : columns) {
            if (column.getColumnDefinition().indexOf("Sum") > 0) {
                StackAmountString = column.getColumnDefinition().substring(
                        column.getColumnDefinition().indexOf("Sum"),
                        column.getColumnDefinition().indexOf(")") + 1
                );
            }
            if (!column.getColumnName().equals("ksrImageCode")) {
                if (k != 0) {
                    query = query + " , ";
                }
                if (!column.getColumnDefinition().equals("")) {
                    query = query + column.getColumnDefinition() + " as " + column.getColumnName();
                } else {
                    query = query + column.getColumnName();
                }
                k++;
            }
        }
        query = query + joinDetail;
        Search_Condition = "'%%'";


        query = query.replaceAll("stackCondition", BrokerStackString);
        query = query.replaceAll("SearchCondition", Search_Condition);


        query = query + " WHERE GoodCode = " + code;

        Log.e("kowsar_query", query);
        gooddetail = new Good();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (Column column : columns) {
                try {
                    switch (column.getColumnType()) {
                        case "0":
                            Log.e("kowsar_query", column.getColumnName());
                            gooddetail.setGoodFieldValue(
                                    column.getColumnName(),
                                    cursor.getString(cursor.getColumnIndex(column.getColumnName()))
                            );
                            break;
                        case "1":
                            gooddetail.setGoodFieldValue(
                                    column.getColumnName(),
                                    String.valueOf(cursor.getInt(cursor.getColumnIndex(column.getColumnName())))
                            );
                            break;
                        case "2":
                            gooddetail.setGoodFieldValue(
                                    column.getColumnName(),
                                    String.valueOf(cursor.getFloat(cursor.getColumnIndex(column.getColumnName())))
                            );
                            break;
                    }
                } catch (Exception ignored) {
                }
            }
            gooddetail.setCheck(false);
            gooddetail.setGoodFieldValue("ActiveStack", cursor.getString(cursor.getColumnIndex("ActiveStack")));

        }
        cursor.close();
        return gooddetail;
    }


    public void InsertActivation(@NotNull Activation activation) {
        SQLiteDatabase db = getWritableDatabase();
        String activationCode = activation.getActivationCode();

        String updateSql = "UPDATE Activation SET ServerURL = ?, SQLiteURL = ? WHERE ActivationCode = ?";
        String insertSql = "INSERT INTO Activation(AppBrokerCustomerCode, ActivationCode, PersianCompanyName, EnglishCompanyName, ServerURL, SQLiteURL, MaxDevice) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement updateStatement = db.compileStatement(updateSql);
        updateStatement.bindString(1, activation.getServerURL());
        updateStatement.bindString(2, activation.getSQLiteURL());
        updateStatement.bindString(3, activationCode);
        int rowsUpdated = updateStatement.executeUpdateDelete();

        if (rowsUpdated == 0) {
            SQLiteStatement insertStatement = db.compileStatement(insertSql);
            insertStatement.bindString(1, activation.getAppBrokerCustomerCode());
            insertStatement.bindString(2, activationCode);
            insertStatement.bindString(3, activation.getPersianCompanyName());
            insertStatement.bindString(4, activation.getEnglishCompanyName());
            insertStatement.bindString(5, activation.getServerURL());
            insertStatement.bindString(6, activation.getSQLiteURL());
            insertStatement.bindString(7, activation.getMaxDevice());
            insertStatement.executeInsert();
        }
    }

    @SuppressLint("Range")
    public ArrayList<Activation> getActivation() {

        query = "Select * From Activation";
        cursor = getWritableDatabase().rawQuery(query, null);
        ArrayList<Activation> activations = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Activation activation = new Activation();
                try {
                    activation.setAppBrokerCustomerCode(cursor.getString(cursor.getColumnIndex("AppBrokerCustomerCode")));
                    activation.setActivationCode(cursor.getString(cursor.getColumnIndex("ActivationCode")));
                    activation.setPersianCompanyName(cursor.getString(cursor.getColumnIndex("PersianCompanyName")));
                    activation.setEnglishCompanyName(cursor.getString(cursor.getColumnIndex("EnglishCompanyName")));
                    activation.setServerURL(cursor.getString(cursor.getColumnIndex("ServerURL")));
                    activation.setSQLiteURL(cursor.getString(cursor.getColumnIndex("SQLiteURL")));
                    activation.setMaxDevice(cursor.getString(cursor.getColumnIndex("MaxDevice")));
                } catch (Exception ignored) {
                }
                activations.add(activation);

            }
        }
        assert cursor != null;
        cursor.close();
        return activations;
    }

    @SuppressLint("Range")
    public Good getGoodBuyBox(String code) {
        GetPreference();


        query = " SELECT IfNull(pf.FactorAmount,0) as FactorAmount ,  DefaultUnitValue,  UnitName ," +
                " IfNull(pf.Price,0) as Price , SellPriceType, MaxSellPrice ," +
                " Case c.PriceTip When 1 Then  SellPrice1 When 2 Then SellPrice2 When 3 Then SellPrice3 " +
                " When 4 Then SellPrice4 When 5 Then SellPrice5 When 6 Then SellPrice6 Else " +
                " Case When g.SellPriceType = 0 Then MaxSellPrice Else 100 End End *  " +
                " Case When g.SellPriceType = 0 Then 1 Else MaxSellPrice/100 End as SellPrice " +
                " FROM Good g " +
                " Join Units on UnitCode =GoodUnitRef " +
                " Left Join (Select GoodRef, Sum(FactorAmount) FactorAmount , Sum(FactorAmount*Price) Price " +
                " From PreFactorRow Where PreFactorRef = " + SH_prefactor_code + " Group BY GoodRef) pf on pf.GoodRef = g.GoodCode  " +
                " Left Join PreFactor h on h.PreFactorCode = " + SH_prefactor_code +
                " Left Join Customer c on c.CustomerCode=h.CustomerRef " +
                " WHERE GoodCode = " + code;


        Log.e("kowsar_query", query);
        gooddetail = new Good();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                gooddetail.setGoodFieldValue("FactorAmount", cursor.getString(cursor.getColumnIndex("FactorAmount")));
                gooddetail.setGoodFieldValue("UnitName", cursor.getString(cursor.getColumnIndex("UnitName")));
                gooddetail.setGoodFieldValue("Price", cursor.getString(cursor.getColumnIndex("Price")));
                gooddetail.setGoodFieldValue("MaxSellPrice", cursor.getLong(cursor.getColumnIndex("MaxSellPrice")) + "");
                gooddetail.setGoodFieldValue("SellPrice", cursor.getLong(cursor.getColumnIndex("SellPrice")) + "");
                gooddetail.setGoodFieldValue("SellPriceType", cursor.getLong(cursor.getColumnIndex("SellPriceType")) + "");
                gooddetail.setGoodFieldValue("DefaultUnitValue", cursor.getLong(cursor.getColumnIndex("DefaultUnitValue")) + "");
            } catch (Exception ignored) {
            }
        }
        cursor.close();
        return gooddetail;
    }

    @SuppressLint("Range")
    public Good getGooddata(String code) {
        GetPreference();

        query = " SELECT * FROM Good g WHERE GoodCode = " + code;


        Log.e("kowsar_query", query);
        Good good_data = new Good();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            good_data.setGoodFieldValue("GoodCode", cursor.getString(cursor.getColumnIndex("GoodCode")));
            good_data.setGoodFieldValue("SellPriceType", cursor.getString(cursor.getColumnIndex("SellPriceType")));
            good_data.setGoodFieldValue("MaxSellPrice", cursor.getString(cursor.getColumnIndex("MaxSellPrice")));
            good_data.setGoodFieldValue("MinSellPrice", cursor.getString(cursor.getColumnIndex("MinSellPrice")));
            good_data.setGoodFieldValue("SellPrice1", cursor.getString(cursor.getColumnIndex("SellPrice1")));
            good_data.setGoodFieldValue("SellPrice2", cursor.getString(cursor.getColumnIndex("SellPrice2")));
            good_data.setGoodFieldValue("SellPrice3", cursor.getString(cursor.getColumnIndex("SellPrice3")));
            good_data.setGoodFieldValue("SellPrice4", cursor.getString(cursor.getColumnIndex("SellPrice4")));
            good_data.setGoodFieldValue("SellPrice5", cursor.getString(cursor.getColumnIndex("SellPrice5")));
            good_data.setGoodFieldValue("SellPrice6", cursor.getString(cursor.getColumnIndex("SellPrice6")));
            good_data.setGoodFieldValue("GoodUnitRef", cursor.getString(cursor.getColumnIndex("GoodUnitRef")));
            good_data.setGoodFieldValue("DefaultUnitValue", cursor.getString(cursor.getColumnIndex("DefaultUnitValue")));

        }
        cursor.close();

        return good_data;
    }

    public ArrayList<Good> getAllGood_pfcode() {
        goods.clear();
        GetPreference();
        query = "SELECT * from PrefactorRow  Where ifnull(PreFactorRef,0)= " + SH_prefactor_code;
        goods = new ArrayList<>();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                gooddetail = new Good();
                gooddetail.setCheck(false);
                goods.add(gooddetail);
            }
        }
        assert cursor != null;
        cursor.close();
        return goods;
    }

    @SuppressLint("Range")
    public void InsertPreFactorHeader(String Search_target, String CustomerRef) {
        String customer = GetRegionText(Search_target);

        String date = Utilities.getCurrentShamsidate();
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        UserInfo user = new UserInfo();
        String brokerCodeQuery = "SELECT * FROM Config WHERE KeyValue = 'BrokerCode'";
        cursor = getWritableDatabase().rawQuery(brokerCodeQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String key = cursor.getString(cursor.getColumnIndex("KeyValue"));
                String value = cursor.getString(cursor.getColumnIndex("DataValue"));
                switch (key) {
                    case "ActiveCode":
                        user.setActiveCode(value);
                        break;
                    case "BrokerCode":
                        user.setBrokerCode(value);
                        break;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        String insertQuery = "INSERT INTO Prefactor (PreFactorKowsarCode, PreFactorDate, PreFactorKowsarDate, " +
                "PreFactorTime, PreFactorExplain, CustomerRef, BrokerRef) VALUES (0, ?, '-----', ?, ?, ?, ?)";
        getWritableDatabase().execSQL(insertQuery, new String[]{date, time, customer, CustomerRef, user.getBrokerCode()});
        getWritableDatabase().close();

    }

    @SuppressLint("Range")
    public void InsertPreFactor(String pfcode, String goodcode, String FactorAmount, String price, String BasketFlag) {
        if (Integer.parseInt(BasketFlag) > 0) {
            if (Float.parseFloat(price) >= 0) {
                query = "Update PreFactorRow set FactorAmount = " + FactorAmount + ", Price = " + price + " Where PreFactorRowCode=" + BasketFlag;
            } else {
                query = "Update PreFactorRow set FactorAmount = " + FactorAmount + " Where PreFactorRowCode=" + BasketFlag;
            }
            getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
        } else {
            query = " Select * From PreFactorRow Where IfNull(PreFactorRef,0)=" + pfcode + " And GoodRef =" + goodcode;
            if (Float.parseFloat(price) >= 0) {
                query = query + " And Price =" + price;
            }
            cursor = getWritableDatabase().rawQuery(query, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                getWritableDatabase().execSQL("Update PreFactorRow set FactorAmount = FactorAmount +" + FactorAmount + " Where PreFactorRowCode=" + cursor.getString(cursor.getColumnIndex("PreFactorRowCode")) + ";");
            } else {
                query = "INSERT INTO PreFactorRow(PreFactorRef, GoodRef, FactorAmount, Price) "
                        //Select " + pfcode + "," + goodcode + ", " + FactorAmount + "," +price
                        + "select PreFactorCode ,GoodCode," + FactorAmount + ", Case When " + price + ">0 Then " + price
                        + " When g.SellPrice1>0 And c.PriceTip= 1 Then Case When g.SellPriceType = 0 Then g.SellPrice1 Else g.SellPrice1 * g.MaxSellPrice /100 End "
                        + " When g.SellPrice2>0 And c.PriceTip= 2 Then Case When g.SellPriceType = 0 Then g.SellPrice2 Else g.SellPrice2 * g.MaxSellPrice /100 End "
                        + " When g.SellPrice3>0 And c.PriceTip= 3 Then Case When g.SellPriceType = 0 Then g.SellPrice3 Else g.SellPrice3 * g.MaxSellPrice /100 End "
                        + " When g.SellPrice4>0 And c.PriceTip= 4 Then Case When g.SellPriceType = 0 Then g.SellPrice4 Else g.SellPrice4 * g.MaxSellPrice /100 End "
                        + " When g.SellPrice5>0 And c.PriceTip= 5 Then Case When g.SellPriceType = 0 Then g.SellPrice5 Else g.SellPrice5 * g.MaxSellPrice /100 End "
                        + " When g.SellPrice6>0 And c.PriceTip= 6 Then Case When g.SellPriceType = 0 Then g.SellPrice6 Else g.SellPrice6 * g.MaxSellPrice /100 End "
                        + " Else MaxSellPrice End "
                        + " From PreFactor p Join Customer c on p.CustomerRef = c.CustomerCode "
                        + " Join Good g on GoodCode=" + goodcode
                        + " Where PreFactorCode=" + pfcode + " Limit 1 ";


                Log.e("kowsar_query", query);
                getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
            }
            cursor.close();
        }
    }


    @SuppressLint("Range")
    public void InsertPreFactorwithPercent(String pfCode, String goodCode, String factorAmount, String price, String basketFlag) {
        SQLiteDatabase db = getWritableDatabase();
        String query;
        if (Integer.parseInt(basketFlag) > 0) {
            if (Float.parseFloat(price) >= 0) {
                query = "UPDATE PreFactorRow SET FactorAmount = " + factorAmount + ", Price = " + price + " WHERE PreFactorRowCode = " + basketFlag;
            } else {
                query = "UPDATE PreFactorRow SET FactorAmount = " + factorAmount + " WHERE PreFactorRowCode = " + basketFlag;
            }
            db.execSQL(query);
        } else {
            query = "SELECT * FROM PreFactorRow WHERE IFNULL(PreFactorRef, 0) = ? AND GoodRef = ?";
            List<String> args = new ArrayList<>();
            args.add(pfCode);
            args.add(goodCode);
            if (Float.parseFloat(price) >= 0) {
                query += " AND Price = ?";
                args.add(price);
            }
            Cursor cursor = db.rawQuery(query, args.toArray(new String[args.size()]));
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                query = "UPDATE PreFactorRow SET FactorAmount = FactorAmount + ? WHERE PreFactorRowCode = ?";
                db.execSQL(query, new String[] { factorAmount, cursor.getString(cursor.getColumnIndex("PreFactorRowCode")) });
            } else {
                query = "INSERT INTO PreFactorRow(PreFactorRef, GoodRef, FactorAmount, Price) "
                        + "SELECT PreFactorCode, GoodCode, ?, ? FROM PreFactor JOIN Good g ON GoodCode = ? WHERE PreFactorCode = ? LIMIT 1";
                db.execSQL(query, new String[] { factorAmount, price, goodCode, pfCode });
            }
            cursor.close();
        }
        db.close();
    }



    @SuppressLint("Range")
    public ArrayList<PreFactor> getAllPrefactorHeader(String Search_target) {

        String name = GetRegionText(Search_target);

        query = " SELECT h.*, s.SumAmount , s.SumPrice , s.RowCount ,n.Title || ' ' || n.FName|| ' ' || n.Name CustomerName FROM PreFactor h Join Customer c  on c.CustomerCode = h.CustomerRef " +
                " join Central n on c.CentralRef=n.CentralCode "
                + " Left Join (SELECT P.PreFactorRef, sum(p.FactorAmount) as SumAmount , sum(p.FactorAmount * p.Price*g.DefaultUnitValue) as SumPrice, count(*) as RowCount "
                + " From Good g Join Units on UnitCode = GoodUnitRef  Join PreFactorRow p on GoodRef = GoodCode  Where IfNull(PreFactorRef, 0)>0 "
                + " Group BY PreFactorRef ) s on h.PreFactorCode = s.PreFactorRef "
                + " Where Replace(Replace(CustomerName,char(1740),char(1610)),char(1705),char(1603)) Like '%" + name + "%'"
                + " Order By h.PreFactorCode DESC";

        ArrayList<PreFactor> prefactor_header = new ArrayList<>();

        cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                PreFactor prefactor = new PreFactor();
                try {
                    prefactor.setPreFactorCode(cursor.getInt(cursor.getColumnIndex("PreFactorCode")));
                    prefactor.setPreFactorDate(cursor.getString(cursor.getColumnIndex("PreFactorDate")));
                    prefactor.setPreFactorTime(cursor.getString(cursor.getColumnIndex("PreFactorTime")));
                    prefactor.setPreFactorkowsarDate(cursor.getString(cursor.getColumnIndex("PreFactorKowsarDate")));
                    prefactor.setPreFactorKowsarCode(cursor.getInt(cursor.getColumnIndex("PreFactorKowsarCode")));
                    prefactor.setPreFactorExplain(cursor.getString(cursor.getColumnIndex("PreFactorExplain")));
                    prefactor.setCustomer(cursor.getString(cursor.getColumnIndex("CustomerName")));
                    prefactor.setSumAmount(cursor.getInt(cursor.getColumnIndex("SumAmount")));
                    prefactor.setSumPrice(cursor.getInt(cursor.getColumnIndex("SumPrice")));
                    prefactor.setRowCount(cursor.getInt(cursor.getColumnIndex("RowCount")));
                } catch (Exception ignored) {
                }
                prefactor_header.add(prefactor);
            }
        }
        assert cursor != null;
        cursor.close();
        return prefactor_header;
    }

    @SuppressLint("Range")
    public ArrayList<PreFactor> getAllPrefactorHeaderopen() {
        query = "SELECT h.*, s.SumAmount , s.SumPrice, s.RowCount ,n.Title || ' ' || n.FName|| ' ' || n.Name CustomerName  " +
                "FROM PreFactor h Join Customer c  on c.CustomerCode = h.CustomerRef "
                + " join Central n on c.CentralRef=n.CentralCode "
                + "Left Join (SELECT P.PreFactorRef, sum(p.FactorAmount) as SumAmount , sum(p.FactorAmount * p.Price*g.DefaultUnitValue) as SumPrice, count(*) as RowCount "
                + "From Good g Join Units on UnitCode = GoodUnitRef  Join PreFactorRow p on GoodRef = GoodCode  Where IfNull(PreFactorRef, 0)>0 "
                + "Group BY PreFactorRef ) s on h.PreFactorCode = s.PreFactorRef Where NOT IfNull(PreFactorKowsarCode, 0)>0 "
                + "Order By h.PreFactorCode DESC";

        ArrayList<PreFactor> prefactor_header = new ArrayList<>();

        cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                PreFactor prefactor = new PreFactor();
                try {


                    prefactor.setPreFactorCode(cursor.getInt(cursor.getColumnIndex("PreFactorCode")));
                    prefactor.setPreFactorDate(cursor.getString(cursor.getColumnIndex("PreFactorDate")));
                    prefactor.setPreFactorTime(cursor.getString(cursor.getColumnIndex("PreFactorTime")));
                    prefactor.setPreFactorkowsarDate(cursor.getString(cursor.getColumnIndex("PreFactorKowsarDate")));
                    prefactor.setPreFactorKowsarCode(cursor.getInt(cursor.getColumnIndex("PreFactorKowsarCode")));
                    prefactor.setPreFactorExplain(cursor.getString(cursor.getColumnIndex("PreFactorExplain")));
                    prefactor.setCustomer(cursor.getString(cursor.getColumnIndex("CustomerName")));
                    prefactor.setSumAmount(cursor.getInt(cursor.getColumnIndex("SumAmount")));
                    prefactor.setSumPrice(cursor.getInt(cursor.getColumnIndex("SumPrice")));
                    prefactor.setRowCount(cursor.getInt(cursor.getColumnIndex("RowCount")));
                } catch (Exception ignored) {
                }
                prefactor_header.add(prefactor);
            }
        }
        assert cursor != null;
        cursor.close();
        return prefactor_header;
    }

    @SuppressLint("Range")
    public ArrayList<Good> getAllPreFactorRows(String Search_target, String aPreFactorCode) {
        String name = GetRegionText(Search_target);
        name = name.replaceAll(" ", "%");
        GetPreference();

        columns = GetColumns("", "", "2");


        query = "SELECT ";

        k = 0;
        for (Column column : columns) {
            if (k != 0) {
                query = query + " , ";
            }
            if (!column.getColumnDefinition().equals("")) {
                query = query + column.getColumnDefinition() + " as " + column.getColumnName();
            } else {
                query = query + column.getColumnName();
            }
            k++;
        }

        query = query + " FROM Good g  " +
                "Join PreFactorRow pf on GoodRef = GoodCode " +
                "Join Units u on u.UnitCode = g.GoodUnitRef  " +
                "Where (Replace(Replace(GoodName,char(1740),char(1610)),char(1705),char(1603)) Like '%" + name + "%' and PreFactorRef = " + aPreFactorCode + ") order by PreFactorRowCode DESC ";


        cursor = getWritableDatabase().rawQuery(query, null);
        Log.e("kowsar_query", query);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                gooddetail = new Good();
                for (Column column : columns) {
                    try {
                        switch (column.getColumnType()) {
                            case "0":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        cursor.getString(cursor.getColumnIndex(column.getColumnName()))
                                );
                                break;
                            case "1":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(cursor.getInt(cursor.getColumnIndex(column.getColumnName())))
                                );
                                break;
                            case "2":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(cursor.getFloat(cursor.getColumnIndex(column.getColumnName())))
                                );
                                break;
                        }
                    } catch (Exception ignored) {
                    }
                }

                goods.add(gooddetail);

            }
        }
        assert cursor != null;
        cursor.close();
        return goods;
    }

    @SuppressLint("Range")
    public void UpdatePreFactorHeader_Customer(String pfcode, String Search_target) {
        String customer = GetRegionText(Search_target);

        ContentValues values = new ContentValues();
        values.put("CustomerRef", customer);

        String selection = "PreFactorCode = ?";
        String[] selectionArgs = { pfcode };

        getWritableDatabase().update("Prefactor", values, selection, selectionArgs);

        String query = "Select * From ( Select Case PriceTip " +
                "When 1 Then  SellPrice1 When 2 Then SellPrice2 When 3 Then SellPrice3  " +
                "When 4 Then   SellPrice4 When 5 Then SellPrice5 When 6 Then SellPrice6 " +
                "Else  Case When g.SellPriceType = 0 Then MaxSellPrice Else 100 End End * " +
                " Case When g.SellPriceType = 0 Then 1 Else MaxSellPrice/100 End as " +
                "NewPrice, Price, GoodCode From PreFactorRow p " +
                "Join PreFactor h on h.PreFactorCode = p.PreFactorRef " +
                "Join Customer on CustomerCode = CustomerRef " +
                "Join Good g on GoodRef = GoodCode Where h.PreFactorCode = " + pfcode + ") ss " +
                "Where Price <> NewPrice";

        Cursor cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String goodCode = cursor.getString(cursor.getColumnIndex("GoodCode"));
                float newPrice = cursor.getFloat(cursor.getColumnIndex("NewPrice"));

                ContentValues rowValues = new ContentValues();
                rowValues.put("Price", newPrice);

                String rowSelection = "PreFactorRef = ? AND GoodRef = ?";
                String[] rowSelectionArgs = { pfcode, goodCode };

                getWritableDatabase().update("PreFactorRow", rowValues, rowSelection, rowSelectionArgs);
            }

            cursor.close();
        }
    }

    @SuppressLint("Range")
    public Integer GetLastPreFactorHeader() {

        String query = "SELECT PreFactorCode FROM Prefactor WHERE PreFactorKowsarCode = 0 ORDER BY PreFactorCode DESC LIMIT 1";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int result = 0;
        if (cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }

    public void update_explain(String pfcode, String explain) {

        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE PreFactor SET PreFactorExplain = ? WHERE IFNULL(PreFactorCode, 0) = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.bindString(1, explain);
        statement.bindString(2, pfcode);
        statement.executeUpdateDelete();
        db.close();
    }

    public void DeletePreFactorRow(String pfcode, String rowcode) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("PreFactorRow", "IfNull(PreFactorRef,0)=? And (PreFactorRowCode=? or 0=?)", new String[]{pfcode, rowcode, rowcode});
        db.close();
    }

    public void DeletePreFactor(String pfcode) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.delete("Prefactor", "IfNull(PreFactorCode,0)=?", new String[]{pfcode});
        }
    }

    public void DeleteEmptyPreFactor() {
        SQLiteDatabase db = getWritableDatabase();
        String subquery = "SELECT PreFactorRef FROM PrefactorRow";
        String query = "DELETE FROM Prefactor WHERE PreFactorCode NOT IN (" + subquery + ")";
        db.execSQL(query);
        db.close();
        
    }

    public void UpdatePreFactor(String preFactorCode, String preFactorKowsarCode, String preFactorDate) {
        String query = "UPDATE PreFactor SET PreFactorKowsarCode = ?, PreFactorKowsarDate = ? WHERE IFNULL(PreFactorCode ,0) = ?;";
        try (SQLiteDatabase db = getWritableDatabase();
             SQLiteStatement statement = db.compileStatement(query)) {
            statement.bindString(1, preFactorKowsarCode);
            statement.bindString(2, preFactorDate);
            statement.bindString(3, preFactorCode);
            statement.executeUpdateDelete();
        } catch (SQLException e) {
            Log.e("updatePreFactor", "Error updating PreFactor", e);
        }
    }

    @SuppressLint("Range")
    public String getFactorSum(String pfcode) {
        query = " select sum(FactorAmount*price*DefaultUnitValue) as result From PreFactorRow join Good on GoodRef=GoodCode Where IfNull(PreFactorRef,0)=" + pfcode;
        Log.e("kowsar_query", query);
        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        long result = cursor.getLong(cursor.getColumnIndex("result"));
        cursor.close();

        return String.valueOf(result);
    }

    @SuppressLint("Range")
    public String getFactorSumAmount(String pfcode) {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {"sum(FactorAmount) as result"};
        String selection = "IfNull(PreFactorRef,0)=?";
        String[] selectionArgs = {pfcode};
        Cursor cursor = db.query("PreFactorRow join Good on GoodRef=GoodCode", columns, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        int result = cursor.getInt(cursor.getColumnIndex("result"));
        cursor.close();
        db.close();
        return String.valueOf(result);
    }

    @SuppressLint("Range")
    public String getFactordate(String pfcode) {
        String result = "";
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("result", "PreFactorDate");

        Cursor cursor = db.query("Prefactor", new String[]{"PreFactorDate as result"},
                "IfNull(PreFactorCode,0)= ?", new String[]{pfcode},
                null, null, null);

        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("result"));
        }
        cursor.close();
        return result;
    }

    @SuppressLint("Range")
    public String getPricetipCustomer(String pfcode) {
        String result = "";
        String[] projection = {"PriceTip"};
        String selection = "IfNull(PreFactorCode,0)= ?";
        String[] selectionArgs = {pfcode};
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables("PreFactor h JOIN Customer c ON c.CustomerCode = h.CustomerRef JOIN Central n ON c.CentralRef = n.CentralCode");
        try (Cursor cursor = builder.query(getWritableDatabase(), projection, selection, selectionArgs, null, null, null)) {
            if (cursor.moveToFirst()) {
                int priceTip = cursor.getInt(cursor.getColumnIndex("PriceTip"));
                result = String.valueOf(priceTip);
            } else {
                result = "فاکتوری انتخاب نشده";
            }
        }
        return result;
    }

    @SuppressLint("Range")
    public String getFactorCustomer(String pfcode) {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {"n.Title", "n.FName", "n.Name"};
        String selection = "IfNull(PreFactorCode,0) = ?";
        String[] selectionArgs = {pfcode};
        String tableName = "PreFactor h " +
                "Join Customer c on c.CustomerCode = h.CustomerRef " +
                "join Central n on c.CentralRef=n.CentralCode";
        Cursor cursor = db.query(tableName, columns, selection, selectionArgs, null, null, null);
        String result;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String title = cursor.getString(cursor.getColumnIndex("Title"));
            String fName = cursor.getString(cursor.getColumnIndex("FName"));
            String name = cursor.getString(cursor.getColumnIndex("Name"));
            result = title + " " + fName + " " + name;
        } else {
            result = "فاکتوری انتخاب نشده";
        }
        cursor.close();
        return result;
    }

    @SuppressLint("Range")
    public long getsum_sumfactor() {
        String[] columns = { "sm" };
        try (Cursor cursor = getWritableDatabase().query("PreFactorRow", columns, null, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndex("sm"));
            }
        }
        return 0;
    }

    @SuppressLint("Range")
    public ArrayList<Customer> AllCustomer(String search_target, boolean aOnlyActive) {

        String name = GetRegionText(search_target);
        name = name.replaceAll(" ", "%").replaceAll("'", "%");
        query = "SELECT u.CustomerCode,u.PriceTip,c.Title || ' ' || c.FName|| ' ' || c.Name CentralName,Address,Manager,Mobile,Phone,Delegacy,y.Name CityName, CustomerBestankar - CustomerBedehkar Bestankar, Active, CentralPrivateCode, EtebarNaghd" +
                ",EtebarCheck, Takhfif, MobileName, Email, Fax, ZipCode, PostCode FROM Customer u " +
                "join Central c on u.CentralRef= c.CentralCode " +
                "Left join Address d on u.AddressRef=d.AddressCode " +
                "Left join City y on d.CityCode=y.CityCode " +
                "join BrokerCustomer cb on cb.CustomerRef=u.CustomerCode " +
                " Where cb.BrokerRef=" + ReadConfig("BrokerCode") +
                " And ((Replace(Replace(CentralName,char(1740),char(1610)),char(1705),char(1603)) Like '%" + name + "%' or " +
                " CustomerCode Like '%" + name + "%' or  " +
                " Replace(Replace( Manager,char(1740),char(1610)),char(1705),char(1603)) Like '%" + name + "%'))";

        if (aOnlyActive) {
            query = query + " And Active = 0";
        }

        query = query + " order by CustomerCode DESC  LIMIT 200";
        ArrayList<Customer> Customers = new ArrayList<>();
        Log.e("kowsar_query", query);
        cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Customer customerdetail = new Customer();
                try {
                    customerdetail.setCustomerCode(cursor.getInt(cursor.getColumnIndex("CustomerCode")));
                    customerdetail.setCustomerName(cursor.getString(cursor.getColumnIndex("CentralName")));
                    customerdetail.setManager(cursor.getString(cursor.getColumnIndex("Manager")));
                    customerdetail.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
                    customerdetail.setPhone(cursor.getString(cursor.getColumnIndex("Phone")));
                    customerdetail.setBestankar(cursor.getInt(cursor.getColumnIndex("Bestankar")));
                } catch (Exception ignored) {
                }

                Customers.add(customerdetail);
            }
        }
        assert cursor != null;
        cursor.close();
        return Customers;
    }

    @SuppressLint("Range")
    public Integer customerCheck(String name) {
        int res = 0;

        String selection = "d_codemelli=?";
        String[] selectionArgs = { name };
        String[] columns = { "CentralCode" };
        Cursor cursor = getReadableDatabase().query("central", columns, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                res = cursor.getInt(cursor.getColumnIndex("CentralCode"));
            }
        }
        cursor.close();

        return res;
    }
    @SuppressLint("Range")
    public ArrayList<Customer> getCityList() {
        ArrayList<Customer> cities = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM city";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Customer city = new Customer();
                try {
                    ContentValues values = new ContentValues();
                    values.put("CityName", cursor.getString(cursor.getColumnIndex("CityName")));
                    values.put("CityCode", cursor.getString(cursor.getColumnIndex("CityCode")));
                    city.setCityName(cursor.getString(cursor.getColumnIndex("CityName")));
                    city.setCityCode(cursor.getString(cursor.getColumnIndex("CityCode")));
                } catch (Exception ignored) {
                }
                cities.add(city);
            }
            cursor.close();
        }
        db.close();
        return cities;
    }


    @SuppressLint("Range")
    public ArrayList<Good> GetksrImageCodes(String code) {
        query = "SELECT ksrImageCode from KsrImage where ObjectRef = " + code;
        ArrayList<Good> Goods = new ArrayList<>();
        Log.e("kowsar_query", query);
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Good gooddetail = new Good();
                try {
                    gooddetail.setGoodFieldValue("KsrImageCode", cursor.getString(cursor.getColumnIndex("KsrImageCode")));
                } catch (Exception ignored) {
                }
                Goods.add(gooddetail);
            }
        }
        assert cursor != null;
        cursor.close();
        return Goods;
    }

    @SuppressLint("Range")
    public String GetLastKsrImageCode(String code) {
        String ksrImageCode = "";

        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {"ksrImageCode"};
        String selection = "ObjectRef = ?";
        String[] selectionArgs = {code};
        String limit = "1";

        Cursor cursor = db.query("KsrImage", columns, selection, selectionArgs, null, null, null, limit);

        if (cursor != null && cursor.moveToFirst()) {
            ksrImageCode = cursor.getString(cursor.getColumnIndex("ksrImageCode"));
        }

        cursor.close();
        db.close();

        return ksrImageCode;
    }

    @SuppressLint("Range")
    public ArrayList<GoodGroup> getAllGroups(String Glstr) {

        String GL = "0";
        if (!Glstr.equals("")) {
            GL = Glstr;
        }

        query = "SELECT * ," +
                "case When L1=0 Then (Select Count(*) From GoodsGrp s Where s.L1=g.GroupCode) " +
                "When L2=0 Then (Select Count(*) From GoodsGrp s Where s.L2=g.GroupCode) " +
                "When L3=0 Then (Select Count(*) From GoodsGrp s Where s.L3=g.GroupCode) " +
                "When L4=0 Then (Select Count(*) From GoodsGrp s Where s.L4=g.GroupCode) " +
                "When L5=0 Then (Select Count(*) From GoodsGrp s Where s.L5=g.GroupCode) " +
                "Else 0 End  ChildNo " +
                " FROM GoodsGrp g WHERE 1=1 ";
        if (Integer.parseInt(GL) > 0) {
            query = query + " And ((L1=" + GL + " And L2=0) or (L2=" + GL + " And L3=0) or (L3=" + GL + " And L4=0) or (L4=" + GL + " And L5=0) or (L5=" + GL + "))";
        } else {
            query = query + " order by 1 desc";
        }

        ArrayList<GoodGroup> groups = new ArrayList<>();

        cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                GoodGroup grp = new GoodGroup();
                try {
                    grp.setGroupCode(cursor.getInt(cursor.getColumnIndex("GroupCode")));
                    grp.setName(cursor.getString(cursor.getColumnIndex("Name")));
                    grp.setL1(cursor.getInt(cursor.getColumnIndex("L1")));
                    grp.setL2(cursor.getInt(cursor.getColumnIndex("L2")));
                    grp.setL3(cursor.getInt(cursor.getColumnIndex("L3")));
                    grp.setL4(cursor.getInt(cursor.getColumnIndex("L4")));
                    grp.setL5(cursor.getInt(cursor.getColumnIndex("L5")));
                    grp.setChildNo(cursor.getInt(cursor.getColumnIndex("ChildNo")));
                } catch (Exception ignored) {
                }
                groups.add(grp);

            }
        }
        assert cursor != null;
        cursor.close();
        return groups;
    }

    @SuppressLint("Range")
    public ArrayList<GoodGroup> getmenuGroups() {
        GetPreference();
        String groupCodeCondition = SH_MenuBroker.isEmpty() ? "9999" : SH_MenuBroker;
        String query = "SELECT * FROM GoodsGrp WHERE Groupcode IN (" + groupCodeCondition + ")";
        ArrayList<GoodGroup> groups = new ArrayList<>();
        try (Cursor cursor = getWritableDatabase().rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                GoodGroup grp = new GoodGroup();
                grp.setGroupCode(cursor.getInt(cursor.getColumnIndex("GroupCode")));
                grp.setName(cursor.getString(cursor.getColumnIndex("Name")));
                grp.setL1(cursor.getInt(cursor.getColumnIndex("L1")));
                grp.setL2(cursor.getInt(cursor.getColumnIndex("L2")));
                grp.setL3(cursor.getInt(cursor.getColumnIndex("L3")));
                grp.setL4(cursor.getInt(cursor.getColumnIndex("L4")));
                grp.setL5(cursor.getInt(cursor.getColumnIndex("L5")));
                groups.add(grp);
            }
        } catch (Exception e) {
            // Log or rethrow the exception
        }
        return groups;
    }



    public void SavePersonalInfo(UserInfo user) {

        if (!user.getBrokerCode().equals("")) {
            ContentValues values = new ContentValues();
            values.put("DataValue", user.getBrokerCode());
            getWritableDatabase().update("Config", values, "KeyValue = ?", new String[]{"BrokerCode"});
        }

    }

    public void SaveConfig(String key, String value) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("KeyValue", key);
        contentValues.put("DataValue", value);

        long result = getWritableDatabase().insertWithOnConflict("Config", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        if (result == -1) {
            Log.e("SaveConfig", "Failed to save config with key: " + key);
        }
        getWritableDatabase().close();
    }

    @SuppressLint("Range")
    public String ReadConfig(String key) {

        if (key.equals("BrokerCode")){
            result="0";
        }

        query = "SELECT DataValue  FROM Config  Where KeyValue= '" + key + "' ;";

        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex("DataValue"));

        }
        cursor.close();
        return result;

    }

    @SuppressLint("Range")
    public void ReplicateGoodtype(Column column) {
        cursor = getWritableDatabase().rawQuery("SELECT COUNT(*) AS cntRec FROM GoodType WHERE GoodType = '" + column.getColumnFieldValue("GoodType") + "'", null);
        cursor.moveToFirst();

        int nc = cursor.getInt(cursor.getColumnIndex("cntRec"));
        if (nc == 0) {
            query = "INSERT INTO GoodType (GoodType,IsDefault) VALUES ('" + column.getColumnFieldValue("GoodType") + "','" + column.getColumnFieldValue("IsDefault") + "');";
            getWritableDatabase().execSQL(query);
        } else {
            query = "UPDATE GoodType SET IsDefault = '" + column.getColumnFieldValue("IsDefault") + "' WHERE GoodType = '" + column.getColumnFieldValue("GoodType") + "';";
            getWritableDatabase().execSQL(query);
        }
        cursor.close();
    }

    public void UpdateSearchColumn(Column column) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE BrokerColumn SET condition = ? WHERE ColumnCode = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.bindString(1, column.getCondition());
        statement.bindLong(2, Long.parseLong(column.getColumnCode()));
        statement.executeUpdateDelete();
        statement.close();
        db.close();
    }
    public void UpdateLocationService(LocationResult locationResult, String gpsDate) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put("Longitude", locationResult.getLastLocation().getLongitude());
            values.put("Latitude", locationResult.getLastLocation().getLatitude());
            values.put("Speed", locationResult.getLastLocation().getSpeed());
            values.put("BrokerRef", ReadConfig("BrokerCode"));
            values.put("GpsDate", gpsDate);
            db.insert("GpsLocation", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ClearSearchColumn() {
        query = "update BrokerColumn set condition = '' ";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public void ReplicateColumn(Column column, int appType) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("SortOrder", column.getColumnFieldValue("SortOrder"));
        values.put("ColumnName", column.getColumnFieldValue("ColumnName"));
        values.put("ColumnDesc", column.getColumnFieldValue("ColumnDesc"));
        values.put("GoodType", column.getColumnFieldValue("GoodType"));
        values.put("ColumnDefinition", column.getColumnFieldValue("ColumnDefinition"));
        values.put("ColumnType", column.getColumnFieldValue("ColumnType"));
        values.put("Condition", column.getColumnFieldValue("Condition"));
        values.put("OrderIndex", column.getColumnFieldValue("OrderIndex"));
        values.put("AppType", appType);

        // Check if the record already exists
        Cursor cursor = db.query("BrokerColumn", null, "ColumnName=?", new String[]{column.getColumnFieldValue("ColumnName")}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            // Update the record
            db.update("BrokerColumn", values, "ColumnName=?", new String[]{column.getColumnFieldValue("ColumnName")});
        } else {
            // Insert the record
            db.insert("BrokerColumn", null, values);
        }
        if (cursor != null) {
            cursor.close();
        }

        // Close the database connection
        db.close();
    }

    public void deleteColumn(String columnName) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("BrokerColumn", "ColumnName=?", new String[]{columnName});
        db.close();
    }

    public void deleteColumn() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM BrokerColumn");
            db.execSQL("DELETE FROM GoodType");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


    public void execQuery(String query) {
        if (query == null || query.isEmpty()) {
            return;
        }
        getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

}