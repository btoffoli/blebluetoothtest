package com.example.blebluetoothtest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.util.LogPrinter;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.util.Log;

import java.util.List;

//import android.bluetooth.le.BluetoothLeScanner

public class MainActivity extends AppCompatActivity {

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;

    private boolean mScanning = false;
    private Handler handler;

    private ScanCallback leScanCallback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Activity self = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final BluetoothManager bluetoothManager;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(self)
                        .setTitle("Bluetooth")
                        .setMessage("Ligar bluetooth?")
                        .setPositiveButton("Sim", (dialog, which) -> {
                            enableBluetooth();
                            scanLeDevice(!mScanning);
                            dialog.dismiss();
                        })
                        .show();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void enableBluetooth() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void scanLeDevice(final boolean enable) {
        final TextView textview_firstView = findViewById(R.id.textview_first);
        textview_firstView.setText("lalala");
//        Log.d(this.getLocalClassName(), "lalala");
//        BluetoothAdapter.LeScanCallback leScanCallback = (device, rssi, scanRecord) -> {
//            textview_firstView.setText(scanRecord.toString());
//        };
        if (leScanCallback == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final LogPrinter log = new LogPrinter(Log.DEBUG, "ScancallBack");
                final Integer[] count = {0};
                leScanCallback = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
//                        super.onScanResult(callbackType, result);
//                        log.println("onScanResult: " + result.toString());
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                textview_firstView.setText(count[0]++);
                            }
                        });
                    }

                    @Override
                    public void onBatchScanResults(List<ScanResult> results) {
//                        super.onBatchScanResults(results);
//                        log.println("onBatchScanResults: " + results.toString());

                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                textview_firstView.setText(count[0]++);
                            }
                        });
                    }

                    @Override
                    public void onScanFailed(int errorCode) {
//                        super.onScanFailed(errorCode);
//                        log.println("onScanFailed: " + errorCode);

                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                textview_firstView.setText("error:" + errorCode);
                            }
                        });
                    }
                };
            }
        }



//        handler = new Handler();
//        handler.postDelayed(() -> {
//            mScanning = false;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                bluetoothAdapter.stopLeScan(leScanCallback);
//            }
//
//        }, SCAN_PERIOD);

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mScanning = true;
            //                bluetoothAdapter.startLeScan(leScanCallback);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        bluetoothAdapter.getBluetoothLeScanner().startScan(leScanCallback);
                    }
                });
            }
        } else {
            mScanning = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        bluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);
                    }
              });
            }
        }
//        ...
    }


}