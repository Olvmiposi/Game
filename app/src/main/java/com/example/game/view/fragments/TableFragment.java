package com.example.game.view.fragments;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.game.R;
import com.example.game.adapter.TableAdapter;
import com.example.game.businessLogic.TableBL;
import com.example.game.model.ClubStats;
import com.example.game.model.League;
import com.example.game.repository.AppDatabase;
import com.example.game.service.IOnBackPressed;
import com.example.game.view.ActiveActivitiesTracker;
import com.example.game.view.MainActivity;
import com.example.game.viewModel.AppViewModel;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class TableFragment extends Fragment implements IOnBackPressed {

    private SearchView searchView;
    private MenuItem searchMenuItem;
    private AppViewModel appViewModel;
    private TableAdapter adapter;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ListView listView;
    private League league;
    private String maxDate, baseUrl;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bundle = getArguments();
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        baseUrl = bundle.getString("baseUrl");
        appViewModel.setBaseUrl(baseUrl);
        appViewModel.init(getContext(), baseUrl);
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

        int leagueId = bundle.getInt("leagueId", 0);
        int season = bundle.getInt("season", 0);
        new Thread(() -> {
            try {
                TableBL tableBL = new TableBL(appDatabase);
                tableBL.SeedTableDB(leagueId, season);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        try{
            league = new League();

            league = appDatabase.getLeagueByIdAndSeason(leagueId, season);

            table = new ArrayList<ClubStats>();

            tableName.setText(league.getName());

            table = appDatabase.getTable(league.getLeagueId(), league.getSeason());

        }catch (NullPointerException e){}





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



        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm", Locale.US);
        table.sort((ClubStats e1, ClubStats e2) -> e1.getDateTime().compareTo(e2.getDateTime()));

        try{
            maxDate = table.get(table.size() - 1).getDateTime();
            newTable = appDatabase.getTableByDate(league.getLeagueId(), league.getSeason(), maxDate );
            adapter = new TableAdapter(getActivity(),newTable, R.layout.tablestats_row , baseUrl);
            listView.setAdapter(adapter);
            adapter.setClubStats((ArrayList<ClubStats>) newTable);
            adapter.notifyDataSetChanged();
        }catch(IndexOutOfBoundsException | NullPointerException e){}


    }
    @Override
    public void onStart() {
        super.onStart();
        ActiveActivitiesTracker.activityStarted(this.getContext());
    }
    @Override
    public void onStop() {
        super.onStop();
        ActiveActivitiesTracker.activityStopped();
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
//        appViewModel.backstackFragment(getView());
//        Toast.makeText(getContext(),"SchrodingerFragment back button pressed",Toast.LENGTH_LONG).show();

    }
}