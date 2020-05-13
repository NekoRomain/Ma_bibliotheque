package com.nekoromain.ma_bibliotheque;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class CameraActivity extends AppCompatActivity {
    private BarcodeDetector detector;
    private SurfaceView cameraView;
    private CameraSource cameraSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS).build();
        PermissionDevice.verifyCameraPermissions(this);
        cameraView = (SurfaceView)findViewById(R.id.surfaceView2);
        cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(640,480)
                .setAutoFocusEnabled(true).build();
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try{
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie){
                    Log.e("CAMERA", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if(barcodes.size() != 0){
                    Intent intent = new Intent();
                    intent.putExtra("ISBN_SCAN", barcodes.valueAt(0).displayValue);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
