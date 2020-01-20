package com.example.mynotes.dao;

import java.util.List;

import com.example.mynotes.model.Note;
import com.example.mynotes.model.Priority;

public interface IMyNotesDbHelper {

    List<Note> getAllNotes();

    List<Note> getNotesByPriority(Priority priority);

    boolean createNote(Note note);

    boolean updateNote(Note note);

    boolean deleteNote(int noteId);

}
