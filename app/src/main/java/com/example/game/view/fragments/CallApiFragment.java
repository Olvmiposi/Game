package com.example.game.view.fragments;

import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.game.R;
import com.example.game.adapter.LeagueAdapter;
import com.example.game.databinding.CallApiActivityBinding;
import com.example.game.model.CallApiModel;
import com.example.game.model.Game;
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
    private MenuItem searchMenuItem;
    private int leagueId;
    private ImageButton callApi, verifyBtn, createGames, updateLeague;
    private String  from_date, to_date, seasonYear;
    private EditText leagueIdEditText, fromDateEditText, toDateEdittext, seasonYearEdittext;

    private CallApiActivityBinding binding;
    public static CallApiFragment newInstance() {
        return new CallApiFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.call_api_activity, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        appViewModel = new ViewModelProvider((ViewModelStoreOwner) this).get(AppViewModel.class);
        appViewModel.init(getContext());
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
        ((MainActivity) getActivity()).disableSwipe();
        ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();
//        binding = DataBindingUtil.setContentView(requireActivity(), R.layout.call_api_activity);
//        binding.setLifecycleOwner(getViewLifecycleOwner());
//        binding.setAppViewModel(appViewModel);


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

        ArrayList<String> maxDate = new ArrayList<>();
        appDatabase.getLeagues().observe((LifecycleOwner) this, new Observer<List<League>>() {
            @Override
            public void onChanged(List<League> leagues) {
                if(leagues != null){
                    adapter = new LeagueAdapter(getActivity(), (ArrayList<League>) leagues, R.layout.league_rows, 0, maxDate);
                    callApiList_View.setAdapter(adapter);
                    adapter.setLeagues((ArrayList<League>) leagues);
                    adapter.notifyDataSetChanged();
                    callApiList_View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            League league = new League();
                            league = (League) adapter.getItem(position);

                            CallApiModel callApiModel = new CallApiModel();
                            callApiModel.setLeague(league.getId());
                            callApiModel.setFrom(fromDateEditText.getText().toString());
                            callApiModel.setTo(toDateEdittext.getText().toString());
                            callApiModel.setSeason(Integer.parseInt(seasonYearEdittext.getText().toString()));

                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setMessage("Call this League ? \n" +
                                    "Name : " + league.getName()  + " \n " +
                                    "Id : " + league.getId() + " \n " +
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
            }
        });
        callApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
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
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                   verifyGameOnClick(v);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
        createGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createGameOnClick(v);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
        updateLeague.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateLeagueFileOnClick(v);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        appViewModel.callApiResponse().observe((LifecycleOwner) this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {

            }
        });

    }

    public void updateLeagueFileOnClick(View view) {
        appViewModel.updateLeagueFile( view);
        appViewModel.updateLeagueFileResponse().observe((LifecycleOwner) this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
            }
        });
    }
    public void verifyGameOnClick(View view) {
        appViewModel.verifyPastGame( view );
        appViewModel.verifyPastGameResponse().observe((LifecycleOwner) this, new Observer<ArrayList<Game>>() {
            @Override
            public void onChanged(ArrayList<Game> games) {
            }
        });
    }
    public void createGameOnClick(View view) {
        appViewModel.createGame( view );
        appViewModel.createGameResponse().observe((LifecycleOwner) this, new Observer<ArrayList<Game>>() {
            @Override
            public void onChanged(ArrayList<Game> games) {
            }
        });
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
        appViewModel.backstackFragment(getView());
        Toast.makeText(getContext(),"CallApiFragment back button pressed",Toast.LENGTH_LONG).show();
    }
}