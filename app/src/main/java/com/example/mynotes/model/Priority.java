package com.example.mynotes.model;

import java.util.Arrays;
import java.util.Optional;

import android.graphics.Color;

/**
 * Presents the priority of a given note.
 * 
 * @author SavoVukoic
 * 
 * @since 1.0
 *
 */
public enum Priority {

    LOW(Color.GREEN), MEDIUM(Color.YELLOW), HIGH(Color.RED);

    private int _color;

    Priority(int color) {
	_color = color;
    }

    public int getColor() {
	return _color;
    }

    public static Optional<Priority> valueOf(Integer value) {
	if (value == null) {
	    return Optional.empty();
	}
	return Arrays.stream(values()).filter(priority -> priority._color == value).findFirst();
    }

    public static String[] getNames(Class<? extends Enum<?>> e) {
	return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }

}
