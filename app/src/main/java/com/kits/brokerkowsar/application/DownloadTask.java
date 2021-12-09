package com.kits.brokerkowsar.application;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.kits.brokerkowsar.activity.SplashActivity;
import com.kits.brokerkowsar.model.DatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask {

    private static final String TAG = "Download Task";
    private Context context;
    CallMethod callMethod;
    private String downloadUrl = "", downloadFileName = "";
    private ProgressDialog progressDialog;

    public DownloadTask(Context context, String downloadUrl) {
        Log.e("test=","1");
        this.context = context;

        this.downloadUrl = downloadUrl;
        callMethod = new CallMethod(context);
        Log.e("test=","2");
        downloadFileName = downloadUrl.substring(downloadUrl.lastIndexOf('/'), downloadUrl.length());//Create file name by picking download file name from URL
        Log.e(TAG, downloadFileName);
        Log.e(TAG, downloadUrl);
        Log.e("test=","3");
        //Start Downloading Task
        new DownloadingTask().execute();
        Log.e("test=","4");
    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File apkStorage1 = null;
        File databasedir = null;
        File databasefile = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            Log.e("test=","5");
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("در حال بارگیری...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Log.e("test=","6");
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.e("test=","7");
            try {
                Log.e("test=","8");
                if (outputFile != null) {
                    progressDialog.dismiss();
                    Log.e("test=","9");

                    callMethod.EditString("UseSQLiteURL", "/data/data/com.kits.brokerkowsar/databases/" + callMethod.ReadString("EnglishCompanyNameUse") + "/KowsarDb.sqlite");
                    DatabaseHelper dbh = new DatabaseHelper(context, callMethod.ReadString("UseSQLiteURL"));
                    dbh.DatabaseCreate();
                    Intent intent = new Intent(context, SplashActivity.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                    Log.e("test=","10");
                } else {
                    Log.e("test=","11");
                    new Handler().postDelayed(() -> {
                        Log.e("test=","12");
                    }, 3000);
                    Log.e("test=","13");
                    Log.e(TAG, "لطفا با مرکز تماس بگیرید");

                }
            } catch (Exception e) {
                Log.e("test=","14");
                e.printStackTrace();


                new Handler().postDelayed(() -> {
                    Log.e("test=","15");
                }, 3000);
                Log.e(TAG, "لطفا با مرکز تماس بگیرید" + e.getLocalizedMessage());

            }


            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection

                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                Log.e("test=","1");
                c.connect();//connect the URL Connection
                Log.e("test=","2");

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());

                }


                //Get File if SD card is present
                if (new CheckForSDCard().isSDCardPresent()) {

                    apkStorage1 = new File(context.getApplicationInfo().dataDir + "/databases");//"+callMethod.ReadString("EnglishCompanyNameUse"));
                    apkStorage = new File(context.getApplicationInfo().dataDir + "/databases/" + callMethod.ReadString("EnglishCompanyNameUse"));
                } else
                    Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();


                if (!apkStorage1.exists()) {
                    apkStorage1.mkdir();
                    Log.e(TAG, "Directory Created.");
                    Log.e(TAG, apkStorage.getPath());
                }

                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                    Log.e(TAG, apkStorage.getPath());
                }
                //If File is not present create directory

                outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                    Log.e(TAG, outputFile.getPath());
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection



                Log.e("test=","3");

                byte[] buffer = new byte[10240];//Set buffer type
                int len1 = 0;//init length
                Log.e("test=","4");
                while ((len1 = is.read(buffer)) != -1) {
                    Log.e("test=","5");
                    fos.write(buffer, 0, len1);//Write new file
                }
                Log.e("test=","6");
                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error hasan " + e.getMessage());
            }

            return null;
        }
    }
}

