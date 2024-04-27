package com.example.game.adapter;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SchrodingerAdapter extends BaseAdapter {
    private final Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Game> games;
    private int layout, isInvinsible;
    private AppViewModel appViewModel;
    private ArrayList<Game> filteredList, myPredictions, possibilities, predictionsList;
    private LinearLayout li, lLayout;
    private SchrodingerFilter gameFilter;
    private Button schrodingerBtn;
    private TextView username, gender, gameType, division, home, away, score1, score2, time, date, vs, position1, position2, pointDifference, headers;
    private ImageView up_arrow1, down_arrow1, up_arrow2, down_arrow2;
    private Game currentGame, newGame;
    private ImageView profilePhoto;
    private ArrayList<Integer> homeScore, awayScore, maxScore;
    private ArrayList<Game>  homeGame, awayGame;
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
    public SchrodingerAdapter(Activity activity, ArrayList<Game> games, int layout, int isInvinsible ) {
        this.activity = activity;
        this.games = games;
        this.isInvinsible = isInvinsible;
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
    @RequiresApi(api = Build.VERSION_CODES.O)
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
        vs = convertView.findViewById(R.id.vs);
        pointDifference = convertView.findViewById(R.id.pointdifference);

        position1 = convertView.findViewById(R.id.position1);
        position2 = convertView.findViewById(R.id.position2);
        up_arrow1 = convertView.findViewById(R.id.up_arrow1);
        down_arrow1 = convertView.findViewById(R.id.down_arrow1);
        up_arrow2 = convertView.findViewById(R.id.up_arrow2);
        down_arrow2 = convertView.findViewById(R.id.down_arrow2);
        lLayout = convertView.findViewById(R.id.layout);
        headers = convertView.findViewById(R.id.time_date);
        currentGame = filteredList.get(position);
        newGame = currentGame;
        table = new ArrayList<ClubStats>();

        if (isInvinsible == 1) {

            username.setVisibility(VISIBLE);

            division.setVisibility(INVISIBLE);
            home.setVisibility(INVISIBLE);
            away.setVisibility(INVISIBLE);
            score1.setVisibility(INVISIBLE);
            score2.setVisibility(INVISIBLE);
            vs.setVisibility(INVISIBLE);

            position1.setVisibility(INVISIBLE);
            position2.setVisibility(INVISIBLE);
            up_arrow1.setVisibility(INVISIBLE);
            down_arrow1.setVisibility(INVISIBLE);
            up_arrow2.setVisibility(INVISIBLE);
            down_arrow2.setVisibility(INVISIBLE);
        }
        else if (isInvinsible == 0) {
            username.setVisibility(VISIBLE);
            division.setVisibility(VISIBLE);
            home.setVisibility(VISIBLE);
            away.setVisibility(VISIBLE);
            score1.setVisibility(VISIBLE);
            score2.setVisibility(VISIBLE);
            vs.setVisibility(VISIBLE);

            position1.setVisibility(VISIBLE);
            position2.setVisibility(VISIBLE);
            up_arrow1.setVisibility(VISIBLE);
            down_arrow1.setVisibility(VISIBLE);
            up_arrow2.setVisibility(VISIBLE);
            down_arrow2.setVisibility(VISIBLE);

            table = appDatabase.getTable(currentGame.getLeagueId());
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
            }catch(IndexOutOfBoundsException | NullPointerException e){

            }

        }
        if(layout == (R.layout.schrodinger_activity_rows))
        {
            username.setText(currentGame.getUsername());
            division.setText(currentGame.getDivision());
            home.setText(currentGame.getHome());
            away.setText(currentGame.getAway());
            score1.setText(currentGame.getScore1());
            score2.setText(currentGame.getScore2());
            time.setText(currentGame.getTime());
            date.setText(currentGame.getDate());

            appViewModel = new ViewModelProvider((MainActivity) context).get(AppViewModel.class);
            appViewModel.init(context);

            Date currentDate = new Date();

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm") ;

            String currentDateTime = dateFormat.format(currentDate);
            String currentTime = timeFormat.format(currentDate);

            try {
                Date date1 = dateFormat.parse(currentDateTime);
                Date date2 = dateFormat.parse(currentGame.getDate());
                Date time1 = timeFormat.parse(currentTime);
                Date time2 = timeFormat.parse(currentGame.getTime());

                assert date2 != null;
                if(date2.before(date1) /*&& time2.before(time1)*/ && currentGame.getChecked() == 0 ){
                    lLayout.setBackgroundColor(Color.parseColor("#DA5353")); //red - if game is schrodinger but not won
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }else if (currentGame.getChecked() == 1){
                    lLayout.setBackgroundColor(Color.parseColor("#FFA733F9"));//purple - game is schrodinger but won
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }else if(date2.after(date1) /*&& time2.after(time1)*/ && currentGame.getChecked() == 0  ){
                    lLayout.setBackgroundColor(Color.parseColor("#F9AA33"));//yellow - if game is in the future or game is schrodinger
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Game newGame = new Game();
                            newGame = filteredList.get(position);

                            Intent Intent = new Intent(context, PredictionsActivity.class);
                            Bundle b = new Bundle();
                            b.putSerializable("game",(Serializable)newGame);
                            Intent.putExtras(b);
                            context.startActivity(Intent);

//                            PredictionsFragment predictionsFragment = new PredictionsFragment();
//                            Bundle args = new Bundle();
//                            args.putSerializable("game",(Serializable)newGame);
//                            predictionsFragment.setArguments(args);
//                            appViewModel.showFragment(predictionsFragment, v);

                        }
                    });
                }else{
                    lLayout.setBackgroundColor(Color.parseColor("#F9AA33"));//yellow - game is a schrodinger
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Game newGame = new Game();
                            newGame = filteredList.get(position);

                            Intent Intent = new Intent(context, PredictionsActivity.class);
                            Bundle b = new Bundle();
                            b.putSerializable("game",(Serializable)newGame);
                            Intent.putExtras(b);
                            context.startActivity(Intent);

//                            PredictionsFragment predictionsFragment = new PredictionsFragment();
//                            Bundle args = new Bundle();
//                            args.putSerializable("game",(Serializable)newGame);
//                            predictionsFragment.setArguments(args);
//                            appViewModel.showFragment(predictionsFragment, v);

                        }
                    });
                }
            } catch (ParseException | NullPointerException e) {

            }

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Game selectedgame = new Game();
                    selectedgame = (Game) filteredList.get(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Remove Schrodinger ? \n" +
                            selectedgame.getUsername());
                    builder.setTitle("Alert !");
                    builder.setCancelable(false);

                    Game finalSelectedgame = selectedgame;

                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                        finalSelectedgame.setSchrodinger(0);
                        appViewModel.verifyGame(v, finalSelectedgame, finalSelectedgame.getId());
                        getGames().remove(finalSelectedgame);
                        filteredList.remove(finalSelectedgame);
                        notifyDataSetChanged();
                    });
                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                        dialog.cancel();
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.setCanceledOnTouchOutside(true);
                    notifyDataSetChanged();
                    return false;
                }
            });

        }
        else if(layout == (R.layout.schrodinger_rows))
        {
            username.setText(currentGame.getUsername());
            division.setText(currentGame.getDivision());
            home.setText(currentGame.getHome());
            away.setText(currentGame.getAway());
            score1.setText(currentGame.getScore1());
            score2.setText(currentGame.getScore2());
            time.setText(currentGame.getTime());
            date.setText(currentGame.getDate());

            myPredictions = new ArrayList<>();
            possibilities = new ArrayList<>();
            maxScore = new ArrayList<>();
            predictionsList = new ArrayList<>();

            predictionsList = (myPredictionsList(currentGame));

            if(Objects.equals(currentGame.checked, 1)){
                lLayout.setBackgroundColor(Color.parseColor("#34833C"));//green
                convertView.setOnClickListener(null);
            }

            if(Objects.equals(currentGame.home, "null")){
                lLayout.setBackgroundColor(Color.parseColor("#00000000"));//white
                convertView.setOnClickListener(null);
            }else {
                for (Game game : predictionsList) {
                    if (Objects.equals(currentGame.username, game.username)){
                        lLayout.setBackgroundColor(Color.parseColor("#FFA733F9"));//purple
                        break;
                    }else{
                        lLayout.setBackgroundColor(Color.parseColor("#F9AA33"));//yellow
                    }
                    //newGame = filteredList.get(position);
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Game newGame = new Game();
                            newGame = filteredList.get(position);

                            Intent Intent = new Intent(context, PredictionsActivity.class);
                            Bundle b = new Bundle();
                            b.putSerializable("game",(Serializable)newGame);
                            Intent.putExtras(b);
                            context.startActivity(Intent);

//                            PredictionsFragment predictionsFragment = new PredictionsFragment();
//                            Bundle args = new Bundle();
//                            args.putSerializable("game",(Serializable)newGame);
//                            predictionsFragment.setArguments(args);
//                            appViewModel.addFragment(predictionsFragment, v);

                        }
                    });
                }
            }

            appViewModel = new ViewModelProvider((MainActivity) context).get(AppViewModel.class);
            appViewModel.init(context);

            schrodingerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Game selectedgame = new Game();
                    selectedgame = ((Game) filteredList.get(position));
                    selectedgame.getScore1();
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Is this the schrodinger result ? " + selectedgame.getUsername() + "  \n" +
                            selectedgame.getHome() + " " +  selectedgame.getScore1() + " \n " +
                            selectedgame.getAway()  + " " + selectedgame.getScore2());
                    builder.setTitle("Alert !");
                    builder.setCancelable(false);
                    Game finalSelectedGame = selectedgame;
                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                        finalSelectedGame.setSchrodinger(1);
                        appViewModel.verifyGame(v, finalSelectedGame, finalSelectedGame.getId());
                        notifyDataSetChanged();
                        dialog.cancel();
                    });
                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                        dialog.cancel();
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.setCanceledOnTouchOutside(true);
                }
            });
        }

        return convertView;
    }

    public ArrayList<Game> myPredictionsList(Game game){

        possibilities = (ArrayList<Game>) appDatabase.getGamePossibilitiess(game.getFixtureId());
        maxScore = appDatabase.getCheckedGamesByHomeAndAway(game.getHome(), game.getAway());

        try{
            int home = maxScore.get(0);
            int away = maxScore.get(1);

            for (int i = 0; i < possibilities.size(); i++) {
                boolean gameCheck = Integer.parseInt(possibilities.get(i).getScore1()) <= home && Integer.parseInt(possibilities.get(i).getScore2()) <= away;
                if (gameCheck) {
                    myPredictions.add(possibilities.get(i));
                }
            }
        }catch(IndexOutOfBoundsException e){

        }
        return myPredictions;
    }

    public void sort(){
        try {
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
                        } catch (ParseException | NullPointerException e) {

                        }
                        if (date1 != null && date2 != null) {
                            return date2.compareTo(date1);
                        }
                    } else{
                        return 1;
                    }
                    return 0;
                }
            });

        } catch (IllegalArgumentException e) {

        }

    }

    public Filter getFilter() {
        if (gameFilter == null) {
            gameFilter = new SchrodingerFilter();
        }
        return gameFilter;
    }

    public class SchrodingerFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<Game> tempList = new ArrayList<Game>();
                for (Game game : games) {
                    if ( game.getUsername().toLowerCase().contains(constraint.toString().toLowerCase())
                            || game.getHome().toLowerCase().contains(constraint.toString().toLowerCase())
                            || game.getAway().toLowerCase().contains(constraint.toString().toLowerCase())
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
