package hyerin.chess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Onclick(View view){
        Button button = (Button)view;
        switch(button.getId()){
            case R.id.Bluetooth:
                break;
            case R.id.Chess_Activity:
                Intent intent = new Intent(this, ChessActivity.class);
                startActivity(intent);
                break;
        }
    }
}
