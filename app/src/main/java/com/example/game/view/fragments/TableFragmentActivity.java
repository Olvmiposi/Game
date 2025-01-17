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
import com.example.game.businessLogic.TableBL;
import com.example.game.model.Game;
import com.example.game.model.League;
import com.example.game.repository.AppDatabase;
import com.example.game.view.MainActivity;
import com.example.game.viewModel.AppViewModel;

import java.util.ArrayList;


public class TableFragmentActivity  extends Fragment implements YearRecyclerViewAdapter.ItemClickListener{
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private AppViewModel appViewModel;
    private LeagueAdapter adapter;
    private YearRecyclerViewAdapter yearRecyclerViewAdapter;
    int currentFirstVisibleItem, currentVisibleItemCount, currentTotalItemCount, currentScrollState;
    private ArrayList<Integer> years, allGamesLeaguesId;
    private ArrayList<Game> gameArrayList, checkedGamesBySeason;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ListView listView;
    private ArrayList<Game> games;
    private LinearLayoutManager linearLayoutManager;
    private static final String LIST_STATE = "listState";
    private static final String recyclerviewState = "recyclerviewState";
    private RecyclerView recyclerView;

    private AppDatabase appDatabase;
    private String maxDate, baseUrl;
    private Parcelable state = null;
    private ArrayList<League> allGamesLeagues;

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
        onCompletion();

        // get the reference of RecyclerView
        // set a LinearLayoutManager with default horizontal orientation and false value for reverseLayout to show the items from start to end
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);



        if(state != null) {
            listView.onRestoreInstanceState(state);
        }


        //get all league id from available games
        allGamesLeaguesId = new ArrayList<>();


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
        maxDate = null;
        ArrayList<String> maxDates = new ArrayList<>();
        ArrayList<Integer> leagueIds = new ArrayList<>();
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

                    mySwipeRefreshLayout.setOnRefreshListener(() -> {
                        for (League league: allGamesLeagues) {
                            new Thread(() -> {
                                try {
                                    TableBL tableBL = new TableBL(appDatabase);
                                    tableBL.SeedTableDB(league.getLeagueId(), league.getSeason());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        }
                        onCompletion();
                    });

                    for(League league: allGamesLeagues) {
                        try{
                            maxDates.add(league.getEnd());
                        }catch(NullPointerException e ){

                        }
                    }
                    adapter = new LeagueAdapter(getActivity(), allGamesLeagues, R.layout.league_activity_rows, 3, maxDates);
                    listView.setAdapter(adapter);
                    adapter.setLeagues(allGamesLeagues);
                    adapter.notifyDataSetChanged();
                    if(state != null) {
                        listView.onRestoreInstanceState(state);
                    }
                    //onCompletion();
                    listView.setTextFilterEnabled(true);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            League selectedLeague = new League();
                            selectedLeague = (League) adapter.getItem(position);

                            TableFragment tableFragment = new TableFragment();
                            Bundle args = new Bundle();
                            args.putString("baseUrl", baseUrl);
                            args.putInt("season", selectedLeague.getSeason());
                            args.putInt("leagueId", selectedLeague.getLeagueId());
                            tableFragment.setArguments(args);
                            appViewModel.replaceFragment(tableFragment, view);
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

                ArrayList<League> currentYearLeague = new ArrayList<>();
                ArrayList<Integer> leagueIds = new ArrayList<>();

                maxDates.clear();
                allGamesLeagues.clear();

                int season = yearRecyclerViewAdapter.getItem(position);

                leagueIds = appDatabase.getDistinctLeagueBySeason(season);
                for (int leagueId:leagueIds) {
                    allGamesLeagues.add(appDatabase.getLeagueByIdAndSeason(leagueId,season));
                }


                mySwipeRefreshLayout.setOnRefreshListener(() -> {
                    for (League league: allGamesLeagues) {
                        new Thread(() -> {
                            try {
                                TableBL tableBL = new TableBL(appDatabase);
                                tableBL.SeedTableDB(league.getLeagueId(), league.getSeason());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                    onCompletion();
                });

                for(League league: allGamesLeagues) {
                    try{
                        maxDates.add(league.getEnd());

                    }catch(NullPointerException e ){

                    }
                }

                //make all password league season year equal to yearRecyclerViewAdapter item click

                adapter = new LeagueAdapter(getActivity(), allGamesLeagues, R.layout.league_activity_rows, 3, maxDates);
                listView.setAdapter(adapter);
                adapter.setLeagues(allGamesLeagues);
                adapter.notifyDataSetChanged();
                if(state != null) {
                    listView.onRestoreInstanceState(state);
                }
                onCompletion();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        League selectedLeague = new League();
                        selectedLeague = (League) adapter.getItem(position);

                        TableFragment tableFragment = new TableFragment();
                        Bundle args = new Bundle();
                        args.putString("baseUrl", baseUrl);
                        args.putInt("season", selectedLeague.getSeason());
                        args.putInt("leagueId", selectedLeague.getLeagueId());
                        tableFragment.setArguments(args);
                        appViewModel.replaceFragment(tableFragment, view);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });



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