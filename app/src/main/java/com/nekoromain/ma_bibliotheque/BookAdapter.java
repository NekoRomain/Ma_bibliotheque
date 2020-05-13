package com.nekoromain.ma_bibliotheque;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Adapter permettant d'afficher les livres de la base de données dans une ListView ou une GridView
 */
public class BookAdapter extends BaseAdapter {

    private Activity activity;
    private List<Book> bookList;
    private List<Book> bookListOriginal;

        public BookAdapter(Activity context, List<Book> data){
            this.activity = context;
            bookList = data;
            bookListOriginal = new ArrayList<>();
            bookListOriginal.addAll(bookList);
    }

    @Override
    public int getCount(){
        return bookList.size();
    }

    @Override
    public Book getItem(int i){
        return bookList.get(i);
    }

    @Override
    public long getItemId(int i){
        return i;
    }

    @Override
    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {
        Book book = bookList.get(position);
        if(convertView == null) {
            convertView = activity.getLayoutInflater()
                    .inflate(R.layout.books_list_item, null, false);
        }
        TextView description = (TextView)convertView.findViewById(R.id.textViewListItem);
        ImageView cover = (ImageView)convertView.findViewById(R.id.imageViewListItem);
        description.setText(book.toString());
        if(book.getPathCover().isEmpty()){
            cover.setImageResource(R.drawable.no_image);
        } else {
            Bitmap myBitmap = BitmapFactory.decodeFile(book.getPathCover());
        /*
        if(!book.getPathCover().contains("app_booksCover/"))
            cover.setRotation(90);*/
            cover.setImageBitmap(myBitmap);
        }

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        bookList.clear();
        if (charText.length() == 0 || charText.equals("")) {
            bookList.addAll(bookListOriginal);
        }
        else
        {
            for (Book b : bookListOriginal) {
                if (b.getBookName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    bookList.add(b);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateOriginList(List<Book> originalList){
        this.bookListOriginal = new ArrayList<>();
        bookListOriginal.addAll(originalList);
    }
}
