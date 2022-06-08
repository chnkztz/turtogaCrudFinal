package com.example.turtogacrudfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    BookListAdapter listAdapter;
    List<Book> bookList;
    ListView bookListView;
    ImageButton addIB, logoutIB;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        bookList = new ArrayList<>();
        listAdapter = new BookListAdapter(bookList, this);
        bookListView = findViewById(R.id.bookListView);
        bookListView.setAdapter(listAdapter);
        getBooks();

        addIB = findViewById(R.id.addIB);
        logoutIB = findViewById(R.id.logoutButton);
        mAuth = FirebaseAuth.getInstance();

        logoutIB.setOnClickListener(view ->{
            mAuth.signOut();
            startActivity(new Intent(MenuActivity.this, LoginActivity.class));
            finish();
        });


        addIB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AddActivity.class));
            }
        });
    }

    private void getBooks() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Book")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                bookList.add(document.toObject(Book.class));
                            }
                        } else {

                        }
                        listAdapter.notifyDataSetChanged();
                    }
                });
    }
}