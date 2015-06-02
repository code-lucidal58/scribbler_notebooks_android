package com.scribblernotebooks.scribblernotebooks.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.List;


public class ScannerActivity extends AppCompatActivity {

    Button manual,back;
    CompoundBarcodeView compoundBarcodeView;

    /**
     *Callback for the scanner
     * */
    private BarcodeCallback barcodeCallback=new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult barcodeResult) {
            compoundBarcodeView.pause();
            Intent i=new Intent(getApplicationContext(),NavigationDrawer.class);
            i.setData(Uri.parse(barcodeResult.getText()));
            startActivity(i);
            finish();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> list) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /**
         * View Setup
         */
        compoundBarcodeView=(CompoundBarcodeView)findViewById(R.id.scanner);
        manual = (Button) findViewById(R.id.swtichToManual);
        back=(Button)findViewById(R.id.back);


        /**Removing default Text*/
        for(int i=0;i<compoundBarcodeView.getChildCount();i++){
            View v=compoundBarcodeView.getChildAt(i);
            if(v instanceof TextView){
                if(((TextView) v).getText().toString().contains("inside the viewfinder")){
                    ((TextView) v).setText("");
                }
            }
        }


        compoundBarcodeView.decodeContinuous(barcodeCallback);

        /**
         * When user presses manual button the Navigation Drawer activity is opened and Manual Code Input fragment is loaded.
         * Scanner stopped as it will slow the interface and consumes lot of CPU
         *
         * onTouch is used instead of onClick as Scanner slows down user interface and makes recognising click slow
         */
        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToManual(v);
            }
        });

        /***/

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compoundBarcodeView.pause();
                finish();
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
        compoundBarcodeView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compoundBarcodeView.resume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return compoundBarcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
