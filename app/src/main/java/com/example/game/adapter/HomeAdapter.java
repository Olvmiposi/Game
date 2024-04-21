package com.example.game.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.game.R;
import com.example.game.model.Game;
import com.example.game.repository.AppDatabase;
import com.example.game.view.MainActivity;
import com.example.game.viewModel.AppViewModel;

import java.util.ArrayList;

public class HomeAdapter extends BaseAdapter {
    private final Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Game> games;
    private int layout;
    private AppViewModel appViewModel;
    private Button schrodingerBtn;
    private TextView username, gender, gameType, division, home, away, score1, score2, time, date;
    private Game currentGame, newGame;
    private AppDatabase appDatabase;
    public ArrayList<Game> getGames() {
        return games;
    }
    public void setGames(ArrayList<Game> games) {
        this.games = games;
    }
    public HomeAdapter(Activity activity, ArrayList<Game> games, int layout ) {
        this.activity = activity;
        this.games = games;
        this.layout = layout;
    }
    @Override
    public int getCount() {
        return games.size();
    }
    @Override
    public Object getItem(int position) {
        return games.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        appDatabase = AppDatabase.getAppDb(context);
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(layout, null);
            convertView.setFocusable(true);
            convertView.setClickable(true);
        }
        username = convertView.findViewById(R.id.username);
        home = convertView.findViewById(R.id.home);

        currentGame = (Game) getItem(position);
        newGame = currentGame;
        if(layout == R.layout.team)
        {
            home.setText(currentGame.getHome());
            username.setText(currentGame.getUsername());

        }
        return convertView;
    }
}
