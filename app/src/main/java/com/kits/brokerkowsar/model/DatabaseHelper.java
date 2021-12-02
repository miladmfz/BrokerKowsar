package com.kits.brokerkowsar.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.kits.brokerkowsar.BuildConfig;
import com.kits.brokerkowsar.application.CallMethod;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class DatabaseHelper extends SQLiteOpenHelper {


    CallMethod callMethod;
    ArrayList<Column> columns;
    ArrayList<Column> columns_search;
    ArrayList<Good> goods;

    Cursor cursor;
    Column column;
    Good gooddetail;

    int limitcolumn;
    String query = "";
    String joinquery;
    String result = "";
    String SH_firstStart;
    String SH_selloff;
    String SH_grid;
    String SH_delay;
    String SH_brokerstack;
    String SH_prefactor_code;
    String SH_prefactor_good;
    String SH_itemamount;
    String SH_MenuBroker;
    boolean SH_activestack;
    boolean SH_real_amount;
    boolean SH_goodamount;
    int k = 0;

    //Replace(Replace(Cast(_______ as nvarchar(500)),char(1610),char(1740)),char(1603),char(1705))

    public DatabaseHelper(Context context, String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, 1);
        this.callMethod = new CallMethod(context);
        this.goods = new ArrayList<>();
    }

    public void DatabaseCreate() {
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS PreFactorRow (PreFactorRowCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE ,PreFactorRef INTEGER, GoodRef INTEGER, FactorAmount INTEGER, Shortage INTEGER, PreFactorDate TEXT,  Price INTEGER)");
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS Prefactor ( PreFactorCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, PreFactorDate TEXT," +
                " PreFactorTime TEXT, PreFactorKowsarCode INTEGER, PreFactorKowsarDate TEXT, PreFactorExplain TEXT, CustomerRef INTEGER, BrokerRef INTEGER)");
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS Favorites ( GoodRef INTEGER )");
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS Customer ( CustomerCode INTEGER, CustomerName TEXT, CustomerAddress TEXT, CustomerSum INTEGER )");
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS Config ( KeyValue TEXT Primary Key, DataValue TEXT)");
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS Units ( UnitCode INTEGER PRIMARY KEY, UnitName TEXT)");
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS BrokerColumn ( ColumnCode INTEGER PRIMARY KEY, SortOrder TEXT, ColumnName TEXT, ColumnDesc TEXT, GoodType TEXT, ColumnDefinition TEXT, ColumnType TEXT, Condition TEXT, OrderIndex TEXT, AppType INTEGER)");
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS GoodType ( GoodTypeCode INTEGER PRIMARY KEY, GoodType TEXT, IsDefault TEXT)");

        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'Good_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'Good_LastRepCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'GoodStack_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'GoodStack_LastRepCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'GoodsGrp_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'GoodsGrp_LastRepCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'GoodGroup_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'GoodGroup_LastRepCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'PropertyValue_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'PropertyValue_LastRepCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'City_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'City_LastRepCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'Address_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'Address_LastRepCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'Central_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'Central_LastRepCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'Customer_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'Customer_LastRepCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'CacheGroup_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'CacheGroup_LastRepCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'BrokerCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'BrokerCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'BrokerStack', '0' Where Not Exists(Select * From Config Where KeyValue = 'BrokerStack')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'MenuBroker', '0' Where Not Exists(Select * From Config Where KeyValue = 'MenuBroker')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'KsrImage_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'KsrImage_LastRepCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'VersionInfo', '" + BuildConfig.VERSION_NAME + "' Where Not Exists(Select * From Config Where KeyValue = 'VersionInfo')");


        getWritableDatabase().execSQL("Create Index IF Not Exists IX_GoodGroup_GoodRef on GoodGroup (GoodRef)");
        getWritableDatabase().execSQL("Create Index IF Not Exists IX_GoodGroup_GroupRef on GoodGroup (GoodGroupRef)");
        getWritableDatabase().execSQL("Create Index IF Not Exists IX_PreFactorRow_GoodRef on PreFactorRow (GoodRef)");
        getWritableDatabase().execSQL("Create Index IF Not Exists IX_PreFactorRow_PreFactorRef on PreFactorRow (PreFactorRef)");
        getWritableDatabase().execSQL("Create Index IF Not Exists IX_Good_GoodUnitRef on Good (GoodUnitRef)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_GoodStack_GoodRef ON GoodStack (GoodRef, StackRef)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodName ON Good (GoodName)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_KsrImage_ObjectRef ON KsrImage (ObjectRef)");

    }


    public void GetLimitColumn(String AppType) {

        try {
            query = "select Count(*) count from GoodType ";

            cursor = getWritableDatabase().rawQuery(query, null);
            cursor.moveToFirst();
            String goodtypecount = cursor.getString(cursor.getColumnIndex("count"));
            cursor.close();
            query = "select Count(*) count from BrokerColumn Where AppType = " + AppType;

            cursor = getWritableDatabase().rawQuery(query, null);
            cursor.moveToFirst();
            String columnscount = cursor.getString(cursor.getColumnIndex("count"));
            cursor.close();
            limitcolumn = Integer.parseInt(columnscount) / Integer.parseInt(goodtypecount);
        }catch (Exception e){
            callMethod.showToast( "تنظیم جدول مشکل دارد");
            Log.e("test",e.getMessage());
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
        this.SH_itemamount = callMethod.ReadString("ItemAmount");
        this.SH_activestack = callMethod.ReadBoolan("ActiveStack");
        this.SH_real_amount = callMethod.ReadBoolan("RealAmount");
        this.SH_goodamount = callMethod.ReadBoolan("GoodAmount");

        String stackCondition = "Where StackRef in (" + SH_brokerstack + ")";
        if (SH_activestack) {
            stackCondition = stackCondition + " And ActiveStack = 1";
        }
        joinquery = " , (Select ki.KsrImageCode From KsrImage ki Where ki.ObjectRef=g.GoodCode Order By ki.IsDefaultImage DESC, ki.KsrImageCode LIMIT 1) As KsrImageCode " +
                "FROM Good g ,FilterTable Join Units u on u.UnitCode = g.GoodUnitRef " +
                " Join (Select GoodRef, Sum(Amount) as StackAmount, Sum(ReservedAmount) as ReservedAmount,ActiveStack" +
                " From GoodStack " + stackCondition + " Group By GoodRef) gs on gs.GoodRef = g.GoodCode " +
                " Left Join (Select GoodRef, Sum(FactorAmount) FactorAmount , Sum(FactorAmount*Price) Price From PreFactorRow" +
                " Where PreFactorRef =" + SH_prefactor_code + " Group BY GoodRef) pf on pf.GoodRef = g.GoodCode " +
                " Left Join PreFactor h on h.PreFactorCode = " + SH_prefactor_code +
                " Left Join Customer c on c.CustomerCode=h.CustomerRef " +
                " Left Join CacheGoodGroup cgg on cgg.GoodRef = g.Goodcode ";


    }

    public String GetGoodTypeFromGood(String code) {
        query = "select GoodType from good where GoodCode = " + code;
        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        result = cursor.getString(cursor.getColumnIndex("GoodType"));
        cursor.close();
        return result;
    }

    public ArrayList<Column> GetColumns(String code, String goodtype, String AppType) {


        switch (AppType) {
            case "0"://        0-detail
                query = "Select * from BrokerColumn where " +
                        "Replace(Replace(Cast(GoodType as nvarchar(500)),char(1610),char(1740)),char(1603),char(1705)) = " +
                        "Replace(Replace(Cast('" + GetGoodTypeFromGood(code) + "' as nvarchar(500)),char(1610),char(1740)),char(1603),char(1705)) " +
                        "And AppType = 0";
                break;
            case "1"://        1-list
            case "2"://        2-basket
                GetLimitColumn(AppType);
                query = "Select * from BrokerColumn where AppType = " + AppType + " limit " + limitcolumn;
                break;
            case "3"://        3-search
                query = "Select * from BrokerColumn where " +
                        "Replace(Replace(Cast(GoodType as nvarchar(500)),char(1610),char(1740)),char(1603),char(1705)) = " +
                        "Replace(Replace(Cast('" + goodtype + "' as nvarchar(500)),char(1610),char(1740)),char(1603),char(1705)) " +
                        "And AppType = 3";
                break;
        }

        Log.e("test",query);
        columns = new ArrayList<>();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Column column = new Column();
                column.setColumnCode(cursor.getString(cursor.getColumnIndex("ColumnCode")));
                column.setSortOrder(cursor.getString(cursor.getColumnIndex("SortOrder")));
                column.setColumnName(cursor.getString(cursor.getColumnIndex("ColumnName")));
                column.setColumnDesc(cursor.getString(cursor.getColumnIndex("ColumnDesc")));
                column.setGoodType(cursor.getString(cursor.getColumnIndex("GoodType")));
                column.setColumnType(cursor.getString(cursor.getColumnIndex("ColumnType")));
                column.setColumnDefinition(cursor.getString(cursor.getColumnIndex("ColumnDefinition")));
                column.setCondition(cursor.getString(cursor.getColumnIndex("Condition")));
                column.setOrderIndex(cursor.getString(cursor.getColumnIndex("OrderIndex")));
                columns.add(column);
            }
        }
        assert cursor != null;
        cursor.close();
        return columns;
    }

    public String GetColumnscount() {

        query = "Select Count(*) result from BrokerColumn " ;
        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        int result = cursor.getInt(cursor.getColumnIndex("result"));
        cursor.close();

        return String.valueOf(result);
    }


    public ArrayList<Column> GetAllGoodType() {
        query = "Select * from GoodType ";
        columns = new ArrayList<>();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                column = new Column();
                column.setGoodType(cursor.getString(cursor.getColumnIndex("GoodType")));
                column.setIsDefault(cursor.getString(cursor.getColumnIndex("IsDefault")));
                columns.add(column);
            }
        }
        assert cursor != null;
        cursor.close();
        return columns;
    }

    public ArrayList<Good> getAllGood(String search, String aGroupCode) {

        GetPreference();

        columns_search = GetColumns("", "كتاب", "3");
        columns = GetColumns("", "", "1");

        search = search.replaceAll(" ", "%");

        query = "With FilterTable As (Select 0 as SecondField) SELECT ";
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

        query = query + joinquery ;
        k = 0;
        if(!search.equals("")) {
            for (Column column : columns_search) {
                if (k == 0) {
                    query = query + " Where (";
                }else{
                    query = query + " or ";
                }
                query = query + column.getColumnName() + " Like '%" + search + "%' ";
                k++;
            }
            query = query + " )";
        }else
        query = query + "where 1=1 ";

            if (SH_goodamount) {
                if (SH_real_amount) {
                    query = query + " And gs.StackAmount-gs.ReservedAmount > 0 ";
                } else {
                    query = query + " And gs.StackAmount > 0 ";
                }
            }


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
                    query = query + column.getColumnName();
                } else {
                    query = query + column.getColumnName() + " DESC ";
                }
                k++;
            }
        }
        query = query + " LIMIT " + SH_itemamount;

        Log.e("test",query);
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                gooddetail = new Good();
                for (Column column : columns) {
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
                }
                gooddetail.setCheck(false);
                goods.add(gooddetail);
            }
        }
        assert cursor != null;
        cursor.close();
        return goods;
    }


    public ArrayList<Good> getAllGood_Extended(String searchbox_result, String aGroupCode) {


        GetPreference();
        columns = GetColumns("", "", "1");


        query = "With FilterTable As (Select 0 as SecondField) SELECT ";
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
        query = query + joinquery;


        query = query + " Where  1=1 ";

        query = query + searchbox_result;

        if (SH_goodamount) {
            if (SH_real_amount) {
                query = query + " And gs.StackAmount-gs.ReservedAmount > 0 ";
            } else {
                query = query + " And gs.StackAmount > 0 ";
            }
        }

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
                    query = query + column.getColumnName();
                } else {
                    query = query + column.getColumnName() + " DESC ";
                }
                k++;
            }
        }


        query = query + " LIMIT " + SH_itemamount;
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                gooddetail = new Good();

                for (Column column : columns) {
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
                }

                gooddetail.setCheck(false);
                goods.add(gooddetail);
            }
        }
        assert cursor != null;
        cursor.close();
        return goods;
    }


    public Good getGoodByCode(String code) {
        GetPreference();
        columns = GetColumns(code, "", "0");

        query = "With FilterTable As (Select 0 as SecondField) SELECT ";
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
        query = query + joinquery;
        query = query + " WHERE GoodCode = " + code;

        Log.e("test",query);
        gooddetail = new Good();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            for (Column column : columns) {

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
            }
            gooddetail.setCheck(false);
        }
        cursor.close();
        return gooddetail;
    }

    public ArrayList<Good> getAllGood_ByDate(String xDayAgo) {
        GetPreference();
        columns = GetColumns("", "", "1");

        query = " With FilterTable As (Select 1 as SecondField) SELECT ";
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
        query = query + joinquery + " Where Date>='" + xDayAgo + "' ";


        if (SH_goodamount) {
            if (SH_real_amount) {
                query = query + " And gs.StackAmount-gs.ReservedAmount > 0 ";
            } else {
                query = query + " And gs.StackAmount > 0 ";
            }
        }
        query = query + " order by ";
        k = 0;
        for (Column column : columns) {
            if (!column.getOrderIndex().equals("0")) {
                if (k != 0) {
                    query = query + " , ";
                }
                if (Integer.parseInt(column.getOrderIndex()) > 0) {
                    query = query + column.getColumnName();
                } else {
                    query = query + column.getColumnName() + " DESC ";
                }
                k++;
            }
        }
        goods = new ArrayList<>();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                gooddetail = new Good();
                for (Column column : columns) {
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
                }
                gooddetail.setCheck(false);
                goods.add(gooddetail);
            }
        }

        assert cursor != null;
        cursor.close();
        return goods;
    }

    public ArrayList<Good> getAllGood_pfcode() {
        GetPreference();
        columns = GetColumns("", "", "2");
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

    public void InsertPreFactorHeader(String Customer, String CustomerRef) {


        String Date = Utilities.getCurrentShamsidate();
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String strDate = sdf.format(calendar.getTime());

        UserInfo user = new UserInfo();
        query = "Select * From Config Where KeyValue = 'BrokerCode' ";
        String key;
        String val = "";
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                key = cursor.getString(cursor.getColumnIndex("KeyValue"));
                val = cursor.getString(cursor.getColumnIndex("DataValue"));
                switch (key) {
                    case "ActiveCode":
                        user.setActiveCode(val);
                        break;
                    case "BrokerCode":
                        user.setBrokerCode(val);
                        break;

                }
            }
        }
        getWritableDatabase().execSQL("INSERT INTO Prefactor" +
                "(PreFactorKowsarCode,PreFactorDate ,PreFactorKowsarDate ,PreFactorTime,PreFactorExplain,CustomerRef,BrokerRef) " +
                "VALUES(0,'" + Date + "','-----','" + strDate + "','" + Customer + "','" + CustomerRef + "','" + val + "'); ");
    }

    public void InsertPreFactor(String pfcode, String goodcode, String FactorAmount, String price, String BasketFlag) {
        if (Integer.parseInt(BasketFlag) > 0) {
            if (Float.parseFloat(price) >= 0) {
                query = "Update PreFactorRow set FactorAmount = " + FactorAmount + ", Price = " + price + " Where PreFactorRowCode=" + BasketFlag;
            } else {
                query = "Update PreFactorRow set FactorAmount = " + FactorAmount + " Where PreFactorRowCode=" + BasketFlag;
            }
            getWritableDatabase().execSQL(query);
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

                query = "INSERT INTO PreFactorRow(PreFactorRef, GoodRef, FactorAmount, Price) Select " + pfcode + "," + goodcode + ", " + FactorAmount + "," +
                        "Case When " + price + ">=0 Then " + price + " Else Case PriceTip When 1 Then SellPrice1 When 2 Then SellPrice2 When 3 Then SellPrice3 When 4 Then SellPrice4 When 5 Then SellPrice5 When 6 Then SellPrice6 End" +
                        "* Case When SellPriceType = 1 Then MaxSellPrice/100 Else 1 End End " +
                        "From Good g Join PreFactor h on 1=1 Join Customer c on h.CustomerRef=c.CustomerCode " +
                        "Where h.PreFactorCode =" + pfcode + " And GoodCode = " + goodcode;

                getWritableDatabase().execSQL(query);
            }
            cursor.close();
        }
    }

    public ArrayList<PreFactor> getAllPrefactorHeader(String name) {


        query = " SELECT h.*, s.SumAmount , s.SumPrice , s.RowCount ,n.CentralName CustomerName FROM PreFactor h Join Customer c  on c.CustomerCode = h.CustomerRef " +
                " join Central n on c.CentralRef=n.CentralCode "
                + " Left Join (SELECT P.PreFactorRef, sum(p.FactorAmount) as SumAmount , sum(p.FactorAmount * p.Price*g.DefaultUnitValue) as SumPrice, count(*) as RowCount "
                + " From Good g Join Units on UnitCode = GoodUnitRef  Join PreFactorRow p on GoodRef = GoodCode  Where IfNull(PreFactorRef, 0)>0 "
                + " Group BY PreFactorRef ) s on h.PreFactorCode = s.PreFactorRef "
                + " Where n.CentralName Like '%" + name + "%'"
                + " Order By h.PreFactorCode DESC";

        ArrayList<PreFactor> prefactor_header = new ArrayList<>();

        cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                PreFactor prefactor = new PreFactor();
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

                prefactor_header.add(prefactor);
            }
        }
        assert cursor != null;
        cursor.close();
        return prefactor_header;
    }

    public ArrayList<PreFactor> getAllPrefactorHeaderopen() {
        query = "SELECT h.*, s.SumAmount , s.SumPrice, s.RowCount ,n.CentralName CustomerName  " +
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

                prefactor_header.add(prefactor);
            }
        }
        assert cursor != null;
        cursor.close();
        return prefactor_header;
    }


    public ArrayList<Good> getAllPreFactorRows(String name, String aPreFactorCode) {
        name = name.replaceAll(" ", "%");
        GetPreference();
        columns = GetColumns("", "", "2");


        query = "With FilterTable As (Select 0 as SecondField) SELECT ";

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

        query = query + " FROM Good g ,FilterTable " +
                "Join PreFactorRow pf on GoodRef = GoodCode " +
                "Join Units u on u.UnitCode = g.GoodUnitRef  " +
                "Where (GoodName Like '%" + name + "%' and PreFactorRef = " + aPreFactorCode + ") order by GoodCode DESC ";



        cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                gooddetail = new Good();
                for (Column column : columns) {

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
                }
                goods.add(gooddetail);

            }
        }
        assert cursor != null;
        cursor.close();
        return goods;
    }

    public void UpdatePreFactorHeader_Customer(String pfcode, String Customer) {
        query = "Update Prefactor set CustomerRef='" + Customer + "' where PreFactorCode = " + pfcode;
        getWritableDatabase().execSQL(query);
        query = "Select * From ( Select Case PriceTip " +
                " When 1 Then SellPrice1 When 2 Then SellPrice2 When 3 Then SellPrice3 " +
                " When 4 Then SellPrice4 When 5 Then SellPrice5 When 6 Then SellPrice6 " +
                "End * Case When SellPriceType = 1 Then MaxSellPrice/100 Else 1 End as NewPrice," +
                " Price, GoodCode From PreFactorRow p " +
                "Join PreFactor h on h.PreFactorCode = p.PreFactorRef " +
                "Join Customer on CustomerCode = CustomerRef " +
                "Join Good g on GoodRef = GoodCode Where h.PreFactorCode = " + pfcode + ") ss " +
                "Where Price<> NewPrice";
        cursor = getWritableDatabase().rawQuery(query, null);


        if (cursor != null) {
            while (cursor.moveToNext()) {

                getWritableDatabase().execSQL("Update PreFactorRow set Price=" + cursor.getString(cursor.getColumnIndex("NewPrice"))
                        + " Where PreFactorRef =" + pfcode + " And GoodRef =" + cursor.getString(cursor.getColumnIndex("GoodCode")));
            }
        }
        assert cursor != null;
        cursor.close();
    }

    public Integer GetLastPreFactorHeader() {

        query = "SELECT PreFactorCode FROM Prefactor Where PreFactorKowsarCode = 0 order by PreFactorCode DESC";

        int Res = 0;
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            Res = cursor.getInt(cursor.getColumnIndex("PreFactorCode"));
        }
        cursor.close();
        return Res;
    }

    public void update_explain(String pfcode, String explain) {
        query = "Update PreFactor set PreFactorExplain = '" + explain + "' Where IfNull(PreFactorCode,0)=" + pfcode;
        getWritableDatabase().execSQL(query);
    }

    public void DeletePreFactorRow(String pfcode, String rowcode) {
        query = " Delete From PreFactorRow Where IfNull(PreFactorRef,0)=" + pfcode + " And (PreFactorRowCode =" + rowcode + " or 0=" + rowcode + ")";
        getWritableDatabase().execSQL(query);
    }

    public void DeletePreFactor(String pfcode) {
        query = " Delete From Prefactor Where IfNull(PreFactorCode,0)=" + pfcode;
        getWritableDatabase().execSQL(query);
    }

    public void DeleteEmptyPreFactor() {
        query = " DELETE FROM Prefactor WHERE PreFactorCode NOT IN (SELECT PreFactorRef FROM PrefactorRow )";
        getWritableDatabase().execSQL(query);
    }

    public void UpdatePreFactor(String PreFactorCode, String PreFactorKowsarCode, String PreFactorDate) {
        query = "Update PreFactor Set PreFactorKowsarCode = " + PreFactorKowsarCode + ", PreFactorKowsarDate = '" + PreFactorDate + "' Where ifnull(PreFactorCode ,0)= " + PreFactorCode + ";";
        getWritableDatabase().execSQL(query);
    }

    public String getFactorSum(String pfcode) {
        query = " select sum(FactorAmount*price*DefaultUnitValue) as result From PreFactorRow join Good on GoodRef=GoodCode Where IfNull(PreFactorRef,0)=" + pfcode;
        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        long result = cursor.getLong(cursor.getColumnIndex("result"));
        cursor.close();

        return String.valueOf(result);
    }

    public String getFactorSumAmount(String pfcode) {
        query = "select sum(FactorAmount) as result From PreFactorRow join Good on GoodRef=GoodCode Where IfNull(PreFactorRef,0)=" + pfcode;
        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        int result = cursor.getInt(cursor.getColumnIndex("result"));
        cursor.close();
        return String.valueOf(result);
    }


    public String getFactordate(String pfcode) {
        query = "select PreFactorDate as result From Prefactor  Where IfNull(PreFactorCode,0)=" + pfcode;
        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        result = cursor.getString(cursor.getColumnIndex("result"));
        cursor.close();
        return result;
    }

    public String getFactorCustomer(String pfcode) {

        query = "SELECT n.CentralName CustomerName  FROM PreFactor h " +
                " Join Customer c  on c.CustomerCode = h.CustomerRef " +
                " join Central n on c.CentralRef=n.CentralCode " +
                " Where IfNull(PreFactorCode,0)= " + pfcode;

        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex("CustomerName"));
            cursor.close();
        } else {
            result = "فاکتوری انتخاب نشده";
        }
        return result;
    }

    public long getsum_sumfactor() {
        query = "select sum(price) as sm From PreFactorRow";

        long Res = 0;
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            Res = cursor.getLong(cursor.getColumnIndex("sm"));
        }
        cursor.close();
        return Res;
    }

    public ArrayList<Customer> AllCustomer(String name, boolean aOnlyActive) {

        name = name.replaceAll(" ", "%");
        query = "SELECT u.CustomerCode,u.PriceTip,CentralName,Address,Manager,Mobile,Phone,Delegacy, CityName, Bestankar, Active, CentralPrivateCode, EtebarNaghd" +
                ",EtebarCheck, Takhfif, MobileName, Email, Fax, ZipCode, PostCode FROM Customer u " +
                "join Central c on u.CentralRef= c.CentralCode " +
                "Left join Address d on u.AddressRef=d.AddressCode " +
                "Left join City y on d.CityCode=y.CityCode" +
                " Where (CentralName Like '%" + name + "%' or CustomerCode Like '%" + name + "%' or  Manager Like '%" + name + "%')";
        if (aOnlyActive) {
            query = query + " And Active = 0";
        }

        query = query + " order by CustomerCode DESC  LIMIT 200";
        ArrayList<Customer> Customers = new ArrayList<>();

        cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Customer customerdetail = new Customer();
                customerdetail.setCustomerCode(cursor.getInt(cursor.getColumnIndex("CustomerCode")));
                customerdetail.setCustomerName(cursor.getString(cursor.getColumnIndex("CentralName")));
                customerdetail.setManager(cursor.getString(cursor.getColumnIndex("Manager")));
                customerdetail.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
                customerdetail.setPhone(cursor.getString(cursor.getColumnIndex("Phone")));
                customerdetail.setBestankar(cursor.getInt(cursor.getColumnIndex("Bestankar")));


                Customers.add(customerdetail);
            }
        }
        assert cursor != null;
        cursor.close();
        return Customers;
    }

    public Integer Customer_check(String name) {
        int res = 0;
        query = "select centralcode from central where d_codemelli ='" + name + "'";

        cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                res = cursor.getInt(cursor.getColumnIndex("CentralCode"));
            }
        }
        assert cursor != null;
        cursor.close();
        return res;
    }

    public ArrayList<Customer> city() {

        query = "SELECT * from city";
        ArrayList<Customer> city = new ArrayList<>();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Customer customerdetail = new Customer();
                customerdetail.setCityName(cursor.getString(cursor.getColumnIndex("CityName")));
                customerdetail.setCityCode(cursor.getString(cursor.getColumnIndex("CityCode")));
                city.add(customerdetail);
            }
        }
        assert cursor != null;
        cursor.close();
        return city;
    }
    public String GetksrImage(String code) {
        query = "select ksrImageCode from ksrImage where ObjectRef = " + code+ " limit 1";
        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        result = cursor.getString(cursor.getColumnIndex("KsrImageCode"));
        cursor.close();
        return result;
    }



    public ArrayList<Good> GetksrImageCodes(String code) {
        query = "SELECT ksrImageCode from KsrImage where ObjectRef = " + code;
        ArrayList<Good> Goods = new ArrayList<>();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Good gooddetail = new Good();
                gooddetail.setGoodFieldValue("KsrImageCode",cursor.getString(cursor.getColumnIndex("KsrImageCode")));
                Goods.add(gooddetail);
            }
        }
        assert cursor != null;
        cursor.close();
        return Goods;
    }

    public ArrayList<GoodGroup> getAllGroups(String GL) {

        query = "SELECT * FROM GoodsGrp WHERE 1=1 ";
        if (Integer.parseInt(GL) > 0) {
            query = query + " And ((L1=" + GL + " And L2=0) or (L2=" + GL + " And L3=0) or (L3=" + GL + " And L4=0) or (L4=" + GL + " And L5=0) or (L5=" + GL + "))";
        } else {
            query = query + " And L1>0 and L2=0 order by 1 desc";
        }

        ArrayList<GoodGroup> groups = new ArrayList<>();

        Log.e("test",query);
        cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor != null) {
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
        }
        assert cursor != null;
        cursor.close();
        return groups;
    }

    public ArrayList<GoodGroup> getmenuGroups() {
        GetPreference();
        if (!SH_MenuBroker.equals(""))
            query = "SELECT * FROM GoodsGrp Where Groupcode in (" + SH_MenuBroker + ")";
        else
            query = "SELECT * FROM GoodsGrp Where Groupcode in (9999)";

        ArrayList<GoodGroup> groups = new ArrayList<>();
        Log.e("test", query);
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
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
        }
        assert cursor != null;
        cursor.close();
        return groups;
    }

    public UserInfo LoadPersonalInfo() {
        UserInfo user = new UserInfo();
        query = "Select * From Config";
        String key;
        String val;
        cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                key = cursor.getString(cursor.getColumnIndex("KeyValue"));
                val = cursor.getString(cursor.getColumnIndex("DataValue"));

                switch (key) {
                    case "Email":
                        user.setEmail(val);
                        break;
                    case "NameFamily":
                        user.setNameFamily(val);
                        break;
                    case "Address":
                        user.setAddress(val);
                        break;
                    case "Mobile":
                        user.setMobile(val);
                        break;
                    case "Phone":
                        user.setPhone(val);
                        break;
                    case "BirthDate":
                        user.setBirthDate(val);
                        break;
                    case "PostalCode":
                        user.setPostalCode(val);
                        break;
                    case "MelliCode":
                        user.setMelliCode(val);
                        break;
                    case "ActiveCode":
                        user.setActiveCode(val);
                        break;
                    case "BrokerCode":
                        Log.e("test_setBrokerCode",val);
                        user.setBrokerCode(val);
                        break;
                }
            }
        }
        return user;
    }

    public void SavePersonalInfo(UserInfo user) {

        if (!user.getBrokerCode().equals("")) {
            query = " Update Config set DataValue = '" + user.getBrokerCode() + "' Where KeyValue = 'BrokerCode';";
            getWritableDatabase().execSQL(query);
            query = " Insert Into Config(KeyValue, DataValue) " +
                    "  Select 'BrokerCode', '" + user.getBrokerCode() + "' Where Not Exists(Select * From Config Where KeyValue = 'BrokerCode');";
            getWritableDatabase().execSQL(query);
        }

    }

    public void SaveConfig(String key,String Value) {

        query = " Insert Into Config(KeyValue, DataValue) Select '"+key+"', '" + Value + "' Where Not Exists(Select * From Config Where KeyValue = '"+key+"');";
        getWritableDatabase().execSQL(query);
        query = " Update Config set DataValue = '" + Value + "' Where KeyValue = '"+key+"' ;";
        getWritableDatabase().execSQL(query);

    }

    public String ReadConfig(String key) {

        query = "SELECT DataValue  FROM Config  Where KeyValue= '"+key+"' ;";

        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex("DataValue"));
            cursor.close();
        }
        return result;

    }

    public void ReplicateGoodtype(Column column) {
        cursor = getWritableDatabase().rawQuery("Select Count(*) AS cntRec From GoodType Where GoodType = '" + column.getColumnFieldValue("GoodType") + "'", null);
        cursor.moveToFirst();

        int nc = cursor.getInt(cursor.getColumnIndex("cntRec"));
        if (nc == 0) {
            getWritableDatabase().execSQL("INSERT INTO GoodType (GoodType,IsDefault)" +
                    " VALUES ('" + column.getColumnFieldValue("GoodType") +
                    "','" + column.getColumnFieldValue("IsDefault") + "'); ");
        }
        cursor.close();
    }

    public void UpdateSearchColumn(Column column) {
        query = "update BrokerColumn set condition = '" + column.getCondition() + "' where ColumnCode= " + column.getColumnCode();
        getWritableDatabase().execSQL(query);
    }

    public void ReplicateColumn(Column column, Integer Apptype) {

        query = "INSERT INTO BrokerColumn" +
                "(SortOrder,ColumnName ,ColumnDesc ,GoodType,ColumnDefinition,ColumnType,Condition,OrderIndex,AppType) " +
                " VALUES ('" + column.getColumnFieldValue("SortOrder") +
                "','" + column.getColumnFieldValue("ColumnName") +
                "','" + column.getColumnFieldValue("ColumnDesc") +
                "','" + column.getColumnFieldValue("GoodType") +
                "','" + column.getColumnFieldValue("ColumnDefinition") +
                "','" + column.getColumnFieldValue("ColumnType") +
                "','" + column.getColumnFieldValue("Condition") +
                "','" + column.getColumnFieldValue("OrderIndex") +
                "'," + Apptype + "); ";
        getWritableDatabase().execSQL(query);
    }

    public void deleteColumn() {
        getWritableDatabase().execSQL("delete from BrokerColumn");
    }

    public void ExecQuery(String Query) {
        getWritableDatabase().execSQL(Query);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

}