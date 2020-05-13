package com.nekoromain.ma_bibliotheque;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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

public class AddBookInfoActivity extends AppCompatActivity {
    final SimpleDateFormat  dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    final Calendar newCalendar = Calendar.getInstance();
    private Button buttonAddBookInfo;
    private static int RESULT_LOAD_IMAGE = 1;
    private EditText isbn13;
    private EditText isbn10;
    private EditText title;
    private EditText publisher;
    private EditText author;
    private EditText date;
    private EditText imageSelect;
    private BooksDataSource datasource;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_info);

        datasource = new BooksDataSource(this);
        datasource.open();
        imageView = (ImageView) findViewById(R.id.imageViewAddBook);
        imageView.setImageResource(R.drawable.no_image);
        isbn13 = (EditText)findViewById(R.id.editTextIsbn13AddBookInfo);
        isbn10 = (EditText)findViewById(R.id.editTextIsbn10AddBookInfo);
        title = (EditText)findViewById(R.id.editTextTitleAddBookInfo);
        publisher = (EditText)findViewById(R.id.editTextPublishersAddBookInfo);
        author = (EditText)findViewById(R.id.editTextAuthorsAddBookInfo);
        date = (EditText)findViewById(R.id.editTextDateAddBookInfo);
        //création du calendrier à afficher
        final DatePickerDialog fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                date.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            //affichage du calendrier
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });

        //selection d'une image sur le téléphone
        imageSelect = (EditText)findViewById(R.id.editAddTextImage);
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

        final Button backButton = (Button)findViewById(R.id.buttonCancelAddBookInfo);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getApplicationContext(), BookAddActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonAddBookInfo = (Button)findViewById(R.id.buttonAddBookInfo);
        buttonAddBookInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Création du bouquin
                if((isbn10.getText() != null ||isbn13.getText()!= null)
                        && (isbn13.getText().toString().length() == 13
                        || isbn10.getText().toString().length() == 10)) {
                    new AlertDialog.Builder(v.getContext()).setTitle("Sauvegarde")
                            .setMessage("Voulez_vous sauvegarder le livre ?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Book book = new Book();
                                    if(isbn13.getText() != null)
                                        book.setIsbn13(isbn13.getText().toString());
                                    if(isbn10.getText() != null)
                                        book.setIsbn10(isbn10.getText().toString());
                                    if(title.getText() != null)
                                        book.setBookName(title.getText().toString());
                                    if(publisher.getText() != null)
                                        book.setPublishers(publisher.getText().toString());
                                    if(author.getText() != null)
                                        book.setAuthors(author.getText().toString());
                                    if(date.getText() != null)
                                        book.setPublishDate(date.getText().toString());
                                    if(imageSelect.getText() != null)
                                        book.setPathCover(imageSelect.getText().toString());
                                    datasource.createBook(book);
                                    datasource.close();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    Toast.makeText(getApplicationContext(), "Livre ajouté", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    finish();
                                }
                            }).setNegativeButton("Non", null).show();
                } else {
                    new AlertDialog.Builder(v.getContext()).setTitle("Erreur").setMessage(R.string.isbn_requis).show();
                    //Toast.makeText(v.getContext(), R.string.isbn_requis, Toast.LENGTH_SHORT).show();
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
            imageSelect = (EditText)findViewById(R.id.editAddTextImage);
            imageSelect.setText(picturePath);
            Toast.makeText(getApplicationContext(), getText(R.string.appuie_image), Toast.LENGTH_SHORT).show();
        }
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
        Intent intent  = new Intent(getApplicationContext(), BookAddActivity.class);
        startActivity(intent);
        finish();
    }


}
