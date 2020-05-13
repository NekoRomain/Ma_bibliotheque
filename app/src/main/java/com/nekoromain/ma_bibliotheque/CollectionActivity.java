package com.nekoromain.ma_bibliotheque;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CollectionActivity extends AppCompatActivity {
    private BooksDataSource datasource;
    final int REQUEST_CODE = 1;
    private List<Book> bookList;
    private SearchView searchView;
    //private ArrayAdapter<Book> bookAdapter;
    private BookAdapter bookAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        PermissionDevice.verifyStoragePermissions(this);
        datasource = new BooksDataSource(this);
        datasource.open();
        bookList = datasource.getAllBooks();
        final ListView listView = (ListView)findViewById(R.id.listViewBook);
        searchView = (SearchView)findViewById(R.id.searchView);
        bookAdapter = new BookAdapter(this, bookList);
        listView.setAdapter(bookAdapter);
        listView.setEmptyView(findViewById(R.id.empty_list_item));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book b = bookAdapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
                intent.putExtra("Book", b);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                bookAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)){
                    bookAdapter.filter("");
                }
                return false;
            }
        });
        int searchCloseButtonId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);

        ImageView closeButton = (ImageView) this.searchView.findViewById(searchCloseButtonId);
        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery("", true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) { // Please, use a final int instead of hardcoded int value
            if (resultCode == RESULT_OK) {
                Intent intent = data;
                int callback = intent.getIntExtra("Callback", 0);
                //book delete
                Log.d("callback value", String.valueOf(callback));
                //Suppresion d'un livre
                if(callback == 1){
                    Book bookToDelete = intent.getParcelableExtra("Book");
                    bookList.remove(bookToDelete);
                    bookAdapter.updateOriginList(bookList);
                    bookAdapter.notifyDataSetChanged();
                }
                //Modification d'un livre reçu
                if(callback == 2){
                    Book bookToDelete = intent.getParcelableExtra("Old book");
                    Book editedBook = intent.getParcelableExtra("Book");
                    bookList.remove(bookToDelete);
                    bookList.add(editedBook);
                    bookAdapter.updateOriginList(bookList);
                    bookAdapter.notifyDataSetChanged();

                }
            }
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        datasource.close();
        Intent intent  = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}
