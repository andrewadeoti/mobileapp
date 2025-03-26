/**
 * CameraActivity handles the camera functionality for ingredient scanning.
 * It uses CameraX to provide a camera preview and capture functionality,
 * with the ability to analyze images for ingredient recognition.
 */
package com.example.recipe_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private PreviewView previewView;
    private ImageCapture imageCapture;
    private FloatingActionButton captureButton;
    private ImageButton switchCameraButton;
    private ImageButton flashButton;
    private ImageButton galleryButton;
    private View resultSheet;
    private ProgressBar progressBar;
    private ImageView capturedImage;
    private TextView recognitionResult;
    private MaterialButton findRecipesButton;
    private MaterialButton retakeButton;
    
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private boolean isFrontCamera = false;
    private boolean isFlashEnabled = false;
    private final ExecutorService cameraExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Camera");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initializeViews();
        setupBottomSheet();
        
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void initializeViews() {
        previewView = findViewById(R.id.viewFinder);
        captureButton = findViewById(R.id.camera_capture_button);
        switchCameraButton = findViewById(R.id.camera_switch_button);
        flashButton = findViewById(R.id.camera_flash_button);
        galleryButton = findViewById(R.id.camera_gallery_button);
        resultSheet = findViewById(R.id.bottom_sheet);
        progressBar = findViewById(R.id.progressBar);
        capturedImage = findViewById(R.id.captured_image);
        recognitionResult = findViewById(R.id.recognition_result);
        findRecipesButton = findViewById(R.id.find_recipes_button);
        retakeButton = findViewById(R.id.retake_button);

        setupClickListeners();
    }

    private void setupClickListeners() {
        captureButton.setOnClickListener(v -> takePhoto());
        
        switchCameraButton.setOnClickListener(v -> {
            isFrontCamera = !isFrontCamera;
            startCamera();
        });
        
        flashButton.setOnClickListener(v -> {
            isFlashEnabled = !isFlashEnabled;
            flashButton.setImageResource(isFlashEnabled ? 
                    R.drawable.ic_flash_on : R.drawable.ic_flash_off);
            startCamera();
        });
        
        galleryButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1000);
        });

        findRecipesButton.setOnClickListener(v -> {
            // TODO: Implement recipe search based on recognized food
            Toast.makeText(this, "Finding recipes...", Toast.LENGTH_SHORT).show();
        });

        retakeButton.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            startCamera();
        });
    }

    private void setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(resultSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    capturedImage.setImageBitmap(null);
                    recognitionResult.setText("");
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = 
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error starting camera", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();

        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(isFrontCamera ? 
                        CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK)
                .build();

        imageCapture = new ImageCapture.Builder()
                .setFlashMode(isFlashEnabled ? 
                        ImageCapture.FLASH_MODE_ON : ImageCapture.FLASH_MODE_OFF)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        try {
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        } catch (Exception e) {
            Toast.makeText(this, "Error binding camera use cases", Toast.LENGTH_SHORT).show();
        }
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        File photoFile = new File(getExternalFilesDir(null),
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg");

        ImageCapture.OutputFileOptions outputOptions = 
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        runOnUiThread(() -> processImage(photoFile));
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        runOnUiThread(() -> Toast.makeText(CameraActivity.this,
                                "Error capturing image", Toast.LENGTH_SHORT).show());
                    }
                });
    }

    private void processImage(File imageFile) {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(imageFile));
            capturedImage.setImageBitmap(bitmap);
            
            // Create ML Kit Image Labeler
            ImageLabeler labeler = ImageLabeling.getClient(
                    new ImageLabelerOptions.Builder()
                            .setConfidenceThreshold(0.7f)
                            .build());

            // Convert bitmap to InputImage
            InputImage image = InputImage.fromBitmap(bitmap, 0);

            // Process the image
            labeler.process(image)
                    .addOnSuccessListener(labels -> {
                        StringBuilder detectedItems = new StringBuilder();
                        for (ImageLabel label : labels) {
                            String text = label.getText();
                            float confidence = label.getConfidence();
                            detectedItems.append(text)
                                    .append(" (")
                                    .append(String.format("%.1f%%", confidence * 100))
                                    .append(")\n");
                        }
                        
                        if (detectedItems.length() > 0) {
                            recognitionResult.setText("Detected Items:\n" + detectedItems);
                        } else {
                            recognitionResult.setText("No food items detected");
                        }
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        progressBar.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error processing image: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });

        } catch (Exception e) {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != 
                    PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                File tempFile = new File(getCacheDir(), "temp_image.jpg");
                try (FileOutputStream out = new FileOutputStream(tempFile)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                }
                processImage(tempFile);
            } catch (Exception e) {
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}