package com.nekoromain.ma_bibliotheque;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BookAddActivity extends AppCompatActivity {

    final static String URL = "https://openlibrary.org/api/books?format=json&jscmd=data&bibkeys=ISBN:";
    final int REQUEST_CODE = 1;
    final int ISBN_LENGTH = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_add);
        final Button scanButton = (Button)findViewById(R.id.buttonScan);

        final EditText isbnField = (EditText)findViewById(R.id.ISBN_field);
        isbnField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(ISBN_LENGTH)});

        final Button sendSearchRequestButton = (Button)findViewById(R.id.search_book_button);
        sendSearchRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isbnField.length() == 10 || isbnField.length() == 13){
                    isbnField.clearFocus();
                    requestBook(isbnField.getText().toString());
                } else {
                    new AlertDialog.Builder(v.getContext()).setTitle("Erreur").setMessage(R.string.isbn_requis).show();
                }

            }
        });

        final Button addBookWithoutISBN = (Button)findViewById(R.id.buttonAddBookNoISBN);
        addBookWithoutISBN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddBookInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });


        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    //send request to the api
    private void requestBook(final String isbn){
        //Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL+isbn, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.has("ISBN:" + isbn)){
                            Book book = createBook(isbn, response);
                            //Page d'info du livre
                            Intent intent = new Intent(getApplicationContext()
                                    , DetailBookAddActivity.class);
                            intent.putExtra("Book", book);
                            startActivityForResult(intent, REQUEST_CODE);
                        } else {
                            //Page d'erreur : livre introuvable dans la base de données
                            Intent errorNoBookAct = new Intent(getApplicationContext()
                                    ,BookNotFoundActivity.class);
                            startActivityForResult(errorNoBookAct, REQUEST_CODE);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Request Book Exception", error.toString());
                    }
                });
                queue.add(jsonObjectRequest);
    }

    private Book createBook(String isbn, JSONObject bookResponse) {
        Book book = new Book();
        try{
            if(bookResponse.has("ISBN:"+isbn)){

                if(bookResponse.getJSONObject("ISBN:"+isbn).has("publishers")){
                    JSONArray publishers = bookResponse.getJSONObject("ISBN:"+isbn)
                            .getJSONArray("publishers");
                    List<String> publishersNames = new ArrayList<String>();
                    for(int i = 0; i< publishers.length(); i++){
                        publishersNames.add(publishers.getJSONObject(i).getString("name"));
                    }
                    book.setPublishers(publishersNames.get(0));
                } else {
                    book.setPublishers("Unknown");
                }
                if(bookResponse.getJSONObject("ISBN:"+isbn).has("authors")){
                    JSONArray authors = bookResponse.getJSONObject("ISBN:"+isbn)
                            .getJSONArray("authors");
                    List<String> authorsName = new ArrayList<String>();
                    for(int i = 0; i < authors.length(); i++){
                        authorsName.add(authors.getJSONObject(i).getString("name"));
                    }
                    book.setAuthors(authorsName.get(0));
                } else {
                    book.setAuthors("Unknown");
                }
                if(bookResponse.getJSONObject("ISBN:"+isbn).has("identifiers")) {
                    JSONObject identifiers = bookResponse.getJSONObject("ISBN:" + isbn)
                            .getJSONObject("identifiers");
                    if(identifiers.has("isbn_10")){
                        String[] sortie = identifiers.getString("isbn_10").split("\"");
                        book.setIsbn10(sortie[1]);
                    }
                   if(identifiers.has("isbn_13")){
                       String[] sortie = identifiers.getString("isbn_13").split("\"");
                       book.setIsbn13(sortie[1]);
                   }
                }

                if(bookResponse.getJSONObject("ISBN:"+isbn).has("title")){
                    book.setBookName(bookResponse.getJSONObject("ISBN:"+isbn).getString("title"));
                }

                if(bookResponse.getJSONObject("ISBN:"+isbn).has("publish_date")){
                    book.setPublishDate(bookResponse.getJSONObject("ISBN:"+isbn)
                            .getString("publish_date"));
                }
                if(bookResponse.getJSONObject("ISBN:"+isbn).has("cover")){
                    book.setPathCover(bookResponse.getJSONObject("ISBN:"+isbn)
                            .getJSONObject("cover").getString("medium"));
                }
            }
        } catch (JSONException e) {
            return null;
        }
        return book;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_CODE){
                String isbnScan = data.getStringExtra("ISBN_SCAN");
                if(isbnScan != null && isbnScan.length() == 13 || isbnScan.length() == 10){
                    requestBook(isbnScan);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Requête lancé"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent mainAct = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(mainAct);
        finish();
    }

}
