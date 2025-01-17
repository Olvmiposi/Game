package com.example.game.view.fragments;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.game.R;
import com.example.game.adapter.AllGamesAdapter;
import com.example.game.model.Game;
import com.example.game.model.League;
import com.example.game.repository.AppDatabase;
import com.example.game.service.IOnBackPressed;
import com.example.game.view.MainActivity;
import com.example.game.viewModel.AppViewModel;

import java.util.ArrayList;

public class AllGamesFragment extends Fragment implements IOnBackPressed, AbsListView.OnScrollListener {
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private AppViewModel appViewModel;
    private AllGamesAdapter adapter;
    private ArrayList<Game> gameArrayList;
    private Parcelable state = null;
    private static final String LIST_STATE = "listState";
    int currentFirstVisibleItem, currentVisibleItemCount, currentTotalItemCount, currentScrollState;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ListView listView;
    private TextView textView;
    private AppDatabase appDatabase;
    private Bundle bundle;
    private ProgressBar progressBar;
    private Button showMore;
    private String league, latestDate, baseUrl;
    private int leagueId, season;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_all_games_layout, container, false);

        Toolbar mToolbar;
        setHasOptionsMenu(true);
        setRetainInstance(true);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar_home);
        if (mToolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);
        }
        mToolbar.setTitle(null);
        mToolbar.inflateMenu(R.menu.options_menu);

        return rootView;
        //return inflater.inflate(R.layout.activity_all_games_layout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        bundle = getArguments();
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        baseUrl = bundle.getString("baseUrl");
        appViewModel.setBaseUrl(baseUrl);
        appViewModel.init(getContext(), baseUrl);
        appDatabase = AppDatabase.getAppDb(getContext());
        mySwipeRefreshLayout = getView().findViewById(R.id.swiperefresh);
        listView  = getView().findViewById(R.id.searchList_view);
        textView = getView().findViewById(R.id.textView);
        progressBar = getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        showMore = requireView().findViewById(R.id.showMore);
        ((MainActivity) requireActivity()).disableSwipe();
        //((AppCompatActivity) requireActivity()).getSupportActionBar().hide();
        league = bundle.getString("league");
        leagueId = bundle.getInt("leagueId", 0);
        latestDate = bundle.getString("latestDate");
        season = bundle.getInt("season", 0);
        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            League league1 = new League();
            league1.setLeagueId(leagueId);
            league1.setSeason(season);
            appViewModel.getGames(league1);
        });



        getTodayGames();
        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Starting a new async task
                myUpdateOperation();
                onCompletion();

            }
        });

        textView.setText(league);
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
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("club");
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //appViewModel.searchGameOnClick(query);
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
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
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
                searchView.setQueryHint("club");
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
                        return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
                    }
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
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
        getActivity().getMenuInflater().inflate(R.menu.options_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView=(SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint("club");
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

    public void getTodayGames(){
        
        gameArrayList = appDatabase.getGamesByLeagueIdAndDate(leagueId,latestDate);

        adapter = new AllGamesAdapter(getActivity(),gameArrayList, R.layout.search_game_rows, baseUrl);
        listView.setAdapter(adapter);
        adapter.setGames(gameArrayList);
        onCompletion();
    }


    public void myUpdateOperation(){
        int leagueId = bundle.getInt("leagueId", 0);
        int season = bundle.getInt("season", 0);
        gameArrayList = appDatabase.getGamesByLeagueIdAndSeason(leagueId, season );

        adapter = new AllGamesAdapter(getActivity(),gameArrayList, R.layout.search_game_rows, baseUrl);
        listView.setAdapter(adapter);
        adapter.setGames(gameArrayList);
        onCompletion();
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
        adapter.notifyDataSetChanged();
        super.onResume();
    }
    @Override
    public void onRefresh() {
    }
    @Override
    public void onBackPressed() {
    }

}