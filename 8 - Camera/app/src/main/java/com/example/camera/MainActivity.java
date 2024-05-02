package com.example.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button buttonTakePhoto;
    private ImageView imageViewPhoto;

    private static final int cameraRequestCode = 222;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.buttonTakePhoto = (Button) findViewById(R.id.buttonTakePhoto);
        this.imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);

        this.buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, cameraRequestCode);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case(cameraRequestCode):
                    this.setImageViewFromCamera(data);
                    break;
            }
        }
    }

    private void setImageViewFromCamera(Intent data) {
        if (data == null) {
            return;
        }

        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        this.imageViewPhoto.setImageBitmap(bitmap);
        Toast.makeText(this,"Data Telah Terload ke ImageView",Toast.LENGTH_SHORT).show();
    }
}