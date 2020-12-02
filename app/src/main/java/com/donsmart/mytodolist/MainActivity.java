package com.donsmart.mytodolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageButton imageButton;
    ArrayList<Note> notes;
    NotesAdapter adapter;
    ImageView ivEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButton = findViewById(R.id.imageButton);
        recyclerView = findViewById(R.id.recyclerview);
        ivEdit = findViewById(R.id.ivEdit);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.note_input, null, false);

                EditText editTitle = view.findViewById(R.id.editTitle);
                EditText editDescription = view.findViewById(R.id.editDescription);


                new AlertDialog.Builder(MainActivity.this)
                        .setView(view)
                        .setTitle("Add note")
                        .setCancelable(true)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String title = editTitle.getText().toString();
                                String description = editDescription.getText().toString();
                                Note note = new Note(title, description);
                               boolean isInserted =  new NotesHandler(MainActivity.this).createNote(note);

                               if (isInserted){
                                   Toast.makeText(MainActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                                   loadNotes();
                               }else {
                                   Toast.makeText(MainActivity.this, "Unable to save note", Toast.LENGTH_SHORT).show();
                               }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();

            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                new NotesHandler(MainActivity.this).deleteNote(notes.get(viewHolder.getAdapterPosition()).getNoteId());
                notes.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        loadNotes();


    }

    public ArrayList<Note> readNotes(){
       ArrayList<Note> notes = new NotesHandler(this).readNotes();
       return notes;
    }

    public void loadNotes(){
        notes = readNotes();

        adapter = new NotesAdapter(notes, MainActivity.this, new NotesAdapter.ItemClicked() {
            @Override
            public void onItemClicked(int position, View view) {
                editNote(notes.get(position).getNoteId(), view);
            }
        });
        recyclerView.setAdapter(adapter);

    }

    private void editNote(int id, View view){
        NotesHandler notesHandler = new NotesHandler(this);

        Note note = notesHandler.readSingleNote(id);

        Intent intent = new Intent(MainActivity.this, EditNote.class);
        intent.putExtra("title", note.getTitle());
        intent.putExtra("description", note.getDescription());
        intent.putExtra("id", note.getNoteId());
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, ViewCompat.getTransitionName(view));
        startActivityForResult(intent, 1, optionsCompat.toBundle());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1){
            loadNotes();
        }
    }
}