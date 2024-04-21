package com.example.game.view.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.game.R;
import com.example.game.adapter.TableAdapter;
import com.example.game.model.ClubStats;
import com.example.game.model.Game;
import com.example.game.model.League;
import com.example.game.repository.AppDatabase;
import com.example.game.service.IOnBackPressed;
import com.example.game.view.MainActivity;
import com.example.game.viewModel.AppViewModel;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TableFragment extends Fragment implements IOnBackPressed {

    private SearchView searchView;
    private MenuItem searchMenuItem;
    private AppViewModel appViewModel;
    private TableAdapter adapter;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ArrayList<Integer> allGamesLeaguesId,allPasswordLeaguesId, homeScore, awayScore;
    private ListView listView;
    private League league;
    private String maxDate;
    private TextView tableName;
    private ArrayList<ClubStats> table, newTable;
    private AppDatabase appDatabase;
    private Bundle bundle;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_table, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        appViewModel.init(getContext());
        appDatabase = AppDatabase.getAppDb(getContext());
        mySwipeRefreshLayout = requireView().findViewById(R.id.swiperefresh);
        listView  = requireView().findViewById(R.id.tablesListView);
        tableName = requireView().findViewById(R.id.tableName);

        mySwipeRefreshLayout = requireView().findViewById(R.id.swiperefresh);

        ((MainActivity) getActivity()).disableSwipe();
        ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();
        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            appViewModel.getStanding(league);
        });

        bundle = getArguments();

        int leagueId = bundle.getInt("leagueId", 0);
        int season = bundle.getInt("season", 0);

        try{
            league = new League();

            league = appDatabase.getLeagueById(leagueId);

            table = new ArrayList<ClubStats>();

            tableName.setText(league.getName());

            table = appDatabase.getTable(league.getId());

        }catch (NullPointerException e){}




        //get all league id from available games
        allGamesLeaguesId = new ArrayList<>();
        allPasswordLeaguesId = new ArrayList<>();

        appDatabase.getGames().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                if(games == null){
                }else {
                    for(Game game : games ) {
                        if(!allGamesLeaguesId.contains(game.getLeagueId()))
                            allGamesLeaguesId.add(game.getLeagueId());
                    }
                }
            }
        });

        appDatabase.getAllCheckedGames().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                if(games == null){
                }else {
                    for(Game game : games ) {
                        if(!allPasswordLeaguesId.contains(game.getLeagueId()))
                            allPasswordLeaguesId.add(game.getLeagueId());
                    }
                }
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (listView== null || listView.getChildCount() == 0) ? 0 : listView.getChildAt(0).getTop();
                mySwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm", Locale.US);
            table.sort((ClubStats e1, ClubStats e2) -> e1.getDateTime().compareTo(e2.getDateTime()));
        }

        try{
            maxDate = table.get(table.size() - 1).getDateTime();
            newTable = appDatabase.getTableByDate(league.getId(), maxDate );
            adapter = new TableAdapter(getActivity(),newTable, R.layout.tablestats_row );
            listView.setAdapter(adapter);
            adapter.setClubStats((ArrayList<ClubStats>) newTable);
            adapter.notifyDataSetChanged();
        }catch(IndexOutOfBoundsException | NullPointerException e){}


    }

    @Override
    public void onRefresh() {
        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            appViewModel.getStanding(league);
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onBackPressed() {
        appViewModel.backstackFragment(getView());
        Toast.makeText(getContext(),"SchrodingerFragment back button pressed",Toast.LENGTH_LONG).show();

    }
}