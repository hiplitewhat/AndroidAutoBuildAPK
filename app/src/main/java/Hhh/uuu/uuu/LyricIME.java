package Hhh.uuu.uuu;
import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.widget.Button;
public class LyricIME extends InputMethodService {
    @Override public View onCreateInputView() {
        Button b = new Button(this);
        b.setText("START");
        b.setOnClickListener(v -> {
            String data = getSharedPreferences("lyrics", 0).getString("data", "");
            String[] lines = data.split("\n");
            int ptr = getSharedPreferences("lyrics", 0).getInt("ptr", 0);
            if (lines.length > 0 && ptr < lines.length) {
                String text = lines[ptr].trim();
                if (!text.isEmpty()) getCurrentInputConnection().commitText(text, 1);
                int next = (ptr + 1) % lines.length;
                getSharedPreferences("lyrics", 0).edit().putInt("ptr", next).apply();
                b.setText(lines[next].isEmpty() ? "NEXT" : lines[next]);
            }
        });
        return b;
    }
}