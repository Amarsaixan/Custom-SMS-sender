package com.example.smssenter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private Button send;
    private TextInputEditText delay, body, number, count;
    private ProgressBar progressBar;
    private TextView txt;
    int c=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = (Button) findViewById(R.id.send);//
        delay = (TextInputEditText) findViewById(R.id.delay);//
        body = (TextInputEditText) findViewById(R.id.body);//
        number = (TextInputEditText) findViewById(R.id.number);//
        count = (TextInputEditText) findViewById(R.id.count);//
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txt = (TextView) findViewById(R.id.status);
        checkThePermissions();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Sending...", Toast.LENGTH_LONG);
                toast.show();
                //sendSMSMessage();

                progressBar.setVisibility(View.VISIBLE);
                c = Integer.parseInt(String.valueOf(count.getText()));
                new MyTask().execute(c);
            }
        });
    }

    private void checkThePermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        1);
            }
        }
    }
    private void sendSMSMessage(){
        String phoneNo = String.valueOf(number.getText());
        String message = String.valueOf(body.getText());
        if(number.getText().length()==0){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Please input phone no", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }else if(body.getText().length()==0){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Please input message body", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
        //Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();

    }
    class MyTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            long delayMlls = Long.parseLong(String.valueOf(delay.getText()));
            for (int c=0; c <= params[0]; c++) {
                try {
                    sendSMSMessage();
                    Thread.sleep(delayMlls);
                    publishProgress(((c*100)/params[0]));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            txt.setText(result);
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPreExecute() {
            txt.setText("Task Starting...");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values[0]!=0) {
                txt.setText("Running..."+ values[0]+"% " + (values[0] * c) / 100 +" completed");
            }
            progressBar.setProgress(values[0]);
        }
    }

}
