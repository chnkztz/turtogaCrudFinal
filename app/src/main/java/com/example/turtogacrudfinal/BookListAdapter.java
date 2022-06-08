package com.example.turtogacrudfinal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BookListAdapter extends BaseAdapter {
    List<Book> bookList;
    Context context;

    public BookListAdapter(List<Book> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.book_list_item, null);
        TextView bookTitleTextView, bookGenreTextView, bookPriceTextView, bookStockTextView;
        ImageButton bookImageButton, editIB, deleteIB;

        bookImageButton = view.findViewById(R.id.bookImageButton);
        bookTitleTextView = view.findViewById(R.id.bookTitleTextView);
        bookGenreTextView = view.findViewById(R.id.bookGenreTextView);
        bookPriceTextView = view.findViewById(R.id.bookPriceTextView);
        bookStockTextView = view.findViewById(R.id.bookStockTextView);
        editIB = view.findViewById(R.id.editIB);
        deleteIB = view.findViewById(R.id.deleteIB);

        bookTitleTextView.setText("Title: " + bookList.get(i).getBookTitle());
        bookGenreTextView.setText("Genre: " + bookList.get(i).getBookGenre());
        bookPriceTextView.setText("Price: " + bookList.get(i).getBookPrice());
        bookStockTextView.setText("Stock: " + bookList.get(i).getBookStock());
        Glide.with(context).load(bookList.get(i).getBookImage()).into(bookImageButton);

        editIB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditUpdateActivity.class);
                intent.putExtra("Book", (Parcelable) bookList.get(i));
                context.startActivity(intent);
            }
        });

        deleteIB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setCancelable(false);
                alert.setTitle("Delete book record");
                alert.setMessage("Are you sure to delete this book record?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int o) {

                        db.collection("Book").document(bookList.get(i).getBookID())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        bookList.remove(i);
                                        notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                alert.setNegativeButton("No", null);
                alert.show();
            }
        });



        return view;
    }
}
