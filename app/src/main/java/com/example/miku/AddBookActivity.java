package com.example.miku;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AddBookActivity extends AppCompatActivity {

    public static ArrayList<BookInfo> addedBooks = new ArrayList<>();

    private EditText titleInput, authorInput, publisherInput, priceInput, isbnInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // ui初期化
        titleInput = findViewById(R.id.titleInput);
        authorInput = findViewById(R.id.authorInput);
        publisherInput = findViewById(R.id.publisherInput);
        priceInput = findViewById(R.id.priceInput);
        isbnInput = findViewById(R.id.isbnInput);
    }

    // ISBN検索
    public void searchByISBN(View view) {
        String isbn = isbnInput.getText().toString().trim();
        if (isbn.isEmpty()) {
            Toast.makeText(this, "ISBNを入力してください", Toast.LENGTH_SHORT).show();
            return;
        }

        new FetchBookInfoTask().execute(isbn);
    }

    // 書籍の追加
    public void onAddBookButtonClicked(View view) {
        String title = titleInput.getText().toString().trim();
        String author = authorInput.getText().toString().trim();
        String publisher = publisherInput.getText().toString().trim();
        String priceStr = priceInput.getText().toString().trim();
        String isbn = isbnInput.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty() || publisher.isEmpty() || priceStr.isEmpty() || isbn.isEmpty()) {
            Toast.makeText(this, "書籍の情報をすべて入力してください", Toast.LENGTH_SHORT).show();
            return;
        }

        int price = Integer.parseInt(priceStr);
        BookInfo newBook = new BookInfo(addedBooks.size() + 1, title, author, publisher, price, isbn);
        addedBooks.add(newBook);

        Intent intent = new Intent();
        intent.putExtra("newBook", newBook);
        setResult(RESULT_OK, intent);

        Toast.makeText(this, "追加成功", Toast.LENGTH_SHORT).show();
        finish();
    }


    private class FetchBookInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String isbn = params[0];
            String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn;

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(AddBookActivity.this, "見つからない", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject jsonResponse = new JSONObject(result);
                JSONArray items = jsonResponse.getJSONArray("items");
                if (items.length() > 0) {
                    JSONObject book = items.getJSONObject(0).getJSONObject("volumeInfo");

                    // データを確保
                    String title = book.optString("title", "");
                    JSONArray authorsArray = book.optJSONArray("authors");
                    String author = (authorsArray != null && authorsArray.length() > 0) ? authorsArray.getString(0) : "";
                    String publisher = book.optString("publisher", "");
                    int price = 0; // 価格は知らない

                    // ✅ 更新 UI
                    titleInput.setText(title);
                    authorInput.setText(author);
                    publisherInput.setText(publisher);
                    priceInput.setText(String.valueOf(price));
                } else {
                    Toast.makeText(AddBookActivity.this, "見つからない", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(AddBookActivity.this, "失敗", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static class BookInfo implements Serializable {
        int id;
        String title;
        String author;
        String publisher;
        int price;
        String isbn;

        public BookInfo(int id, String title, String author, String publisher, int price, String isbn) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.publisher = publisher;
            this.price = price;
            this.isbn = isbn;
        }
    }
}
