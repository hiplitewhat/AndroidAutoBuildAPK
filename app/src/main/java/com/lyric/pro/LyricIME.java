package com.lyric.pro;
import android.inputmethodservice.InputMethodService;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.*;
import android.view.inputmethod.InputConnection;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class LyricIME extends InputMethodService {
    private int ptr = 0;
    private Button main;
    private ArrayList<String> lines = new ArrayList<>();
    private SharedPreferences p;

    @Override
    public View onCreateInputView() {
        p = getSharedPreferences("lyrics", MODE_PRIVATE);
        ptr = p.getInt("ptr", 0);
        lines.clear();
        String raw = p.getString("data", "");
        if (raw.isEmpty()) return createErrorView("No Data");
        try {
            String[] items = raw.split(Pattern.quote("~~~"));
            for (String item : items) {
                String[] parts = item.split(":::");
                if (parts.length >= 1) lines.add(parts[0]);
            }
        } catch (Exception e) {}

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#111111"));
        main = new Button(this);
        main.setLayoutParams(new LinearLayout.LayoutParams(-1, 250));
        main.setBackgroundColor(Color.parseColor("#10B981"));
        main.setTextColor(Color.WHITE);
        updateText();

        main.setOnClickListener(v -> {
            InputConnection ic = getCurrentInputConnection();
            if (ic != null && !lines.isEmpty()) {
                if (ptr >= lines.size()) ptr = 0;
                ic.commitText(lines.get(ptr) + " ", 1);
                ptr++;
                if (ptr >= lines.size()) ptr = 0;
                p.edit().putInt("ptr", ptr).apply();
                updateText();
            }
        });
        root.addView(main);
        return root;
    }

    private void updateText() {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (main != null && !lines.isEmpty()) main.setText(lines.get(Math.min(ptr, lines.size()-1)));
        });
    }

    private View createErrorView(String m) {
        TextView t = new TextView(this); t.setText(m); t.setTextColor(Color.RED); return t;
    }
}