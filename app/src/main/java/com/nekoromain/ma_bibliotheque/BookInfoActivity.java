package com.nekoromain.ma_bibliotheque;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class BookInfoActivity extends AppCompatActivity {
    private ImageView cover;
    private BooksDataSource datasource;
    private TextView infoText;
    private Button deleteButton;
    private Button cancelButton;
    private Button editButton;
    final int REQUEST_CODE = 1;
    private Bitmap image;
    private Book book;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        PermissionDevice.verifyStoragePermissions(this);
        Intent intent  = getIntent();
        book = intent.getParcelableExtra("Book");
        cover = (ImageView)findViewById(R.id.imageViewInfo);
        infoText = (TextView)findViewById(R.id.textViewBookInfo);
        deleteButton = (Button)findViewById(R.id.deleteBookInfo);
        cancelButton = (Button)findViewById(R.id.cancelBookInfo) ;
        editButton = (Button)findViewById(R.id.editButtonBookInfo);
        datasource = new BooksDataSource(this);
        datasource.open();
        infoText.setText(book.toString());
        if(book.getPathCover().isEmpty()){
            cover.setImageResource(R.drawable.no_image);
        } else {
            image = BitmapFactory.decodeFile(book.getPathCover());
            if(!book.getPathCover().contains("app_booksCover/"))
                cover.setRotation(90);
            cover.setImageBitmap(image);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext()).setTitle("Suppression")
                        .setMessage("Voulez-vous vraiment supprimer le livre de la collection ?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                datasource.deleteBook(book, true);
                                datasource.close();
                                Intent intent  = new Intent();
                                intent.putExtra("Callback", 1);
                                intent.putExtra("Book", book);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }).setNegativeButton("Non", null).show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditBookActivity.class);
                intent.putExtra("Book", book);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }


    @Override
    protected void onResume() {
        datasource.open();
        //bookAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        datasource.close();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) { // Please, use a final int instead of hardcoded int value
            if (resultCode == RESULT_OK) {
                Intent intent = data;
                int callback = intent.getIntExtra("Callback", 0);
                if(callback == 2){
                    Intent intentN = new Intent(getApplicationContext(),CollectionActivity.class);
                    intentN.putExtra("Callback", 2);
                    intentN.putExtra("Book", intent.getParcelableExtra("Book"));
                    intentN.putExtra("Old book", intent.getParcelableExtra("old book"));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            } else if(requestCode == RESULT_CANCELED){
                book = getIntent().getParcelableExtra("Book");
                infoText.setText(book.toString());
                image = BitmapFactory.decodeFile(book.getPathCover());
                /*
                if(!book.getPathCover().contains("app_booksCover/"))
                    cover.setRotation(90);*/
                cover.setImageBitmap(image);
            }
        }
    }

}
