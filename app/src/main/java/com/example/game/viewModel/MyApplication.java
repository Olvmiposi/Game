package com.example.game.viewModel;

import android.app.Application;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.RequiresApi;

import com.example.game.service.MainMenuActivityService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.P)
public class MyApplication extends Application {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler mainThreadHandler = Handler.createAsync(Looper.getMainLooper());

    private MainMenuActivityService menuActivityService;

    public Handler getMainThreadHandler() {
        return mainThreadHandler;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}