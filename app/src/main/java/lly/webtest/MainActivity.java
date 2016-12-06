package lly.webtest;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private EditText edit_id;
    private EditText edit_pass;
    private Button get_btn;
    private TextView success;
    private String path = "http://httpbin.org/get";
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x00:
                    success.setText((String)msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        edit_id = (EditText) findViewById(R.id.edit_id);
        edit_pass = (EditText) findViewById(R.id.edit_pass);
        get_btn = (Button) findViewById(R.id.get_btn);
        success = (TextView) findViewById(R.id.success);
    }
    protected void get(View v){
        final String edit_idText = edit_id.getText().toString();
        final String edit_passText = edit_pass.getText().toString();
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
//                    URL url = new URL(path + "?" + edit_idText + "=" +edit_passText);
//                    URLConnection urlConnection = url.openConnection();
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(path + "?" + edit_idText + "=" + edit_passText);
                    HttpResponse response = httpClient.execute(httpGet);
                    InputStream inputStream = response.getEntity().getContent();

//                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String results = new String();

                    while(true){
                        String tag = bufferedReader.readLine();
                        if(TextUtils.isEmpty(tag))
                            break;
                        else{
                            results += tag;
                        }
                    }
                    Message message = handler.obtainMessage();
                    message.what = 0x00;
                    message.obj = results;
                    handler.sendMessage(message);
                    inputStream.close();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
