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
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.game.R;
import com.example.game.model.ClubStats;
import com.example.game.model.Game;
import com.example.game.repository.AppDatabase;
import com.example.game.view.PredictionsActivity;
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
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PasswordAdapter extends BaseAdapter implements Filterable {
    private final Activity activity;
    private LayoutInflater inflater;
    private AllPasswordFilter gameFilter;
    private ArrayList<Game> filteredList;
    private ArrayList<Game> games;
    private int layout, isInvinsible ;
    private AppViewModel appViewModel;
    private Button schrodingerBtn;
    private TextView username, gender, gameType, division, home, away, score1, score2, time, date, vs, position1, position2, pointDifference, headers, odds, fixtureId;
    private ImageView up_arrow1, down_arrow1, up_arrow2, down_arrow2;
    private ArrayList<ClubStats> table, newTable;
    private String maxDate, baseUrl;
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
    public PasswordAdapter(Activity activity, ArrayList<Game> games, int layout, int isInvinsible, String baseUrl  ) {
        this.activity = activity;
        this.games = games;
        this.layout = layout;
        this.filteredList = games;
        this.isInvinsible = isInvinsible;
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
        vs = convertView.findViewById(R.id.vs);
        schrodingerBtn = convertView.findViewById(R.id.schrodingerBtn);

        fixtureId = convertView.findViewById(R.id.fixtureId);

        position1 = convertView.findViewById(R.id.position1);
        position2 = convertView.findViewById(R.id.position2);
        up_arrow1 = convertView.findViewById(R.id.up_arrow1);
        down_arrow1 = convertView.findViewById(R.id.down_arrow1);
        up_arrow2 = convertView.findViewById(R.id.up_arrow2);
        down_arrow2 = convertView.findViewById(R.id.down_arrow2);
        pointDifference = convertView.findViewById(R.id.pointdifference);
        headers = convertView.findViewById(R.id.time_date);

        odds = convertView.findViewById(R.id.odds);

        //currentGame = (Game) getItem(position);
        currentGame = filteredList.get(position);
        newGame = currentGame;


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

            headers.setVisibility(View.GONE);
            try {
                List<String> outList = filteredList.stream().map(m -> String.valueOf(m.getDate())).distinct().collect(Collectors.toList());

                for (int i = 0; i < outList.size(); i++) {
                    if (String.valueOf(currentGame.getDate()) == outList.get(i)) {
                        headers.setVisibility(VISIBLE);

                        SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
                        SimpleDateFormat format2 = new SimpleDateFormat("MMMM d, yyyy");
                        Date date = null;

                        date = format1.parse(String.valueOf(currentGame.getDate()));
                        headers.setText(format2.format(date));

                        break;
                    }
                }
            } catch (ParseException | RuntimeException e) {
                //throw new RuntimeException(e);
            }


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

        if(layout == R.layout.passwords_rows)
        {
            username.setText(currentGame.getUsername());
            division.setText(currentGame.getDivision());
            home.setText(currentGame.getHome());
            away.setText(currentGame.getAway());
            score1.setText(currentGame.getScore1());
            score2.setText(currentGame.getScore2());
            time.setText(currentGame.getTime());
            date.setText(String.valueOf(currentGame.getDate()));

            fixtureId.setText( String.valueOf(currentGame.getFixtureId()));


            odds.setText(currentGame.getOdds());

            division.setVisibility(INVISIBLE);

            appViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(AppViewModel.class);
            appViewModel.setBaseUrl(baseUrl);
            appViewModel.init(context, baseUrl);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Game newGame = new Game();
                    newGame = filteredList.get(position);

                    Intent Intent = new Intent(context, PredictionsActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("game",(Serializable)newGame);
                    Intent.putExtras(b);
                    Intent.putExtra("baseUrl", baseUrl);
                    activity.startActivity(Intent);

                }
            });

        }
        if (isInvinsible == 1) {

            division.setVisibility(View.INVISIBLE);
            home.setVisibility(View.INVISIBLE);
            away.setVisibility(View.INVISIBLE);
            score1.setVisibility(View.INVISIBLE);
            score2.setVisibility(View.INVISIBLE);
            vs.setVisibility(View.INVISIBLE);

            pointDifference.setVisibility(INVISIBLE);
            position1.setVisibility(INVISIBLE);
            position2.setVisibility(INVISIBLE);
            up_arrow1.setVisibility(INVISIBLE);
            down_arrow1.setVisibility(INVISIBLE);
            up_arrow2.setVisibility(INVISIBLE);
            down_arrow2.setVisibility(INVISIBLE);

        }
        if (isInvinsible == 0) {

            division.setVisibility(View.INVISIBLE);

            home.setVisibility(View.VISIBLE);
            away.setVisibility(View.VISIBLE);
            score1.setVisibility(View.VISIBLE);
            score2.setVisibility(View.VISIBLE);
            vs.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public void sort(){
        Collections.sort(filteredList, new Comparator<Game>() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public int compare(Game game1, Game game2) {
                if (game1.getDate() != null) {
                    String time1 = game1.getTime();
                    String time2 = game2.getTime();

                    String sDate1 = game1.getDate() + " " + time1;
                    String sDate2 = game2.getDate() + " " + time2;

                    Date date1 = null;
                    Date date2 = null;

                    try {
                        date1 = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.FRANCE).parse(sDate1);
                        date2 = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.FRANCE).parse(sDate2);
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

    @Override
    public Filter getFilter() {
        if (gameFilter == null) {
            gameFilter = new AllPasswordFilter();
        }
        return gameFilter;
    }

    public class AllPasswordFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<Game> tempList = new ArrayList<Game>();
                for (Game game : games) {
                    if ((game.getScore1().toLowerCase() + " " + game.getScore2().toLowerCase()).contains(constraint.toString().toLowerCase())
                            || (game.getScore2().toLowerCase() + " " + game.getScore1().toLowerCase()).contains(constraint.toString().toLowerCase())
                            || game.getAway().toLowerCase().contains(constraint.toString().toLowerCase())
                            || game.getHome().toLowerCase().contains(constraint.toString().toLowerCase())
                            || (game.getAway().toLowerCase() + " " + game.getHome().toLowerCase() ).contains(constraint.toString().toLowerCase())
                            || (game.getHome().toLowerCase() + " " + game.getAway().toLowerCase() ).contains(constraint.toString().toLowerCase())
                            || game.getUsername().toLowerCase().contains(constraint.toString().toLowerCase())
                            || game.getScore1().toLowerCase().contains(constraint.toString().toLowerCase())
                            || game.getScore2().toLowerCase().contains(constraint.toString().toLowerCase())
                            || String.valueOf(game.getDate()).toLowerCase().contains(constraint.toString().toLowerCase())){
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
