package com.demo.bookscollection.database;

/**
 * Created by sujata on 8/10/15.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.demo.bookscollection.model.BookModel;
import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bookscollection.db";
    public static final String BOOK_TABLE_NAME = "book";
    public static final String BOOK_ID = "book_id";
    public static final String BOOK_NAME = "book_name";
    public static final String BOOK_AUTHOR_NAME = "book_author_name";
    public static final String BOOK_PUBLISHER_NAME = "book_publisher_name";
    public static final String BOOK_FILE_PATH = "book_file_path";
    public static final String BOOK_FILE_NAME = "book_file_name";
    public static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + BOOK_TABLE_NAME + " " +
                        "(" +
                        BOOK_ID + " integer primary key AUTOINCREMENT, " +
                        BOOK_NAME + " text," +
                        BOOK_AUTHOR_NAME + " text," +
                        BOOK_PUBLISHER_NAME + " text," +
                        BOOK_FILE_PATH + " text," +
                        BOOK_FILE_NAME + " text "+
                        ")"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + BOOK_TABLE_NAME + " ");
        onCreate(db);
    }

    public boolean insertBook(String bookName, String authorName, String publisherName, String bookFilePath,String bookFileName)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("book_name", bookName);
            contentValues.put("book_author_name", authorName);
            contentValues.put("book_publisher_name", publisherName);
            contentValues.put("book_file_path", bookFilePath);
            contentValues.put("book_file_name", bookFileName);
            db.insert(BOOK_TABLE_NAME, null, contentValues);
            db.close();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateBookDetails(Object entity) {
        BookModel bookModel = (BookModel)entity;
        if (bookModel == null) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.update(BOOK_TABLE_NAME,
                generateContentValuesFromObject(bookModel), BOOK_ID + "=" + bookModel.book_id, null);
//        long result = db.insertWithOnConflict(BOOK_TABLE_NAME, null, generateContentValuesFromObject(bookModel) ,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return result != -1;

    }

    public ContentValues generateContentValuesFromObject(BookModel bookModel) {
        if (bookModel == null) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(BOOK_ID, bookModel.book_id);
        values.put(BOOK_NAME, bookModel.book_name);
        Log.d("book name: ",bookModel.book_name);
        values.put(BOOK_AUTHOR_NAME, bookModel.book_author);
        values.put(BOOK_PUBLISHER_NAME, bookModel.book_publisher);
        values.put(BOOK_FILE_PATH, bookModel.book_file_path);
        values.put(BOOK_FILE_NAME, bookModel.book_file_name);

        return values;
    }


    public String[] getAllColumns() {
        return new String[] {
                BOOK_ID,
                BOOK_NAME,
                BOOK_AUTHOR_NAME,
                BOOK_PUBLISHER_NAME,
                BOOK_FILE_PATH,
                BOOK_FILE_NAME
        };
    }

    public ArrayList<BookModel> read() {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        Cursor cursor = mDatabase.query(BOOK_TABLE_NAME, getAllColumns(), null,
                null, null, null, null);
        ArrayList<BookModel> tests = new ArrayList<BookModel>();
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                tests.add(generateObjectFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return tests;
    }

    public boolean deleteBook(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isdeleted =  db.delete(BOOK_TABLE_NAME, BOOK_ID + "=" + id, null)>0 ;
        db.close();
        return isdeleted;
    }

    public BookModel generateObjectFromCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        BookModel bookModel = new BookModel();
        bookModel.book_id = cursor.getInt(cursor.getColumnIndex(BOOK_ID));
        bookModel.book_name = cursor.getString(cursor.getColumnIndex(BOOK_NAME));
        bookModel.book_author = cursor.getString(cursor.getColumnIndex(BOOK_AUTHOR_NAME));
        bookModel.book_publisher = cursor.getString(cursor.getColumnIndex(BOOK_PUBLISHER_NAME));
        bookModel.book_file_path = cursor.getString(cursor.getColumnIndex(BOOK_FILE_PATH));
        bookModel.book_file_name = cursor.getString(cursor.getColumnIndex(BOOK_FILE_NAME));
        return bookModel;
    }
}
