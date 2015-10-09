package com.demo.bookscollection.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import com.demo.bookscollection.R;
import com.demo.bookscollection.Utils.AppConstant;
import com.demo.bookscollection.database.DbHelper;
import com.demo.bookscollection.model.BookModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sujata on 8/10/15.
 */
public class AddNewBook extends AppCompatActivity {

    private static final int CAMERA_PIC_REQUEST = 1;
    private Toolbar mToolbar;
    private FrameLayout imageViewframeLayout;
    private ImageView newBookImageView;
    private EditText bookNameEditText;
    private EditText authorNameEditText;
    private EditText publisherNameEditText;
    private String bookNameText;
    private String authorNameText;
    private String publisherNameText;
    private DbHelper db;
//    private RelativeLayout takePhotoRelativeLayout;
    Handler mhandler = new Handler();
    private ScrollView scrollView;
    private boolean fromEdit;
    private BookModel bookModel;
    private RelativeLayout takePhotoBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_book);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);





        fromEdit = (boolean) getIntent().getBooleanExtra(AppConstant.FROM_EDIT, false);
        bookModel = (BookModel) getIntent().getParcelableExtra("bookmodel");

        AppConstant.FILE_PATH = null;
        AppConstant.FILE_NAME = null;
        db = new DbHelper(this);

        imageViewframeLayout = (FrameLayout) findViewById(R.id.imageViewframeLayout);
        newBookImageView = (ImageView) findViewById(R.id.newBookImageView);
//        takePhotoRelativeLayout = (RelativeLayout) findViewById(R.id.takePhoto);
//        takePhotoRelativeLayout.setVisibility(View.VISIBLE);
        bookNameEditText = (EditText) findViewById(R.id.bookNameEditText);
        authorNameEditText = (EditText) findViewById(R.id.authorNameEditText);
        publisherNameEditText = (EditText) findViewById(R.id.publisherNameEditText);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        takePhotoBg = (RelativeLayout) findViewById(R.id.takePhotoBg);

        imageViewframeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_PIC_REQUEST);
            }
        });

        bookNameEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                        bookNameEditText.requestFocus();
                    }
                }, 500);
                return false;
            }
        });

        if(fromEdit){
            mToolbar.setTitle(getResources().getString(R.string.edit_book_text));
            inflateBookModel();
        }else{
            mToolbar.setTitle(getResources().getString(R.string.add_a_book_text));
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.back_icon);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void inflateBookModel() {
        if(bookModel!= null){
            bookNameEditText.setText(bookModel.book_name);
            authorNameEditText.setText(bookModel.book_author);
            publisherNameEditText.setText(bookModel.book_publisher);
            loadImageFromInternalStorage(bookModel.book_file_path,bookModel.book_file_name);
        }
    }

    public String getFileName(){
        int randomNumber = (int)(Math.random()*9000)+1000;
        String fileName = "image_"+randomNumber+".jpg";
        return fileName;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST) {
            if(data != null && data.getExtras()!=null) {
                Bitmap bookBitmap = (Bitmap) data.getExtras().get("data");
                newBookImageView.setImageBitmap(bookBitmap);
                takePhotoBg.setVisibility(View.GONE);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bookBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    AppConstant.FILE_PATH = saveToInternalSorage(bookBitmap);
                } else {
                    String dir = Environment.getExternalStorageDirectory() + File.separator + "bookscollection";
                    File folder = new File(dir); //folder name
                    folder.mkdirs();
                    AppConstant.FILE_NAME = getFileName();
                    File file = new File(dir, AppConstant.FILE_NAME); //getFileName()
                    try {
                        file.createNewFile();
                        FileOutputStream fo = new FileOutputStream(file);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    AppConstant.FILE_PATH = folder.getAbsolutePath();//+"/"+filename;
                }
            }
        }
    }

    private String saveToInternalSorage(Bitmap bookBitmap){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("bookscollection", Context.MODE_PRIVATE);
        AppConstant.FILE_NAME = getFileName();
        File mypath=new File(directory,AppConstant.FILE_NAME);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bookBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    private void loadImageFromInternalStorage(String path, String book_file_name){
        if (path != null) {
            try {
                File f = new File(path,book_file_name);
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                newBookImageView.setImageBitmap(b);
//                imageViewframeLayout.setVisibility(View.GONE);
            }
            catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveBook();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveBook() {

        if(bookNameEditText.getText()!=null){
            bookNameText = bookNameEditText.getText().toString();
        }
            if(bookNameText.length() == 0){
                Toast.makeText(AddNewBook.this,""+getResources().getString(R.string.error_book_name_text),Toast.LENGTH_LONG).show();
                return;
            }

        if(authorNameEditText.getText()!=null){
            authorNameText = authorNameEditText.getText().toString();
        }
        if(publisherNameEditText.getText()!=null){
            publisherNameText = publisherNameEditText.getText().toString();
        }

        if(fromEdit){
            BookModel model = new BookModel();
            model.book_id = bookModel.book_id;
            if(AppConstant.FILE_PATH != null) {
                model.book_file_path = AppConstant.FILE_PATH;
            }else{
                model.book_file_path = bookModel.book_file_path;
            }
            if(AppConstant.FILE_NAME != null) {
                model.book_file_name = AppConstant.FILE_NAME;
            }else{
                model.book_file_name = bookModel.book_file_name;
            }
            if(publisherNameText.length() > 0){
                model.book_publisher = publisherNameText;
            }else{
                model.book_publisher = bookModel.book_publisher;
            }
            if(authorNameText.length() > 0){
                model.book_author = authorNameText;
            }else{
                model.book_author = bookModel.book_author;
            }

            if(bookNameText.length() > 0){
                model.book_name = bookNameText;
            }else{
                model.book_name = bookModel.book_name;
            }
            boolean success =  db.updateBookDetails(model);
            if(success){
                finish();
            }else {
                Toast.makeText(AddNewBook.this, "" + getResources().getString(R.string.error_update_text), Toast.LENGTH_LONG).show();
                return;
            }
        }else {
            boolean success = db.insertBook(bookNameText, authorNameText, publisherNameText, AppConstant.FILE_PATH,AppConstant.FILE_NAME);
            if (success) {
                finish();
            } else {
                Toast.makeText(AddNewBook.this, "" + getResources().getString(R.string.error_insert_text), Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
}
