package com.example.game.adapter;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.game.R;
import com.example.game.model.ClubStats;
import com.example.game.model.Game;
import com.example.game.repository.AppDatabase;
import com.example.game.viewModel.AppViewModel;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;

public class PredictionAdapter extends BaseAdapter implements Filterable {
    private  Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Game> games;
    private GameFilter gameFilter;
    private ArrayList<Game> filteredList, checkedGamesArrayList;
    private int layout, checkedsize;
    private LinearLayout li, lLayout;
    private AppDatabase appDatabase;
    private Button verifyGameBtn;
    private AppViewModel appViewModel;
    private ImageView ifSchrodinger;
    private TextView username, division, home, away, score1, score2, time, date, position1, position2, pointDifference, odds, checked;
    private ImageView up_arrow1, down_arrow1, up_arrow2, down_arrow2;
    private ArrayList<ClubStats> table, newTable;
    private String maxDate, baseUrl;

    private Bundle bundle;
    private ClubStats homePosition, awayPosition;
    private Game currentGame;

    public PredictionAdapter() {

    }

    public ArrayList<Game> getGames() {
        return games;
    }
    public void setGames(ArrayList<Game> games) {
        this.games = games;
    }
    public PredictionAdapter(Activity activity, ArrayList<Game> games, int layout, String baseUrl ) {
        this.activity = activity;
        this.games = games;
        this.layout = layout;
        this.filteredList = games;
        this.baseUrl = baseUrl;
        getFilter();
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
        final Context context = parent.getContext();
        appViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(AppViewModel.class);
        appViewModel.setBaseUrl(baseUrl);
        appViewModel.init(context, baseUrl);

        appDatabase = AppDatabase.getAppDb(context);
        currentGame = filteredList.get(position);


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
        ifSchrodinger = convertView.findViewById(R.id.ifSchrodinger);
        verifyGameBtn = convertView.findViewById(R.id.verifyGameBtn);
        position1 = convertView.findViewById(R.id.position1);
        position2 = convertView.findViewById(R.id.position2);
        up_arrow1 = convertView.findViewById(R.id.up_arrow1);
        down_arrow1 = convertView.findViewById(R.id.down_arrow1);
        up_arrow2 = convertView.findViewById(R.id.up_arrow2);
        down_arrow2 = convertView.findViewById(R.id.down_arrow2);
        pointDifference = convertView.findViewById(R.id.pointdifference);
        lLayout = convertView.findViewById(R.id.layout);
        odds = convertView.findViewById(R.id.odds);
        checked = convertView.findViewById(R.id.checked);

        position1.setVisibility(VISIBLE);
        position2.setVisibility(VISIBLE);
        up_arrow1.setVisibility(VISIBLE);
        down_arrow1.setVisibility(VISIBLE);
        up_arrow2.setVisibility(VISIBLE);
        down_arrow2.setVisibility(VISIBLE);

        table = appDatabase.getTable(currentGame.getLeagueId(), currentGame.getSeason());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("MM/dd/yyyy HH:mm")
                    .toFormatter();

            table.sort((ClubStats e1, ClubStats e2) -> e1.getDateTime().compareTo(e2.getDateTime()));

            try{
                maxDate = table.get(table.size() - 1).getDateTime();

                homePosition = appDatabase.getGamePosition(currentGame.getLeagueId(), currentGame.getSeason(), maxDate, currentGame.getHome());
                awayPosition = appDatabase.getGamePosition(currentGame.getLeagueId(), currentGame.getSeason(), maxDate, currentGame.getAway());

                if(homePosition == null ){
                    position1.setText(String.valueOf(0));
                    pointDifference.setText(String.valueOf(0));

                }else{
                    position1.setText(String.valueOf(homePosition.getPosition()));
                }

                if(awayPosition == null ){
                    position2.setText(String.valueOf(0));
                    pointDifference.setText(String.valueOf(0));

                }else{
                    position2.setText(String.valueOf(awayPosition.getPosition()));
                }

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

        if (currentGame.getSchrodinger() == 1){
            ifSchrodinger.setVisibility(View.VISIBLE);
        }else{
            ifSchrodinger.setVisibility(View.GONE);
        }

        if(currentGame.getChecked() == 1){
            lLayout.setBackgroundColor(Color.parseColor("#34833C"));
        }else{
            lLayout.setBackgroundColor(0);
        }


        if(layout == (R.layout.prediction_rows))
        {
            username.setText(currentGame.getUsername());
            division.setText(currentGame.getDivision());
            home.setText(currentGame.getHome());
            away.setText(currentGame.getAway());
            score1.setText(currentGame.getScore1());
            score2.setText(currentGame.getScore2());
            time.setText(currentGame.getTime());
            date.setText(String.valueOf(currentGame.getDate()));

            division.setVisibility(View.GONE);

            odds.setText(currentGame.getOdds());

            username.setTextColor(Color.RED);

            checkedsize = appDatabase.getAllCheckedGames(currentGame.getUsername().replaceAll("\\d", ""), currentGame.getLeagueId());

            checked.setText(Integer.toString(checkedsize));


            verifyGameBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Game selectedgame = new Game();
                    selectedgame = ((Game) filteredList.get(position));

                    if(selectedgame.schrodinger == 1){
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setMessage("Remove Schrodinger ? \n" +
                                selectedgame.getUsername());
                        builder.setTitle("Alert !");
                        builder.setCancelable(false);
                        Game finalSelectedgame = selectedgame;
                        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                            finalSelectedgame.setSchrodinger(0);
                            appViewModel.updateSchrodinger(v, finalSelectedgame, finalSelectedgame.getId());
                        });
                        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                            dialog.cancel();
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        alertDialog.setCanceledOnTouchOutside(true);
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setMessage("Is this result familiar ? \n" +
                                selectedgame.getHome() + " " + selectedgame.getScore1() + " \n " +
                                selectedgame.getAway() + " " + selectedgame.getScore2() + " \n " );
                        builder.setTitle("Alert !");
                        builder.setCancelable(false);
                        Game finalSelectedgame = selectedgame;
                        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                            finalSelectedgame.setSchrodinger(1);
                            appViewModel.updateSchrodinger(v, finalSelectedgame, finalSelectedgame.getId());
                        });
                        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                            dialog.cancel();
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        alertDialog.setCanceledOnTouchOutside(true);
                    }
                }
            });
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (gameFilter == null) {
            gameFilter = new GameFilter();
        }
        return gameFilter;
    }
    public class GameFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<Game> tempList = new ArrayList<Game>();
                for (Game game : games) {
                    if (  (game.getScore1().toLowerCase() + " " + game.getScore2().toLowerCase()).contains(constraint.toString().toLowerCase())
                        || (game.getScore2().toLowerCase() + " " + game.getScore1().toLowerCase()).contains(constraint.toString().toLowerCase())
                        || game.getAway().toLowerCase().contains(constraint.toString().toLowerCase())
                        || game.getHome().toLowerCase().contains(constraint.toString().toLowerCase())
                        || game.getUsername().toLowerCase().contains(constraint.toString().toLowerCase())
                        || game.getScore1().toLowerCase().contains(constraint.toString().toLowerCase())
                        || game.getScore2().toLowerCase().contains(constraint.toString().toLowerCase())
                    ){
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


