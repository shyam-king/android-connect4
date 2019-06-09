package com.github.shyamking.connectfour;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.hardware.display.DisplayManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    GameContainer gameContainer;
    LinearLayout rootLayout;
    TextView helperText;
    Toolbar toolbar;
    static Activity activity;
    ResultListener resultListener = new ResultListener() {
        @Override
        public void onResult(String result) {
            Intent i = new Intent(getApplicationContext(), com.github.shyamking.connectfour.result.class);
            i.putExtra("winner", result);
            startActivity(i);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameContainer = new GameContainer(this);
        rootLayout = findViewById(R.id.root);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        activity = this;
        gameContainer.setResultListener(resultListener);

        rootLayout.addView(gameContainer, LinearLayout.LayoutParams.MATCH_PARENT, -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);
        return true;
    }

    int pxFromDp(float dp) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        return (int)(dp * metrics.densityDpi / 160f) ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.undo:
                gameContainer.undo();
                return true;

            case R.id.reset:
                gameContainer.reset();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
