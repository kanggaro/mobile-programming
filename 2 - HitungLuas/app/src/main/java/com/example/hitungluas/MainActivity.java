package com.example.hitungluas;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private EditText editBil1, editBil2;
    private TextView textHasil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editBil1=(EditText)findViewById(R.id.editTextBil1);
        editBil2=(EditText)findViewById(R.id.editTextBil2);
        textHasil=(TextView)findViewById(R.id.textViewHasil);
    }

    public void kali(View v){
        float bil1,bil2, hasil;
        bil1=Float.parseFloat(editBil1.getText().toString());
        bil2=Float.parseFloat(editBil2.getText().toString());
        hasil=bil1*bil2;
        textHasil.setText(bil1+"*"+bil2+" = "+hasil);
    }
}
