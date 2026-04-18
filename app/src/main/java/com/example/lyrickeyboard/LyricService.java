package com.example.lyrickeyboard;
import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.widget.*;
import android.view.inputmethod.InputConnection;
import android.graphics.Color;

public class LyricService extends InputMethodService {
    @Override
    public View onCreateInputView() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.parseColor("#1e293b"));
        layout.setPadding(20, 20, 20, 20);

        String[][] data = {
            {"Chorus", "Never gonna give you up"},{"Bridge", "I just wanna tell you how I'm feeling"}
        };

        for (final String[] item : data) {
            Button btn = new Button(this);
            btn.setText(item[0]);
            btn.setOnClickListener(v -> {
                InputConnection ic = getCurrentInputConnection();
                if (ic != null) ic.commitText(item[1] + " ", 1);
            });
            layout.addView(btn);
        }
        scroll.addView(layout);
        return scroll;
    }
}