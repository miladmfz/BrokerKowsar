package com.kits.brokerkowsar.application;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.button.MaterialButton;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.activity.SearchActivity;
import com.kits.brokerkowsar.model.Column;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.webService.APIClient;
import com.kits.brokerkowsar.webService.APIInterface;

import java.util.ArrayList;

public class Search_box {

    private final Context mContext;

    private final DatabaseHelper dbh;

    APIInterface apiInterface;

    String Where;
    Spinner spinner;
    ArrayList<Column> Goodtype;
    ArrayList<String> Goodtype_array = new ArrayList<>();
    ArrayList<Column> Columns;
    LinearLayoutCompat layout_view;
    MaterialButton btn_search;
    Dialog dialog;
    CallMethod callMethod;

    public Search_box(Context context) {
        this.mContext = context;
        this.Where = "";
        callMethod = new CallMethod(mContext);

        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));

        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

    }


    public void search_pro() {


        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//title laye nadashte bashim
        dialog.setContentView(R.layout.search_box);
        spinner = dialog.findViewById(R.id.search_box_spinner);
        layout_view = dialog.findViewById(R.id.search_box_layout_pro);


        int i = 0;
        int j = 0;
        Goodtype = dbh.GetAllGoodType();
        for (Column Column_Goodtype : Goodtype) {
            Goodtype_array.add(Column_Goodtype.getColumnFieldValue("goodtype"));
            if (Integer.parseInt(Column_Goodtype.getColumnFieldValue("IsDefault")) == 1) {
                j = i;
            }
            i++;
        }

        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(mContext,
                android.R.layout.simple_spinner_item, Goodtype_array);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);
        spinner.setSelection(j);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                layout_view.removeAllViews();
                pro_c(Goodtype_array.get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        dialog.show();

    }


    public void pro_c(String Goodtype) {

        try{
            Columns = dbh.GetColumns("", Goodtype, "3");
        }catch (Exception E){
            Log.e("test",E.getMessage());

        }
        Log.e("test","0");
        Log.e("test",Columns.size()+"");

        for (Column Column : Columns) {
            Log.e("test","1");

            Column.setSearch("");
            Log.e("test","2");

            if (Integer.parseInt(Column.getColumnFieldValue("SortOrder")) > 1) {
                Log.e("test","3");

                layout_view.setOrientation(LinearLayoutCompat.VERTICAL);
                LinearLayoutCompat layout_view_child = new LinearLayoutCompat(mContext);
                layout_view_child.setOrientation(LinearLayoutCompat.HORIZONTAL);
                layout_view_child.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                layout_view_child.setWeightSum(1);
                layout_view_child.setPadding(5, 5, 5, 5);

                TextView extra_TextView1 = new TextView(mContext);
                extra_TextView1.setText(NumberFunctions.PerisanNumber(Column.getColumnFieldValue("ColumnDesc")));
                extra_TextView1.setBackgroundResource(R.color.white);
                extra_TextView1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 0.7));
                extra_TextView1.setTextSize(14);
                extra_TextView1.setPadding(2, 2, 2, 2);
                extra_TextView1.setGravity(Gravity.CENTER);
                extra_TextView1.setTextColor(mContext.getColor(R.color.grey_1000));
                layout_view_child.addView(extra_TextView1);

                EditText extra_EditText = new EditText(mContext);
                extra_EditText.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 0.3));
                extra_EditText.setTextSize(15);
                extra_EditText.setId(Integer.parseInt(Column.getColumnFieldValue("sortorder")));
                extra_EditText.setHint(Column.getColumnFieldValue("ColumnCode"));
                extra_EditText.setText(Column.getColumnFieldValue("Condition"));
                extra_EditText.setHintTextColor(mContext.getColor(R.color.white));
                extra_EditText.setId(View.generateViewId());
                extra_EditText.setPadding(2, 2, 2, 2);
                extra_EditText.setGravity(Gravity.CENTER);
                extra_EditText.setTextColor(mContext.getColor(R.color.grey_1000));
                layout_view_child.addView(extra_EditText);


                layout_view.addView(layout_view_child);


            }
        }
        Log.e("test","4");


        btn_search = new MaterialButton(mContext);

        btn_search.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        btn_search.setText(NumberFunctions.PerisanNumber("اعمال فیلتر ها"));
        btn_search.setTextSize(12);
        btn_search.setTextColor(mContext.getColor(R.color.grey_1000));
        btn_search.setStrokeColor(ColorStateList.valueOf(mContext.getColor(R.color.grey_1000)));
        btn_search.setStrokeWidth(2);
        btn_search.setBackgroundTintList(ColorStateList.valueOf(mContext.getColor(R.color.white)));
        btn_search.setOnClickListener(v -> {
            for (int i = 0; i < layout_view.getChildCount(); i++) {
                if (layout_view.getChildAt(i) instanceof LinearLayoutCompat) {
                    LinearLayoutCompat LinearLayoutCompat = (androidx.appcompat.widget.LinearLayoutCompat) layout_view.getChildAt(i);
                    for (int j = 0; j < LinearLayoutCompat.getChildCount(); j++) {
                        if (LinearLayoutCompat.getChildAt(j) instanceof EditText) {
                            EditText et = (EditText) LinearLayoutCompat.getChildAt(j);
                            for (Column Column : Columns) {
                                if (et.getHint().toString().equals(Column.getColumnFieldValue("ColumnCode"))) {
                                    Column.setSearch(NumberFunctions.EnglishNumber(et.getText().toString()));
                                    Column.setCondition(NumberFunctions.EnglishNumber(et.getText().toString()));
                                    dbh.UpdateSearchColumn(Column);

                                }
                            }
                        }
                    }
                }
            }
            Where=" And GoodType= '"+Goodtype+"' ";
            for (Column Column : Columns) {
                if (!Column.getColumnFieldValue("search").equals("")) {
                    if(Column.getColumnType().equals("0")){
                        if (!Column.getColumnName().equals("")){
                            if (!Column.getColumnFieldValue("columndefinition").equals(""))
                                Where = Where + " And Replace(Replace(" + Column.getColumnFieldValue("columndefinition") + ",char(1740),char(1610)),char(1705),char(1603)) Like '%" + dbh.GetRegionText(Column.getColumnFieldValue("search")) + "%'  ";
                            else
                                Where = Where + " And Replace(Replace(" + Column.getColumnFieldValue("ColumnName") + ",char(1740),char(1610)),char(1705),char(1603)) Like '%" + dbh.GetRegionText(Column.getColumnFieldValue("search")) + "%' ";
                        }else{
                            String search_condition= " '%" + dbh.GetRegionText(Column.getColumnFieldValue("search")) + "%' ";
                            Where = Where + " And " + Column.getColumnFieldValue("columndefinition") ;
                            Where=Where.replace("SearchCondition",search_condition);
                        }
                    }else{
                        if (!Column.getColumnName().equals("")){
                            if (!Column.getColumnFieldValue("columndefinition").equals(""))
                                Where = Where + " And " + Column.getColumnFieldValue("columndefinition") + " Like '%" + dbh.GetRegionText(Column.getColumnFieldValue("search")) + "%'  ";
                            else
                                Where = Where + " And " + Column.getColumnFieldValue("ColumnName") + " Like '%" + dbh.GetRegionText(Column.getColumnFieldValue("search")) + "%' ";
                        }else{
                            String search_condition= " '%" + dbh.GetRegionText(Column.getColumnFieldValue("search")) + "%' ";
                            Where = Where + " And " + Column.getColumnFieldValue("columndefinition") ;
                            Where=Where.replace("SearchCondition",search_condition);
                        }
                    }
                }
            }

            SearchActivity activity = (SearchActivity) mContext;
            activity.proSearchCondition = Where;
            activity.PageMoreData = "0";
            activity.GetDataFromDataBase();
            dialog.dismiss();

        });
        layout_view.addView(btn_search);
    }


}
