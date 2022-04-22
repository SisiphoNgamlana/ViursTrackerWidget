package com.example.viurstrackerwidget.data;

import android.app.Application;

import com.example.viurstrackerwidget.workers.CleanupWorker;
import com.example.viurstrackerwidget.workers.DownloadJsonWorker;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import static com.example.viurstrackerwidget.workers.Constants.API_URL;
import static com.example.viurstrackerwidget.workers.Constants.JSON_PROCESSING_WORK_NAME;
import static com.example.viurstrackerwidget.workers.Constants.TAG_OUTPUT;

public class VirusViewModel extends AndroidViewModel {

    public static final String STRING_URL = "api_url";
    private WorkManager workManager;
    private LiveData<List<WorkInfo>> savedWorkInfo;
    private String outputData;

    public VirusViewModel(@NonNull Application application) {
        super(application);
        workManager = WorkManager.getInstance(application);
        savedWorkInfo = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT);
    }

    public void setOutputData(String outputData) {
        this.outputData = outputData;
    }

    public String getOutputData() {
        return outputData;
    }

    public LiveData<List<WorkInfo>> getSavedWorkInfo() {
        return savedWorkInfo;
    }

    public void downloadJson() {

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        WorkContinuation continuation = workManager.beginUniqueWork(
                JSON_PROCESSING_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(CleanupWorker.class));

        OneTimeWorkRequest download = new OneTimeWorkRequest.Builder(DownloadJsonWorker.class)
                .setConstraints(constraints)
                .addTag(TAG_OUTPUT)
                .setInputData(createInputUrl())
                .build();

        continuation.then(download);
        continuation.enqueue();
    }

    void cancelWork() {
        workManager.cancelUniqueWork(JSON_PROCESSING_WORK_NAME);
    }

    private Data createInputUrl() {
        Data.Builder builder = new Data.Builder();
        builder.putString(STRING_URL, API_URL);

        return builder.build();
    }
}
