package com.example.supreme.tictactoe;

import android.app.Activity;
import android.os.Bundle;

public class TicTacToeActivity extends Activity {
    private TicTacToeView view_;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view_ = new TicTacToeView(this);
        setContentView(view_);
    }
}
