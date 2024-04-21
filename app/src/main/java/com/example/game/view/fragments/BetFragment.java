package com.example.game.view.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.game.R;
import com.example.game.adapter.BetAdapter;
import com.example.game.adapter.ListAdapter;
import com.example.game.model.Bet;
import com.example.game.model.BetResponse;
import com.example.game.model.Game;
import com.example.game.repository.AppDatabase;
import com.example.game.service.IOnBackPressed;
import com.example.game.view.MainActivity;
import com.example.game.viewModel.AppViewModel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BetFragment extends DialogFragment implements IOnBackPressed {
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private AppViewModel appViewModel;
    private BetAdapter adapter;
    private ArrayList<Integer> allGamesLeaguesId,allPasswordLeaguesId, homeScore, awayScore;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private ListAdapter listAdapter;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ListView bets_listView;
    private AppDatabase appDatabase;

    private Button generateRB;
    private EditText no_games, dateEditText;

    private Bet bet;
    private ProgressBar progressBar;

    public static BetFragment newInstance() {
        return new BetFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.bet_activity, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        appViewModel.init(getContext());
        appDatabase = AppDatabase.getAppDb(getContext());
        mySwipeRefreshLayout = getView().findViewById(R.id.swiperefresh);
        bets_listView  = getView().findViewById(R.id.bets_listView);

        progressBar = getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        no_games = getView().findViewById(R.id.no_games);
        dateEditText = getView().findViewById(R.id.date);
        generateRB = getView().findViewById(R.id.generateRB);
        ((MainActivity) getActivity()).disableSwipe();
        ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();
        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            onCompletion();
        });


        int no = 3;

        Date currentDate = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        String currentDateTime = dateFormat.format(currentDate);

        no_games.setText(String.valueOf(no));
        dateEditText.setText(currentDateTime);

        //get all league id from available games
        allGamesLeaguesId = new ArrayList<>();
        allPasswordLeaguesId = new ArrayList<>();

        appDatabase.getGames().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                if(games == null){
                }else {
                    for(Game game : games ) {
                        if(!allGamesLeaguesId.contains(game.getLeagueId()))
                            allGamesLeaguesId.add(game.getLeagueId());
                    }
                }
            }
        });

        appDatabase.getAllCheckedGames().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                if(games == null){
                }else {
                    for(Game game : games ) {
                        if(!allPasswordLeaguesId.contains(game.getLeagueId()))
                            allPasswordLeaguesId.add(game.getLeagueId());
                    }
                }
            }
        });

        generateRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!dateEditText.getText().toString().equals("")) {
                        int no = Integer.parseInt(no_games.getText().toString());
                        String date = dateEditText.getText().toString();
                        bet = new Bet();
                        bet.setNo_games(no);
                        bet.setDate(date);
                        appViewModel.getPairs(bet);
                        //
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
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

        bets_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (bets_listView== null || bets_listView.getChildCount() == 0) ? 0 : bets_listView.getChildAt(0).getTop();
                mySwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        myUpdateOperation();


    }

    public void myUpdateOperation(){

        appViewModel.getPairsResponse().observe(getViewLifecycleOwner(), new Observer<ArrayList<BetResponse>>() {
            @Override
            public void onChanged(ArrayList<BetResponse> arrayLists) {
                if(arrayLists == null){
                    bets_listView.setAdapter(null);
                }else {
                    adapter = new BetAdapter((MainActivity)getActivity(), (ArrayList<BetResponse>)  arrayLists, R.layout.bet_group_rows);
                    bets_listView.setAdapter(adapter);
                    adapter.setGames((ArrayList<BetResponse>) arrayLists);
                    adapter.notifyDataSetChanged();
                    onCompletion();


//                    bets_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            ArrayList<Game> selectedgame = adapter.getGames().get(position).getGames();
//
//                            LayoutInflater inflater = ((MainActivity)requireActivity()).getLayoutInflater();
//                            View newconvertView = (View) inflater.inflate(R.layout.dialog_main, parent, false);
//
//                            builder = new AlertDialog.Builder(getContext())
//                                    .setNegativeButton("Cancel", (dialog, which) -> {dialog.cancel();dialog.dismiss();});
//
//
//                            builder.setTitle("Group " + (Integer.valueOf(position) + 1));
//                            builder.setView(newconvertView);
//                            ListView lv = (ListView) newconvertView.findViewById(R.id.dialogListView);
//                            listAdapter = new ListAdapter(getActivity(), (ArrayList<Game>) selectedgame,  R.layout.bets_rows);
//                            lv.setAdapter(listAdapter);
//                            listAdapter.setGames((ArrayList<Game>) selectedgame);
//                            listAdapter.notifyDataSetChanged();
//
//
//
//                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                    Game newGame = new Game();
//                                    newGame = (Game) selectedgame.get(position);
//
//                                    PredictionsFragment predictionsFragment = new PredictionsFragment();
//                                    Bundle args = new Bundle();
//                                    args.putSerializable("game",(Serializable)newGame);
//                                    predictionsFragment.setArguments(args);
//                                    appViewModel.addFragment(predictionsFragment, view);
//                                    alertDialog.dismiss();
//                                }
//                            });
//
//                            alertDialog = builder.create();
//                            alertDialog.show();
//                            alertDialog.setCanceledOnTouchOutside(true);
//                            alertDialog.setCancelable(true);
//                        }
//                    });
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

    @Override
    public void onRefresh() {

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onBackPressed() {
        //appViewModel.backstackFragment(getView());
    }

}