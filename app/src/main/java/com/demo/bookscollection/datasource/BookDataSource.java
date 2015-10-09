package com.demo.bookscollection.datasource;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.demo.bookscollection.R;
import com.demo.bookscollection.Utils.AppConstant;
import com.demo.bookscollection.activity.AddNewBook;
import com.demo.bookscollection.database.DbHelper;
import com.demo.bookscollection.model.BookModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by sujata on 08/10/15.
 */
public class BookDataSource extends RecyclerView.Adapter<BookDataSource.ViewHolder> {

    private final ArrayList<BookModel> mbookModelArrayList;
    private final Context mContext;
    private final DbHelper db;
    private View view;
    private int position;

    public BookDataSource(ArrayList<BookModel> bookModelArrayList,Context context){
        mbookModelArrayList = bookModelArrayList;
        mContext = context;
        db = new DbHelper(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_row_layout, parent, false);
        ViewHolder dataObjectHolder = new ViewHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        BookModel model = mbookModelArrayList.get(position);
        holder.bookName.setText(model.book_name);
        holder.authorName.setText(model.book_author);
        holder.publisherName.setText(model.book_publisher);
        this.position = position;

        if (model.book_file_path != null) {
            try {
                File f = new File(model.book_file_path,model.book_file_name);
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                holder.bookImageView.setImageBitmap(b);
            }
            catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }

        holder.item_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, AddNewBook.class).putExtra(AppConstant.FROM_EDIT, true).putExtra("bookmodel", mbookModelArrayList.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mbookModelArrayList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final TextView bookName;
        private final TextView authorName;
        private final TextView publisherName;
        private final ImageView bookImageView;
        private final View item_View;
        public TextView mTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            bookName = (TextView) itemView.findViewById(R.id.bookName);
            authorName = (TextView) itemView.findViewById(R.id.authorName);
            publisherName = (TextView) itemView.findViewById(R.id.publisherName);
            bookImageView = (ImageView) itemView.findViewById(R.id.bookImageView);
            this.item_View = itemView;
        }

        @Override
        public void onClick(View v) {
//            mbookModelArrayList.get();
//            mContext.startActivity(new Intent(mContext, AddNewBook.class).putExtra(AppConstant.FROM_EDIT, true).putExtra("bookmodel", mbookModelArrayList.get(position)));
        }

      @Override
        public boolean onLongClick(View v) {
            showDialog();
            return true;
        }
    }

    public  void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getResources().getString(R.string.delete_a_book));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                BookModel bookModel = mbookModelArrayList.get(position);
                boolean isdeleted = db.deleteBook(bookModel.book_id);
                if(isdeleted) {
                    mbookModelArrayList.remove(position);
                    notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
