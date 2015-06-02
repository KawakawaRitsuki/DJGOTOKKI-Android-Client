
//Charset:UTF-8

package com.kawakawaplanning.djgotokki_android_client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
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
    public static final int MENU_SELECT_A = 0;
    TextView textView;
    private Vibrator vib;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
        if(PREFERENCE_INIT == getState() ){
            //初回起動時のみ表示する
            SharedPreferences.Editor editor = spf.edit();
            editor.putString("ip_preference","192.168.XXX.XXX");
            editor.putString("port_preference", "10000");
            editor.apply();
            setState(PREFERENCE_BOOTED);
        }

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
                Toast.makeText(getApplicationContext(),"send completed!",Toast.LENGTH_SHORT).show();
                    SendThread send = new SendThread(MainActivity.this,id[position]+","+title[position]);
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

    private void setState(int state) {
        // SharedPreferences設定を保存
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putInt("InitState", state).commit();

        //ログ表示
    }

    //データ読み出し
    private int getState() {
        // 読み込み
        int state;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        state = sp.getInt("InitState", PREFERENCE_INIT);

        //ログ表示
        return state;
    }



}
class SendThread extends Thread{
    String sendTxt;
    Context context;

    public SendThread(Context con,String str){
        sendTxt = str;
        context = con;
    }

    public void run(){

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        String ip = spf.getString("ip_preference", "");
        String port = spf.getString("port_preference", "10000");

        Socket socket = null;

        try {
            socket = new Socket(ip,Integer.parseInt(port));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);


            pw.println(sendTxt);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if( socket != null){
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}