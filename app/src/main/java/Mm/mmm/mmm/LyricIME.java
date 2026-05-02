package Mm.mmm.mmm;
import android.inputmethodservice.InputMethodService;
import android.view.*;
import android.widget.*;

public class LyricIME extends InputMethodService {
    TextView statusView;

    @Override public View onCreateInputView() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 8, 16, 8);

        statusView = new TextView(this);
        statusView.setTextSize(10);
        statusView.setPadding(0, 0, 0, 6);
        updateStatus();

        LinearLayout btnRow = new LinearLayout(this);
        Button prev = new Button(this); prev.setText("◀ PREV");
        Button next = new Button(this); next.setText("NEXT ▶");
        Button undo = new Button(this); undo.setText("↩ UNDO");
        Button reset = new Button(this); reset.setText("⟳ RESET");

        next.setOnClickListener(v -> {
            String[] lines = getSharedPreferences("lyr", 0).getString("data", "").split("\n");
            int ptr = getSharedPreferences("lyr", 0).getInt("ptr", 0);
            if (ptr < lines.length) {
                getCurrentInputConnection().commitText(lines[ptr] + "\n", 1);
                getSharedPreferences("lyr", 0).edit().putInt("ptr", ptr + 1).apply();
                updateStatus();
            } else {
                Toast.makeText(this, "End of lyrics", 0).show();
            }
        });

        prev.setOnClickListener(v -> {
            int ptr = getSharedPreferences("lyr", 0).getInt("ptr", 0);
            if (ptr > 0) {
                getSharedPreferences("lyr", 0).edit().putInt("ptr", ptr - 1).apply();
                Toast.makeText(this, "Moved back", 0).show();
                updateStatus();
            }
        });

        undo.setOnClickListener(v -> {
            int ptr = getSharedPreferences("lyr", 0).getInt("ptr", 0);
            if (ptr > 0) {
                getSharedPreferences("lyr", 0).edit().putInt("ptr", ptr - 1).apply();
                getCurrentInputConnection().deleteSurroundingText(1000, 0);
                Toast.makeText(this, "Step Back Applied", 0).show();
                updateStatus();
            }
        });

        reset.setOnClickListener(v -> {
            getSharedPreferences("lyr", 0).edit().putInt("ptr", 0).apply();
            Toast.makeText(this, "Reset to start", 0).show();
            updateStatus();
        });

        btnRow.addView(prev); btnRow.addView(next); btnRow.addView(undo); btnRow.addView(reset);
        layout.addView(statusView);
        layout.addView(btnRow);
        return layout;
    }

    void updateStatus() {
        String data = getSharedPreferences("lyr", 0).getString("data", "");
        int ptr = getSharedPreferences("lyr", 0).getInt("ptr", 0);
        int total = data.isEmpty() ? 0 : data.split("\n").length;
        if (statusView != null) statusView.setText("Line " + ptr + " / " + total);
    }
}