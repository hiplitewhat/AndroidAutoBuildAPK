package com.lyric.checkpoint;
import android.inputmethodservice.InputMethodService;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.*;
import android.view.inputmethod.InputConnection;
import android.graphics.Color;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class LyricIME extends InputMethodService {
    private int curIdx = 0;

    @Override
    public View onCreateInputView() {
        SharedPreferences p = getSharedPreferences("lyric_data", MODE_PRIVATE);
        String raw = p.getString("raw", "");
        curIdx = p.getInt("ptr", 0);

        final ArrayList<String> lines = new ArrayList<>();
        final ArrayList<Integer> cpIndices = new ArrayList<>();

        String[] items = raw.split(Pattern.quote("~~~"));
        for(int i=0; i<items.length; i++){
            String[] parts = items[i].split(":::");
            if(parts.length == 2){
                lines.add(parts[0]);
                if(parts[1].equals("Y")) cpIndices.add(i);
            }
        }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#1A1A1A"));

        if(!cpIndices.isEmpty()) {
            HorizontalScrollView hsv = new HorizontalScrollView(this);
            LinearLayout cpBar = new LinearLayout(this);
            for(final int idx : cpIndices) {
                Button cpBtn = new Button(this);
                cpBtn.setText("JUMP: " + lines.get(idx));
                cpBtn.setTextSize(9);
                cpBtn.setOnClickListener(v -> {
                    curIdx = idx;
                    p.edit().putInt("ptr", curIdx).apply();
                    // We need a way to refresh UI, for now users can just press next
                });
                cpBar.addView(cpBtn);
            }
            hsv.addView(cpBar);
            root.addView(hsv);
        }

        if(!lines.isEmpty()){
            if(curIdx >= lines.size()) curIdx = 0;
            final Button next = new Button(this);
            next.setText("SEND: " + lines.get(curIdx));
            next.setHeight(220);
            next.setBackgroundColor(Color.parseColor("#10B981"));
            next.setTextColor(Color.WHITE);
            next.setOnClickListener(v -> {
                InputConnection ic = getCurrentInputConnection();
                if(ic != null){
                    ic.commitText(lines.get(curIdx) + " ", 1);
                    curIdx++;
                    if(curIdx >= lines.size()) curIdx = 0;
                    p.edit().putInt("ptr", curIdx).apply();
                    next.setText("SEND: " + lines.get(curIdx));
                }
            });
            root.addView(next);
        }

        return root;
    }
}