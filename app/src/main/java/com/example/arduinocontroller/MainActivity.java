package com.example.arduinocontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    final int BUTTON_NUM = 8;
    final int[] BUTTON_IDS = {R.id.button_connect,R.id.button_disconnect,R.id.button_red,R.id.button_green,R.id.button_blue,R.id.button_gaming,R.id.button_wave,R.id.button_warm};
    Button[] buttons = new Button[BUTTON_NUM];

    SeekBar[] seekBars = new SeekBar[3];
    final int[] SEEKBAR_IDS = {R.id.seekBar_red,R.id.seekBar_green,R.id.seekBar_blue};

    private final static String IP="192.168.1.17";
    private final static int PORT=80;
//    private final static String IP="192.168.1.14";
//    private final static int PORT=8080;
    private Socket socket;
    private OutputStream out;

    int seekBarMax = 255;
    int red,green,blue = seekBarMax;
    int mode = 0;

    TextView[] textViews = new TextView[3];
    final int[] TEXTVIEW_IDS = {R.id.textView_red,R.id.textView_green,R.id.textView_blue};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < BUTTON_NUM; i++) {
            buttons[i] = findViewById(BUTTON_IDS[i]);
            buttons[i].setOnClickListener(this);
        }

        for(int i = 0; i<SEEKBAR_IDS.length;i++){
            seekBars[i] = findViewById(SEEKBAR_IDS[i]);
            seekBars[i].setMax(seekBarMax);
            seekBars[i].setProgress(seekBarMax);
            seekBars[i].setOnSeekBarChangeListener(this);
        }

        for (int i = 0; i < TEXTVIEW_IDS.length; i++) {
            textViews[i] = findViewById(TEXTVIEW_IDS[i]);
            textViews[i].setOnClickListener(this);
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        Thread thread = new Thread(){
            public void run(){
                try{
                    connect(IP, PORT);		//resume時にソケットを開く
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        try{
            socket.close();
            socket=null;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void connect(String ip,int port) {
        Log.d("test", "connect start");
        try {
            //ソケット接続
            socket = new Socket(ip, port);
            if (socket.isConnected() && socket != null) {
                out = socket.getOutputStream();
                Log.d("test", "接続成功");
            } else {
                Log.d("test", "接続失敗");
            }
        } catch (Exception e) {
            Log.d("test", e.toString());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_red:
                sendData(0,255, 0, 0);
//                setSeekBar(seekBarMax, 0, 0);
                break;
            case R.id.button_green:
                sendData(0,0, 255, 0);
//                setSeekBar(0, seekBarMax, 0);
                break;
            case R.id.button_blue:
                sendData(0,0, 0, 255);
//                setSeekBar(0, 0, seekBarMax);
                break;
            case R.id.button_gaming:
                sendData(1,1, 0, 0);
//                setSeekBar(0, 0, seekBarMax);
                break;
            case R.id.button_wave:
                sendData(1,0, 1, 0);
//                setSeekBar(0, 0, seekBarMax);
                break;
            case R.id.button_warm:
            sendData(1,0, 0, 1);
//                setSeekBar(0, 0, seekBarMax);
            break;
        }
    }

    void setSeekBar(int r, int g, int b){
        seekBars[0].setProgress(r);
        seekBars[1].setProgress(g);
        seekBars[2].setProgress(b);
    }

    private void sendSeekBarData(int mode, int r, int g, int b) {
        try {
            if(socket!=null && socket.isConnected()){
                Log.d("test","socket.isConnected()");
                PrintWriter pw = new PrintWriter(out, true);
                pw.println(mode + "," + r + "," + g + "," + b);
//                pw.println(r + "," + g + "," + b);
                Log.d("test","送信！成功しました");
            }
        }catch(Exception e){
            Log.d("test","送信！！失敗しました");
            e.printStackTrace();
        }
    }

    private void sendData(final int mode, final int red, final int green, final int blue){
        Thread thread = new Thread() {
            public void run() {
                sendSeekBarData(mode, red, green, blue);
            }
        };
        thread.start();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, final int i, boolean b) {
        switch (seekBar.getId()){
            case R.id.seekBar_red:
                Log.d("test","R:"+i);
                red = i;
                textViews[0].setText("RED:"+red);
                sendData(0,red,green,blue);
                break;
            case R.id.seekBar_green:
                Log.d("test","G:"+i);
                green=i;
                textViews[1].setText("GREEN:"+green);
                sendData(0,red,green,blue);
                break;
            case R.id.seekBar_blue:
                Log.d("test","B:"+i);
                blue=i;
                textViews[2].setText("BLUE:"+blue);
                sendData(0,red,green,blue);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            default:
        }

    }
}