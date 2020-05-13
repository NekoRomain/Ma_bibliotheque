package com.nekoromain.ma_bibliotheque;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import pl.droidsonroids.gif.GifImageView;

public class DetailBookAddActivity extends AppCompatActivity {
    private BooksDataSource datasource;
    private Bitmap coverImage;
    private GifImageView imageCoverView;
    private Button addBookButton;
    private Book book;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        datasource = new BooksDataSource(this);
        datasource.open();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_book_add);
        //get the book in the intent
        Intent intent = getIntent();

        book = intent.getParcelableExtra("Book");
        final TextView viewInfoBook = (TextView)findViewById(R.id.textViewBookInfoAdd);
        //final WebView viewCover = (WebView)findViewById(R.id.webViewInfoBookAdd);
        imageCoverView = (GifImageView) findViewById(R.id.imageViewInfoAdd);
        final Button cancelButton = (Button)findViewById(R.id.cancelButtonAdd);
        addBookButton = (Button)findViewById(R.id.addBookButtonAdd);
        addBookButton.setClickable(false);
        new ImageLoaderClass().execute(book.getPathCover());
        //viewCover.loadUrl(book.getPathCover());
        viewInfoBook.setText(book.toString());

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),BookAddActivity.class);
                startActivity(intent);
                finish();
            }
        });

        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(v.getContext()).setTitle("Ajout livre").setMessage("Voulez-vous ajouter le livre ?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(datasource.isExiste((!book.getIsbn13().equals(""))? book.getIsbn13(): book.getIsbn10())){
                            new AlertDialog.Builder(v.getContext()).setTitle("Erreur").setMessage(R.string.book_already_existe).show();
                            /*Toast.makeText(v.getContext(), R.string.book_already_existe,
                                    Toast.LENGTH_SHORT).show();*/
                        } else {
                            new ImageDownLoaderClass().execute();
                        }
                    }
                }).setNegativeButton("Non", null).show();


            }
        });
    }



    public String saveToInternalStorage(Bitmap bitmapImage, String imageName){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir

        File directory = cw.getDir("booksCover", Context.MODE_PRIVATE);
        Log.d("cw: ", directory.getAbsolutePath());
        // Create imageDir
        File mypath=new File(directory,imageName+".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }

    //Get the image in a Bitmap
    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //For OutOfMemory
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),BookAddActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    private class ImageLoaderClass extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
           // GifDrawable myDrawable;
            super.onPreExecute();
        }
        protected Bitmap doInBackground(String... args) {
            try {
                coverImage = getBitmapFromURL(args[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return coverImage;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                imageCoverView.setImageBitmap(image);
            } else {
                imageCoverView.setImageResource(R.drawable.no_image);
            }
            addBookButton.setClickable(true);
        }
    }

    private class ImageDownLoaderClass extends AsyncTask<String, String, Book> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected Book doInBackground(String... args) {
            try {
                if(book.getIsbn13() != "")
                    book.setPathCover(saveToInternalStorage(coverImage, book.getIsbn13()));
                else
                    book.setPathCover(saveToInternalStorage(coverImage, book.getIsbn10()));
                datasource.createBook(book);
                datasource.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Livre ajouté", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return book;
        }

        protected void onPostExecute() {

        }
    }
}
