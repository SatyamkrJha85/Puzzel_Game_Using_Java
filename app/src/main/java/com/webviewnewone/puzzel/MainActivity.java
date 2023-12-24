package com.webviewnewone.puzzel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;


public class MainActivity extends AppCompatActivity {

    Button start,test;
    static String  imageString;
    int status = 0;
    int score;
    TextView points,timeView;
    EditText col,row;
    String nCol,nRow;
    static ArrayList<Bitmap> chunkedImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.start);
        col = findViewById(R.id.col);
        row = findViewById(R.id.row);


        //base64 encode
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);


        start.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               nCol = col.getText().toString().trim();
               nRow = row.getText().toString().trim();

               if(nCol.isEmpty() || nRow.isEmpty()){
                   Toast.makeText(getApplication(), "No of row and columns can not be empty", Toast.LENGTH_SHORT).show();
                    return;
               }

                if(parseInt(nCol)<2 || parseInt(nRow)<2){
                    Toast.makeText(getApplication(), "No of row and columns should be grater than or equal 2", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(parseInt(nCol)>4 || parseInt(nRow)>3){
                    Toast.makeText(getApplication(), "No of row and columnns should be between 3 and 4", Toast.LENGTH_SHORT).show();
                    return;
                }
                showAlertDialog(parseInt(nRow),parseInt(nCol));

            }
        });



        Intent mIntent = getIntent();
        status = mIntent.getIntExtra("status", 0);
        score = mIntent.getIntExtra("score", 0);
        String time = mIntent.getStringExtra("time");

        if(status==1){
            points = findViewById(R.id.score);
            String p = String.valueOf(score);
            points.setText("Score : "+p );
            points.setVisibility(points.VISIBLE);

            timeView = findViewById(R.id.time);
            timeView.setText(time);
            timeView.setVisibility(points.VISIBLE);

        }


    }



    private void showAlertDialog(int r , int c) {
        MyDialog dialog = new MyDialog(r,c);
        dialog.show(getSupportFragmentManager(),"test");

    }



}