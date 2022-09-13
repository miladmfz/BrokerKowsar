package com.kits.brokerkowsar.application;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.Executor;


public class WManager extends Worker {

    Context mcontext;
    Replication replication;
    CallMethod callMethod ;

    public WManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mcontext = context;
        callMethod=new CallMethod(context);


    }

    @NonNull
    @Override

    public Result doWork() {
        AutomaticReplication();
        return Result.success();
    }

    public void AutomaticReplication() {
            replication = new Replication(getApplicationContext());
            replication.DoingReplicateAuto();
    }
}
