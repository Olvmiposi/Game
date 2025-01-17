package com.example.game.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.game.R;
import com.example.game.databinding.LoginActivityBinding;
import com.example.game.response.LoginResponse;
import com.example.game.view.ActiveActivitiesTracker;
import com.example.game.viewModel.AppViewModel;

public class Login extends Fragment {
    private View view;
    private AppViewModel appViewModel;
    private LoginActivityBinding binding;
    private  Bundle bundle;
    private String baseUrl;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.login_activity, parent, false);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        bundle = getArguments();
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        baseUrl = "http://10.0.0.85:3002";
        appViewModel.setBaseUrl(baseUrl);
        appViewModel.init(getContext(), baseUrl);
        binding = DataBindingUtil.setContentView(requireActivity(), R.layout.login_activity);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setAppViewModel(appViewModel);

        appViewModel.loginUser().observe(getViewLifecycleOwner(), new Observer<LoginResponse>() {
            @Override
            public void onChanged(LoginResponse loginResponse) {
                if (loginResponse != null){
                    getActivity().finish();
                }
            }
        });
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
}
