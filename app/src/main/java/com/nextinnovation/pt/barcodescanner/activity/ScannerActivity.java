package com.nextinnovation.pt.barcodescanner.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.OptIn;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.TorchState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.nextinnovation.pt.barcodescanner.R;

import java.util.concurrent.Executor;

public class ScannerActivity extends ComponentActivity {

    public static final String EXTRA_SCAN_VALUE = "scan_value";

    private static final String TAG = "ScannerActivity";

    private PreviewView previewView;
    private Button torchButton;

    private BarcodeScanner barcodeScanner;
    private ProcessCameraProvider cameraProvider;
    private ImageAnalysis imageAnalysis;
    private Camera camera;
    private Executor mainExecutor;

    // Accessed only on the main thread.
    private boolean processingFrame;
    private boolean deliveredResult;
    private boolean destroyed;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {
                        if (granted) {
                            startCamera();
                        } else {
                            Toast.makeText(
                                    this,
                                    R.string.camera_permission_required,
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        // Back or Cancel returns RESULT_CANCELED unless scanning succeeds.
        setResult(RESULT_CANCELED);

        previewView = findViewById(R.id.cameraPreview);
        torchButton = findViewById(R.id.buttonTorch);

        mainExecutor = ContextCompat.getMainExecutor(this);

        // Default configuration recognizes all supported barcode formats.
        barcodeScanner = BarcodeScanning.getClient();

        findViewById(R.id.buttonCancel).setOnClickListener(view -> finish());
        torchButton.setOnClickListener(view -> toggleTorch());

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(this);

        future.addListener(() -> {
            if (destroyed || isFinishing()) {
                return;
            }

            try {
                cameraProvider = future.get();

                if (!cameraProvider.hasCamera(
                        CameraSelector.DEFAULT_BACK_CAMERA
                )) {
                    showCameraError();
                    return;
                }

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(
                                ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                        )
                        .build();

                /*
                 * This callback only creates InputImage and starts ML Kit's
                 * asynchronous task. It does not perform blocking inference.
                 *
                 * Using the main executor also keeps scanner state serialized.
                 */
                imageAnalysis.setAnalyzer(mainExecutor, this::analyzeFrame);

                camera = cameraProvider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                );

                configureTorch();

            } catch (Exception exception) {
                Log.e(TAG, "Unable to bind camera", exception);
                showCameraError();
            }
        }, mainExecutor);
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void analyzeFrame(ImageProxy imageProxy) {
        if (destroyed || deliveredResult || processingFrame) {
            imageProxy.close();
            return;
        }

        Image mediaImage = imageProxy.getImage();

        if (mediaImage == null) {
            imageProxy.close();
            return;
        }

        processingFrame = true;

        try {
            InputImage input = InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.getImageInfo().getRotationDegrees()
            );

            barcodeScanner.process(input)
                    .addOnSuccessListener(mainExecutor, barcodes -> {
                        if (destroyed || isFinishing() || deliveredResult) {
                            return;
                        }

                        for (Barcode barcode : barcodes) {
                            String value = barcode.getRawValue();

                            if (value != null && !value.isEmpty()) {
                                returnScanResult(value);
                                break;
                            }
                        }
                    })
                    .addOnFailureListener(mainExecutor, exception -> {
                        Log.e(TAG, "Barcode processing failed", exception);

                        if (!destroyed && !isFinishing()) {
                            Toast.makeText(
                                    this,
                                    R.string.scanner_failed,
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();
                        }
                    })
                    .addOnCompleteListener(mainExecutor, task -> {
                        // Always release the camera frame after ML Kit finishes.
                        imageProxy.close();
                        processingFrame = false;
                    });

        } catch (Exception exception) {
            imageProxy.close();
            processingFrame = false;

            Log.e(TAG, "Unable to create or process input image", exception);

            if (!destroyed && !isFinishing()) {
                Toast.makeText(
                        this,
                        R.string.scanner_failed,
                        Toast.LENGTH_LONG
                ).show();

                finish();
            }
        }
    }

    private void returnScanResult(String value) {
        deliveredResult = true;

        if (imageAnalysis != null) {
            imageAnalysis.clearAnalyzer();
        }

        Intent result = new Intent();
        result.putExtra(EXTRA_SCAN_VALUE, value);

        setResult(RESULT_OK, result);
        finish();
    }

    private void configureTorch() {
        boolean hasFlash = camera.getCameraInfo().hasFlashUnit();
        torchButton.setEnabled(hasFlash);

        if (!hasFlash) {
            return;
        }

        camera.getCameraInfo().getTorchState().observe(this, state -> {
            boolean enabled = state != null && state == TorchState.ON;

            torchButton.setText(
                    enabled ? R.string.torch_off : R.string.torch_on
            );
        });
    }

    private void toggleTorch() {
        if (camera == null || !camera.getCameraInfo().hasFlashUnit()) {
            return;
        }

        Integer currentState =
                camera.getCameraInfo().getTorchState().getValue();

        boolean enable =
                currentState == null || currentState != TorchState.ON;

        ListenableFuture<Void> future =
                camera.getCameraControl().enableTorch(enable);

        future.addListener(() -> {
            try {
                future.get();
            } catch (Exception exception) {
                Log.w(TAG, "Unable to change torch", exception);

                if (!destroyed && !isFinishing()) {
                    Toast.makeText(
                            this,
                            R.string.torch_failed,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        }, mainExecutor);
    }

    private void showCameraError() {
        Toast.makeText(
                this,
                R.string.camera_unavailable,
                Toast.LENGTH_LONG
        ).show();

        finish();
    }

    @Override
    protected void onDestroy() {
        destroyed = true;

        if (imageAnalysis != null) {
            imageAnalysis.clearAnalyzer();
        }

        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }

        if (barcodeScanner != null) {
            barcodeScanner.close();
        }

        super.onDestroy();
    }
}