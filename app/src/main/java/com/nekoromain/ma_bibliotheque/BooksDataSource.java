package com.nekoromain.ma_bibliotheque;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BooksDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_TITLE, MySQLiteHelper.COLUMN_PUBLISHER, MySQLiteHelper.COLUMN_AUTHOR,
            MySQLiteHelper.COLUMN_DATE, MySQLiteHelper.COLUMN_ISBN13, MySQLiteHelper.COLUMN_ISBN10,
            MySQLiteHelper.COLUMN_COVERPATH};

    public BooksDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Book createBook(String title, String publisher, String author, String date, String isbn13,
                              String isbn10, String pathCover) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TITLE, title);
        values.put(MySQLiteHelper.COLUMN_PUBLISHER, publisher);
        values.put(MySQLiteHelper.COLUMN_AUTHOR, author);
        values.put(MySQLiteHelper.COLUMN_DATE, date);
        values.put(MySQLiteHelper.COLUMN_ISBN13, isbn13);
        values.put(MySQLiteHelper.COLUMN_ISBN10, isbn10);
        values.put(MySQLiteHelper.COLUMN_COVERPATH, pathCover);
        long insertId = database.insert(MySQLiteHelper.TABLE_BOOKS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Book newComment = cursorToBook(cursor);
        cursor.close();
        return newComment;
    }

    public Book createBook(Book book) {
        ContentValues values = new ContentValues(14);
        values.put(MySQLiteHelper.COLUMN_TITLE, book.getBookName());
        values.put(MySQLiteHelper.COLUMN_PUBLISHER, book.getPublishers());
        values.put(MySQLiteHelper.COLUMN_AUTHOR, book.getAuthors());
        values.put(MySQLiteHelper.COLUMN_DATE, book.getPublishDate());
        values.put(MySQLiteHelper.COLUMN_ISBN13, book.getIsbn13());
        values.put(MySQLiteHelper.COLUMN_ISBN10, book.getIsbn10());
        values.put(MySQLiteHelper.COLUMN_COVERPATH, book.getPathCover());
        long insertId = database.insert(MySQLiteHelper.TABLE_BOOKS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Book newComment = cursorToBook(cursor);
        cursor.close();
        return newComment;
    }

    public boolean isExiste(String ISBN){
        Cursor cursor;
        boolean trouver = false;
        if(ISBN != null){
            boolean isISBN13 = (ISBN.length() == 13);
            if(isISBN13){
               cursor = database.query(MySQLiteHelper.TABLE_BOOKS,
                        allColumns, MySQLiteHelper.COLUMN_ISBN13 + " = '"+ISBN+"'"
                       , null, null, null, null);
            } else {
                cursor = database.query(MySQLiteHelper.TABLE_BOOKS,
                        allColumns, MySQLiteHelper.COLUMN_ISBN10 + " = '"+ISBN+"'"
                        , null, null, null, null);
            }
            cursor.moveToFirst();
            while(!cursor.isAfterLast() && !trouver){
                trouver = true;
            }
        }
        return trouver;
    }

    public void deleteBook(Book book, boolean deleteImage) {
        String isbn13 = book.getIsbn13();
        String isbn10 = book.getIsbn10();
        if(deleteImage && book.getPathCover().contains("booksCover/"))
            deleteImage(book.getPathCover());
        if(!isbn13.equals("")) {
            System.out.println("Book deleted with isbn13: " + isbn13);
            database.delete(MySQLiteHelper.TABLE_BOOKS, MySQLiteHelper.COLUMN_ISBN13
                    + " = '"+isbn13+"'", null);
        } else if (!isbn10.equals("")) {
            System.out.println("Book deleted with isbn10: " + isbn10);
            database.delete(MySQLiteHelper.TABLE_BOOKS, MySQLiteHelper.COLUMN_ISBN10
                    + " = '"+isbn10+"'", null);
        }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<Book>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS,
                allColumns, null, null, null, null, MySQLiteHelper.COLUMN_TITLE+" asc");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Book book = cursorToBook(cursor);
            books.add(book);
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        return books;
    }

    private boolean deleteImage(String uri){
        File file = new File(uri);
        boolean deleted = false;
        if(file.exists()){
            deleted = file.delete();
        }
        return deleted;
    }

    private Book cursorToBook(Cursor cursor) {
        Book book = new Book();
        book.setBookName(cursor.getString(1));
        book.setPublishers(cursor.getString(2));
        book.setAuthors(cursor.getString(3));
        book.setPublishDate(cursor.getString(4));
        book.setIsbn13(cursor.getString(5));
        book.setIsbn10(cursor.getString(6));
        book.setPathCover(cursor.getString(7));
        return book;
    }

}
