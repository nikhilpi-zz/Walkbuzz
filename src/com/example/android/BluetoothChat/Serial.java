package com.example.android.BluetoothChat;


import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import android.util.Log;

public class Serial {
	
    private static final String TAG = "SerialReader";
    private static final String MAKR_ENABLE = "/sys/class/makr/makr/5v_enable";
    private static final String TTY_PATH = "/dev/ttyHSL2";

    private static OutputStream mOutputStream;        // serial output
    private static InputStream mInputStream;          // serial input
    private static CommPortIdentifier mPortId = null; // will be set if port found
    private static SerialPort serialPort;
    
    private static boolean mRunning = true;
    
    public static boolean isEnabled()
    {
        boolean status = false;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(MAKR_ENABLE));
            String rv = br.readLine();
            if (rv.matches("on")) {
                return true;
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (br != null) {
                try { br.close(); } catch (IOException e) { }
            }
        }
        return status;
    }
    
    public static void enable(boolean value)
    {
        BufferedWriter writer = null;
        try {
            FileOutputStream fos = new FileOutputStream(MAKR_ENABLE);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            writer = new BufferedWriter(osw);
            if (value)
                writer.write("on\n");
            else
                writer.write("off\n");
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (writer != null) {
                try { writer.close(); } catch (IOException e) { }
            }
        }
    }
    
    public static void initializeSerial()
    {

        // enable the device
        if (!isEnabled()) {
            enable(true);
        }

        // FIXME: we shouldn't have to specify the TTY_PATH at this point but
        // these are early days.
        System.setProperty("gnu.io.rxtx.SerialPorts", TTY_PATH);
        Enumeration<CommPortIdentifier> portIdentifiers = CommPortIdentifier.getPortIdentifiers();
        while (portIdentifiers.hasMoreElements()) {
            CommPortIdentifier pid = portIdentifiers.nextElement();

            Log.d(TAG, "Got : " + pid.getName());
            if (pid.getPortType() == CommPortIdentifier.PORT_SERIAL
                    && pid.getName().equals(TTY_PATH)) {
                Log.d(TAG, "found " + pid.getName());
                mPortId = pid;
                break;
            }
        }

        // ****************************************************
        // *  Setup the Serial Port
        // ****************************************************
        if (mPortId != null) {

            try {
                serialPort = (SerialPort) mPortId.open("GNU IO Test", 2000);

                serialPort.setSerialPortParams(
                        9600,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
                mInputStream = serialPort.getInputStream();
                mOutputStream = serialPort.getOutputStream();
            } catch (PortInUseException e) {
                Log.d(TAG, e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            } catch (UnsupportedCommOperationException e) {
                Log.d(TAG, e.getMessage());
            }
        } else {
            return; // we failed
        }

    }
    
    public static void WriteSerial(String cmd){

        try {

            OutputStreamWriter osw = new OutputStreamWriter(mOutputStream, "UTF-8");
            BufferedWriter writer = new BufferedWriter(osw);

            try {
                    // using the buffered writer, flush just seemed to be ignored....
                    osw.write(cmd+"\n");
                    osw.flush();

            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }

        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }


    }



    public static void startSerialReader() {
/*
        // Do something long
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while(mRunning){
                try {
                    InputStreamReader isr = new InputStreamReader(mInputStream, "UTF-8");
                    BufferedReader reader = new BufferedReader(isr);

                    try {

                        MAKRstr = reader.readLine();
                        Log.d(TAG, "read: " + MAKRstr);
                        //TextView t = (TextView)findViewById(R.id.text);

                    } catch (IOException e) {
                        Log.d(TAG, e.getMessage());
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    text.post(new Runnable() {
                        @Override
                        public void run() {
                            text.setText("MAKR Count: "+MAKRstr);
                        }
                    });

                } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
           }

            }
        };
        new Thread(runnable).start(); */
    }
    
    public static void end() {
        mRunning = false;
        serialPort.close();
    }
}
