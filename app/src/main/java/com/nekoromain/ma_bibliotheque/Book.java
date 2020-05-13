package com.nekoromain.ma_bibliotheque;

import android.os.Parcel;
import android.os.Parcelable;


//Object Parcelable : transférable entre 2 Activity (plus rapide que rendre l'objet Serializable)
public class Book implements Parcelable, Comparable{

    private String isbn13;
    private String isbn10;
    private String bookName;
    private String publishers;
    private String publishDate;
    private String authors;
    private String pathCover;


    public Book() {
        super();
        bookName = "";
        publishers = "";
        publishDate = "";
        authors = "";
        pathCover = "";
        isbn13 = "";
        isbn10 = "";
    }

    public Book(String isbn13, String isbn10, String bookName, String publishers,
                 String publishDate, String authors, String pathCover) {
        super();
        this.isbn13 = isbn13;
        this.isbn10 = isbn10;
        this.bookName = bookName;
        this.publishers = publishers;
        this.publishDate = publishDate;
        this.authors = authors;
        this.pathCover = pathCover;
    }

    public Book(Book book) {
        super();
        this.isbn13 = book.getIsbn13();
        this.isbn10 = book.getIsbn10();
        this.bookName = book.getBookName();
        this.publishers = book.getPublishers();
        this.publishDate = book.getPublishDate();
        this.authors = book.getAuthors();
        this.pathCover = book.getPathCover();
    }


    //write object values to parcel for storage
    @Override
    public void writeToParcel(Parcel dest, int flags){
        //write all properties to the parcel

        dest.writeString(isbn13);
        dest.writeString(isbn10);
        dest.writeString(bookName);
        dest.writeString(publishers);
        dest.writeString(publishDate);
        dest.writeString(authors);
        dest.writeString(pathCover);
    }

    //constructor used for parcel
    protected Book(Parcel parcel){
        //read and set saved values from parcel
        isbn13 = parcel.readString();
        isbn10 = parcel.readString();
        bookName = parcel.readString();
        //publishers = parcel.readArrayList(String.class.getClassLoader());
        publishers = parcel.readString();
        publishDate = parcel.readString();
        //authors = parcel.readArrayList(String.class.getClassLoader());
        authors = parcel.readString();
        pathCover = parcel.readString();
    }

    //creator - used when un-parceling our parcle (creating the object)
    public static final Parcelable.Creator<Book> CREATOR =
            new Parcelable.Creator<Book>(){

        @Override
        public Book createFromParcel(Parcel parcel) {
            return new Book(parcel);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[0];
        }
    };

    //return hashcode of object
    public int describeContents() {
        return hashCode();
    }

    //GETTERS AND SETTERS
    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getPublishers() {
        return publishers;
    }

    public void setPublishers(String publishers) {
        this.publishers = publishers;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPathCover() {
        return pathCover;
    }

    public void setPathCover(String pathCover) {
        this.pathCover = pathCover;
    }

    @Override
    public String toString(){
        return "Titre: " + bookName
                + "\nEditeur: " + publishers.toString()
                +"\nAuteur: " + authors.toString()
                +"\nDate de publication: " +  publishDate
                +"\nISBN 13: " + isbn13
                + "\nISBN 10: " + isbn10
                ;
    }

    @Override
    public boolean equals(Object o){
        if(this == o)
            return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Book book = (Book)o;
        if(!this.bookName.equals(book.getBookName()))
            return false;
        if(!this.publishers.equals(book.getPublishers()))
            return false;
        if(!this.authors.equals(book.getAuthors()))
            return false;
        if(!this.publishDate.equals(book.getPublishDate()))
            return false;
        if(!this.isbn13.equals(book.getIsbn13()))
            return false;
        if(!this.isbn10.equals(book.getIsbn10()))
            return false;
        if(!this.pathCover.equals(book.getPathCover()))
            return false;
        return true;
    }


    @Override
    public int compareTo(Object o) {
        Book b = (Book)o;
        return (b.getBookName().compareTo(((Book) o).getBookName()));
    }
}
