package com.example.blebluetoothtest;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.util.ArraySet;
import android.util.LogPrinter;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import android.bluetooth.le.BluetoothLeScanner

public class MainActivity extends AppCompatActivity {

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;

    private boolean mScanning = false;
    private Handler handler;

    private ScanCallback leScanCallback = null;

    RecyclerView bleList;

    List<BLEDevice> devices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity self = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestBlePermissions(this, REQUEST_ENABLE_BT);

//        buildRecycler();

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
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                scanLeDevice(!mScanning);
                            }
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

    private void requestBlePermissions(final Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                requestCode);
    }

    private void enableBluetooth() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void scanLeDevice(final boolean enable) {


        bleList.clearAnimation();

//        Log.d(this.getLocalClassName(), "lalala");
//        BluetoothAdapter.LeScanCallback leScanCallback = (device, rssi, scanRecord) -> {
//            textview_firstView.setText(scanRecord.toString());
//        };

        if (leScanCallback == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                leScanCallback = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        super.onScanResult(callbackType, result);
//                        log.println("onScanResult: " + result.toString());
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d( "onScanResult", result.getDevice().toString());
                                BLEDevice device = new BLEDevice(result.getDevice().getAddress(), result.getScanRecord().getDeviceName());

                            }
                        });
                    }

                    @Override
                    public void onBatchScanResults(List<ScanResult> results) {
//                        super.onBatchScanResults(results);
//                        log.println("onBatchScanResults: " + results.toString());
                        //Não funcionou os dados em batch
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("onBatchScanResults", results.toString());
//                                results.forEach(scanResult -> bleList.append(scanResult.getScanRecord().getDeviceName() + "\n"));
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
//                                bleList.setText("error:" + errorCode);
                                Log.e("onScanFailed", errorCode + "");
                            }
                        });
                    }
                };
            }
        }



        handler = new Handler();
        handler.postDelayed(() -> {
            mScanning = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);
            }

        }, SCAN_PERIOD);

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
    }


    private void buildRecycler() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.ble_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BLEDeviceRecyclerAdapter(devices));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

}