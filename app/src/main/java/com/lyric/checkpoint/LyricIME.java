package com.lyric.checkpoint;
import android.inputmethodservice.InputMethodService;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.*;
import android.view.inputmethod.InputConnection;
import android.graphics.Color;
import java.util.ArrayList;

public class LyricIME extends InputMethodService {
    private int curIdx = 0;

    @Override
    public View onCreateInputView() {
        SharedPreferences p = getSharedPreferences("cp_lyrics", MODE_PRIVATE);
        String raw = p.getString("data", "");
        curIdx = p.getInt("idx", 0);

        final ArrayList<String> lines = new ArrayList<>();
        final ArrayList<Integer> checkpoints = new ArrayList<>();

        String[] items = raw.split("\\\|\\\|");
        for(int i=0; i<items.length; i++){
            String[] parts = items[i].split(":::");
            if(parts.length == 2){
                lines.add(parts[0]);
                if(parts[1].equals("Y")) checkpoints.add(i);
            }
        }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#1A1A1A"));

        // Checkpoint Toolbar
        if(!checkpoints.isEmpty()) {
            HorizontalScrollView hsv = new HorizontalScrollView(this);
            LinearLayout cpBar = new LinearLayout(this);
            for(final int cpIdx : checkpoints) {
                Button cpBtn = new Button(this);
                cpBtn.setText("JUMP: " + lines.get(cpIdx));
                cpBtn.setTextSize(10);
                cpBtn.setOnClickListener(v -> {
                    curIdx = cpIdx;
                    p.edit().putInt("idx", curIdx).apply();
                    Toast.makeText(this, "Jumped to " + lines.get(cpIdx), Toast.LENGTH_SHORT).show();
                });
                cpBar.addView(cpBtn);
            }
            hsv.addView(cpBar);
            root.addView(hsv);
        }

        // Main Next Button
        if(!lines.isEmpty()){
            if(curIdx >= lines.size()) curIdx = 0;
            final Button next = new Button(this);
            next.setText("NEXT: " + lines.get(curIdx));
            next.setHeight(250);
            next.setBackgroundColor(Color.parseColor("#3B82F6"));
            next.setTextColor(Color.WHITE);
            next.setOnClickListener(v -> {
                InputConnection ic = getCurrentInputConnection();
                if(ic != null){
                    ic.commitText(lines.get(curIdx) + " ", 1);
                    curIdx++;
                    if(curIdx >= lines.size()) curIdx = 0;
                    p.edit().putInt("idx", curIdx).apply();
                    next.setText("NEXT: " + lines.get(curIdx));
                }
            });
            root.addView(next);
        } else {
            TextView tv = new TextView(this);
            tv.setText("No data. Open App to setup.");
            root.addView(tv);
        }

        return root;
    }
}