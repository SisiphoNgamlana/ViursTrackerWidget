package com.example.viurstrackerwidget.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.example.viurstrackerwidget.workers.Constants.DATA_OUTPUT;
import static com.example.viurstrackerwidget.workers.Constants.STRING_URL;

public class DownloadJsonWorker extends Worker {
    public DownloadJsonWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String inputUrl = getInputData().getString(STRING_URL);
        Data.Builder builder = new Data.Builder();
        builder.putString( DATA_OUTPUT, VirusWorkerUtils.processJson(inputUrl));
        Data outputData = builder.build();

        return Result.success(outputData);
    }
}
