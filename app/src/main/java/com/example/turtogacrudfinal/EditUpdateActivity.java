package com.example.turtogacrudfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EditUpdateActivity extends AppCompatActivity {

    EditText bookTitleEdit, bookGenretitleEdit, bookPricetitleEdit, bookStocktitleEdit;
    Button updateButton;
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    ImageView addPhotoIV;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_update);

        bookGenretitleEdit = findViewById(R.id.bookGenretitleEdit);
        bookTitleEdit = findViewById(R.id.bookTitleEdit);
        bookPricetitleEdit = findViewById(R.id.bookPricetitleEdit);
        bookStocktitleEdit = findViewById(R.id.bookStocktitleEdit);
        updateButton = findViewById(R.id.updateButton);
        addPhotoIV = findViewById(R.id.addPhotoIV);

        if (getIntent().getExtras() != null) {
            addPhotoIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, 500);
                }
            });

            Book book = (Book) getIntent().getExtras().getParcelable("Book");
            bookTitleEdit.setText(book.getBookTitle());
            bookGenretitleEdit.setText(book.getBookGenre());
            bookPricetitleEdit.setText(Double.toString(book.getBookPrice()));
            bookStocktitleEdit.setText(Double.toString(book.getBookStock()));
            Glide.with(this).load(book.getBookImage()).into(addPhotoIV);

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
try{
    book.setBookTitle(bookTitleEdit.getText().toString());
    book.setBookGenre(bookGenretitleEdit.getText().toString());
    book.setBookPrice(Double.parseDouble(bookPricetitleEdit.getText().toString()));
    book.setBookStock(Double.parseDouble(bookStocktitleEdit.getText().toString()));

}catch (Exception e){
    AlertDialog.Builder alert = new AlertDialog.Builder(EditUpdateActivity.this);
    alert.setCancelable(false);
    alert.setTitle("Error!");
    alert.setMessage("Please input correct value.");
    alert.setPositiveButton("Okay", null);
    alert.show();
}

                    if (!book.getBookTitle().isEmpty() && !book.getBookGenre().isEmpty() && !book.getBookImage().toString().isEmpty()
                            && book.getBookPrice() >= 0 && book.getBookStock() >= 0){
                        ProgressDialog progressDialog = new ProgressDialog(EditUpdateActivity.this);
                        progressDialog.setMessage("Uploalding, Please wait...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference().child("Book").child(FirebaseAuth.getInstance().getCurrentUser().getUid() + imageUri.getLastPathSegment());

                        UploadTask uploadTask = storageRef.putFile(imageUri);
                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return storageRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    book.setBookImage(downloadUri.toString());
                                    db.collection("Book").document(book.getBookID()).set(book).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(EditUpdateActivity.this, "Book successfully added!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                                            }
                                            else
                                            {
                                                AlertDialog.Builder alert = new AlertDialog.Builder(EditUpdateActivity.this);
                                                alert.setCancelable(false);
                                                alert.setTitle("Error!");
                                                alert.setMessage(task.getException().getLocalizedMessage());
                                                alert.setPositiveButton("Okay", null);
                                                alert.show();
                                            }
                                        }
                                    });
                                } else {
                                    // Handle failures
                                    progressDialog.dismiss();
                                    AlertDialog.Builder alert = new AlertDialog.Builder(EditUpdateActivity.this);
                                    alert.setCancelable(false);
                                    alert.setTitle("Error!");
                                    alert.setMessage(task.getException().getLocalizedMessage());
                                    alert.setPositiveButton("Okay", null);
                                    alert.show();
                                    // ...
                                }
                            }
                        });
                    }
                    else{
                        AlertDialog.Builder alert = new AlertDialog.Builder(EditUpdateActivity.this);
                        alert.setCancelable(false);
                        alert.setTitle("Error!");
                        alert.setMessage("Please populate all fields.");
                        alert.setPositiveButton("Okay", null);
                        alert.show();
                    }


                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 500:
                if(resultCode == RESULT_OK){
                    try {
                        imageUri = data.getData();
                        final InputStream imageStream = getApplicationContext().getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        addPhotoIV.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }
}