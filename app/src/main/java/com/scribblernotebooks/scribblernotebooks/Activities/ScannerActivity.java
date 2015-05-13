package com.scribblernotebooks.scribblernotebooks.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;

import eu.livotov.zxscan.ScannerView;


public class ScannerActivity extends AppCompatActivity {

//    EditText manualCodeInput;
    ScannerView scanner;
    InputMethodManager inputMethodManager;
    long[] vibratePattern={50,50,50};
    Button manual;
    Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        scanner=(ScannerView)findViewById(R.id.scanner);
        inputMethodManager=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
        manual=(Button)findViewById(R.id.swtichToManual);

        manual.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scanner.stopScanner();
                switchToManual(v);
                return true;
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        startScan();
        scanner.setScannerViewEventListener(new ScannerView.ScannerViewEventListener() {
            @Override
            public void onScannerReady() {

            }


            @Override
            public void onScannerFailure(int cameraError) {

            }

            @Override
            public boolean onCodeScanned(String data) {
                stopScan();
                vibrator.vibrate(vibratePattern,1);
                Intent i=new Intent(getApplicationContext(),ManualScribblerCode.class);
                i.putExtra(Constants.EXTRA_DEAL_CODE,data);
                startActivity(i);
                finish();
                return false;
            }
        });
    }

    void stopScan() {
        scanner.stopScanner();
    }

    void startScan(){
        scanner.startScanner();
    }

    public void switchToManual(View view){
        startActivity(new Intent(this, ManualScribblerCode.class));
        finish();
    }
        
        

        @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            stopScan();
            vibrator.cancel();
//            scanner.getCamera().stop();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanner.stopScanner();
    }
}
