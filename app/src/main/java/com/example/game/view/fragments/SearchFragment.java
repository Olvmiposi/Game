package com.example.game.view.fragments;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.example.game.adapter.SearchAdapter;
import com.example.game.adapter.SearchFragmentAdapter;
import com.example.game.model.Game;
import com.example.game.model.SearchString;
import com.example.game.repository.AppDatabase;
import com.example.game.service.IOnBackPressed;
import com.example.game.view.ActiveActivitiesTracker;
import com.example.game.view.MainActivity;
import com.example.game.viewModel.AppViewModel;
import com.example.game.viewModel.SchrodingerViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class SearchFragment extends Fragment implements IOnBackPressed, AbsListView.OnScrollListener {
    private SchrodingerViewModel schrodingerViewModel;
    private AppViewModel appViewModel;
    //private SchrodingerAdapter adapter;

    private SearchFragmentAdapter adapter;
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
    private TextView textView;
    private ProgressBar progressBar;
    private ArrayList<Game> myList = new ArrayList<Game>();
    private ArrayList<Game> newGames = new ArrayList<Game>();
    private ArrayList<Game> gameToRemove = new ArrayList<Game>();

    private Menu mOptionsMenu;
    private Bundle bundle;
    private ArrayList<SearchString> strings;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private int layout, isInvinsible;
    private SearchAdapter searchAdapter;
    private String currentDateTime, baseUrl;;
    private Button showMore;

    public static SearchFragment newInstance() {
        return new SearchFragment();
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
        setRetainInstance(true);

        bundle = getArguments();
        schrodingerViewModel = new ViewModelProvider(this).get(SchrodingerViewModel.class);
        baseUrl = bundle.getString("baseUrl");
        schrodingerViewModel.init(getContext(), baseUrl);
        appDatabase = AppDatabase.getAppDb(getContext());
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        baseUrl = bundle.getString("baseUrl");
        appViewModel.setBaseUrl(baseUrl);
        appViewModel.init(getContext(), baseUrl);

        schrodinger_ListView = requireView().findViewById(R.id.schrodinger_ListView);
        textView = requireView().findViewById(R.id.textView);
        recentSearch = requireView().findViewById(R.id.recentSearch);
        progressBar = requireView().findViewById(R.id.progressBar);
        showMore = requireView().findViewById(R.id.showMore);

        progressBar.setVisibility(View.GONE);
        recentSearch.setVisibility(View.GONE);
        showMore.setVisibility(View.GONE);

        mySwipeRefreshLayout = requireView().findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            schrodingerViewModel.getSchrodingerGames();
        });

        ((MainActivity) getActivity()).disableSwipe();

        state = savedInstanceState;


        isInvinsible = bundle.getInt("isInvinsible", 0);


        searchString = new SearchString();
        strings = new ArrayList<>();
        strings = appDatabase.getSearchString();
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        currentDateTime = dateFormat.format(currentDate);

        textView.setText("Search Password");

        myUpdateOperation();


        //handleSearch(getActivity().getIntent());

        leagueId = bundle.getInt("leagueId", 0);

        schrodinger_ListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (schrodinger_ListView == null || schrodinger_ListView.getChildCount() == 0) ? 0 : schrodinger_ListView.getChildAt(0).getTop();
                mySwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        recentSearch.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (recentSearch == null || recentSearch.getChildCount() == 0) ? 0 : recentSearch.getChildAt(0).getTop();
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
                searchView.setQueryHint("username or club");
                requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

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
                        try {
                            if (newText.length() > 1){
                                searchUsername(newText);
                                adapter.getFilter().filter(newText);
                            }
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
                        return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
                    }
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        recentSearch.setVisibility(View.GONE);
                        return true; // OR FALSE IF YOU DON'T WANT IT TO CLOSE!
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
                try {
                    if (newText.length() > 1){
                        searchUsername(newText);
                        adapter.getFilter().filter(newText);
                    }
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

                return true; // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
            }
        });
    }

    public void searchUsername(String strings){
        myList = appDatabase.searchUsernames(strings+"%");
        for (Game game: myList) {
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String currentDateTime = dateFormat.format(currentDate);

            try{
                Date date1 = (Date) dateFormat.parse(currentDateTime);
                Date date2 = (Date) dateFormat.parse(String.valueOf(game.getDate()));

                if(date1.after(date2) && game.getChecked() != 1){
                    gameToRemove.add(game);
                    System.out.println("Date1 is after Date2");
                }else if (date1.before(date2)){
                    newGames.add(game);
                }

            }catch(Exception e){

            }



        }
        myList.removeAll(gameToRemove);
        adapter = new SearchFragmentAdapter(getActivity(), myList , R.layout.schrodinger_rows, isInvinsible, baseUrl);
        schrodinger_ListView.setAdapter(adapter);
        adapter.setGames(myList);

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

    public void myUpdateOperation(){
        leagueId = bundle.getInt("leagueId", 0);
        schrodingerViewModel.getSearchSchrodingerResponse().observe(getViewLifecycleOwner(), games1 -> {
            if(games1 != null){
                recentSearch.setVisibility(View.GONE);
                textView.setText("Result");
                newGames = (ArrayList<Game>)moveNullsToEnd(games1);
                adapter = new SearchFragmentAdapter(getActivity(), newGames , R.layout.schrodinger_rows, isInvinsible, baseUrl);
                schrodinger_ListView.setAdapter(adapter);
                if(state != null) {
                    schrodinger_ListView.onRestoreInstanceState(state);
                }
                adapter.setGames(newGames);
                onCompletion();
            }else{
                schrodinger_ListView.setAdapter(null);

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