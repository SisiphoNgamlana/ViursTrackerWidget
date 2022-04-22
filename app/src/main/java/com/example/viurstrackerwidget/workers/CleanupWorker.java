package com.example.viurstrackerwidget.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.example.viurstrackerwidget.workers.Constants.CLEANING;

public class CleanupWorker extends Worker {

    public CleanupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = new Data.Builder()
                .putString(CLEANING, "Cleaning...")
                .build();

        return Result.success(data);
    }
}
