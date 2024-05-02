package com.example.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.widget.Toast;
import android.database.Cursor;

public class MainActivity extends AppCompatActivity {

    private EditText nrp,nama;
    private Button btnSimpan,btnTampil;
    private SQLiteDatabase dbku;
    private SQLiteOpenHelper opendb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        nrp = (EditText) findViewById(R.id.nrp);
        nama = (EditText) findViewById(R.id.nama);
        btnSimpan = (Button) findViewById(R.id.simpan);
        btnTampil = (Button) findViewById(R.id.tampil);
        btnSimpan.setOnClickListener(operasi);
        btnTampil.setOnClickListener(operasi);

        opendb = new SQLiteOpenHelper(this, "db.sql", null, 1){
            @Override
            public void onCreate(SQLiteDatabase db) {}
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
        };

        dbku = opendb.getWritableDatabase();
        dbku.execSQL("CREATE TABLE IF NOT EXISTS mahasiswa(nrp TEXT PRIMARY KEY, nama TEXT)");

    }

    @Override
    protected void onStop(){
        dbku.close();
        opendb.close();
        super.onStop();
    }

    View.OnClickListener operasi = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if (v.getId() == R.id.simpan) {
                simpan();
            } else if (v.getId() == R.id.tampil) {
                tampil();
            }
        }
    };


    private void simpan()    {
        ContentValues dataku = new ContentValues();
        dataku.put("nrp",nrp.getText().toString());
        dataku.put("nama",nama.getText().toString());
        dbku.insert("mahasiswa",null,dataku);
        Toast.makeText(this,"Data Tersimpan",Toast.LENGTH_LONG).show();
    }

    private void tampil(){
        Cursor cur = dbku.rawQuery("select * from mahasiswa where nrp='" +
                nrp.getText().toString()+ "'",null);
        if(cur.getCount() >0) {
            Toast.makeText(this,"Data Ditemukan Sejumlah " +
                    cur.getCount(),Toast.LENGTH_LONG).show();
            cur.moveToFirst();
            int columnIndex = cur.getColumnIndex("nama");
            if (columnIndex != -1) {
                String result = cur.getString(columnIndex);
                nama.setText(result); // set the text of the EditText to the result
            }
        }
        else
            Toast.makeText(this,"Data Tidak Ditemukan",Toast.LENGTH_LONG).show();
    }


}