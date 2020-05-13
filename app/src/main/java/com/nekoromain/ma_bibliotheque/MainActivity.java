package com.nekoromain.ma_bibliotheque;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private int backButtonCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backButtonCount = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button add_book_button = (Button)findViewById(R.id.add_book_button);

        add_book_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addBookAct = new Intent(getApplicationContext(),BookAddActivity.class);
                startActivity(addBookAct);
                finish();
            }
        });

        final Button collectionButton = (Button)findViewById(R.id.collection_button);
        collectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CollectionActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //When press back button, return to the HOME
    @Override
    public void onBackPressed()
    {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(this, R.string.back_phrase, Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        backButtonCount = 0;
    }

}
