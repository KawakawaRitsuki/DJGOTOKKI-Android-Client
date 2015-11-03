
//Charset:UTF-8

package com.kawakawaplanning.djgotokki_android_client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kawakawaplanning.djgotokki_android_client.http.HttpConnector;
import com.kawakawaplanning.djgotokki_android_client.http.OnHttpErrorListener;
import com.kawakawaplanning.djgotokki_android_client.http.OnHttpResponseListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    /** Called when the activity is first created. */

    public static final int PREFERENCE_INIT = 0;
    public static final int PREFERENCE_BOOTED = 1;
    static EditText EtSend;
    static ListView Lv;
    static String id[];
    static String title[];
    static Bitmap thumb[];
    static List<CustomList> objects;
    static Context con;
    public static final int MENU_SELECT_A = 0;
    TextView textView;
    private Vibrator vib;
    static Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        con = this;
        handler = new Handler();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);

        id = new String[50];
        title  = new String[50];
        thumb  = new Bitmap[50];
        EtSend = (EditText)findViewById(R.id.txtSend);
        vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        objects = new ArrayList<CustomList>();


        Lv = (ListView)findViewById(R.id.listView);
        Lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long i) {
                SendThread send = new SendThread(MainActivity.this,id[position]);
                send.start();

            }

        });

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vib.vibrate(50);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                new Search(MainActivity.this, MainActivity.EtSend.getText().toString()).execute();
            }
        });

        EditText edittext1 = (EditText) findViewById(R.id.txtSend);
        edittext1.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    textView.setFocusable(true);
                    textView.setFocusableInTouchMode(true);
                    textView.requestFocus();

                    new Search(MainActivity.this, MainActivity.EtSend.getText().toString()).execute();
                }
                return true; // falseを返すと, IMEがSearch→Doneへと切り替わる
            }
        });
        edittext1.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // EditTextのフォーカスが外れた場合
                if (hasFocus == false) {
                    // ソフトキーボードを非表示にする
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0, MENU_SELECT_A, 0, "設定");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SELECT_A:
                Intent intent = new Intent();
                intent.setClassName("com.kawakawaplanning.djgotokki_android_client", "com.kawakawaplanning.djgotokki_android_client.Preferences");
                startActivity(intent);
                return true;
        }
        return false;
    }

}
class SendThread extends Thread{
    String videoId;
    String serverId;
    SharedPreferences spf;
    Context con;

    public SendThread(Context con,String videoId){
        this.videoId = videoId;
        this.con = con;
    }

    public void run(){
        spf = PreferenceManager.getDefaultSharedPreferences(con);
        if(!spf.getString("server_id_preference","").equals("")) {
            HttpConnector connector = new HttpConnector("add", "{\"server_id\":\"" +spf.getString("server_id_preference","")+ "\",\"video_id\":\"" + videoId + "\"}");
            connector.setOnHttpResponseListener(new OnHttpResponseListener() {
                @Override
                public void onResponse(String response) {
                    if(response.equals("0")){
                        MainActivity.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(con, "送信完了", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }else {
                        MainActivity.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(con, "サーバーが見つかりませんでした。サーバーIDを確認して下さい。", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            });
            connector.setOnHttpErrorListener(new OnHttpErrorListener() {
                @Override
                public void onError(int error) {
                    MainActivity.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(con, "サーバーに接続できませんでした。インターネットの接続を確認して下さい。", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
            connector.post();

        }else {
            MainActivity.handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(con, "サーバーが登録されていません。設定画面から登録してください。", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }


}