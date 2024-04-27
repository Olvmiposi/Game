package com.example.game.adapter;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.example.game.R;
import com.example.game.model.ClubStats;
import com.example.game.model.Game;
import com.example.game.repository.AppDatabase;
import com.example.game.view.MainActivity;
import com.example.game.view.MainActivity;
import com.example.game.view.PredictionsActivity;
import com.example.game.view.fragments.PredictionsFragment;
import com.example.game.viewModel.AppViewModel;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ListAdapter extends BaseAdapter {
    private final Activity activity;
    private LayoutInflater inflater;
    private AllGamesFilter gameFilter;
    private ArrayList<Game> games;
    private ArrayList<Game> filteredList;
    private int layout, isInvinsible;
    private AppViewModel appViewModel;
    private Button schrodingerBtn;
    private TextView username, gender, gameType, division, home, away, score1, score2, time, date, position1, position2, pointDifference;
    private ImageView up_arrow1, down_arrow1, up_arrow2, down_arrow2;

    private ArrayList<ClubStats> table, newTable;
    private String maxDate;
    private ClubStats homePosition, awayPosition;
    private Game currentGame, newGame;
    private ImageView profilePhoto;
    private AppDatabase appDatabase;
    public ArrayList<Game> getGames() {
        return games;
    }
    public void setGames(ArrayList<Game> games) {
        this.games = games;
    }
    public ListAdapter(Activity activity, ArrayList<Game> games, int layout ) {
        this.activity = activity;
        this.games = games;
        this.filteredList = games;
        this.layout = layout;
    }
    @Override
    public int getCount() {
        return filteredList.size();
    }
    @Override
    public Object getItem(int position) {
        return filteredList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        sort();
        final Context context = parent.getContext();
        appDatabase = AppDatabase.getAppDb(context);
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
                convertView = inflater.inflate(layout, null);
        }
        username = convertView.findViewById(R.id.username);
        division =  convertView.findViewById(R.id.division);
        home = convertView.findViewById(R.id.home);
        away =  convertView.findViewById(R.id.away);
        score1 = convertView.findViewById(R.id.score1);
        score2 =  convertView.findViewById(R.id.score2);
        time = convertView.findViewById(R.id.time);
        date =  convertView.findViewById(R.id.date);
        schrodingerBtn = convertView.findViewById(R.id.schrodingerBtn);
        currentGame = filteredList.get(position);
        //currentGame = (Game) getItem(position);
        newGame = currentGame;

        position1 = convertView.findViewById(R.id.position1);
        position2 = convertView.findViewById(R.id.position2);
        up_arrow1 = convertView.findViewById(R.id.up_arrow1);
        down_arrow1 = convertView.findViewById(R.id.down_arrow1);
        up_arrow2 = convertView.findViewById(R.id.up_arrow2);
        down_arrow2 = convertView.findViewById(R.id.down_arrow2);
        pointDifference = convertView.findViewById(R.id.pointdifference);

        position1.setVisibility(VISIBLE);
        position2.setVisibility(VISIBLE);
        up_arrow1.setVisibility(VISIBLE);
        down_arrow1.setVisibility(VISIBLE);
        up_arrow2.setVisibility(VISIBLE);
        down_arrow2.setVisibility(VISIBLE);

        table = appDatabase.getTable(currentGame.getLeagueId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("MM/dd/yyyy HH:mm")
                    .toFormatter();

            table.sort((ClubStats e1, ClubStats e2) -> e1.getDateTime().compareTo(e2.getDateTime()));

            try{
                maxDate = table.get(table.size() - 1).getDateTime();

                homePosition = appDatabase.getGamePosition(currentGame.getLeagueId(), maxDate, currentGame.getHome());
                awayPosition = appDatabase.getGamePosition(currentGame.getLeagueId(), maxDate, currentGame.getAway());

                position1.setText(String.valueOf(homePosition.getPosition()));
                position2.setText(String.valueOf(awayPosition.getPosition()));

                if(homePosition.getPosition() > awayPosition.getPosition()){
                    pointDifference.setText(String.valueOf(awayPosition.getPoints() - homePosition.getPoints()));
                    up_arrow2.setVisibility(VISIBLE);
                    down_arrow1.setVisibility(VISIBLE);
                    up_arrow1.setVisibility(INVISIBLE);
                    down_arrow2.setVisibility(INVISIBLE);
                }if (awayPosition.getPosition() > homePosition.getPosition()){
                    pointDifference.setText(String.valueOf(homePosition.getPoints() - awayPosition.getPoints()));
                    up_arrow1.setVisibility(VISIBLE);
                    down_arrow2.setVisibility(VISIBLE);
                    up_arrow2.setVisibility(INVISIBLE);
                    down_arrow1.setVisibility(INVISIBLE);
                }

            }catch(IndexOutOfBoundsException | NullPointerException e){}

        }

        if(layout == R.layout.today_game_rows)
        {
            division.setText(currentGame.getDivision());
            home.setText(currentGame.getHome());
            away.setText(currentGame.getAway());
            time.setText(currentGame.getTime());
            date.setText(currentGame.getDate());
            appViewModel = new ViewModelProvider((MainActivity)context).get(AppViewModel.class);
            appViewModel.init((MainActivity)context);

        }else if(layout == R.layout.bets_rows)
        {
            username.setText(currentGame.getUsername());
            division.setText(currentGame.getDivision());
            home.setText(currentGame.getHome());
            away.setText(currentGame.getAway());
            score1.setText(currentGame.getScore1());
            score2.setText(currentGame.getScore2());
            time.setText(currentGame.getTime());
            date.setText(currentGame.getDate());
            appViewModel = new ViewModelProvider((MainActivity)context).get(AppViewModel.class);
            appViewModel.init((MainActivity)context);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Game newGame = new Game();
//                    newGame = (Game) getItem(position);
//
//                    PredictionsFragment predictionsFragment = new PredictionsFragment();
//                    Bundle args = new Bundle();
//                    args.putSerializable("game",(Serializable)newGame);
//                    predictionsFragment.setArguments(args);
//                    appViewModel.addFragment(predictionsFragment, v);

                    newGame = (Game) getItem(position);
                    Intent Intent = new Intent(context, PredictionsActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("game",(Serializable)newGame);
                    Intent.putExtras(b);
                    context.startActivity(Intent);

                }
            });

        }else if(layout == R.layout.search_game_rows)
        {
            division.setText(currentGame.getDivision());
            home.setText(currentGame.getHome());
            away.setText(currentGame.getAway());
            time.setText(currentGame.getTime());
            date.setText(currentGame.getDate());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Game newGame = new Game();
                    newGame = (Game) getItem(position);

                    Intent Intent = new Intent(context, PredictionsActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("game",(Serializable)newGame);
                    Intent.putExtras(b);
                    context.startActivity(Intent);

//                    PredictionsFragment predictionsFragment = new PredictionsFragment();
//                    Bundle args = new Bundle();
//                    args.putSerializable("game",(Serializable)newGame);
//                    predictionsFragment.setArguments(args);
//                    appViewModel.addFragment(predictionsFragment, v);

                }
            });
        }
        else if(layout == R.layout.passwords_rows)
        {
            username.setText(currentGame.getUsername());
            division.setText(currentGame.getDivision());
            home.setText(currentGame.getHome());
            away.setText(currentGame.getAway());
            score1.setText(currentGame.getScore1());
            score2.setText(currentGame.getScore2());
            time.setText(currentGame.getTime());
            date.setText(currentGame.getDate());
            appViewModel = new ViewModelProvider((MainActivity)context).get(AppViewModel.class);
            appViewModel.init((MainActivity)context);
        }

        return convertView;
    }

    public void sort(){
        Collections.sort(filteredList, new Comparator<Game>() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public int compare(Game t1, Game t2) {
                if (t1.getDate() != null) {

                    String sDate1 = t1.getDate();
                    String sDate2 = t2.getDate();

                    Date date1 = null;
                    Date date2 = null;

                    try {
                        date1 = new SimpleDateFormat("MM/dd/yyyy").parse(sDate1);
                        date2 = new SimpleDateFormat("MM/dd/yyyy").parse(sDate2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (date1 != null && date2 != null) {
                        return date2.compareTo(date1);
                    }
                }else{
                    return -1;
                }
                return 0;
            }
        });
    }

    public Filter getFilter() {
        if (gameFilter == null) {
            gameFilter = new AllGamesFilter();
        }
        return gameFilter;
    }

    public class AllGamesFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<Game> tempList = new ArrayList<Game>();
                for (Game game : games) {
                    if ( (game.getScore1().toLowerCase() + " " + game.getScore2().toLowerCase()).contains(constraint.toString().toLowerCase())
                            || (game.getScore2().toLowerCase() + " " + game.getScore1().toLowerCase()).contains(constraint.toString().toLowerCase())
                            || game.getAway().toLowerCase().contains(constraint.toString().toLowerCase())
                            || game.getHome().toLowerCase().contains(constraint.toString().toLowerCase())
                            || game.getUsername().toLowerCase().contains(constraint.toString().toLowerCase())
                            || game.getScore1().toLowerCase().contains(constraint.toString().toLowerCase())
                            || game.getScore2().toLowerCase().contains(constraint.toString().toLowerCase())
                            || game.getDate().toLowerCase().contains(constraint.toString().toLowerCase())){
                        tempList.add(game);
                    }
                }
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = games.size();
                filterResults.values = games;
            }
            return filterResults;
        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<Game>) results.values;
            notifyDataSetChanged();
        }
    }
}
