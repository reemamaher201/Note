package com.reema.reemanote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NoteAdapter.ItemClickListener, NoteAdapter.ItemClickListener2 {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Note> items;
    NoteAdapter adapter;
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    RecyclerView rv;
    ImageView delete;
    EditText updateTitle;
    EditText updateContent;
    Button addd;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateTitle = findViewById(R.id.update_title);
        updateContent = findViewById(R.id.update_content);
        rv = findViewById(R.id.rvRest);
        items = new ArrayList<Note>();
        adapter = new NoteAdapter(this, items, this, this);
        delete = findViewById(R.id.delete);
        addd = findViewById(R.id.addd);
        addd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });
        GetAllNotes();
    }


    private void GetAllNotes() {

        db.collection("Notes").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d("tag", "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                if (documentSnapshot.exists()) {
                                    String id = documentSnapshot.getId();
                                    String title = documentSnapshot.getString("Title");
                                    String content = documentSnapshot.getString("Content");
                                    Note note = new Note(id, title,content);
                                    items.add(note);
                                    rv.setLayoutManager(layoutManager);
                                    rv.setHasFixedSize(true);
                                    rv.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    Log.e("LogDATA", items.toString());
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("LogDATA", "get failed with ");


                    }
                });
    }


    public void Delete(final Note note) {
        db.collection("Notes").document(note.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e("tag", "deleted");
                        items.remove(note);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("tag", "fail");
                    }
                });
    }


    public void updateNote(final Note note) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");
        builder.setTitle("Content");
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog, null);
        builder.setView(customLayout);
        builder.setPositiveButton(
                "Update",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        updateTitle = customLayout.findViewById(R.id.update_title);

                        db.collection("Notes").document(note.getId()).update("Title", updateTitle.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("tag", "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("tag", "Error updating document", e);
                                    }
                                });
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onItemClick(int position, String id) {

        Delete(items.get(position));
    }

    @Override
    public void onItemClick2(int position, String id) {

        updateNote(items.get(position));

    }
}