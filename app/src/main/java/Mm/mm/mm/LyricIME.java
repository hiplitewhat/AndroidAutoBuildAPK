package Mm.mm.mm;
import android.inputmethodservice.InputMethodService;
import android.view.*;
import android.widget.*;
public class LyricIME extends InputMethodService {
    @Override public View onCreateInputView() {
        LinearLayout layout = new LinearLayout(this);
        Button next = new Button(this); next.setText("NEXT");
        Button undo = new Button(this); undo.setText("UNDO");
        next.setOnClickListener(v -> {
            String[] lines = getSharedPreferences("lyr", 0).getString("data", "").split("\n");
            int ptr = getSharedPreferences("lyr", 0).getInt("ptr", 0);
            if(ptr < lines.length) {
                getCurrentInputConnection().commitText(lines[ptr], 1);
                getSharedPreferences("lyr", 0).edit().putInt("ptr", ptr + 1).apply();
            }
        });
        undo.setOnClickListener(v -> {
            int ptr = getSharedPreferences("lyr", 0).getInt("ptr", 0);
            if(ptr > 0) {
                getSharedPreferences("lyr", 0).edit().putInt("ptr", ptr - 1).apply();
                Toast.makeText(this, "Step Back Applied", 0).show();
            }
        });
        layout.addView(undo); layout.addView(next);
        return layout;
    }
}