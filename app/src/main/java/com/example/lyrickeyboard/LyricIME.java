package com.example.lyrickeyboard;
import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.view.inputmethod.InputConnection;
import android.graphics.Color;

public class LyricIME extends InputMethodService {
    @Override
    public View onCreateInputView() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.parseColor("#4f46e5"));
        layout.setPadding(32, 32, 32, 32);

        String[][] data = {
            {"Chorus 1", "Never gonna give you up\nNever gonna let you down\nNever gonna run around and desert you"},
            {"Verse 1", "We're no strangers to love\nYou know the rules and so do I"}
        };

        for (final String[] item : data) {
            Button btn = new Button(this);
            btn.setText(item[0]);
            btn.setAllCaps(false);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 0, 16);
            btn.setLayoutParams(lp);
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