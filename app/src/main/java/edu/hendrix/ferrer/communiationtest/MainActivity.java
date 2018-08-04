package edu.hendrix.ferrer.communiationtest;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity implements TalkerListener {

    private ArduinoTalker talker;
    private TextView numSends, message2send, statusBox, timeBox, responseBox;

    private int numResponsesExpected = 0;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numSends = findViewById(R.id.numSends);
        message2send = findViewById(R.id.messageBox);
        statusBox = findViewById(R.id.statusBox);
        timeBox = findViewById(R.id.timeBox);
        responseBox = findViewById(R.id.responseBox);
        deviceCheck();
    }

    private void deviceCheck() {
        if (talker == null || !talker.connected()) {
            talker = new ArduinoTalker((UsbManager) getSystemService(Context.USB_SERVICE));
            if (talker.connected()) {
                statusBox.setText("Device is good to go!");
                talker.addListener(this);
            } else {
                statusBox.setText(talker.getStatusMessage());
            }
        } else {
            statusBox.setText(talker.getStatusMessage());
        }
    }

    public void send(View view) {
        byte[] bytes = message2send.getText().toString().getBytes(Charset.forName("UTF-8"));
        numResponsesExpected = bytes.length;
        Log.i(TAG, "Starting send; " + numResponsesExpected + " responses expected");
        talker.send(bytes);
        Log.i(TAG, "Send called");
    }

    @Override
    public void sendComplete(int status) {
        Log.i(TAG,"Checking status");
        final String stat = talker.getStatusMessage();
        Log.i(TAG, "Status message: " + stat);
        runOnUiThread(new Runnable() {public void run() {
            statusBox.setText(stat);
            Log.i(TAG, "status box set");
            message2send.setText("");
        }});
        Log.i(TAG, "cleared message box; calling receive");
        talker.receive();
        Log.i(TAG, "returned from receive");
    }

    @Override
    public void receiveComplete(int status, byte[] received) {
        StringBuilder back = new StringBuilder();
        for (int i = 0; i < received.length; i++) {
            back.append((char)received[i]);
        }
        final String backStr = back.toString();
        Log.i(TAG, "Received: " + back);
        runOnUiThread(new Runnable() {
            public void run() {
                responseBox.setText(responseBox.getText() + "\n" + backStr);
                statusBox.setText(talker.getStatusMessage());
            }
        });

        numResponsesExpected -= 1;
        Log.i(TAG, "Remaining responses expected: " + numResponsesExpected);
        if (numResponsesExpected > 0) {
            talker.receive();
        }
    }

    @Override
    public void error() {
        runOnUiThread(new Runnable() {
            public void run() {
                statusBox.setText("Error: " + talker.getStatusMessage());
            }
        });
    }
}