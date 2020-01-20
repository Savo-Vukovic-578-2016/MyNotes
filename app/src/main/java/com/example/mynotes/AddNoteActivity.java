package com.example.mynotes;

import static com.example.mynotes.model.Constants.CONTENT;
import static com.example.mynotes.model.Constants.ID;
import static com.example.mynotes.model.Constants.PRIORITY;
import static com.example.mynotes.model.Constants.TAG;

import com.example.mynotes.dao.MyNotesDbHelper;
import com.example.mynotes.model.Note;
import com.example.mynotes.model.Priority;
import com.google.android.material.snackbar.Snackbar;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class AddNoteActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.add_note_menu, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

	EditText multiInput = findViewById(R.id.inputContent);
	EditText tag = findViewById(R.id.editTextTag);

	String inputText = multiInput.getText().toString();
	if (inputText.isEmpty()) {
	    return true;
	}

	Intent intent = new Intent(Intent.ACTION_SEND);
	intent.setType("text/plain");
	intent.putExtra(Intent.EXTRA_SUBJECT, tag.getText().toString());
	intent.putExtra(Intent.EXTRA_TEXT, inputText);
	startActivity(Intent.createChooser(intent, "Share your MyNote"));

	return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_add_note);

	// set color of the toolbar
	getSupportActionBar()
		.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryMyNotes, null)));

	Spinner spinner = findViewById(R.id.spinner);
	ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
		Priority.getNames(Priority.class));
	spinner.setAdapter(adapter);

	EditText tag = findViewById(R.id.editTextTag);
	EditText multiInput = findViewById(R.id.inputContent);

	Button saveButton = findViewById(R.id.buttonAdd);
	Button updateButton = findViewById(R.id.buttonUpdate);
	Button deleteButton = findViewById(R.id.buttonDelete);
	TextView textView = findViewById(R.id.activePriority);
	textView.setVisibility(View.INVISIBLE);

	// only when the add button is clicked
	flipButtonVisibility(updateButton);
	flipButtonVisibility(deleteButton);

	// optional parameters, only passed when an item is clicked
	String passedContent = getIntent().getStringExtra(CONTENT);
	String passedTag = getIntent().getStringExtra(TAG);
	Priority passedPriority = null;
	if (passedContent != null) {
	    passedPriority = Priority.valueOf(getIntent().getStringExtra(PRIORITY));
	}

	// when an item is clicked
	if (validatePassedInputs(passedContent, passedTag, passedPriority)) {
	    flipButtonVisibility(updateButton);
	    flipButtonVisibility(deleteButton);
	    flipButtonVisibility(saveButton);

	    spinner.setSelection(passedPriority.ordinal());
	    textView.setVisibility(View.VISIBLE);
	    textView.setBackground(getDrawableByPriority(passedPriority));

	    multiInput.setText(passedContent);
	    tag.setText(passedTag);
	}

	// when the save button is clicked
	saveButton.setOnClickListener(view -> {
	    if (multiInput.getText().toString().isEmpty()) {
		Snackbar.make(view, "Note cannot be empty!", Snackbar.LENGTH_SHORT).show();
		return;
	    }

	    MyNotesDbHelper dbHelper = new MyNotesDbHelper(this, null);

	    String inputTag = tag.getText().toString().isEmpty() ? "###" : tag.getText().toString();
	    if (validateInputTag(tag.getText().toString())) {
		Snackbar.make(view, "Maximum length of a note tag is 10!", Snackbar.LENGTH_SHORT).show();
		return;
	    }

	    String priority = spinner.getSelectedItem().toString();

	    Note note = new Note.Builder().withTag(inputTag).withContent(multiInput.getText().toString())
		    .withPriority(Priority.valueOf(priority)).build();

	    boolean result = dbHelper.createNote(note);
	    if (!result) {
		Snackbar.make(view, "Note wasn't created successfully!", Snackbar.LENGTH_SHORT).show();
	    } else {
		// Note was added successfully, go back to the main window
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	    }
	});

	updateButton.setOnClickListener(view -> {
	    if (multiInput.getText().toString().isEmpty()) {
		Snackbar.make(view, "Note cannot be empty!", Snackbar.LENGTH_SHORT).show();
		return;
	    }

	    MyNotesDbHelper dbHelper = new MyNotesDbHelper(this, null);
	    String inputTag = tag.getText().toString().isEmpty() ? "###" : tag.getText().toString();
	    if (validateInputTag(tag.getText().toString())) {
		Snackbar.make(view, "Maximum length of a note tag is 10!", Snackbar.LENGTH_SHORT).show();
		return;
	    }

	    String priority = spinner.getSelectedItem().toString();
	    int noteId = getIntent().getIntExtra(ID, -1);
	    if (noteId == -1) {
		return;
	    }
	    Note note = new Note.Builder().withTag(inputTag).withContent(multiInput.getText().toString())
		    .withPriority(Priority.valueOf(priority)).withId(noteId).build();

	    boolean result = dbHelper.updateNote(note);
	    if (!result) {
		Snackbar.make(view, "Note wasn't created successfully!", Snackbar.LENGTH_SHORT).show();
	    } else {
		// Note was updated successfully, go back to the main window
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	    }
	});

	deleteButton.setOnClickListener(view -> {

	    MyNotesDbHelper dbHelper = new MyNotesDbHelper(this, null);
	    int noteId = getIntent().getIntExtra(ID, -1);
	    if (noteId == -1) {
		return;
	    }
	    boolean result = dbHelper.deleteNote(noteId);
	    if (!result) {
		Snackbar.make(view, "We were unable to delete your note, try again later!", Snackbar.LENGTH_SHORT)
			.show();
	    } else {
		// Note was updated successfully, go back to the main window
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	    }

	});

    }

    private boolean validatePassedInputs(String tag, String content, Priority priority) {
	if (tag == null || content == null || priority == null) {
	    return false;
	}
	return !tag.isEmpty() && !content.isEmpty();
    }

    private void flipButtonVisibility(Button button) {
	if (button.getVisibility() == View.VISIBLE) {
	    button.setVisibility(View.INVISIBLE);
	} else {
	    button.setVisibility(View.VISIBLE);
	}
    }

    private Drawable getDrawableByPriority(Priority priority) {
	Drawable result = null;
	switch (priority) {
	case LOW:
	    result = ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_corner_green, null);
	    break;
	case HIGH:
	    result = ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_corner_red, null);
	    break;
	case MEDIUM:
	    result = ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_corner_yellow, null);
	    break;
	}
	return result;
    }

    private boolean validateInputTag(String tag) {
	return tag.length() > 10;
    }
}
