package com.example.game.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.game.R;
import com.example.game.model.ClubStats;
import com.example.game.repository.AppDatabase;
import com.example.game.viewModel.AppViewModel;

import java.util.ArrayList;

public class TableAdapter extends BaseAdapter {
    private final Activity activity;
    private LayoutInflater inflater;
    private ArrayList<ClubStats> clubStats;
    private int layout;
    private AppViewModel appViewModel;
    private TextView positio, name, points, allPlayed, win, draw, lose, goalsFor, goalsAgainst, goalsDiff;
    private ClubStats currentTable, newTable;
    private AppDatabase appDatabase;
    private String baseUrl;
    public ArrayList<ClubStats> getClubStats() {
        return clubStats;
    }
    public void setClubStats(ArrayList<ClubStats> clubStats) {
        this.clubStats = clubStats;
    }
    public TableAdapter(Activity activity, ArrayList<ClubStats> clubStats, int layout, String baseUrl ) {
        this.activity = activity;
        this.clubStats = clubStats;
        this.layout = layout;
        this.baseUrl = baseUrl;

    }
    @Override
    public int getCount() {
        return clubStats.size();
    }
    @Override
    public Object getItem(int position) {
        return clubStats.get(position);
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
        }
        positio = convertView.findViewById(R.id.position);
        name =  convertView.findViewById(R.id.name);
        points = convertView.findViewById(R.id.points);
        allPlayed =  convertView.findViewById(R.id.allPlayed);
        win = convertView.findViewById(R.id.win);
        draw =  convertView.findViewById(R.id.draw);
        lose = convertView.findViewById(R.id.lose);
        goalsFor =  convertView.findViewById(R.id.goalsFor);
        goalsAgainst =  convertView.findViewById(R.id.goalsAgainst);
        goalsDiff = convertView.findViewById(R.id.goalsDiff);


        currentTable = clubStats.get(position);
        //currentTable = (Table) getItem(position);
        newTable = currentTable;

        if(layout == R.layout.tablestats_row)
        {
            positio.setText(String.valueOf( currentTable.getPosition()));
            name.setText(String.valueOf( currentTable.getName()));
            points.setText(String.valueOf( currentTable.getPoints()));
            allPlayed.setText(String.valueOf( currentTable.getAllPlayed()));
            win.setText(String.valueOf( currentTable.getWin()));
            draw.setText(String.valueOf( currentTable.getDraw()));
            lose.setText(String.valueOf( currentTable.getLose()));
            goalsFor.setText(String.valueOf( currentTable.getGoalsFor()));
            goalsAgainst.setText(String.valueOf( currentTable.getGoalsAgainst()));
            goalsDiff.setText(String.valueOf( currentTable.getGoalsDiff()));

            appViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(AppViewModel.class);
            appViewModel.setBaseUrl(baseUrl);
            appViewModel.init(context, baseUrl);
        }

        return convertView;
    }

}
