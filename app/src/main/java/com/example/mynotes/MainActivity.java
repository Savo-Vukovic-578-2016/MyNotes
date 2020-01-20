package com.example.mynotes;

import static com.example.mynotes.model.Constants.CONTENT;
import static com.example.mynotes.model.Constants.ID;
import static com.example.mynotes.model.Constants.PRIORITY;
import static com.example.mynotes.model.Constants.TAG;

import java.util.List;

import com.example.mynotes.dao.IMyNotesDbHelper;
import com.example.mynotes.dao.MyNotesDbHelper;
import com.example.mynotes.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	Toolbar toolbar = findViewById(R.id.toolbar);
	setSupportActionBar(toolbar);

	FloatingActionButton fab = findViewById(R.id.fab);

	// if the action button is clicked move on to the add note activity
	fab.setOnClickListener(view -> {
	    Intent intent = new Intent(this, AddNoteActivity.class);
	    startActivity(intent);
	});

	GridView gridView = findViewById(R.id.grid);
	List<Note> notes = getNotes();
	GridAdapter adapter = new GridAdapter(this, notes);
	gridView.setAdapter(adapter);

	gridView.setOnItemClickListener((parent, view, position, id) -> {
	    Intent intent = new Intent(this, AddNoteActivity.class);

	    // put extra information about the clicked item
	    Note clickedNote = notes.get(position);
	    intent.putExtra(CONTENT, clickedNote.getContent());
	    intent.putExtra(TAG, clickedNote.getTag());
	    intent.putExtra(PRIORITY, clickedNote.getPriority().name());
	    intent.putExtra(ID, clickedNote.getId());

	    startActivity(intent);
	});

    }

    @Override
    public void onRestart() {
	finish();
	startActivity(getIntent());
	super.onRestart();
    }

    class GridAdapter extends BaseAdapter {

	private String _property;
	private String _value;
	private final List<Note> _notes;
	private Context _context;

	public GridAdapter(Context context, String property, String value) {
	    _context = context;
	    _property = property;
	    _value = value;
	    _notes = null;
	}

	public GridAdapter(Context context, List<Note> notes) {
	    _context = context;
	    _notes = notes;
	}

	@Override
	public int getCount() {
	    return getNotes().size();
	}

	@Override
	public Object getItem(int position) {
	    return getNotes().get(position);
	}

	@Override
	public long getItemId(int position) {
	    return getNotes().get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

	    // get the note
	    Note note = getNotes().get(position);

	    // get the layout for the note
	    if (convertView == null) {
		final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		convertView = layoutInflater.inflate(R.layout.grid_item_layout, null);
	    }

	    // acquire the elements of the grid item layout
	    TextView tag = convertView.findViewById(R.id.contentTag);
	    TextView content = convertView.findViewById(R.id.content);

	    // customize the give note by priority
	    Drawable background;
	    Drawable header;
	    switch (note.getPriority()) {
	    case HIGH:
		background = ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_corner_red, null);
		header = ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_corner_red_header, null);
		break;
	    case MEDIUM:
		background = ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_corner_yellow, null);
		header = ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_corner_yellow_header, null);
		break;
	    default:
		background = ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_corner_green, null);
		header = ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_corner_green_header, null);
		break;
	    }
	    content.setBackground(background);
	    tag.setBackground(header);

	    // set the tag and the content text
	    String noteText = note.getContent().length() < 6 ? note.getContent() : note.getContent().substring(0, 6);
	    content.setText(String.format("%s ...", noteText));
	    tag.setText(note.getTag());

	    return convertView;
	}

	public String getProperty() {
	    return _property;
	}

	public void setProperty(String property) {
	    _property = property;
	}

	public String getValue() {
	    return _value;
	}

	public void setValue(String value) {
	    _value = value;
	}

	public List<Note> getNotes() {
	    return _notes;
	}

	private Context getContext() {
	    return _context;
	}
    }

    private List<Note> getNotes() {
	IMyNotesDbHelper dbHelper = new MyNotesDbHelper(this, null);
	return dbHelper.getAllNotes();
    }

}
