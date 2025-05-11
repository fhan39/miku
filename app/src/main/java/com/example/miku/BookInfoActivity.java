package com.example.miku;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BookInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        // 获取 Intent 传递的书籍数据
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        int id = intent.getIntExtra("id", -1);
        String title = intent.getStringExtra("title");
        String author = intent.getStringExtra("author");
        String publisher = intent.getStringExtra("publisher");
        int price = intent.getIntExtra("price", 0);
        String isbn = intent.getStringExtra("isbn");


        TextView titleView = findViewById(R.id.titleView);
        TextView authorView = findViewById(R.id.authorView);
        TextView publisherView = findViewById(R.id.publisherView);
        TextView priceView = findViewById(R.id.priceView);
        TextView isbnView = findViewById(R.id.isbnView);


        if (titleView != null) titleView.setText(title != null ? title : "未知");
        if (authorView != null) authorView.setText(author != null ? author : "未知");
        if (publisherView != null) publisherView.setText(publisher != null ? publisher : "未知");
        if (priceView != null) priceView.setText(price > 0 ? String.valueOf(price) : "未知");
        if (isbnView != null) isbnView.setText(isbn != null ? isbn : "未知");
    }
}
