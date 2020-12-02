package com.donsmart.mytodolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class NotesHandler extends DatabaseHelper{
    public NotesHandler(Context context) {
        super(context);
    }

    public boolean createNote(Note note){

        ContentValues cv = new ContentValues();
        cv.put("title",note.getTitle());
        cv.put("description", note.getDescription());

        SQLiteDatabase db = this.getWritableDatabase();
        boolean isSuccessful = db.insert("NoteTable", null, cv) > 0;
        db.close();
        return isSuccessful;
    }

    public ArrayList<Note> readNotes(){
        ArrayList<Note> notes = new ArrayList<>();

        String sqlQuery = "SELECT * FROM NoteTable ORDER BY id ASC";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(sqlQuery, null);


        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String description = cursor.getString(cursor.getColumnIndex("description"));

            Note note = new Note(title, description);
            note.setNoteId(id);
            notes.add(note);
        }

        cursor.close();
        db.close();
        return notes;
    }

    public Note readSingleNote(int id){

        Note note = null;
        String sqlQuery = "SELECT * FROM NoteTable WHERE id=" + id;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(sqlQuery,null);

        if (cursor.moveToFirst()){
            int noteID = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String description = cursor.getString(cursor.getColumnIndex("description"));

            note = new Note(title, description);
            note.setNoteId(noteID);
        }
        cursor.close();
        db.close();

        return note;
    }

    public boolean updateNote(Note note){

        ContentValues cv = new ContentValues();
        cv.put("title", note.getTitle());
        cv.put("description", note.getDescription());

        SQLiteDatabase db = this.getWritableDatabase();

        boolean isSuccessful = db.update("NoteTable", cv, "id="+ note.getNoteId(),null)>0;
        db.close();
        return isSuccessful;
    }

    public boolean deleteNote(int id){
        boolean isDeleted;
        SQLiteDatabase db = this.getWritableDatabase();
        isDeleted = db.delete("NoteTable", "id=" + id, null) > 0;
        db.close();
        return isDeleted;
    }

}
