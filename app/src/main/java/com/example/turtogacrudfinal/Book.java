package com.example.turtogacrudfinal;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Book implements Serializable, Parcelable {

    String bookTitle;
    String bookGenre;
    String bookID;
    double bookPrice;
    double bookStock;
    String bookImage;

    public Book() {
    }

    public Book(String bookTitle, String bookGenre, String bookID, double bookPrice, double bookStock, String bookImage) {
        this.bookTitle = bookTitle;
        this.bookGenre = bookGenre;
        this.bookID = bookID;
        this.bookPrice = bookPrice;
        this.bookStock = bookStock;
        this.bookImage = bookImage;
    }

    protected Book(Parcel in) {
        bookTitle = in.readString();
        bookGenre = in.readString();
        bookID = in.readString();
        bookPrice = in.readDouble();
        bookStock = in.readDouble();
        bookImage = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookGenre() {
        return bookGenre;
    }

    public void setBookGenre(String bookGenre) {
        this.bookGenre = bookGenre;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public double getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(double bookPrice) {
        this.bookPrice = bookPrice;
    }

    public double getBookStock() {
        return bookStock;
    }

    public void setBookStock(double bookStock) {
        this.bookStock = bookStock;
    }

    public String getBookImage() {
        return bookImage;
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookTitle);
        dest.writeString(bookGenre);
        dest.writeString(bookID);
        dest.writeDouble(bookPrice);
        dest.writeDouble(bookStock);
        dest.writeString(bookImage);
    }
}
