package com.example.game.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.example.game.R;
import com.example.game.model.BetResponse;
import com.example.game.model.Game;
import com.example.game.repository.AppDatabase;
import com.example.game.view.MainActivity;
import com.example.game.viewModel.AppViewModel;

import java.util.ArrayList;

public class BetAdapter extends BaseAdapter {
    private final Activity activity;
    private LayoutInflater inflater;
    private ArrayList<BetResponse> games;

    private HomeAdapter homeAdapter;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private int layout;
    private AppViewModel appViewModel;
    private Button schrodingerBtn;
    private ListAdapter listAdapter;
    private TextView  group, no_of_bets;
    private ListView teamListView;
    private String baseUrl;
    private Game  newGame;
    private ArrayList<Game> currentGame;
    private ImageView profilePhoto;
    private AppDatabase appDatabase;
    public ArrayList<BetResponse> getGames() {
        return games;
    }
    public void setGames(ArrayList<BetResponse> games) {
        this.games = games;
    }
    public BetAdapter(Activity activity, ArrayList<BetResponse> games, int layout , String baseUrl) {
        this.activity = activity;
        this.games = games;
        this.layout = layout;
        this.baseUrl = baseUrl;
    }
    @Override
    public int getCount() {
        return games.size();
    }
    @Override
    public Object getItem(int position) {
        return games.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        appDatabase = AppDatabase.getAppDb(context);
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(layout, null);
            convertView.setFocusable(true);
            convertView.setClickable(true);
        }
        teamListView = convertView.findViewById(R.id.teamListView);
        group = convertView.findViewById(R.id.group);
        no_of_bets = convertView.findViewById(R.id.no_of_bets);


        currentGame = games.get(position).getGames();
        if(layout == R.layout.bet_group_rows)
        {

            appViewModel = new ViewModelProvider((MainActivity) context).get(AppViewModel.class);
            appViewModel.setBaseUrl(baseUrl);
            appViewModel.init(context, baseUrl);

            homeAdapter = new HomeAdapter( activity, (ArrayList<Game>) currentGame,  R.layout.team);
            teamListView.setAdapter(homeAdapter);
            homeAdapter.setGames((ArrayList<Game>) currentGame);
            homeAdapter.notifyDataSetChanged();

            group.setText("Group " + (Integer.valueOf(position) + 1));
            no_of_bets.setText(String.valueOf(games.get(position).getGames().size()));
            ArrayList<Game> selectedgame = getGames().get(position).getGames();




            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = ((MainActivity)activity).getLayoutInflater();
                    View newconvertView = (View) inflater.inflate(R.layout.dialog_main, parent, false);

                    builder = new AlertDialog.Builder((MainActivity)activity)
                            .setNegativeButton("Cancel", (dialog, which) -> {dialog.cancel();dialog.dismiss();});


                    builder.setTitle("Group " + (Integer.valueOf(position) + 1));
                    builder.setView(newconvertView);
                    ListView lv = (ListView) newconvertView.findViewById(R.id.dialogListView);
                    listAdapter = new ListAdapter(activity, (ArrayList<Game>) selectedgame,  R.layout.bets_rows, baseUrl );
                    lv.setAdapter(listAdapter);
                    listAdapter.setGames((ArrayList<Game>) selectedgame);
                    listAdapter.notifyDataSetChanged();


//                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            newGame = (Game) selectedgame.get(position);
//                            PredictionsFragment predictionsFragment = new PredictionsFragment();
//                            Bundle args = new Bundle();
//                            args.putSerializable("game",(Serializable)newGame);
//                            predictionsFragment.setArguments(args);
//                            appViewModel.addFragment(predictionsFragment, view);
//
//                        }
//                    });

                    alertDialog = builder.create();
                    alertDialog.show();
                    if (selectedgame != null) {
                        alertDialog.show();
                    }

                    alertDialog.setCanceledOnTouchOutside(true);
                    alertDialog.setCancelable(true);
                }
            });

        }
        return convertView;
    }

}
