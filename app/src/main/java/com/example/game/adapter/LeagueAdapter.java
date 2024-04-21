package com.example.game.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.game.R;
import com.example.game.model.League;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class LeagueAdapter extends BaseAdapter implements Filterable {
    private  Activity activity;
    private LayoutInflater inflater;
    private ArrayList<League> leagues;
    private LeagueFilter leagueFilter;
    private ImageView newImageView;
    private ArrayList<String> maxDate;
    private LinearLayout li;
    private ArrayList<League> filteredList;
    private int layout, colorChange;
    private TextView leagueId, name, season, start, end;
    private League currentLeague, newLeague;
    public ArrayList<League> getLeagues() {
        return leagues;
    }

    public void setLeagues(ArrayList<League> leagues) {
        this.leagues = leagues;
    }

    public LeagueAdapter(Activity activity, ArrayList<League> leagues, int layout, int colorChange, ArrayList<String> maxDate ) {
        this.activity = activity;
        this.leagues = leagues;
        this.layout = layout;
        this.colorChange = colorChange;
        this.filteredList = leagues;
        this.maxDate = maxDate;
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
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
                convertView = inflater.inflate(layout, null);
        }
        leagueId = convertView.findViewById(R.id.leagueId);
        name =  convertView.findViewById(R.id.name);
        season = convertView.findViewById(R.id.season);
        start =  convertView.findViewById(R.id.start);
        end = convertView.findViewById(R.id.end);
        li = (LinearLayout)convertView.findViewById(R.id.layout);

        currentLeague = (League) getItem(position);
        newLeague = currentLeague;

        if(layout == R.layout.league_rows)
        {
            leagueId.setText(String.valueOf(currentLeague.getId()));
            name.setText(currentLeague.getName());
            season.setText(String.valueOf(currentLeague.getSeason()));
            start.setText(currentLeague.getStart());
            end.setText(currentLeague.getEnd());
        }else if(layout == R.layout.league_activity_rows){


            if (colorChange == 1){// default is 0, red
                li.setBackgroundColor(Color.parseColor("#34833C"));// green
            }
            if (colorChange == 2){
                li.setBackgroundColor(Color.parseColor("#7C42CD"));// purple
            }
            if (colorChange == 3){
                li.setBackgroundColor(Color.parseColor("#CF7843"));// orange
            }

            newImageView = convertView.findViewById(R.id.newGame);

            newImageView.setVisibility(View.GONE);

            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

                    String Date = LocalDate.now().format(formatter);

                    if (Objects.equals(maxDate.get(position), Date)){
                        newImageView.setVisibility(View.VISIBLE);
                    }else{
                        newImageView.setVisibility(View.GONE);
                    }

                }

            }catch (IndexOutOfBoundsException e){

            }

            name.setText(currentLeague.getName());
            season.setText(String.valueOf(currentLeague.getSeason()));
            start.setText(currentLeague.getStart());
            end.setText(currentLeague.getEnd());

//            try {
//
//            }catch (NullPointerException e){
//
//            }
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (leagueFilter == null) {
            leagueFilter = new LeagueFilter();
        }
        return leagueFilter;
    }

    public class LeagueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<League> tempList = new ArrayList<League>();
                for (League league : leagues) {
                    if ( league.getName().toLowerCase().contains(constraint.toString().toLowerCase())
                        ||  String.valueOf(league.getSeason()).toLowerCase().contains(constraint.toString().toLowerCase())
                        ||  String.valueOf(league.getId()).toLowerCase().contains(constraint.toString().toLowerCase())
                        || league.getStart().toLowerCase().contains(constraint.toString().toLowerCase())
                        || league.getEnd().toLowerCase().contains(constraint.toString().toLowerCase())
                    ){
                        tempList.add(league);
                    }
                }
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = leagues.size();
                filterResults.values = leagues;
            }
            return filterResults;
        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<League>) results.values;
            notifyDataSetChanged();
        }
    }
}


