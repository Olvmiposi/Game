package com.example.game.adapter;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.example.game.R;
import com.example.game.model.SearchString;
import com.example.game.view.MainActivity;
import com.example.game.viewModel.AppViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SearchAdapter extends BaseAdapter {

    private Context mContext;
    private List<SearchString> list;

    private TextView username;
    private LayoutInflater inflater;
    private String usernameText, baseUrl;
    private SearchString searchString;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private AppViewModel appViewModel;
    private Menu mOptionsMenu;
    public SearchAdapter(Context mContext, List<SearchString> list, Menu mOptionsMenu, String baseUrl) {
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
        this.list = list;
        this.baseUrl = baseUrl;
        this.mOptionsMenu = mOptionsMenu;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if(list==null) return 0;
        return list.size();
    }
    public void setStrings(ArrayList<SearchString> strings) {
        this.list = strings;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Context context = parent.getContext();
        sort();
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.search_row  , parent, false);
            convertView.setFocusable(true);
            convertView.setClickable(true);
        }
        username = convertView.findViewById(R.id.username);

        searchString = (SearchString) getItem(position);
        usernameText = searchString.getUsername();
        username.setText(String.valueOf(usernameText));

        appViewModel = new ViewModelProvider((MainActivity) context).get(AppViewModel.class);
        appViewModel.setBaseUrl(baseUrl);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchManager searchManager = (SearchManager)((MainActivity)context).getSystemService(Context.SEARCH_SERVICE);
                Menu menu = mOptionsMenu;
                searchMenuItem = menu.findItem(R.id.search);
                searchView = (SearchView) searchMenuItem.getActionView();
                //SearchView searchView=(SearchView) ((SchrodingerActivity)context).findViewById(R.id.search);
                searchString = (SearchString) getItem(position);
                searchView.setQuery(searchString.getUsername(), true);

            }
        });


        return convertView;
    }

    public void sort(){
        Collections.sort(list, new Comparator<SearchString>() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public int compare(SearchString t1, SearchString t2) {
                if (t1.getDate() != null) {

                    String sDate1 = t1.getDate();
                    String sDate2 = t2.getDate();

                    Date date1 = null;
                    Date date2 = null;

                    try {
                        date1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(sDate1);
                        date2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(sDate2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (date1 != null && date2 != null) {
                        return date2.compareTo(date1);
                    }
                }else{
                    return -1;
                }
                return 0;
            }
        });
    }
}
