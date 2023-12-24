package com.webviewnewone.puzzel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class MyDialog extends AppCompatDialogFragment {

    static GridView grid;
    static TextView point;
    public static boolean sol = false;
    private static int points=1000;
    static int COLUMNS = 3;
    static int DIMENSIONS = COLUMNS*COLUMNS;
    static int colWidth,colHeight;
    public static String up = "up";
    public static String down = "down";
    public static String left = "left";
    public static String right = "right";
    private static String[] tileList;
    static ArrayList<ImageView> buttons;
    static Button exit,done,hint;
    static Chronometer simpleChronometer;
    static Bitmap decodedImage;
    static ArrayList<Bitmap> chunkedImages;
    boolean movtile = false;



    public MyDialog() {}


    public MyDialog(int r, int c) {

        COLUMNS = c;
        DIMENSIONS = r*c;

    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.image_grid, null);
        builder.setView(view);


        points=1000;

        Log.i("col",String.valueOf(COLUMNS));
        Log.i("col",String.valueOf(DIMENSIONS));

        up = "up";
        down = "down";
        left = "left";
        right = "right";

        //timer
        simpleChronometer = (Chronometer) view.findViewById(R.id.simpleChronometer);
        simpleChronometer.start();
        simpleChronometer.setFormat("Time : %s");

        grid=view.findViewById(R.id.grid);
        point=view.findViewById(R.id.point);
        exit = view.findViewById(R.id.btnExit);
        done = view.findViewById(R.id.btnDone);
        hint = view.findViewById(R.id.btnHint);

        done.setEnabled(false);
        sol = false;

        exit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("score",0);
                //intent.putExtra("time",0);
                intent.putExtra("status",1);
                startActivity(intent);

            }
        });

        done.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("score",points);
                intent.putExtra("status",1);
                CharSequence t = simpleChronometer.getText();
                intent.putExtra("time",String.valueOf(t));
                startActivity(intent);

            }
        });

        hint.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                if(points<200){
                    Toast.makeText(getContext(), "No Enough Points", Toast.LENGTH_SHORT).show();
                    done.setEnabled(true);
                    return;
                }

                points=points-200;
                point.setText("Points "+ String.valueOf(points));
                String temp;



                for (int randInt = 0; randInt < DIMENSIONS; randInt++) {

                    temp = tileList[randInt];
                    if(parseInt(temp)!=randInt){
                        move(randInt,parseInt(temp),temp);
                        break;
                    }

                }
            }


        });


        //get real image from the string
        byte[] imageBytes = Base64.decode(MainActivity.imageString, Base64.DEFAULT);
        decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        splitImage(decodedImage,DIMENSIONS);


        done.setEnabled(false);
        init();
        scramble();
        setDimensions();



        return builder.create();

    }

    public void move(int current,int des,String img){

        String temp = tileList[des];
        tileList[des]=img;
        tileList[current]=temp;
        display(getContext());
        if (isSolved()) Toast.makeText(getContext(), "YOU WIN!", Toast.LENGTH_SHORT).show();

    }

    private void splitImage(Bitmap image, int chunkNumbers) {

        //For the number of rows and columns of the grid to be displayed
        int rows, cols;

        //For height and width of the small image chunks
        int chunkHeight, chunkWidth;

        //To store all the small image chunks in bitmap format in this list
        chunkedImages = new ArrayList<Bitmap>(chunkNumbers);

        //Getting the scaled bitmap of the source image

        Bitmap bitmap = image;

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        rows = DIMENSIONS/COLUMNS;
        cols =COLUMNS;
        chunkHeight = bitmap.getHeight() / rows;
        chunkWidth = bitmap.getWidth() / cols;

        //xCoord and yCoord are the pixel positions of the image chunks
        int yCoord = 0;
        for (int x = 0; x < rows; x++) {
            int xCoord = 0;
            for (int y = 0; y < cols; y++) {
                chunkedImages.add(Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight));
                xCoord += chunkWidth;
            }
            yCoord += chunkHeight;
        }

    }



    private void init() {

        tileList = new String[DIMENSIONS];
        for (int i = 0; i < DIMENSIONS; i++) {
            tileList[i] = String.valueOf(i);
        }
    }

    private void scramble() {
        int index;
        String temp;
        Random random = new Random();

        for (int i = tileList.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = tileList[index];
            tileList[index] = tileList[i];
            tileList[i] = temp;
        }
    }

    private void setDimensions() {
        ViewTreeObserver vto = grid.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                grid.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int displayWidth = grid.getMeasuredWidth();


                colWidth = displayWidth/COLUMNS;
                colHeight = colWidth;

                grid.setColumnWidth(colWidth);
                grid.setNumColumns(COLUMNS);
                Context con = grid.getContext();
                display(con);
            }
        });
    }




    private void  display(Context context) {
        buttons = new ArrayList<>();
        ImageView button;


        for (int i = 0; i < tileList.length; i++) {
            button = new ImageView(context);
            for(int j = 0; j < tileList.length; j++) {
                if (tileList[i].equals(String.valueOf(j))) {
                    Bitmap resized = Bitmap.createScaledBitmap(chunkedImages.get(j), colWidth, colHeight, true);
                    button.setImageBitmap(resized);
                    break;
                }
            }
            buttons.add(button);
        }
        grid.setAdapter(new GridAdaptor(buttons, colWidth, colHeight));
    }

    private void swap(Context context, int currentPosition, int swap) {

        if(sol){
            return;
        }
        if(points<10){
            simpleChronometer.stop();
            Toast.makeText(context, "Game Over!", Toast.LENGTH_SHORT).show();
            done.setEnabled(true);
            return;
        }

        String newPosition = tileList[currentPosition + swap];
        tileList[currentPosition + swap] = tileList[currentPosition];
        tileList[currentPosition] = newPosition;
        points=points-10;
        display(context);
        point.setText("Points "+ String.valueOf(points));
        if (isSolved()) Toast.makeText(context, "YOU WIN!", Toast.LENGTH_SHORT).show();
    }

    private boolean isSolved() {


        boolean solved = false;

        for (int i = 0; i < tileList.length; i++) {
            if (tileList[i].equals(String.valueOf(i))) {
                solved = true;
            } else {
                solved = false;
                break;
            }
        }

        //game over
        if(points<10){
            simpleChronometer.stop();
        }

        if(solved){
            done.setEnabled(true);
            hint.setClickable(false);
            exit.setVisibility(exit.GONE);
            sol = true;
            simpleChronometer.stop();
        }
        return solved;
    }



    public void moveTiles(Context context, String direction, int position) {


        // Upper-left-corner tile
        if (position == 0) {

            if (direction.equals(right)) swap(context, position, 1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Upper-center tiles
        } else if (position > 0 && position < COLUMNS - 1) {
            if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Upper-right-corner tile
        } else if (position == COLUMNS - 1) {
            if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Left-side tiles
        } else if (position > COLUMNS - 1 && position < DIMENSIONS - COLUMNS &&
                position % COLUMNS == 0) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Right-side AND bottom-right-corner tiles
        } else if (position == COLUMNS * 2 - 1 || position == COLUMNS * 3 - 1) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) {

                // Tolerates only the right-side tiles to swap downwards as opposed to the bottom-
                // right-corner tile.
                if (position <= DIMENSIONS - COLUMNS - 1) swap(context, position,
                        COLUMNS);
                else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Bottom-left corner tile
        } else if (position == DIMENSIONS - COLUMNS) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Bottom-center tiles
        } else if (position < DIMENSIONS - 1 && position > DIMENSIONS - COLUMNS) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(right)) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Center tiles
        } else {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(right)) swap(context, position, 1);
            else swap(context, position, COLUMNS);
        }
    }



}
