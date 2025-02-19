package com.example.game.view.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.game.R;
import com.example.game.adapter.ListAdapter;
import com.example.game.model.ClubStats;
import com.example.game.model.Game;
import com.example.game.model.League;
import com.example.game.model.User;
import com.example.game.repository.AppDatabase;
import com.example.game.service.IOnBackPressed;
import com.example.game.service.MainMenuActivityService;
import com.example.game.view.MainActivity;
import com.example.game.view.PredictionsActivity;
import com.example.game.viewModel.AppViewModel;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HomeFragment extends Fragment implements IOnBackPressed {

    private AppViewModel appViewModel;
    private ListAdapter adapter;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private MainMenuActivityService menuActivityService;
    private ListView listView;
    private League league;
    private String latestDate, baseUrl;
    private TextView tableName, textView;
    private ArrayList<ClubStats> table, newTable;
    private ArrayList<Game> gamesArrayList;
    private Parcelable state;
    private Bundle bundle;
    private AppDatabase appDatabase;
    private ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_home, container, false);

        Toolbar mToolbar;
        setHasOptionsMenu(true);
        setRetainInstance(true);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar_home);
        if (mToolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);

        }
        mToolbar.setTitle(null);

        mToolbar.inflateMenu(R.menu.main_menu);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button even
                //Log.d("BACKBUTTON", "Back button clicks");
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return rootView;
        //return inflater.inflate(R.layout.activity_home, container, false);
    }

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
        mySwipeRefreshLayout = getView().findViewById(R.id.swiperefresh);
        progressBar = getView().findViewById(R.id.progressBar);
        listView  = getView().findViewById(R.id.homeListView);
        textView = getView().findViewById(R.id.textView);

        progressBar.setVisibility(View.GONE);
        ((MainActivity) getActivity()).disableSwipe();
        //((AppCompatActivity) requireActivity()).getSupportActionBar().show();
        myUpdateOperation();
        onCompletion();

        if (Objects.equals(baseUrl, "http://10.0.0.85:3000")){
            textView.setText("121 Database Today Game");
        }else if (Objects.equals(baseUrl, "http://10.0.0.85:3001")){
            textView.setText("Season Database Today Game");
        }else if (Objects.equals(baseUrl, "http://10.0.0.85:3002")){
            textView.setText("Infinite Database Today Game");
        }else {
            textView.setText("Today Game");
        }



        menuActivityService = new MainMenuActivityService(baseUrl);

        Intent serviceintent = new Intent (getContext(), MainMenuActivityService.class);

        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            appViewModel.getTodayGame();
            getActivity().startService(serviceintent);
            onCompletion();

        });

        ((MainActivity) getActivity()).setInfo();

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


    public void myUpdateOperation(){

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String currentDateTime = dateFormat.format(currentDate);


        appDatabase.getGames().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                if(games != null){
                    ArrayList<String> dates = new ArrayList<>();
                    for (Game game: games) {
                        dates.add(String.valueOf(game.getDate()));
                    }
                    try{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US);


                            ArrayList<Game> newgames = new ArrayList<>();
                            Date currentDate = new Date();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            String currentDateTime = dateFormat.format(currentDate);

                            for (Game game: games) {
                                Date date1 = (Date) dateFormat.parse(currentDateTime);
                                Date date2 = (Date) dateFormat.parse(String.valueOf(game.getDate()));
                                if(date1.after(date2)){
                                    System.out.println("Date1 is after Date2");
                                }else if (date1.before(date2)){
                                    newgames.add(game);
                                }
                            }

                            latestDate = Objects.requireNonNull(newgames.stream()
                                    .map(d -> LocalDate.parse(String.valueOf(d.getDate()), dtf))
                                    .min(Comparator.comparing(LocalDate::toEpochDay))
                                    .orElse(null)).format(dtf);


                            gamesArrayList = appDatabase.getTodayGames(currentDateTime);

                            if(!gamesArrayList.isEmpty()){

                                adapter = new ListAdapter(getActivity(), (ArrayList<Game>) gamesArrayList, R.layout.today_game_rows, baseUrl);
                                listView.setAdapter(adapter);
                                adapter.setGames((ArrayList<Game>) gamesArrayList);
                                if(state != null) {
                                    listView.onRestoreInstanceState(state);
                                }
                                adapter.notifyDataSetChanged();
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Game newGame = new Game();
                                        newGame = (Game) adapter.getItem(position);

                                        Intent Intent = new Intent(getContext(), PredictionsActivity.class);
                                        Bundle b = new Bundle();
                                        b.putSerializable("game",(Serializable)newGame);
                                        Intent.putExtras(b);
                                        Intent.putExtra("baseUrl", baseUrl);
                                        requireActivity().startActivity(Intent);
                                    }
                                });

                            }else{
                                appDatabase.getTodayGame(latestDate).observe( getViewLifecycleOwner(), new Observer<List<Game>>() {
                                    @Override
                                    public void onChanged(List<Game> games) {
                                        if(games != null){
                                            adapter = new ListAdapter(getActivity(), (ArrayList<Game>) games, R.layout.today_game_rows, baseUrl);
                                            listView.setAdapter(adapter);
                                            adapter.setGames((ArrayList<Game>) games);
                                            if(state != null) {
                                                listView.onRestoreInstanceState(state);
                                            }
                                            adapter.notifyDataSetChanged();

                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    Game newGame = new Game();
                                                    newGame = (Game) adapter.getItem(position);



                                                    Intent Intent = new Intent(getContext(), PredictionsActivity.class);
                                                    Bundle b = new Bundle();
                                                    b.putSerializable("game",(Serializable)newGame);
                                                    Intent.putExtras(b);
                                                    Intent.putExtra("baseUrl", baseUrl);
                                                    requireActivity().startActivity(Intent);
                                                }
                                            });
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }

                    }catch (NullPointerException | ParseException e) {

                    }

                }
            }
        });
    }
    public void onCompletion() {
        if (mySwipeRefreshLayout.isRefreshing()) {
            //adapter.notifyDataSetChanged();
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);

        return true;
    }
    @SuppressLint("WrongThread")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        User loggedInUser = appDatabase.getLoggedInUser();
        switch (item.getItemId()) {
            case R.id.nav_logout:
                appDatabase.removeUser(loggedInUser);
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("baseUrl", baseUrl);
                startActivity(intent);
                requireActivity().getSharedPreferences("myKey", 0).edit().clear().apply();
                appDatabase.clearAllTables();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {

    }
    @Override
    public void onPause(){
        // Save ListView state @ onPause
        state = listView.onSaveInstanceState();
        super.onPause();
    }
    @Override
    public void onBackPressed() {
    }
}