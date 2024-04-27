package com.example.game.view.fragments;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
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
import java.util.stream.Collectors;

public class ShowCallApiFragment extends Fragment implements IOnBackPressed {
    private AppViewModel appViewModel;
    private AppDatabase appDatabase;
    private LeagueAdapter adapter;
    private ListView callApiList_View;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private int leagueId;
    private Bundle bundle;
    private ArrayList<League> leagueArrayList;
    private String  from_date, to_date, seasonYear;
    private TextView textView;
    private CallApiActivityBinding binding;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setRetainInstance(true);
        return inflater.inflate(R.layout.show_call_api_activity, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        appViewModel = new ViewModelProvider((ViewModelStoreOwner) this).get(AppViewModel.class);
        appViewModel.init(getContext());
        appDatabase = AppDatabase.getAppDb(getContext());
        // TODO: Use the ViewModel
        callApiList_View  = getView().findViewById(R.id.callApiList_View);
        textView = getView().findViewById(R.id.textView);
        progressBar = getView().findViewById(R.id.progressBar);
        mySwipeRefreshLayout = getView().findViewById(R.id.swiperefresh);

        ((MainActivity) getActivity()).disableSwipe();
        //((AppCompatActivity) requireActivity()).getSupportActionBar().hide();

        Toolbar mToolbar;
        setHasOptionsMenu(true);
        setRetainInstance(true);
        mToolbar = (Toolbar) requireView().findViewById(R.id.toolbar_home);
        if (mToolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);
        }
        mToolbar.setTitle(null);

        mToolbar.inflateMenu(R.menu.options_menu);

        bundle = getArguments();


        leagueId = bundle.getInt("leagueId", 0);

        ArrayList<String> maxDate = new ArrayList<>();

        leagueArrayList = (ArrayList<League>) appDatabase.getLeaguesById(leagueId);

        if(leagueArrayList != null){

            adapter = new LeagueAdapter(getActivity(), (ArrayList<League>) leagueArrayList, R.layout.callapi_activity_rows, 0, maxDate);
            callApiList_View.setAdapter(adapter);
            adapter.setLeagues((ArrayList<League>) leagueArrayList);
            adapter.notifyDataSetChanged();

            textView.setText(leagueArrayList.get(0).getName());
            callApiList_View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    League league = new League();
                    league = (League) adapter.getItem(position);

                    CallApiModel callApiModel = new CallApiModel();
                    callApiModel.setLeague(league.getLeagueId());
                    callApiModel.setFrom(league.getStart());
                    callApiModel.setTo(league.getEnd());
                    callApiModel.setSeason(league.getSeason());

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("Call this League ? \n" +
                            "Name : " + league.getName()  + " \n " +
                            "Id : " + league.getLeagueId() + " \n " +
                            "from : " + callApiModel.getFrom());
                    builder.setTitle("Alert !");
                    builder.setCancelable(false);

                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                        appViewModel.callApi(callApiModel, view);
                    });
                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                        dialog.cancel();
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.setCanceledOnTouchOutside(true);
                }
            });
        }else{
            callApiList_View.setAdapter(null);
        }

//        appDatabase.getLeaguesById(leagueId).observe((LifecycleOwner) this, new Observer<List<League>>() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onChanged(List<League> leagues) {
//
//            }
//        });

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


    public void callApi(View v, League selectedLeague) {

        CallApiModel callApiModel = new CallApiModel();
        callApiModel.setLeague(selectedLeague.getLeagueId());
        callApiModel.setFrom(selectedLeague.getStart());
        callApiModel.setTo(selectedLeague.getEnd());
        callApiModel.setSeason(selectedLeague.getSeason());

        appViewModel.callApi(callApiModel, v);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getActivity().getMenuInflater();
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
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
        searchView.setQueryHint("League Name, leagueID");
    }

    @Override
    public void onBackPressed() {
        //appViewModel.backstackFragment(getView());
        //Toast.makeText(getContext(),"CallApiFragment back button pressed",Toast.LENGTH_LONG).show();
    }
}