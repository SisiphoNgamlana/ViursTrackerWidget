package com.example.viurstrackerwidget;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.example.viurstrackerwidget.data.VirusViewModel;

import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.WorkInfo;

import static com.example.viurstrackerwidget.workers.Constants.DATA_OUTPUT;

/**
 * Implementation of App Widget functionality.
 */
public class VirusWidget extends AppWidgetProvider {

    private static VirusViewModel virusViewModel;
    private static String infectedNum;
    private static String deathNum;
    private static String recoveredNum;
    private static Observer<List<WorkInfo>> widgetObserver;


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        VirusViewModel virusViewModel = new ViewModelProvider.AndroidViewModelFactory((Application) context.getApplicationContext())
                .create(VirusViewModel.class);

        CharSequence widgetText = context.getString(R.string.appwidget_text);

        widgetObserver = new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if(workInfos == null || workInfos.isEmpty()){
                    return;
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
                    populateUI(context);
                }
            }
        }};
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.virus_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setTextViewText(R.id.infected, infectedNum);
        views.setTextViewText(R.id.death, deathNum);
        views.setTextViewText(R.id.recovered, recoveredNum);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void populateUI(Context context) {

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}