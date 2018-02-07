package com.baraa.bsoft.epnoxlocation.Services;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by baraa on 04/01/2018.
 */

public class Download extends AsyncTask<String,Void,String> {
    private static final String TAG = "Download";
    private DownloadListener mDownloadListener;
    public interface DownloadListener{
        void onDownloadComplete(String dataText);
    }

    @Override
    protected void onPostExecute(String s) {
        mDownloadListener.onDownloadComplete(s);
        super.onPostExecute(s);

    }

    @Override
    protected String doInBackground(String... strings) {
        return startDownloading(strings[0]);
    }

    public void setDownloadListener(DownloadListener downloadListener){
        mDownloadListener = downloadListener;
    }
    private String startDownloading(String urls){
        StringBuilder strBuilder = new StringBuilder();
        String line;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(urls);
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while (null != ( line=bufferedReader.readLine())){
                strBuilder.append(line).append("\n");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
                try {
                    if (bufferedReader != null)
                        bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        //Log.d(TAG, "startDownloading: \n"+ strBuilder.toString());
        return strBuilder.toString();
    }
}
