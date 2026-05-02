package Hhh.uuu.uuu;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(1);
        l.setPadding(60, 60, 60, 60);
        EditText input = new EditText(this);
        input.setHint("PASTE LYRICS HERE");
        Button b = new Button(this);
        b.setText("SAVE TO KEYBOARD");
        b.setOnClickListener(v -> {
            getSharedPreferences("lyrics", 0).edit().putString("data", input.getText().toString()).putInt("ptr", 0).apply();
            Toast.makeText(this, "Lyrics Saved!", 0).show();
        });
        l.addView(input); l.addView(b);
        setContentView(l);
    }
}