package com.example.viurstrackerwidget.workers;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import androidx.annotation.WorkerThread;

final public class VirusWorkerUtils {

    public static final String TAG = VirusWorkerUtils.class.getName();

    @WorkerThread
    public static String processJson(String inputUrl) {

        try {
            URL url = new URL(inputUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            int responceCode = httpURLConnection.getResponseCode();
            if (responceCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Http response failed" + responceCode);
            } else {
                Scanner scanner = new Scanner(url.openStream());
                StringBuilder stringBuilder = new StringBuilder();

                while (scanner.hasNext()) {
                    stringBuilder.append(scanner.nextLine());
                }
                Log.d(TAG, "processing Json: " + stringBuilder.toString());
                scanner.close();

                return stringBuilder.toString();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
