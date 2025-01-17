package com.example.game.view.fragments;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.game.R;
import com.example.game.model.Game;
import com.example.game.model.SearchString;
import com.example.game.repository.AppDatabase;
import com.example.game.service.IOnBackPressed;
import com.example.game.view.ActiveActivitiesTracker;
import com.example.game.view.MainActivity;
import com.example.game.viewModel.AppViewModel;
import com.example.game.viewModel.SchrodingerViewModel;
import com.example.game.adapter.SchrodingerAdapter;
import com.example.game.adapter.SearchAdapter;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class SchrodingerFragment extends Fragment implements IOnBackPressed, AbsListView.OnScrollListener {
    private SchrodingerViewModel schrodingerViewModel;
    private AppViewModel appViewModel;
    private SchrodingerAdapter adapter;
    private ListView schrodinger_ListView, recentSearch;
    private AppDatabase appDatabase;
    private SearchString searchString;
    private static final String LIST_STATE = "listState";
    int currentFirstVisibleItem, currentVisibleItemCount, currentTotalItemCount, currentScrollState;
    private Parcelable state = null;
    private int lastViewedPosition, topOffset, leagueId, season, isInvinsible;
    private SearchView searchView;
    private Menu menu;
    private MenuItem searchMenuItem;
    private ProgressBar progressBar;
    private ArrayList<Game> myList = new ArrayList<Game>();
    private ArrayList<Game> newGames = new ArrayList<Game>();
    private Menu mOptionsMenu;
    private Bundle bundle;
    private ArrayList<SearchString> strings;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private Button showMore;
    private int layout;
    private SearchAdapter searchAdapter;
    private String currentDateTime, latestDate, baseUrl;

    public static SchrodingerFragment newInstance() {
        return new SchrodingerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schrodinger, container, false);

        Toolbar mToolbar;
        setHasOptionsMenu(true);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar_home);
        if (mToolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);
        }
        mToolbar.setTitle(null);

        mToolbar.inflateMenu(R.menu.options_menu);
        mOptionsMenu = mToolbar.getMenu();
        return rootView;
        //return inflater.inflate(R.layout.fragment_schrodinger, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bundle = getArguments();
        baseUrl = bundle.getString("baseUrl");
        schrodingerViewModel = new ViewModelProvider(this).get(SchrodingerViewModel.class);
        schrodingerViewModel.init(getContext(), baseUrl);
        appDatabase = AppDatabase.getAppDb(getContext());

        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);

        appViewModel.setBaseUrl(baseUrl);
        appViewModel.init(getContext(), baseUrl);

        schrodinger_ListView  = requireView().findViewById(R.id.schrodinger_ListView);

        recentSearch = requireView().findViewById(R.id.recentSearch);
        progressBar = requireView().findViewById(R.id.progressBar);
        showMore = requireView().findViewById(R.id.showMore);

        progressBar.setVisibility(View.GONE);
        recentSearch.setVisibility(View.GONE);

        mySwipeRefreshLayout = requireView().findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            schrodingerViewModel.getSchrodingerGames();
        });

        ((MainActivity) getActivity()).disableSwipe();

        state = savedInstanceState;



        searchString = new SearchString();
        strings = new ArrayList<>();
        strings = appDatabase.getSearchString();
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        currentDateTime = dateFormat.format(currentDate);

        leagueId = bundle.getInt("leagueId", 0);
        season = bundle.getInt("season", 0);
        isInvinsible = bundle.getInt("isInvinsible", 0);
        latestDate =  bundle.getString("latestDate");


        getTodaySchrodingerGames();
        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myUpdateOperation();
            }
        });



        //handleSearch(getActivity().getIntent());



        schrodinger_ListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (schrodinger_ListView== null || schrodinger_ListView.getChildCount() == 0) ? 0 : schrodinger_ListView.getChildAt(0).getTop();
                mySwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        recentSearch.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (recentSearch== null || recentSearch.getChildCount() == 0) ? 0 : recentSearch.getChildAt(0).getTop();
                mySwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });


    }
    @Override
    public void onRefresh() {
        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            schrodingerViewModel.getSchrodingerGames();
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
        mOptionsMenu = menu;
        this.menu = menu;

        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("username or club");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchString.setUsername(query);
                searchString.setDate(currentDateTime);
                appDatabase.addSearchString(searchString);

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
                recentSearch.setVisibility(View.VISIBLE);
                searchAdapter = new SearchAdapter(getActivity(), strings, mOptionsMenu, baseUrl);
                recentSearch.setAdapter(searchAdapter);
                recentSearch.setClickable(true);
                searchAdapter.setStrings(strings);
                //searchAdapter.notifyDataSetChanged();

                return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                recentSearch.setVisibility(View.GONE);
                int isInvinsible = bundle.getInt("isInvinsible", 0);
                leagueId = bundle.getInt("leagueId", 0);
                myList = (ArrayList<Game>) appDatabase.getSchrodingerGamesList(leagueId);
                adapter = new SchrodingerAdapter((AppCompatActivity) getActivity(), (ArrayList<Game>) myList, R.layout.schrodinger_activity_rows, isInvinsible, baseUrl);

                schrodinger_ListView.setAdapter(adapter);
                adapter.setGames((ArrayList<Game>) myList);
                onCompletion();
                return true; // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
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
                searchView.setQueryHint("username or club");

                int isInvinsible = bundle.getInt("isInvinsible", 0);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        searchString.setUsername(query);
                        searchString.setDate(currentDateTime);
                        appDatabase.addSearchString(searchString);

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
                        recentSearch.setVisibility(View.VISIBLE);
                        searchAdapter = new SearchAdapter(getActivity(), strings, mOptionsMenu, baseUrl);
                        recentSearch.setAdapter(searchAdapter);
                        recentSearch.setClickable(true);
                        searchAdapter.setStrings(strings);
                        //searchAdapter.notifyDataSetChanged();

                        return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
                    }
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        recentSearch.setVisibility(View.GONE);

                        int isInvinsible = bundle.getInt("isInvinsible", 0);
                        leagueId = bundle.getInt("leagueId", 0);
                        myList = (ArrayList<Game>) appDatabase.getSchrodingerGamesList(leagueId);
                                        adapter = new SchrodingerAdapter((AppCompatActivity) getActivity(), (ArrayList<Game>) myList, R.layout.schrodinger_activity_rows, isInvinsible, baseUrl);

                        schrodinger_ListView.setAdapter(adapter);
                        adapter.setGames((ArrayList<Game>) myList);
                        onCompletion();

                        return true; // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
                    }
                });

                break;
            default:
                break;
        }
        super.onOptionsItemSelected(item);
        return false;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        requireActivity().getMenuInflater().inflate(R.menu.options_menu, menu);
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView=(SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setQueryHint("username or club");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchString.setUsername(query);
                searchString.setDate(currentDateTime);
                appDatabase.addSearchString(searchString);

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
                recentSearch.setVisibility(View.VISIBLE);
                searchAdapter = new SearchAdapter(getActivity(), strings, mOptionsMenu, baseUrl);
                recentSearch.setAdapter(searchAdapter);
                recentSearch.setClickable(true);
                searchAdapter.setStrings(strings);
                //searchAdapter.notifyDataSetChanged();

                return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                recentSearch.setVisibility(View.GONE);

                int isInvinsible = bundle.getInt("isInvinsible", 0);
                leagueId = bundle.getInt("leagueId", 0);
                myList = (ArrayList<Game>) appDatabase.getSchrodingerGamesList(leagueId);
                adapter = new SchrodingerAdapter((AppCompatActivity) getActivity(), (ArrayList<Game>) myList, R.layout.schrodinger_activity_rows, isInvinsible, baseUrl);

                schrodinger_ListView.setAdapter(adapter);
                adapter.setGames((ArrayList<Game>) myList);
                onCompletion();

                return true; // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
            }
        });

    }

    public static List<Game> moveNullsToEnd(final List<Game> games) {

        final List<Game> newGames = new ArrayList<Game>();
        final List<Game> nullGames = new ArrayList<Game>();

        for (Game game : games) {
            if (!Objects.equals(game.getHome(), "null")) {
                newGames.add(game);
            }else{
                nullGames.add(game);
            }
        }
        newGames.addAll(nullGames);

        return newGames;
    }

    public void getTodaySchrodingerGames(){
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String currentDateTime = dateFormat.format(currentDate);

        appDatabase.getSchrodingerGamesByDate(currentDateTime, leagueId).observe(getViewLifecycleOwner() , games -> {
            if (!games.isEmpty()) {
                adapter = new SchrodingerAdapter(getActivity(), (ArrayList<Game>) games, R.layout.schrodinger_activity_rows, isInvinsible,baseUrl);
                schrodinger_ListView.setAdapter(adapter);
                if(state != null) {
                    schrodinger_ListView.onRestoreInstanceState(state);
                }
                adapter.setGames((ArrayList<Game>) games);
                adapter.notifyDataSetChanged();
                onCompletion();
            }else {
                appDatabase.getSchrodingerGamesByDate(latestDate, leagueId).observe(getViewLifecycleOwner() , games1 -> {
                    if (games1 != null) {
                        adapter = new SchrodingerAdapter(getActivity(), (ArrayList<Game>) games1, R.layout.schrodinger_activity_rows, isInvinsible,baseUrl);
                        schrodinger_ListView.setAdapter(adapter);
                        if(state != null) {
                            schrodinger_ListView.onRestoreInstanceState(state);
                        }
                        adapter.setGames((ArrayList<Game>) games1);
                        adapter.notifyDataSetChanged();
                        onCompletion();
                    }else{
                        schrodinger_ListView.setAdapter(null);
                    }

                });
            }

        });
    }

    public void myUpdateOperation(){
        appDatabase.getldSchrodingerGamesList(leagueId).observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                if(games != null){

                    adapter = new SchrodingerAdapter(getActivity(), (ArrayList<Game>) games, R.layout.schrodinger_activity_rows, isInvinsible, baseUrl);
                    schrodinger_ListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    if(state != null) {
                        schrodinger_ListView.onRestoreInstanceState(state);
                    }
                    adapter.setGames((ArrayList<Game>) games);
                    onCompletion();
                }else {
                    schrodinger_ListView.setAdapter(null);
                    if(state != null) {
                        schrodinger_ListView.onRestoreInstanceState(state);
                    }
                }
            }
        });


    }
    public void onCompletion() {
        if (mySwipeRefreshLayout.isRefreshing()) {
            mySwipeRefreshLayout.setRefreshing(false);
        }
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
    public void onSaveInstanceState(Bundle Bstate) {
        state = schrodinger_ListView.onSaveInstanceState();
        Bstate.putParcelable(LIST_STATE, state);
        super.onSaveInstanceState(Bstate);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onBackPressed() {

    }

}