package com.reema.reemanote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {

    EditText editTextTitle;
    EditText editTextContent;
    Button buttonSave;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        editTextTitle = findViewById(R.id.et_title);
        buttonSave = findViewById(R.id.btn_save);
        editTextContent = findViewById(R.id.et_content);

    }


    public void saveToFirebase(View v) {

        String title = editTextTitle.getText().toString();
        String content = editTextContent.getText().toString();

        Map<String, Object> product = new HashMap<>();
        if (!title.isEmpty()) {
            product.put("Title", title);
            product.put("Content", content);
            db.collection("Notes")
                    .add(product)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                              @Override
                                              public void onSuccess(DocumentReference documentReference) {
                                                  openActivity();
                                                  Log.e("TAG", "Data added successfully to database");
                                              }
                                          }
                    )

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("TAG", "Failed to add database");

                        }
                    });

        } else {
            Toast.makeText(this, "Please Fill fields", Toast.LENGTH_SHORT).show();
        }

    }

    public void openActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}