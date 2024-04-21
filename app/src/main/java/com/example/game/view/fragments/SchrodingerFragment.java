package com.example.game.view.fragments;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.SearchRecentSuggestions;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.game.R;
import com.example.game.helpers.MySuggestionProvider;
import com.example.game.model.Game;
import com.example.game.model.SearchString;
import com.example.game.repository.AppDatabase;
import com.example.game.service.IOnBackPressed;
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
    private SchrodingerAdapter adapter = null;
    private ListView schrodinger_ListView, recentSearch;
    private AppDatabase appDatabase;
    private SearchString searchString;
    private static final String LIST_STATE = "listState";
    int currentFirstVisibleItem, currentVisibleItemCount, currentTotalItemCount, currentScrollState;
    private Parcelable state = null;
    private int lastViewedPosition, topOffset, leagueId;
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
    private int layout;
    private SearchAdapter searchAdapter;
    private String currentDateTime;

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
        schrodingerViewModel = new ViewModelProvider(this).get(SchrodingerViewModel.class);
        schrodingerViewModel.init(getContext());
        appDatabase = AppDatabase.getAppDb(getContext());

        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        appViewModel.init(getContext());

        schrodinger_ListView  = requireView().findViewById(R.id.schrodinger_ListView);

        recentSearch = requireView().findViewById(R.id.recentSearch);
        progressBar = requireView().findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);
        recentSearch.setVisibility(View.GONE);

        mySwipeRefreshLayout = requireView().findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            schrodingerViewModel.getSchrodingerGames();
        });

        ((MainActivity) getActivity()).disableSwipe();

        state = savedInstanceState;

        bundle = getArguments();

        searchString = new SearchString();
        strings = new ArrayList<>();
        strings = appDatabase.getSearchString();
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        currentDateTime = dateFormat.format(currentDate);

        myUpdateOperation(savedInstanceState);


        handleSearch(getActivity().getIntent());

        leagueId = bundle.getInt("leagueId", 0);

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

    public void handleSearch( Intent intent)
    {
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            SearchRecentSuggestions searchRecentSuggestions=new SearchRecentSuggestions(getContext(),
                    MySuggestionProvider.AUTHORITY,MySuggestionProvider.MODE);

            searchRecentSuggestions.saveRecentQuery(query,null);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
        mOptionsMenu = menu;
        this.menu = menu;

        schrodingerViewModel = new ViewModelProvider(this).get(SchrodingerViewModel.class);
        schrodingerViewModel.init(getContext());
        appDatabase = AppDatabase.getAppDb(getContext());
        schrodinger_ListView  = getView().findViewById(R.id.schrodinger_ListView);

        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("username");


        int isInvinsible = bundle.getInt("isInvinsible", 0);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchString.setUsername(query);
                searchString.setDate(currentDateTime);
                schrodingerViewModel.searchSchrodingerOnClick(query);
                appDatabase.addSearchString(searchString);

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
                recentSearch.setVisibility(View.VISIBLE);
                searchAdapter = new SearchAdapter(getActivity(), strings, mOptionsMenu);
                recentSearch.setAdapter(searchAdapter);
                if(state != null) {
                    schrodinger_ListView.onRestoreInstanceState(state);
                }
                recentSearch.setClickable(true);
                searchAdapter.setStrings(strings);
                searchAdapter.notifyDataSetChanged();

                return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                recentSearch.setVisibility(View.GONE);
                myUpdateOperation(new Bundle());
                if(state != null) {
                    schrodinger_ListView.onRestoreInstanceState(state);
                }
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
                searchView.setQueryHint("username");

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        searchString.setUsername(query);
                        searchString.setDate(currentDateTime);
                        schrodingerViewModel.searchSchrodingerOnClick(query);
                        appDatabase.addSearchString(searchString);

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
                        recentSearch.setVisibility(View.VISIBLE);
                        searchAdapter = new SearchAdapter(getActivity(), strings, mOptionsMenu);
                        recentSearch.setAdapter(searchAdapter);
                        recentSearch.setClickable(true);
                        searchAdapter.setStrings(strings);
                        searchAdapter.notifyDataSetChanged();

                        return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
                    }
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        recentSearch.setVisibility(View.GONE);
                        myUpdateOperation(new Bundle());
                        if(state != null) {
                            schrodinger_ListView.onRestoreInstanceState(state);
                        }
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
        searchView.setQueryHint("username");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchString.setUsername(query);
                searchString.setDate(currentDateTime);
                schrodingerViewModel.searchSchrodingerOnClick(query);
                appDatabase.addSearchString(searchString);

                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

    }



    private void updateOptionsMenu() {
        if (mOptionsMenu != null) {
            onPrepareOptionsMenu(mOptionsMenu);
        }
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

//        for (int i = 0, remaining = games.size() - newGames.size(); i < remaining; i++) {
//            newGames.add(games.get(remaining));
//        }

        return newGames;
    }

    public void myUpdateOperation(Bundle savedInstanceState){
        int isInvinsible = bundle.getInt("isInvinsible", 0);
        leagueId = bundle.getInt("leagueId", 0);

        state = null;
        //state = schrodinger_ListView.onSaveInstanceState();

        //if using schrodinger table
        //myList = (ArrayList<Game>) appDatabase.getSchrodingerGamesListByLeagueId(leagueId);


        myList = (ArrayList<Game>) appDatabase.getSchrodingerGamesList(leagueId);


        adapter = new SchrodingerAdapter(getActivity(), myList, R.layout.schrodinger_activity_rows, isInvinsible);
        schrodinger_ListView.setAdapter(adapter);
        // Restore previous state (including selected item index and scroll position)
        if(state != null) {
            schrodinger_ListView.onRestoreInstanceState(state);
        }
        adapter.setGames((ArrayList<Game>) myList);
        //adapter.notifyDataSetChanged();
        onCompletion();


        schrodingerViewModel.getSearchSchrodingerResponse().observe(getViewLifecycleOwner(), games -> {
            if(games != null){
                newGames = (ArrayList<Game>)moveNullsToEnd(games);
                adapter = new SchrodingerAdapter(getActivity(), newGames , R.layout.schrodinger_rows, isInvinsible);
                schrodinger_ListView.setAdapter(adapter);
                // Restore previous state (including selected item index and scroll position)

                if(state != null) {
                    schrodinger_ListView.onRestoreInstanceState(state);
                    //onRestoreInstanceState(bundle);
                }
                adapter.setGames(newGames);
                //adapter.notifyDataSetChanged();
            }else{
                schrodinger_ListView.setAdapter(null);
                if(state != null) {
                    schrodinger_ListView.onRestoreInstanceState(state);
                    //onRestoreInstanceState(bundle);
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
        //ActiveActivitiesTracker.activityStarted(this.getBaseContext());
    }
    @Override
    public void onStop() {
        super.onStop();
        //ActiveActivitiesTracker.activityStopped();
    }
    //@Override
    protected void onRestoreInstanceState(Bundle Bstate) {
        //super.onRestoreInstanceState(Bstate);
        state = Bstate.getParcelable(LIST_STATE);
    }
    @Override
    public void onSaveInstanceState(Bundle Bstate) {
        super.onSaveInstanceState(Bstate);
        state = schrodinger_ListView.onSaveInstanceState();
        Bstate.putParcelable(LIST_STATE, state);
    }

    @Override
    public void onResume(){
        adapter.notifyDataSetChanged();
        onSaveInstanceState(new Bundle());
        super.onResume();
    }

    //@Override
    public void onRestart() {
        adapter.notifyDataSetChanged();
        //super.onRestart();

    }

    @Override
    public void onBackPressed() {
//        //getActivity().getSupportFragmentManager().popBackStack();
//        appViewModel.backstackFragment(getView());
//        Toast.makeText(getContext(),"SchrodingerFragment back button pressed",Toast.LENGTH_LONG).show();
//        return false;
    }

}