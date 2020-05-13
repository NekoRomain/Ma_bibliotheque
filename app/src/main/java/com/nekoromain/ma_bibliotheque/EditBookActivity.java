package com.nekoromain.ma_bibliotheque;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditBookActivity extends AppCompatActivity {
    final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    final Calendar newCalendar = Calendar.getInstance();
    private static int RESULT_LOAD_IMAGE = 1;
    private BooksDataSource datasource;
    private Book oldBook;
    private Book newBook;
    private EditText isbn13;
    private EditText isbn10;
    private EditText title;
    private EditText publisher;
    private EditText author;
    private EditText date;
    private EditText imageSelect;
    private ImageView imageView;
    private Bitmap bitmapImage;
    private Button backButton;
    private Button confirmButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);
        datasource = new BooksDataSource(this);
        datasource.open();
        oldBook = getIntent().getParcelableExtra("Book");
        newBook = new Book(oldBook);
        isbn13 = (EditText)findViewById(R.id.editTextIsbn13EditBookInfo);
        isbn10 = (EditText)findViewById(R.id.editTextIsbn10EditBookInfo);
        title = (EditText)findViewById(R.id.editTextTitleEditBookInfo);
        publisher = (EditText)findViewById(R.id.editTextPublishersEditBookInfo);
        author = (EditText)findViewById(R.id.editTextAuthorsEditBookInfo);
        date = (EditText)findViewById(R.id.editTextDateEditBookInfo);
        imageSelect = (EditText)findViewById(R.id.editTextImage);
        imageView = (ImageView) findViewById(R.id.imageViewEditBook);

        backButton = (Button)findViewById(R.id.buttonCancelEditBookInfo);
        confirmButton = (Button)findViewById(R.id.buttonEditBookInfo) ;

        isbn13.setText(oldBook.getIsbn13());
        isbn10.setText(oldBook.getIsbn10());
        title.setText(oldBook.getBookName());
        publisher.setText(oldBook.getPublishers());
        author.setText(oldBook.getAuthors());
        date.setText(oldBook.getPublishDate());
        imageSelect.setText(oldBook.getPathCover());

        /*
        if(!oldBook.getPathCover().contains("booksCover/"))
            imageView.setRotation(90);*/
        if(oldBook.getPathCover().isEmpty()){
            imageView.setImageResource(R.drawable.no_image);
        } else {
            bitmapImage = BitmapFactory.decodeFile(oldBook.getPathCover());
            imageView.setImageBitmap(bitmapImage);
        }


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext()).setTitle("Sauvegarder les changement")
                        .setMessage(R.string.saveEdit)
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                newBook.setIsbn13(isbn13.getText().toString());
                                newBook.setIsbn10(isbn10.getText().toString());
                                newBook.setPublishers(publisher.getText().toString());
                                newBook.setAuthors(author.getText().toString());
                                newBook.setPublishDate(date.getText().toString());
                                newBook.setBookName(title.getText().toString());
                                newBook.setPathCover(imageSelect.getText().toString());
                                boolean delete = !oldBook.getPathCover().equals(newBook.getPathCover());
                                datasource.deleteBook(oldBook, delete);
                                datasource.createBook(newBook);
                                Intent intent = new Intent();
                                intent.putExtra("Callback", 2);
                                intent.putExtra("Book", newBook);
                                intent.putExtra("Old book", oldBook);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                }).setNegativeButton("Non", null).show();

            }
        });

        //création du calendrier à afficher
        final DatePickerDialog fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                date.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        //affichage du calendrier
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });

        //sélection d'une image sur le téléphone
        imageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!imageSelect.getText().toString().isEmpty()){
                    imageSelect.setText("");
                    imageView.setImageResource(R.drawable.no_image);
                    return true;
                } else {
                    return false;
                }

            }
        });

    }

    //choix et chargement de l'image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==  RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null ) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            imageView.setImageURI(selectedImage);
            imageSelect.setText(picturePath);
            Toast.makeText(getApplicationContext(), getText(R.string.appuie_image), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("Book", oldBook);
        setResult(RESULT_CANCELED, intent);
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

}
