package com.example.game.view.fragments;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.game.R;
import com.example.game.adapter.LeagueAdapter;
import com.example.game.databinding.CallApiActivityBinding;
import com.example.game.model.CallApiModel;
import com.example.game.model.League;
import com.example.game.repository.AppDatabase;
import com.example.game.service.IOnBackPressed;
import com.example.game.view.MainActivity;
import com.example.game.viewModel.AppViewModel;

import java.util.ArrayList;
import java.util.List;

public class CallApiFragment extends Fragment implements IOnBackPressed {
    private AppViewModel appViewModel;
    private AppDatabase appDatabase;
    private LeagueAdapter adapter;
    private ListView callApiList_View;
    private SearchView searchView;
    private ArrayList<League> favoriteLeagues;
    private ArrayList<Integer> favoriteLeaguesInt;

    private MenuItem searchMenuItem;
    private int leagueId;
    private ImageButton callApi, verifyBtn, createGames, updateLeague;
    private String  from_date, to_date, seasonYear, baseUrl;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ProgressBar progressBar;
    private EditText leagueIdEditText, fromDateEditText, toDateEdittext, seasonYearEdittext;
    private CallApiActivityBinding binding;

    private static final String LIST_STATE = "listState";
    private Parcelable state = null;
    private Bundle bundle;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setRetainInstance(true);

        return inflater.inflate(R.layout.call_api_activity, container, false);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        bundle = getArguments();
        appViewModel = new ViewModelProvider((ViewModelStoreOwner) this).get(AppViewModel.class);
        baseUrl = bundle.getString("baseUrl");
        appViewModel.setBaseUrl(baseUrl);
        appViewModel.init(getContext(), baseUrl);
        appDatabase = AppDatabase.getAppDb(getContext());
        // TODO: Use the ViewModel
        callApiList_View  = getView().findViewById(R.id.callApiList_View);
        leagueIdEditText = getView().findViewById(R.id.leagueId);
        fromDateEditText = getView().findViewById(R.id.from_date);
        toDateEdittext = getView().findViewById(R.id.to_date);
        seasonYearEdittext = getView().findViewById(R.id.seasonYear);
        callApi = getView().findViewById(R.id.callApi);
        verifyBtn = getView().findViewById(R.id.verify);
        createGames = getView().findViewById(R.id.create);
        updateLeague = getView().findViewById(R.id.updateLeagueFile);
        progressBar = getView().findViewById(R.id.progressBar);
        mySwipeRefreshLayout = getView().findViewById(R.id.swiperefresh);
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

        fromDateEditText.setText("2024-01-01");
        toDateEdittext.setText("2025-01-01");
        seasonYearEdittext.setText("2024");

        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            appViewModel.getAllLeagues();
            onCompletion();
        });

        if(state != null) {
            callApiList_View.onRestoreInstanceState(state);
        }

        ArrayList<String> maxDate = new ArrayList<>();
//        ArrayList<Integer> leagueIdArrayList = new ArrayList<>();
        //ArrayList<League> leaguesToShow = (ArrayList<League>) bundle.getSerializable("allUniqueLeagues");

        favoriteLeagues =  new ArrayList<>();
        favoriteLeaguesInt =  new ArrayList<>();

        favoriteLeaguesInt = appDatabase.getDistinctLeague();

        for (int id: favoriteLeaguesInt) {
            League newLeague = appDatabase.getLeagueById(id);
            favoriteLeagues.add(newLeague);
        }

        favoriteLeagues.size();


        ArrayList<League> leaguesToShow = new ArrayList<League>(favoriteLeagues);

        leaguesToShow.addAll( (ArrayList<League>) bundle.getSerializable("allUniqueLeagues"));


        if(leaguesToShow != null){

            adapter = new LeagueAdapter(getActivity(), (ArrayList<League>) leaguesToShow, R.layout.callapi_activity_rows, 0, maxDate);
            callApiList_View.setAdapter(adapter);
            if(state != null) {
                callApiList_View.onRestoreInstanceState(state);
            }
            adapter.setLeagues((ArrayList<League>) leaguesToShow);
            adapter.notifyDataSetChanged();
            callApiList_View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    League league = new League();
                    league = (League) adapter.getItem(position);

                    ShowCallApiFragment showCallApiFragment = new ShowCallApiFragment();
                    Bundle args = new Bundle();
                    args.putString("baseUrl", baseUrl);
                    args.putInt("leagueId", league.getLeagueId());
                    showCallApiFragment.setArguments(args);
                    appViewModel.replaceFragment(showCallApiFragment, view);
                }
            });
        }else{
            callApiList_View.setAdapter(null);
        }

        callApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Thread(){
                    public void run(){
                        try {
                            callApi(v);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();

            }
        });
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Thread(){
                    public void run(){
                        try {
                            verifyGameOnClick(v);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();


            }
        });
        createGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Thread(){
                    public void run(){
                        try {
                            createGameOnClick(v);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();


            }
        });
        updateLeague.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Thread(){
                    public void run(){
                        try {
                            updateLeagueFileOnClick(v);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
            }
        });

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

        callApiList_View.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (callApiList_View== null || callApiList_View.getChildCount() == 0) ? 0 : callApiList_View.getChildAt(0).getTop();
                mySwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

    }


    public void callApi(View v) {

        if (!leagueIdEditText.getText().toString().equals("")) {
            leagueId = Integer.parseInt(leagueIdEditText.getText().toString());
            from_date = fromDateEditText.getText().toString();
            to_date = toDateEdittext.getText().toString();
            seasonYear = seasonYearEdittext.getText().toString();

            CallApiModel callApiModel = new CallApiModel();
            callApiModel.setLeague(leagueId);
            callApiModel.setFrom(from_date);
            callApiModel.setTo(to_date);
            callApiModel.setSeason(Integer.parseInt(seasonYear));

            appViewModel.callApi(callApiModel, v);
        }
    }
    public void updateLeagueFileOnClick(View view) {
        appViewModel.updateLeagueFile( view);
    }
    public void verifyGameOnClick(View view) {
        appViewModel.verifyPastGame( view );
    }
    public void createGameOnClick(View view) {
        appViewModel.createGame( view );
    }

    @Override
    public void onRefresh() {

    }
    public void onCompletion() {
        if (mySwipeRefreshLayout.isRefreshing()) {
            adapter.notifyDataSetChanged();
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("League Name, leagueID");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    adapter.getFilter().filter(newText);
                    adapter.notifyDataSetChanged();
                }catch (NullPointerException e){

                }
                return false;
            }
        });

        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if(state != null) {
                    callApiList_View.onRestoreInstanceState(state);
                }
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //myUpdateOperation();
                if(state != null) {
                    callApiList_View.onRestoreInstanceState(state);
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
                searchView.setQueryHint("League Name, leagueID");
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
                            adapter.notifyDataSetChanged();
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
        searchView.setQueryHint("League Name, leagueID");
    }


    @Override
    public void onSaveInstanceState(Bundle Bstate) {
        super.onSaveInstanceState(Bstate);
        state = callApiList_View.onSaveInstanceState();
        Bstate.putParcelable(LIST_STATE, state);

        if(state != null) {
            callApiList_View.onRestoreInstanceState(state);
        }
    }

    public void onRestoreInstanceState(Bundle Bstate) {
        state = Bstate.getParcelable(LIST_STATE);
        if(state != null) {
            callApiList_View.onRestoreInstanceState(state);
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

    @Override
    public void onBackPressed() {
        //appViewModel.backstackFragment(getView());
        //Toast.makeText(getContext(),"CallApiFragment back button pressed",Toast.LENGTH_LONG).show();
    }
}