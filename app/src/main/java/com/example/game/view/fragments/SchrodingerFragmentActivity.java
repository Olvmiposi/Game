package com.example.game.view.fragments;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class SchrodingerFragmentActivity  extends Fragment implements YearRecyclerViewAdapter.ItemClickListener{
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private AppViewModel appViewModel;
    private LeagueAdapter adapter;
    private YearRecyclerViewAdapter yearRecyclerViewAdapter;
    int currentFirstVisibleItem, currentVisibleItemCount, currentTotalItemCount, currentScrollState;
    private ArrayList<Integer> years;
    private ArrayList<Game> gameArrayList, checkedGamesBySeason;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ListView listView;
    private ArrayList<Game> games;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Integer> allGamesLeaguesId, allSchrodingerLeaguesId, homeScore, awayScore;
    private ArrayList<League> allGamesLeagues;

    private static final String LIST_STATE = "listState";
    private static final String recyclerviewState = "recyclerviewState";
    private RecyclerView recyclerView;

    private Button table, allGames, password, schrodinger;
    private AppDatabase appDatabase;
    private String maxDate, latestDate, baseUrl;
    private Parcelable state = null;
    private int isInvinsible;
    private Bundle bundle;
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
        bundle = getArguments();
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        baseUrl = bundle.getString("baseUrl");
        appViewModel.setBaseUrl(baseUrl);
        appViewModel.init(getContext(), baseUrl);
        appDatabase = AppDatabase.getAppDb(getContext());
        mySwipeRefreshLayout = requireView().findViewById(R.id.swiperefresh);
        listView  = requireView().findViewById(R.id.list_view);
        recyclerView = requireView().findViewById(R.id.rvYears);

        progressBar = requireView().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        ((MainActivity) requireActivity()).disableSwipe();
        //((AppCompatActivity) requireActivity()).getSupportActionBar().hide();


        Toolbar mToolbar;
        setHasOptionsMenu(true);
        mToolbar = (Toolbar) requireView().findViewById(R.id.toolbar_home);
        if (mToolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);
        }
        mToolbar.setTitle(null);

        mToolbar.inflateMenu(R.menu.options_menu);


        isInvinsible =  this.getArguments().getInt("isInvinsible", 0);
        //latestDate = bundle.getString("latestDate");
        allGamesLeaguesId = new ArrayList<>();
        allGamesLeagues = new ArrayList<>();
        years = new ArrayList<>();
        years.add(2025);
        years.add(2024);
        years.add(2023);
        years.add(2022);
        years.add(2021);
        years.add(2020);
        years.add(2019);
        years.add(2018);

        myUpdateOperation();
        // get the reference of RecyclerView
        // set a LinearLayoutManager with default horizontal orientation and false value for reverseLayout to show the items from start to end
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        if(state != null) {
            listView.onRestoreInstanceState(state);
        }

        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            appViewModel.getSchrodingerGames();
            onCompletion();
        });

        allSchrodingerLeaguesId = new ArrayList<>();

        

        // for api 3001 i.e 2024api

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

    //@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
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
                        try {
                            adapter.getFilter().filter(newText);
                        }catch (NullPointerException e){

                        }
                        return false;
                    }
                });

                searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        if(state != null) {
                            listView.onRestoreInstanceState(state);
                        }
                        return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
                    }
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        if(state != null) {
                            listView.onRestoreInstanceState(state);
                        }
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



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void myUpdateOperation(){
        ArrayList<String> maxDates = new ArrayList<>();
        ArrayList<Integer> leagueIds = new ArrayList<>();

        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            appViewModel.getSchrodingerGames();
            onCompletion();
        });
        yearRecyclerViewAdapter = new YearRecyclerViewAdapter(getActivity(), years);


        //while true, look for the latest checked games from the years
        // if latest game is found, break
        //////////////////////////////////////////////////////////
        try{
            int count = -1;
            while(true) {
                count++;

                int season = yearRecyclerViewAdapter.getItem(count);
                leagueIds = appDatabase.getDistinctLeagueBySeason(season);
                for (int leagueId:leagueIds) {
                    allGamesLeagues.add(appDatabase.getLeagueByIdAndSeason(leagueId,season));
                }

                if(!leagueIds.isEmpty()) {
                    yearRecyclerViewAdapter.setSelected_position(count);

                    for(League league: allGamesLeagues) {
                        gameArrayList = (ArrayList<Game>) appDatabase.getCheckedGamesByLatestDate2(league.getLeagueId(), league.getSeason());
                        try{
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US);

                            maxDate = Objects.requireNonNull(gameArrayList.stream()
                                    .map(d -> LocalDate.parse(String.valueOf(d.getDate()), dtf))
                                    .max(Comparator.comparing(LocalDate::toEpochDay))
                                    .orElse(null)).format(dtf);

                            maxDates.add(maxDate);
                        }catch(NullPointerException e ){

                        }
                    }
                    adapter = new LeagueAdapter(getActivity(), allGamesLeagues, R.layout.league_activity_rows, 2, maxDates);
                    listView.setAdapter(adapter);
                    if(state != null) {
                        listView.onRestoreInstanceState(state);
                    }
                    adapter.setLeagues(allGamesLeagues);
                    adapter.notifyDataSetChanged();
                    //onCompletion();
                    listView.setTextFilterEnabled(true);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            League selectedLeague = new League();
                            selectedLeague = (League) adapter.getItem(position);

                            gameArrayList = (ArrayList<Game>) appDatabase.getSchrodingerByLeagueIdAndSeason(selectedLeague.getLeagueId(), selectedLeague.getSeason());
                            try{
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US);


                                ArrayList<Game> newgames = new ArrayList<>();
                                for (Game game: gameArrayList) {
                                    Date currentDate = new Date();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                    String currentDateTime = dateFormat.format(currentDate);
                                    Date date1 = (Date) dateFormat.parse(currentDateTime);
                                    Date date2 = (Date) dateFormat.parse(String.valueOf(game.getDate()));
                                    if(date1.after(date2)){
                                        System.out.println("Date1 is after Date2");
                                    }else if (date1.before(date2)){
                                        newgames.add(game);
                                    }
                                }
                                latestDate = Objects.requireNonNull(newgames.stream()
                                        .map(d -> LocalDate.parse(String.valueOf(d.getDate()), dtf))
                                        .min(Comparator.comparing(LocalDate::toEpochDay))
                                        .orElse(null)).format(dtf);

                                maxDates.add(latestDate);
                            }catch(NullPointerException | ParseException e ){
                            }


                            SchrodingerFragment schrodingerFragment = getSchrodingerFragment(selectedLeague);
                            appViewModel.replaceFragment(schrodingerFragment, view);

                        }
                    });

                    break;

                }

            }

        }catch(NullPointerException | IndexOutOfBoundsException e){}


        recyclerView.setAdapter(yearRecyclerViewAdapter); // set the Adapter to RecyclerView

        yearRecyclerViewAdapter.setClickListener(new YearRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                ArrayList<Integer> leagueIds = new ArrayList<>();

                maxDates.clear();
                allGamesLeagues.clear();

                int season = yearRecyclerViewAdapter.getItem(position);
                leagueIds = appDatabase.getDistinctLeagueBySeason(season);
                for (int leagueId:leagueIds) {
                    allGamesLeagues.add(appDatabase.getLeagueByIdAndSeason(leagueId,season));
                }


                for(League league: allGamesLeagues) {
                    gameArrayList = (ArrayList<Game>) appDatabase.getCheckedGamesByLatestDate2(league.getLeagueId(), league.getSeason());
                    try{
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US);

                        latestDate = Objects.requireNonNull(gameArrayList.stream()
                                .map(d -> LocalDate.parse(String.valueOf(d.getDate()), dtf))
                                .max(Comparator.comparing(LocalDate::toEpochDay))
                                .orElse(null)).format(dtf);

                        maxDates.add(latestDate);
                    }catch(NullPointerException e ){

                    }
                }

                adapter = new LeagueAdapter(getActivity(), allGamesLeagues, R.layout.league_activity_rows, 2, maxDates);
                listView.setAdapter(adapter);
                if(state != null) {
                    listView.onRestoreInstanceState(state);
                }
                adapter.setLeagues(allGamesLeagues);
                adapter.notifyDataSetChanged();

                listView.setTextFilterEnabled(true);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        League selectedLeague = new League();
                        selectedLeague = (League) adapter.getItem(position);

                        gameArrayList = (ArrayList<Game>) appDatabase.getSchrodingerByLeagueIdAndSeason(selectedLeague.getLeagueId(), selectedLeague.getSeason());
                        try{
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US);


                            ArrayList<Game> newgames = new ArrayList<>();
                            for (Game game: gameArrayList) {
                                Date currentDate = new Date();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                String currentDateTime = dateFormat.format(currentDate);
                                Date date1 = (Date) dateFormat.parse(currentDateTime);
                                Date date2 = (Date) dateFormat.parse(String.valueOf(game.getDate()));
                                if(date1.after(date2)){
                                    System.out.println("Date1 is after Date2");
                                }else if (date1.before(date2)){
                                    newgames.add(game);
                                }
                            }
                            latestDate = Objects.requireNonNull(newgames.stream()
                                    .map(d -> LocalDate.parse(String.valueOf(d.getDate()), dtf))
                                    .min(Comparator.comparing(LocalDate::toEpochDay))
                                    .orElse(null)).format(dtf);

                            maxDates.add(latestDate);
                        }catch(NullPointerException | ParseException e ){
                        }

                        SchrodingerFragment schrodingerFragment = getSchrodingerFragment(selectedLeague);
                        appViewModel.replaceFragment(schrodingerFragment, view);

                    }
                });
            }
        });
    }

    @NonNull
    private SchrodingerFragment getSchrodingerFragment(League selectedLeague) {
        SchrodingerFragment schrodingerFragment = new SchrodingerFragment();
        Bundle args = new Bundle();
        args.putString("baseUrl", baseUrl);
        args.putInt("isInvinsible", isInvinsible);
        args.putInt("season", selectedLeague.getSeason());
        args.putInt("leagueId", selectedLeague.getLeagueId());
        args.putString("latestDate", latestDate);
        schrodingerFragment.setArguments(args);
        return schrodingerFragment;
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
        super.onResume();
    }
    @Override
    public void onSaveInstanceState(Bundle Bstate) {
        super.onSaveInstanceState(Bstate);
        state = listView.onSaveInstanceState();
        Bstate.putParcelable(LIST_STATE, state);
        if(state != null) {
            listView.onRestoreInstanceState(state);
        }
    }
    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getActivity(), "You clicked " + yearRecyclerViewAdapter.getItem(position) + " on item position " + position, Toast.LENGTH_SHORT).show();
    }
}