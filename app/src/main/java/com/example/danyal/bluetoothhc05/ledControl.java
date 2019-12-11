package com.example.danyal.bluetoothhc05;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class ledControl extends AppCompatActivity {

    Button btn1, btn2, btn3, btn4, btn5, btnDis;
    String address = null;
    TextView lumn;
    private ProgressDialog progress;
    BluetoothAdapter bluetoothAdapter = null;
    BluetoothSocket btSocket = null;
    BluetoothServerSocket btServerSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private Handler h;
    private StringBuilder sb = new StringBuilder();

    // AsycTaks's
    ConnectBT connectBT;
    Receive receive;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progress.dismiss();
        receive.cancel(true);
        connectBT.cancel(true);
        if ( btSocket!=null ) {
            try {

                btSocket.close();
                btSocket=null;
            } catch(IOException e) {
                Log.e("Fucker", "OnDestroy:Can't close btSocket");
            }
        }
        //while(btSocket.isConnected());
        //btSocket=null;

        //Disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);

        setContentView(R.layout.activity_led_control);

        btn1 = (Button) findViewById(R.id.button2);
        btn2 = (Button) findViewById(R.id.button3);
        btn3 = (Button) findViewById(R.id.button5);
        btn4 = (Button) findViewById(R.id.button6);
        btn5 = (Button) findViewById(R.id.button7);
        btnDis = (Button) findViewById(R.id.button4);
        lumn = (TextView) findViewById(R.id.textView2);

        connectBT= new ConnectBT();
        connectBT.execute();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("1");
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("2");
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("3");
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("4");
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("5");
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Disconnect();
            }
        });

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 3:                                                   // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
                        if (endOfLineIndex > 0) {                                            // if end-of-line,
                            String sbprint = sb.substring(0, endOfLineIndex);               // extract string
                            sb.delete(0, sb.length());
                            Toast.makeText(ledControl.this, "got", Toast.LENGTH_SHORT).show();
                            // and clear
                            //incoming.setText("Data from Arduino: " + sbprint);            // update TextView
                        }
                        //Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
                        break;
                }
            };
        };


        //receive= new Receive();
        //receive.execute();
    }

    private void sendSignal ( String number ) {
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(number.getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void receiveSignal ( String number ) {
        if ( btSocket != null ) {
            try {
                btSocket.getInputStream().read();
            } catch (IOException e) {
                msg("Error");
            }
        }
    }





    private class Receive extends AsyncTask<Void,String,Void>{

        byte[] buff = new byte[256];
        int BytesAval =0;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        InputStream inputStream = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                inputStream = btSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Fucker","Receive: Can't getInputStream()");
            }
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (BytesAval >0){
                //Toast.makeText(ledControl.this, "post", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //String s = new String(values[0]);
            Toast.makeText(ledControl.this, values[0], Toast.LENGTH_SHORT).show();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            while (true) {
                if(isCancelled()){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("Fucker", "Receive/isCancelled:Can't close inputStram");

                    }
                    if ( btSocket!=null ) {
                            try {
                            btSocket.close();
                        } catch(IOException e) {
                            Log.e("Fucker", "Receive/isCancelled:Can't close btSocket");
                        }

                    }
                    //while(btSocket.isConnected());
                    //btSocket=null;


                    break;
                }
                if (btSocket != null) {
                    try {
                        //InputStream inputStream = btSocket.getInputStream();
                        //if(btSocket.getInputStream().available()>4){
                        //StringBuilder total = new StringBuilder();



                        String line = bufferedReader.readLine();
                        //total.append(line).append('\n');
/*
                        for (String line; (line = r.readLine()) != null; ) {
                            total.append(line).append('\n');
                        }
*/

                        publishProgress(line);
                        //publishProgress(buff);
/*
                        if(inputStream.available()>4){
                            BytesAval = btSocket.getInputStream().read(buff);
                            publishProgress(buff);
                        }
*/

                    } catch (IOException e) {
                        //msg("Error");
                        Log.e("Fucker","Recieve,doInBackground");
                    }
                }
            }
            return null;
        }

    }

    private void Disconnect () {
        //a.l.
        receive.cancel(true);
        connectBT.cancel(true);

        if ( btSocket!=null ) {
            try {
                btSocket.close();
            } catch(IOException e) {
                msg("Error");
            }
        }

        finish();
    }

    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private void close_BT(){
        if ( btSocket!=null ) {
            try {

                btSocket.close();
                //btSocket=null;
            } catch(IOException e) {
                Log.e("Fucker", "Can't close btSocket");
            }
        }

    }

    private  class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected  void onPreExecute () {
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please Wait!!!");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            int a;
            a=1;
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            bluetoothAdapter.cancelDiscovery();
            if ( btSocket==null || !isBtConnected ) {
                try {
                    btSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    btSocket.connect();
                } catch (IOException e) {
                    ConnectSuccess = false;
                    Log.e("Fucker","ConnectBT,doInBackground");
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                Log.i("Fucker", "ConnectBT:Connection Failed");
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected");
                isBtConnected = true;

                receive= new Receive();
                receive.execute();



            }

            progress.dismiss();
        }
    }


}
