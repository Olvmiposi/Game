package com.example.game.adapter;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

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
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.example.game.R;
import com.example.game.model.ClubStats;
import com.example.game.model.Game;
import com.example.game.repository.AppDatabase;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AllGamesAdapter extends BaseAdapter implements Filterable {
    private final Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Game> games;
    private AllGameFilter gameFilter;
    private ArrayList<Game> filteredList;
    private int layout;

    private AppViewModel appViewModel;
    private Button schrodingerBtn;
    private Game currentGame, newGame;
    private ImageView profilePhoto;

    private TextView username, gender, gameType, division, home, away, score1, score2, time, date, position1, position2, pointDifference, headers;
    private ImageView up_arrow1, down_arrow1, up_arrow2, down_arrow2;

    private ArrayList<ClubStats> table, newTable;
    private String maxDate;
    private ClubStats homePosition, awayPosition;

    private AppDatabase appDatabase;
    public ArrayList<Game> getGames() {
        return games;
    }
    public void setGames(ArrayList<Game> games) {
        this.games = games;
    }
    public AllGamesAdapter(Activity activity, ArrayList<Game> games, int layout ) {
        this.activity = activity;
        this.games = games;
        this.layout = layout;
        this.filteredList = games;
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
        appDatabase = AppDatabase.getAppDb(context);
        //currentGame = (Game) getItem(position);
        currentGame = filteredList.get(position);

        appViewModel = new ViewModelProvider((MainActivity) context).get(AppViewModel.class);
        appViewModel.init(context);

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

        position1 = convertView.findViewById(R.id.position1);
        position2 = convertView.findViewById(R.id.position2);
        up_arrow1 = convertView.findViewById(R.id.up_arrow1);
        down_arrow1 = convertView.findViewById(R.id.down_arrow1);
        up_arrow2 = convertView.findViewById(R.id.up_arrow2);
        down_arrow2 = convertView.findViewById(R.id.down_arrow2);
        pointDifference = convertView.findViewById(R.id.pointdifference);
        headers = convertView.findViewById(R.id.time_date);

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

            headers.setVisibility(View.GONE);
            try {

                List<String> outList = filteredList.stream().map(Game::getDate).distinct().collect(Collectors.toList());

                for (int i = 0; i < outList.size(); i++) {
                    if (currentGame.getDate() == outList.get(i)) {
                        headers.setVisibility(VISIBLE);

                        SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
                        SimpleDateFormat format2 = new SimpleDateFormat("MMMM d, yyyy");
                        Date date = null;

                        date = format1.parse(currentGame.getDate());
                        headers.setText(format2.format(date));


                        break;
                    }
                }
            } catch (ParseException | RuntimeException e) {
                //throw new RuntimeException(e);
            }

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


        newGame = currentGame;
        if(layout == R.layout.search_game_rows)
        {
            division.setText(currentGame.getDivision());
            home.setText(currentGame.getHome());
            away.setText(currentGame.getAway());
            time.setText(currentGame.getTime());
            date.setText(currentGame.getDate());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    newGame = (Game) filteredList.get(position);
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

        return convertView;
    }


    @Override
    public Filter getFilter() {
        if (gameFilter == null) {
            gameFilter = new AllGameFilter();
        }
        return gameFilter;
    }

    public class AllGameFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<Game> tempList = new ArrayList<Game>();
                for (Game game : games) {
                    if ( game.getHome().toLowerCase().contains(constraint.toString().toLowerCase())
                            ||  String.valueOf(game.getAway()).toLowerCase().contains(constraint.toString().toLowerCase())
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
