package com.example.viurstrackerwidget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.WorkInfo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.viurstrackerwidget.data.VirusViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.example.viurstrackerwidget.workers.Constants.DATA_OUTPUT;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private VirusViewModel virusViewModel;
    private ProgressBar progressBar;
    private TextView infected, deaths, recovered, countries, newCases, newDeaths, loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        infected = findViewById(R.id.infected);
        deaths = findViewById(R.id.deaths);
        recovered = findViewById(R.id.recovered);
        countries = findViewById(R.id.countries);
        newCases = findViewById(R.id.new_cases);
        newDeaths = findViewById(R.id.new_deaths);
        loading = findViewById(R.id.loading);

        virusViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(VirusViewModel.class);

        getVirusStatus();
    }

    public void getVirusStatus() {
        virusViewModel.getSavedWorkInfo().observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.isEmpty()) {
                    return;
                }
                WorkInfo workInfo = workInfos.get(0);
                boolean isFinished = workInfo.getState().isFinished();
                if (!isFinished) {
                    showWorkInProgress();
                } else {
                    Data outputData = workInfo.getOutputData();
                    String outputDataString = outputData.getString(DATA_OUTPUT);
                    if (!TextUtils.isEmpty(outputDataString)) {
                        virusViewModel.setOutputData(outputDataString);
                    }
                    populateUI();
                }
            }
        });
        virusViewModel.downloadJson();
    }

    private void showWorkInProgress() {
        progressBar.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
    }

    private void populateUI() {
        String outputDataString = virusViewModel.getOutputData();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(outputDataString);
            Log.d(TAG, "populateUI: " + jsonObject.getString("results"));
            JSONArray resultArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject resultJsonObject = resultArray.getJSONObject(i);
                infected.setText(String.format(getString(R.string.deaths),
                        resultJsonObject.getLong("total_cases")));

                deaths.setText(String.format(getString(R.string.deaths),
                        resultJsonObject.getLong("total_death")));

                recovered.setText(String.format(getString(R.string.deaths),
                        resultJsonObject.getLong("total_recovered")));

                newCases.setText(String.format(getString(R.string.deaths),
                        resultJsonObject.getLong("total_new_cases_today")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        processingDone();
    }

    private void processingDone() {
        progressBar.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
    }
}