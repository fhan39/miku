package com.example.miku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_BOOK_REQUEST = 1;
    private ArrayList<AddBookActivity.BookInfo> addedBooks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSearchButtonClicked(View view) {
        Intent intent = new Intent(this, ResultActivity.class);
        EditText keywordText = findViewById(R.id.keywordText);
        String query = keywordText.getText().toString().trim();

        if (!query.isEmpty()) {
            intent.putExtra("QUERY", query);
            intent.putExtra("ADDED_BOOKS", addedBooks);
            startActivity(intent);
        } else {
            Toast.makeText(this, "検索キーワードを入力して下さい", Toast.LENGTH_SHORT).show();
        }
    }

    // 追加画面に移る
    public void onAddButtonClicked(View view) {
        Intent intent = new Intent(this, AddBookActivity.class);
        startActivityForResult(intent, ADD_BOOK_REQUEST);
    }

    // AddBookActivityのデータ
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_BOOK_REQUEST && resultCode == RESULT_OK) {
            AddBookActivity.BookInfo newBook = (AddBookActivity.BookInfo) data.getSerializableExtra("newBook");
            if (newBook != null) {
                addedBooks.add(newBook);
            }
        }
    }
}

