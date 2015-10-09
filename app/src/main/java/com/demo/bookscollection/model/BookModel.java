package com.demo.bookscollection.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sujata on 07/10/15.
 */
public class BookModel implements Parcelable {

    public String book_name;
    public String book_author;
    public String book_publisher;
    public int book_id;
    public String book_file_path;
    public String book_file_name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(book_name);
        parcel.writeInt(book_id);
        parcel.writeString(book_publisher);
        parcel.writeString(book_file_path);
        parcel.writeString(book_author);
        parcel.writeString(book_file_name);
    }

    public BookModel(Parcel in) {
        book_name = in.readString();
        book_id = in.readInt();
        book_publisher = in.readString();
        book_file_path = in.readString();
        book_author = in.readString();
        book_file_name = in.readString();
    }

    public BookModel(){

    }

    public static final Parcelable.Creator<BookModel> CREATOR = new Parcelable.Creator<BookModel>() {
        public BookModel createFromParcel(Parcel in) {
            return new BookModel(in);
        }

        public BookModel[] newArray(int size) {
            return new BookModel[size];
        }
    };

}
