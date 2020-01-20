package com.example.mynotes.dao;

import static com.example.mynotes.model.Constants.CONTENT;
import static com.example.mynotes.model.Constants.PRIORITY;
import static com.example.mynotes.model.Constants.TAG;

import java.util.ArrayList;
import java.util.List;

import com.example.mynotes.model.Note;
import com.example.mynotes.model.Priority;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

/**
 * Concrete implementation of the {@link IMyNotesDbHelper} interface, presents a
 * class that's use to interact with the database connected to the
 * <quote>MyNotes</quote> application.
 * 
 * @since 1.0
 * 
 * @author SavoVukovic
 * 
 */
public class MyNotesDbHelper extends SQLiteOpenHelper implements IMyNotesDbHelper {

    private final static String CREATE_TABLE_NOTES = "CREATE TABLE IF NOT EXISTS MN_NOTES(NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, CONTENT VARCHAR(255) NOT NULL, TAG VARCHAR(255) NOT NULL, PRIORITY INT)";
    private final static String DATABASE_NAME = "my_notes";
    private final static String TABLE_NOTES = "MN_NOTES";

    private final static int DATABASE_VERSION = 1;

    public MyNotesDbHelper(@Nullable Context context, SQLiteDatabase.CursorFactory factory) {
	super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    /**
     * Installing process, create a database that goes along with the application.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
	db.execSQL(CREATE_TABLE_NOTES);
    }

    /**
     * Updating process, update the application by recreating the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
	onCreate(db);
    }

    /**
     * Get all available notes in the application
     *
     * @return {@link List<Note>}.
     */
    @Override
    public List<Note> getAllNotes() {
	SQLiteDatabase db = this.getReadableDatabase();
	Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES + " ORDER BY TAG ASC", null);

	List<Note> result = new ArrayList<>();
	if (cursor.moveToFirst()) {
	    do {
		// initiate the note builder
		Note note = new Note.Builder().withContent(cursor.getString(1))
			.withId(Integer.valueOf(cursor.getString(0)))
			.withPriority(Priority.valueOf(cursor.getInt(3)).orElse(Priority.LOW))
			.withTag(cursor.getString(2)).build();
		// add all the notes to the result list
		result.add(note);
	    } while (cursor.moveToNext());
	}
	return result;
    }

    /**
     * Get all available notes by their {@link Priority}
     *
     * @param priority,
     *            priority that's used to search for the given notes.
     * 
     * @return {@link List<Note>} with the given <code>Priority</code>
     * 
     */
    @Override
    public List<Note> getNotesByPriority(Priority priority) {
	return null;
    }

    /**
     * Create a note as same as the supplied parameter.
     * 
     * @param note,
     *            {@link Note} that needs to be created.
     * 
     * @return true if the note is successfully created.
     * 
     */
    @Override
    public boolean createNote(Note note) {
	SQLiteDatabase db = this.getWritableDatabase();
	ContentValues contentValues = new ContentValues();
	contentValues.put(CONTENT, note.getContent());
	contentValues.put(TAG, note.getTag());
	contentValues.put(PRIORITY, note.getPriority().getColor());

	long result = db.insertOrThrow(TABLE_NOTES, null, contentValues);
	return result != -1;
    }

    /**
     * Update a note with the parameters supplied from the given input.
     * 
     * @param note,
     *            note parameters that need to be updated.
     * 
     * @return true if the targeted note has been successfully updated.
     * 
     */
    @Override
    public boolean updateNote(Note note) {
	SQLiteDatabase db = this.getWritableDatabase();
	ContentValues contentValues = new ContentValues();
	contentValues.put(CONTENT, note.getContent());
	contentValues.put(TAG, note.getTag());
	contentValues.put(PRIORITY, note.getPriority().getColor());

	long result = db.update(TABLE_NOTES, contentValues, "NOTE_ID=?", new String[] { String.valueOf(note.getId()) });

	return result != -1;
    }

    /**
     * Delete a note with the supplied identifier.
     * 
     * @param noteId,
     *            unique identifier of every {@link Note}.
     * 
     * @return true if a note has been deleted.
     */
    @Override
    public boolean deleteNote(int noteId) {
	SQLiteDatabase db = this.getWritableDatabase();
	return db.delete(TABLE_NOTES, "NOTE_ID=?", new String[] { String.valueOf(noteId) }) != -1;
    }
}
