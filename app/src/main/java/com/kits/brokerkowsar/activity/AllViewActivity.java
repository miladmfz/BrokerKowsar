package com.kits.brokerkowsar.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.adapters.ProductAdapter;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.Category;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.GoodGroup;
import com.kits.brokerkowsar.model.Product;
import com.kits.brokerkowsar.webService.APIClient;
import com.kits.brokerkowsar.webService.APIInterface;

import java.util.ArrayList;
import java.util.List;


public class AllViewActivity extends AppCompatActivity {


    APIInterface apiInterface;
    CallMethod callMethod;
    DatabaseHelper dbh;
    Toolbar toolbar;
    ArrayList<Category> categories = new ArrayList<>();
    RecyclerView rc;
    Category category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_view);


        Config();
        try {
            Handler handler = new Handler();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                handler.postDelayed(this::init, 100);
            }
        } catch (Exception ignored) {

        }


    }


    public void Config() {
        callMethod = new CallMethod(this);
        String databaseName = callMethod.ReadString("DatabaseName");
        dbh = new DatabaseHelper(this, databaseName);
        dbh.ClearSearchColumn();

        toolbar = findViewById(R.id.allview_toolbar);
        setSupportActionBar(toolbar);

        rc = findViewById(R.id.allview_rc);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);


    }


    public void init() {
        List<Category> categories = new ArrayList<>();

        List<GoodGroup> groupHeaders = dbh.getAllGroups(dbh.ReadConfig("GroupCodeDefult"));
        for (GoodGroup groupHeader : groupHeaders) {
            List<Product> products = new ArrayList<>();
            List<GoodGroup> groupRows = dbh.getAllGroups(groupHeader.getGoodGroupFieldValue("groupcode"));
            for (GoodGroup groupRow : groupRows) {
                Product product = new Product(
                        groupRow.getGoodGroupFieldValue("Name"),
                        Integer.parseInt(groupRow.getGoodGroupFieldValue("groupcode")),
                        Integer.parseInt(groupRow.getGoodGroupFieldValue("ChildNo")));
                products.add(product);
            }
            Category category = new Category(
                    groupHeader.getGoodGroupFieldValue("Name"),
                    products,
                    Integer.parseInt(groupHeader.getGoodGroupFieldValue("groupcode")),
                    Integer.parseInt(groupHeader.getGoodGroupFieldValue("ChildNo")));
            categories.add(category);
        }

        ProductAdapter adapter = new ProductAdapter(categories, App.getContext());
        RecyclerView rc = findViewById(R.id.allview_rc);
        rc.setAdapter(adapter);
        rc.setLayoutManager(new LinearLayoutManager(this));
    }


}