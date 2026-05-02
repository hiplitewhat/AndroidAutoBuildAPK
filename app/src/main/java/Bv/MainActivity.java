package Bv;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(1);
        l.setPadding(50, 50, 50, 50);
        EditText input = new EditText(this);
        input.setHint("Paste Lyrics Here");
        Button b = new Button(this);
        b.setText("Save to Keyboard");
        b.setOnClickListener(v -> {
            getSharedPreferences("lyrics", 0).edit().putString("data", input.getText().toString()).putInt("ptr", 0).apply();
            Toast.makeText(this, "Saved!", 0).show();
        });
        l.addView(input); l.addView(b);
        setContentView(l);
    }
}