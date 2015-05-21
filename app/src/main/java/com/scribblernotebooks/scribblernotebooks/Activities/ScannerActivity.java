package com.scribblernotebooks.scribblernotebooks.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.scribblernotebooks.scribblernotebooks.R;

import eu.livotov.zxscan.ScannerView;


public class ScannerActivity extends AppCompatActivity {

    ScannerView scanner;
    Button manual,back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /**
         * View Setup
         */
        scanner = (ScannerView) findViewById(R.id.scanner);
        manual = (Button) findViewById(R.id.swtichToManual);
        back=(Button)findViewById(R.id.back);


        /**
         * When user presses manual button the Navigation Drawer activity is opened and Manual Code Input fragment is loaded.
         * Scanner stopped as it will slow the interface and consume lot of CPU
         *
         * onTouch is used instead of onClick as Scanner slows down user interface and makes recognising click slow
         */
        manual.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scanner.stopScanner();
                switchToManual(v);
                return true;
            }
        });

        /**
         * Start Scanner and set the functions to be executed after events have occured
         */
        scanner.startScanner();
        scanner.setScannerViewEventListener(new ScannerView.ScannerViewEventListener() {
            @Override
            public void onScannerReady() {
            }

            @Override
            public void onScannerFailure(int cameraError) {
            }

            @Override
            public boolean onCodeScanned(String data) {
                scanner.stopScanner();

                /**
                 * Pass value of scanned QR code to the Navigation Drawer activity for the popup to be shown
                 * Check if QR code data is valid i.e if QR code does not contain the url
                 */
                Intent i = new Intent(getApplicationContext(), NavigationDrawer.class);
                try {
                    i.setData(Uri.parse(data));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Invalid Scribbler QR code", Toast.LENGTH_LONG).show();
                    finish();
                }
                startActivity(i);
                finish();
                return false;
            }
        });

        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return false;
            }
        });
    }

    public void switchToManual(View view) {
        startActivity(new Intent(this, NavigationDrawer.class));
        finish();
    }

    /**
     * When activity paused, stop the scanner to prevent memory leaks
     */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            scanner.stopScanner();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
