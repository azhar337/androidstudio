package com.example.face;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.face.Helper.GraphicsOverlay;

import com.example.face.Helper.RectOverlay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import dmax.dialog.SpotsDialog;


public class MainActivity extends AppCompatActivity {
    private Button faceDetectButton;
    private GraphicsOverlay graphicsOverlay;
    private CameraView cameraView;
    AlertDialog alertDialog;
    private Object test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        faceDetectButton = findViewById(R.id.detect_face_btn);
        graphicsOverlay = findViewById(R.id.graphic_overlay);
        cameraView = findViewById(R.id.camera_view);

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please wait, Processing,,")
                .setCancelable(false)
                .build();

        faceDetectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.start();
                cameraView.captureImage();
                graphicsOverlay.clear();
            }
        });


        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {


            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                alertDialog.show();
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();
                processFacedetection(bitmap);


            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

    }

    private void processFacedetection(Bitmap bitmap) {

        InputImage image = InputImage.fromBitmap(bitmap, 0);
        FaceDetectorOptions faceDetectorOptions =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();
        FaceDetector faceDetector = FaceDetection.getClient(faceDetectorOptions);


        Task<List<Face>> result =
                faceDetector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {

                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        getFaceResult(faces);
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });


    }



    private void getFaceResult(List<Face> faces) {

        int counter=0;
       for (Face face: faces){
            Rect rect=face.getBoundingBox();
            RectOverlay rectOverlay= new RectOverlay(graphicsOverlay,rect);
            graphicsOverlay.add(rectOverlay);
           FaceLandmark leftEar = face.getLandmark(FaceLandmark.LEFT_EAR);
           if (leftEar != null) {
               Toast.makeText(MainActivity.this, "1", Toast.LENGTH_LONG).show();
           }
           if (face.getSmilingProbability() != null) {
               float smilingProbability = face.getSmilingProbability();
               String test1 = String.valueOf(smilingProbability);
               Toast.makeText(MainActivity.this, "2"+test1, Toast.LENGTH_LONG).show();
           }
           if (face.getRightEyeOpenProbability() != null) {
               float rightEyeOpenProb = face.getRightEyeOpenProbability();
               test=String.valueOf(rightEyeOpenProb);
               Toast.makeText(MainActivity.this, "test"+test, Toast.LENGTH_LONG).show();
           }


           counter=counter +1;
        }
        alertDialog.dismiss();

    }


    @Override
    protected void onPause(){

        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onResume(){
        super.onResume();
        cameraView.start();


    }



}