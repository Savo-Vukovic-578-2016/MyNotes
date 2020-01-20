package com.example.mynotes.model;

/**
 * Class representation of the actual note that's going to be saved/pulled from
 * the database.
 *
 *
 * @author SavoVukovic
 * 
 * @since 1.0
 */
public class Note {

    private int _id;

    private String _content;

    private String _tag;

    private Priority _priority;

    private Note(Builder builder) {
	_id = builder._id;
	_content = builder._content;
	_tag = builder._tag;
	_priority = builder._priority;
    }

    public static class Builder {
	private int _id;
	private String _content;
	private String _tag;
	private Priority _priority;

	public Builder withId(int id) {
	    _id = id;
	    return this;
	}

	public Builder withContent(String content) {
	    _content = content;
	    return this;
	}

	public Builder withTag(String tag) {
	    _tag = tag;
	    return this;
	}

	public Builder withPriority(Priority priority) {
	    _priority = priority;
	    return this;
	}

	public Note build() {
	    return new Note(this);
	}

    }

    public int getId() {
	return _id;
    }

    public String getContent() {
	return _content;
    }

    public String getTag() {
	return _tag;
    }

    public Priority getPriority() {
	return _priority;
    }
}
