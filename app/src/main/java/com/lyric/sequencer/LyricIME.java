package com.lyric.sequencer;
import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.widget.*;
import android.view.inputmethod.InputConnection;
import android.graphics.Color;

public class LyricIME extends InputMethodService {
    @Override
    public View onCreateInputView() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        root.setPadding(20, 20, 20, 20);

        String[][] segments = {
            {"Greeting", "Hello, this is my lyric keyboard!"},{"Chorus", "Look at the stars, look how they shine for you..."}
        };

        for (final String[] s : segments) {
            Button b = new Button(this);
            b.setText(s[0]);
            b.setAllCaps(false);
            b.setOnClickListener(v -> {
                InputConnection ic = getCurrentInputConnection();
                if (ic != null) ic.commitText(s[1] + " ", 1);
            });
            root.addView(b);
        }
        scroll.addView(root);
        return scroll;
    }
}