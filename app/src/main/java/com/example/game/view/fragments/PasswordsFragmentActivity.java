package com.example.game.view.fragments;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.game.R;
import com.example.game.adapter.LeagueAdapter;
import com.example.game.adapter.YearRecyclerViewAdapter;
import com.example.game.model.Game;
import com.example.game.model.League;
import com.example.game.repository.AppDatabase;
import com.example.game.view.MainActivity;
import com.example.game.viewModel.AppViewModel;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class PasswordsFragmentActivity  extends Fragment implements YearRecyclerViewAdapter.ItemClickListener{
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private AppViewModel appViewModel;
    private LeagueAdapter adapter;
    private YearRecyclerViewAdapter yearRecyclerViewAdapter;
    int currentFirstVisibleItem, currentVisibleItemCount, currentTotalItemCount, currentScrollState;
    private ArrayList<Integer> years;
    private ArrayList<Game> gameArrayList, checkedGamesBySeason;
    private ArrayList<League> allPasswordLeagues;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ListView listView;
    private ArrayList<Game> games;
    private LinearLayoutManager linearLayoutManager;
    private League league;
    private ArrayList<Integer>   homeScore, awayScore;
    private static final String recyclerviewState = "recyclerviewState";
    private RecyclerView recyclerView;
    private Button table, allGames, password, schrodinger;
    private AppDatabase appDatabase;
    private String maxDate = null;
    private static final String LIST_STATE = "listState";
    private Parcelable state = null;
    private Bundle bundle;
    private int isInvinsible;

    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setRetainInstance(true);

        return inflater.inflate(R.layout.fragment_league_activity, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        appViewModel.init(getContext());
        appDatabase = AppDatabase.getAppDb(getContext());
        mySwipeRefreshLayout = getView().findViewById(R.id.swiperefresh);
        listView  = getView().findViewById(R.id.list_view);
        recyclerView = getView().findViewById(R.id.rvYears);
        progressBar = getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        ((MainActivity) getActivity()).disableSwipe();
        //((AppCompatActivity) requireActivity()).getSupportActionBar().hide();
        bundle = getArguments();

        Toolbar mToolbar;
        setHasOptionsMenu(true);
        mToolbar = (Toolbar) requireView().findViewById(R.id.toolbar_home);
        if (mToolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);
        }
        mToolbar.setTitle(null);
        mToolbar.inflateMenu(R.menu.options_menu);

        isInvinsible =  this.getArguments().getInt("isInvinsible", 0);

        years = new ArrayList<>();
        years.add(2024);
        years.add(2023);
        years.add(2022);
        years.add(2021);
        years.add(2020);
        years.add(2019);
        years.add(2018);

        // get the reference of RecyclerView
        // set a LinearLayoutManager with default horizontal orientation and false value for reverseLayout to show the items from start to end
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        if(state != null) {
            listView.onRestoreInstanceState(state);
        }

        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            appViewModel.getTodayUsername();
            appViewModel.getCheckedGames();
        });

        //get all league id from available games
        allPasswordLeagues = (ArrayList<League>) bundle.getSerializable("allPasswordLeagues");
        //


        myUpdateOperation();
        onCompletion();


        appViewModel.isLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    progressBar.setVisibility(View.VISIBLE);
                }else{
                    progressBar.setVisibility(View.GONE);
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

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void myUpdateOperation(){

        //create year recycler adapter to get each year
        ////////////////////////////////////////////////////////////
        yearRecyclerViewAdapter = new YearRecyclerViewAdapter(getActivity(), years);

        //maxDate is null;
        maxDate = null;

        // array list of maxDates to get the maximum dates of each league games
        ArrayList<String> maxDates = new ArrayList<>();

        //while true, look for the latest checked games from the years
        // if latest game is found, break
        //////////////////////////////////////////////////////////
        try{
            ArrayList<League> currentYearLeague = new ArrayList<>();
            int count = 0;
            while(true) {
                count++;

                int season = yearRecyclerViewAdapter.getItem(count);

                checkedGamesBySeason = appDatabase.getAllCheckedGamesBySeason(season);

                if(!checkedGamesBySeason.isEmpty()) {
                    yearRecyclerViewAdapter.setSelected_position(count);

                    for(League league: allPasswordLeagues) {
                        if (league.getSeason() == season) {
                            currentYearLeague.add(league);
                        }
                    }

                    for(League league: currentYearLeague) {
                        gameArrayList = (ArrayList<Game>) appDatabase.getCheckedGamesByLatestDate2(league.getLeagueId(), league.getSeason());
                        try{
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                            maxDate = Objects.requireNonNull(gameArrayList.stream()
                                    .map(d -> LocalDate.parse(d.getDate(), dtf))
                                    .max(Comparator.comparing(LocalDate::toEpochDay))
                                    .orElse(null)).format(dtf);

                            maxDates.add(maxDate);
                        }catch(NullPointerException e ){

                        }
                    }

                    adapter = new LeagueAdapter(getActivity(), currentYearLeague, R.layout.league_activity_rows, 1, maxDates);
                    listView.setAdapter(adapter);
                    if(state != null) {
                        listView.onRestoreInstanceState(state);
                    }

                    adapter.setLeagues(currentYearLeague);
                    adapter.notifyDataSetChanged();

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            League selectedLeague = new League();
                            selectedLeague = (League) adapter.getItem(position);
                            League finalSelectedLeague = selectedLeague;

                            gameArrayList = (ArrayList<Game>) appDatabase.getCheckedGamesByLatestDate2(finalSelectedLeague.getLeagueId(), finalSelectedLeague.getSeason());

                            try{
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                                maxDate = Objects.requireNonNull(gameArrayList.stream()
                                        .map(d -> LocalDate.parse(d.getDate(), dtf))
                                        .max(Comparator.comparing(LocalDate::toEpochDay))
                                        .orElse(null)).format(dtf);

                                String date = maxDate;
                                if (date != null){

                                    PasswordsFragment passwordsFragment = new PasswordsFragment();
                                    Bundle args = new Bundle();
                                    args.putInt("isInvinsible", isInvinsible);
                                    args.putInt("leagueId", finalSelectedLeague.getLeagueId());
                                    args.putInt("season", finalSelectedLeague.getSeason());
                                    args.putString("league", finalSelectedLeague.getName());
                                    args.putString("maxDate", date);
                                    passwordsFragment.setArguments(args);
                                    appViewModel.addFragment(passwordsFragment, view);

                                    adapter.notifyDataSetChanged();
                                    //allPasswordsLeagues.clear(); // clear list view after swipe refresh
                                }

                            }catch(NullPointerException e ){

                            }

                        }

                    });
                    break;

                }else{

                }

            }

        }catch(IndexOutOfBoundsException e){}



        recyclerView.setAdapter(yearRecyclerViewAdapter); // set the Adapter to RecyclerView

        yearRecyclerViewAdapter.setClickListener(new YearRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ArrayList<League> currentYearLeague = new ArrayList<>();

                //currentYearLeague.clear();
                maxDates.clear();

                mySwipeRefreshLayout.setOnRefreshListener(() -> {
                    appViewModel.getTodayUsername();
                    onCompletion();
                });

                checkedGamesBySeason = appDatabase.getAllCheckedGamesBySeason(yearRecyclerViewAdapter.getItem(position));
                int season = yearRecyclerViewAdapter.getItem(position);

                for(League league: allPasswordLeagues) {
                    //league.setSeason(years.get(count));
                    if (league.getSeason() == season) {
                        currentYearLeague.add(league);
                    }
                }

                for(League league: currentYearLeague) {
                    //league.setSeason(yearRecyclerViewAdapter.getItem(position));
                    gameArrayList = (ArrayList<Game>) appDatabase.getCheckedGamesByLatestDate2(league.getLeagueId(), league.getSeason());
                    try{
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                        maxDate = Objects.requireNonNull(gameArrayList.stream()
                                .map(d -> LocalDate.parse(d.getDate(), dtf))
                                .max(Comparator.comparing(LocalDate::toEpochDay))
                                .orElse(null)).format(dtf);

                        maxDates.add(maxDate);

                    }catch(NullPointerException e ){

                    }
                }

                //make all password league season year equal to yearRecyclerViewAdapter item click

                adapter = new LeagueAdapter(getActivity(), currentYearLeague, R.layout.league_activity_rows, 1, maxDates);
                listView.setAdapter(adapter);
                if(state != null) {
                    listView.onRestoreInstanceState(state);
                }
                adapter.setLeagues(currentYearLeague);
                adapter.notifyDataSetChanged();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        League selectedLeague = new League();
                        selectedLeague = (League) adapter.getItem(position);
                        League finalSelectedLeague = selectedLeague;

                        gameArrayList = (ArrayList<Game>) appDatabase.getCheckedGamesByLatestDate2(selectedLeague.getLeagueId(), selectedLeague.getSeason());
                        try{
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                            maxDate = Objects.requireNonNull(gameArrayList.stream()
                                    .map(d -> LocalDate.parse(d.getDate(), dtf))
                                    .max(Comparator.comparing(LocalDate::toEpochDay))
                                    .orElse(null)).format(dtf);

                            String date = maxDate;
                            if (date != null){

                                PasswordsFragment passwordsFragment = new PasswordsFragment();
                                Bundle args = new Bundle();
                                args.putInt("isInvinsible", isInvinsible);
                                args.putInt("leagueId", finalSelectedLeague.getLeagueId());
                                args.putInt("season", finalSelectedLeague.getSeason());
                                args.putString("league", finalSelectedLeague.getName());
                                args.putString("maxDate", date);
                                passwordsFragment.setArguments(args);
                                appViewModel.addFragment(passwordsFragment, view);
                                adapter.notifyDataSetChanged();
                                //allPasswordsLeagues.clear(); // clear list view after swipe refresh
                            }
                        }catch(NullPointerException e ){

                        }
                    }
                });


            }
        });
    }

    //@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint(" leagues ");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if(state != null) {
                    listView.onRestoreInstanceState(state);
                }
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //myUpdateOperation();
                if(state != null) {
                    listView.onRestoreInstanceState(state);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
                searchMenuItem = item;
                searchView = (SearchView) MenuItemCompat.getActionView(item);
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
                searchView.setIconifiedByDefault(false);
                searchView.setQueryHint(" leagues ");
                searchView.requestFocus();


                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.getFilter().filter(newText);
                        return false;
                    }
                });

                searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {

                        return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
                    }
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {

                        return true; // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
                    }
                });
        }
        super.onOptionsItemSelected(item);
        return false;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.options_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView=(SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint(" leagues ");
    }

    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        this.currentFirstVisibleItem = firstVisibleItem;
        this.currentVisibleItemCount = visibleItemCount;
        this.currentTotalItemCount = totalItemCount;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.currentScrollState = scrollState;
        this.isScrollCompleted();
    }

    private void isScrollCompleted() {
        if (currentFirstVisibleItem + currentVisibleItemCount >= currentTotalItemCount) {
            if (this.currentVisibleItemCount > 0
                    && this.currentScrollState == SCROLL_STATE_IDLE) {
            }
        }
    }

    public void onCompletion() {
        if (mySwipeRefreshLayout.isRefreshing()) {
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onResume(){
        //adapter.notifyDataSetChanged();
        onSaveInstanceState(new Bundle());
        onRestoreInstanceState(new Bundle());
        super.onResume();
    }
    //@Override
    public void onRestoreInstanceState(Bundle Bstate) {
        state = Bstate.getParcelable(LIST_STATE);
        if(state != null) {
            listView.onRestoreInstanceState(state);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle Bstate) {
        super.onSaveInstanceState(Bstate);
        state = listView.onSaveInstanceState();
        Bstate.putParcelable(LIST_STATE, state);
        Bstate.putSerializable("list", (Serializable) checkedGamesBySeason);

        if(state != null) {
            listView.onRestoreInstanceState(state);
        }
    }



    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getActivity(), "You clicked " + yearRecyclerViewAdapter.getItem(position) + " on item position " + position, Toast.LENGTH_SHORT).show();
    }
}