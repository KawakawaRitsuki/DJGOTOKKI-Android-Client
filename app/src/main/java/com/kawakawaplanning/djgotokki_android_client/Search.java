package com.kawakawaplanning.djgotokki_android_client;

/**
 * Created by KP on 15/04/27.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Search extends AsyncTask<String, Integer, Long> implements OnCancelListener {
    final String TAG = "MyAsyncTask";
    ProgressDialog dialog;
    Context context;
    String SearchText;
    ProgressDialog progressDialog;

    public Search(Context context ,String SeText) {
        this.SearchText = SeText;

        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        MainActivity.objects.clear();
        MainActivity.Lv.setAdapter(null);
        progressDialog = new ProgressDialog(context);
        // プログレスダイアログのタイトルを設定します
        progressDialog.setTitle("検索中");
        // プログレスダイアログのメッセージを設定します
        progressDialog.setMessage("検索中です。しばらくお待ちください。");
        // プログレスダイアログの確定(false)/不確定(true)を設定します
        progressDialog.setIndeterminate(false);
        // プログレスダイアログのスタイルを円スタイルに設定します
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // プログレスダイアログのキャンセルが可能かどうかを設定します
        progressDialog.setCancelable(false);
        // プログレスダイアログを表示します
        progressDialog.show();
    }

    @Override
    protected Long doInBackground(String... params) {
        try {
            String url = "https://www.googleapis.com/youtube/v3/search?key=AIzaSyBEdFSE1PClEDQ2AnvQJ-SGe5QM9VIXJBQ&part=id,snippet&maxResults=50&type=video";
            url += "&q=" + URLEncoder.encode(SearchText);
            Log.v("result", url);
            HttpUriRequest httpGet = new HttpGet(url);
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            HttpResponse httpResponse = defaultHttpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                try {
                    JSONObject json = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
                    JSONArray items = json.getJSONArray("items");

                    for(int i = 0; i != 50;i++) {
                        JSONObject ob = items.getJSONObject(i);

                        JSONObject ob2 = items.getJSONObject(i);

                        CustomList item = new CustomList();
//                        item.setImagaData(getData("http://i.ytimg.com/vi/" + ob.getJSONObject("id").get("videoId").toString() + "/hqdefault.jpg"));
                        item.setTextData(ob2.getJSONObject("snippet").get("title").toString());
                        MainActivity.objects.add(item);

                        MainActivity.id[i] = ob.getJSONObject("id").get("videoId").toString();
                        MainActivity.title[i] = ob2.getJSONObject("snippet").get("title").toString();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {

        }
        return null;
    }
    public Bitmap getData(String _url) {
        try {
            URL url = new URL(_url);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
    }
    @Override protected void onCancelled() {
        Log.d(TAG, "onCancelled"); dialog.dismiss();
    }
    @Override protected void onPostExecute(Long result) {

        CustomAdapter customAdapater = new CustomAdapter(context, 0, MainActivity.objects);

        MainActivity.Lv.setAdapter(customAdapater);
        progressDialog.cancel();
    }
    @Override public void onCancel(DialogInterface dialog) {
        Log.d(TAG, "Dialog onCancell... calling cancel(true)");
        this.cancel(true);
    }
}