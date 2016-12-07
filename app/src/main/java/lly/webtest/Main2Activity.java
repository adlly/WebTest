package lly.webtest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.DefaultedHttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    private EditText edit_id;
    private EditText edit_pass;
    private Button sucess;
    private String path = "http://httpbin.org/get";

    /*
    http://httpbin.org/get?search=abc
    http://httpbin.org/post
    aa:xx

     */

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x11:
                    text.setText((String)msg.obj);
                    break;
            }
        }
    };
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
    }

    private void initView() {
        edit_id = (EditText) findViewById(R.id.edit_id);
        edit_pass = (EditText) findViewById(R.id.edit_pass);
        sucess = (Button) findViewById(R.id.success);
        text = (TextView) findViewById(R.id.text);
        sucess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get(view);
            }
        });

    }

    private void get(View v) {
        final String name = edit_id.getText().toString();
        final String password = edit_pass.getText().toString();
        new Thread(){
            @Override
            public void run() {
                super.run();
                HttpClient httpClient = new DefaultHttpClient();
                //HttpGet httpGet = new HttpGet(path + "?" + name + "=" +password);
                HttpPost post = new HttpPost("http://httpbin.org/post");

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("key", "value"));
                params.add(new BasicNameValuePair(name, password));

                try {
                    post.setEntity(new UrlEncodedFormEntity(params));


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try {
                    HttpResponse response = httpClient.execute(post);
                    InputStream inputStream = response.getEntity().getContent();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String results = new String();
                    while(true){
                        String tag = bufferedReader.readLine();
                        if(tag == null)
                            break;
                        else
                            results += tag;
                    }
                    Message message = handler.obtainMessage();
                    message.what = 0x11;
                    message.obj = results;
                    handler.sendMessage(message);
                    inputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

}
