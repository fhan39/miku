package com.example.miku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultActivity extends AppCompatActivity {
    private ArrayList<HashMap<String, String>> listData;
    private ArrayList<AddBookActivity.BookInfo> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        listData = new ArrayList<>();
        bookList = new ArrayList<>();

        // 初次加载数据
        loadAndDisplayData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 确保界面刷新时包含追加的书籍
        loadAndDisplayData();
    }

    private void loadAndDisplayData() {
        String query = getIntent().getStringExtra("QUERY");

        listData.clear();
        bookList.clear();

        // 先添加本地新增的书籍
        for (AddBookActivity.BookInfo book : AddBookActivity.addedBooks) {
            addBookToList(book);
        }

        if (query != null && !query.isEmpty()) {
            SearchTask task = new SearchTask();
            task.setListener(new SearchTask.Listener() {
                @Override
                public void onSuccess(String result) {
                    parseAndUpdateList(result);
                }
            });
            task.execute(query);
        } else {
            updateListView();
        }
    }

    private void parseAndUpdateList(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                AddBookActivity.BookInfo bookInfo = new AddBookActivity.BookInfo(
                        jsonObject.getInt("ID"),
                        jsonObject.getString("TITLE"),
                        jsonObject.getString("AUTHOR"),
                        jsonObject.getString("PUBLISHER"),
                        jsonObject.getInt("PRICE"),
                        jsonObject.getString("ISBN")
                );
                addBookToList(bookInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateListView();
    }

    private void addBookToList(AddBookActivity.BookInfo book) {
        bookList.add(book);
        HashMap<String, String> map = new HashMap<>();
        map.put("title", book.title);
        map.put("author", book.author);
        listData.add(map);
    }

    private void updateListView() {
        SimpleAdapter adapter = new SimpleAdapter(this,
                listData,
                android.R.layout.simple_list_item_2,
                new String[]{"title", "author"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // 💡 添加点击事件监听器，让书籍可以被点击
        listView.setOnItemClickListener((parent, view, position, id) -> {
            AddBookActivity.BookInfo bookInfo = bookList.get(position);

            // 跳转到 BookInfoActivity，并传递书籍信息
            Intent intent = new Intent(ResultActivity.this, BookInfoActivity.class);
            intent.putExtra("id", bookInfo.id);
            intent.putExtra("title", bookInfo.title);
            intent.putExtra("author", bookInfo.author);
            intent.putExtra("publisher", bookInfo.publisher);
            intent.putExtra("price", bookInfo.price);
            intent.putExtra("isbn", bookInfo.isbn);
            startActivity(intent);
        });
    }

}
