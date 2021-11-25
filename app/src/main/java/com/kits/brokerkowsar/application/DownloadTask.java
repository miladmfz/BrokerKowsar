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
        this.context = context;

        this.downloadUrl = downloadUrl;
        callMethod = new CallMethod(context);

        downloadFileName = downloadUrl.substring(downloadUrl.lastIndexOf('/'), downloadUrl.length());//Create file name by picking download file name from URL
        Log.e(TAG, downloadFileName);
        Log.e(TAG, downloadUrl);

        //Start Downloading Task
        new DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File apkStorage1 = null;
        File databasedir = null;
        File databasefile = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("در حال بارگیری...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    progressDialog.dismiss();


                    callMethod.EditString("UseSQLiteURL", "/data/data/com.kits.brokerkowsar/databases/" + callMethod.ReadString("EnglishCompanyNameUse") + "/KowsarDb.sqlite");
                    DatabaseHelper dbh = new DatabaseHelper(context, callMethod.ReadString("UseSQLiteURL"));
                    dbh.DatabaseCreate();
                    Intent intent = new Intent(context, SplashActivity.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                } else {

                    new Handler().postDelayed(() -> {

                    }, 3000);

                    Log.e(TAG, "لطفا با مرکز تماس بگیرید");

                }
            } catch (Exception e) {
                e.printStackTrace();


                new Handler().postDelayed(() -> {

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
                c.connect();//connect the URL Connection

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

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

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

