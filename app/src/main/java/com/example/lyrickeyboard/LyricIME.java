package com.example.lyrickeyboard;
import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.widget.*;
import android.view.inputmethod.InputConnection;
import android.graphics.Color;
import android.view.Gravity;

public class LyricIME extends InputMethodService {
    @Override
    public View onCreateInputView() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        root.setPadding(20, 20, 20, 20);

        TextView head = new TextView(this);
        head.setText("Lyric Keyboard");
        head.setGravity(Gravity.CENTER);
        head.setPadding(0, 0, 0, 20);
        root.addView(head);

        String[][] data = {
            {"Chorus", "Hello World!"}
        };

        for (final String[] pair : data) {
            Button b = new Button(this);
            b.setText(pair[0]);
            b.setAllCaps(false);
            b.setOnClickListener(v -> {
                InputConnection ic = getCurrentInputConnection();
                if (ic != null) ic.commitText(pair[1] + " ", 1);
            });
            root.addView(b);
        }
        return root;
    }
}