package com.example.game.view.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
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
import com.example.game.adapter.PasswordAdapter;
import com.example.game.model.Game;
import com.example.game.repository.AppDatabase;
import com.example.game.view.ActiveActivitiesTracker;
import com.example.game.view.MainActivity;
import com.example.game.viewModel.AppViewModel;
import com.example.game.service.IOnBackPressed;

import java.util.ArrayList;

public class PasswordsFragment extends Fragment implements IOnBackPressed, AbsListView.OnScrollListener  {
    private AppViewModel appViewModel;
    private AppDatabase appDatabase;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private PasswordAdapter adapter;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    int currentFirstVisibleItem, currentVisibleItemCount, currentTotalItemCount, currentScrollState;
    private String maxDate, league, baseUrl ;
    private int isInvinsible;
    private TextView textView, highest;

    private Button showMore;
    private Parcelable state;
    private Bundle bundle;
    private final ArrayList<Game> gamesList = new ArrayList<Game>();
    private int leagueId, season;
    private ListView todayUsernameList_View;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.passwords_activity, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bundle = getArguments();
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        baseUrl = bundle.getString("baseUrl");
        appViewModel.setBaseUrl(baseUrl);
        appViewModel.init(getContext(), baseUrl);
        appDatabase = AppDatabase.getAppDb(getContext());
        todayUsernameList_View  = requireView().findViewById(R.id.todayUsernameList_View);
        mySwipeRefreshLayout = requireView().findViewById(R.id.swiperefresh);
        textView = requireView().findViewById(R.id.textView);
        showMore = requireView().findViewById(R.id.showMore);

        ((MainActivity) requireActivity()).disableSwipe();

        isInvinsible = bundle.getInt("isInvinsible", 0);
        leagueId = bundle.getInt("leagueId", 0);
        season = bundle.getInt("season", 0);
        league =  bundle.getString("league");
        maxDate = bundle.getString("maxDate");

        Toolbar mToolbar;
        setHasOptionsMenu(true);
        mToolbar = (Toolbar) requireView().findViewById(R.id.toolbar_home);
        if (mToolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);
        }
        mToolbar.setTitle(null);

        mToolbar.inflateMenu(R.menu.options_menu);

        showMore.setText("Load More...");

        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Starting a new async task
                getAllCheckedGamesByLeagueId();
            }
        });

        textView.setText(league);

        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            appViewModel.getTodayUsername();
        });

        //get today games//
        getTodayCheckedGames();

        todayUsernameList_View.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (todayUsernameList_View== null || todayUsernameList_View.getChildCount() == 0) ? 0 : todayUsernameList_View.getChildAt(0).getTop();
                mySwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });
        
    }


    public void getTodayCheckedGames(){
        appDatabase.getCheckedGamesByDate(maxDate, leagueId ).observe(getViewLifecycleOwner() , games -> {
            if (games != null) {
                adapter = new PasswordAdapter(getActivity(), (ArrayList<Game>) games, R.layout.passwords_rows, isInvinsible, baseUrl);
                todayUsernameList_View.setAdapter(adapter);
                adapter.setGames((ArrayList<Game>) games);
                if(state != null) {
                    todayUsernameList_View.onRestoreInstanceState(state);
                }
                adapter.notifyDataSetChanged();
                onCompletion();
            }else{
                todayUsernameList_View.setAdapter(null);
            }

        });
    }
    public void getAllCheckedGamesByLeagueId(){
        appDatabase.getAllCheckedGamesByLeagueId(leagueId, season).observe(this, games -> {
            adapter = new PasswordAdapter(getActivity(), (ArrayList<Game>) games, R.layout.passwords_rows, isInvinsible, baseUrl);
            todayUsernameList_View.setAdapter(adapter);
            adapter.setGames((ArrayList<Game>) games);
            // Restore previous state (including selected item index and scroll position)
            if(state != null) {
                todayUsernameList_View.onRestoreInstanceState(state);
            }
            adapter.notifyDataSetChanged();
            onCompletion();
        });
    }
    public void onCompletion() {
        if (mySwipeRefreshLayout.isRefreshing()) {
            mySwipeRefreshLayout.setRefreshing(false);
        }
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
        searchView.setQueryHint("MM/DD/YYYY, password, club");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
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
                searchView.setQueryHint("MM/DD/YYYY, password, club");
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
        searchView.setQueryHint("MM/DD/YYYY, password, club");
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
    public void onPause(){
        // Save ListView state @ onPause
        state = todayUsernameList_View.onSaveInstanceState();
        super.onPause();
    }
    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onRefresh() {

    }
    @Override
    public void onBackPressed() {
//        appViewModel.backstackFragment(getView());
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
}