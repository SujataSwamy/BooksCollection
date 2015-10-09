package com.demo.bookscollection.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.demo.bookscollection.R;
import com.demo.bookscollection.database.DbHelper;
import com.demo.bookscollection.datasource.BookDataSource;
import com.demo.bookscollection.i.RecyclerViewClickListener;
import com.demo.bookscollection.model.BookModel;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private BookDataSource mAdapter;
    private Toolbar mToolbar;
    private DbHelper db;
    private RelativeLayout introTextRelativeLayout;
    private ArrayList<BookModel> bookModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        introTextRelativeLayout = (RelativeLayout) findViewById(R.id.introTextRelativeLayout);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
         bookModelList = checkForExistingBooks();
        if(bookModelList != null){
            mAdapter = new BookDataSource(bookModelList,MainActivity.this);
        }else {
            mAdapter = new BookDataSource(new ArrayList<BookModel>(),MainActivity.this);
        }

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(),"cccc",Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    public ArrayList<BookModel> checkForExistingBooks(){
        db = new DbHelper(this);
        ArrayList<BookModel> bookModelList = db.read();
        if(bookModelList.size() > 0){
            introTextRelativeLayout.setVisibility(View.GONE);
            mAdapter = new BookDataSource(bookModelList,MainActivity.this);
            mRecyclerView.setAdapter(mAdapter);
        }else {
            introTextRelativeLayout.setVisibility(View.VISIBLE);
        }
        return  bookModelList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForExistingBooks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(db != null) {
            db.close();
            db = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            startActivity(new Intent(MainActivity.this,AddNewBook.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        mAdapter = new BookDataSource(bookModelList,MainActivity.this);
    }
}
