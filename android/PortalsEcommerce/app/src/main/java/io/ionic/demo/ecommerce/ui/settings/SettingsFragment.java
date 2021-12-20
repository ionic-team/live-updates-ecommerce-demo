package io.ionic.demo.ecommerce.ui.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.ionic.demo.ecommerce.MainActivity;
import io.ionic.demo.ecommerce.R;
import io.ionic.liveupdates.AppState;
import io.ionic.liveupdates.LiveUpdate;
import io.ionic.liveupdates.LiveUpdateManager;

/**
 * Displays products to be purchased.
 */
public class SettingsFragment extends Fragment {

    private MainActivity context;
    private SettingsViewModel settingsViewModel;

    public static SettingsFragment newInstance(MainActivity context) {
        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.setContext(context);
        return settingsFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        Button syncButton = root.findViewById(R.id.test_sync_button);
        syncButton.setOnClickListener(v -> {
            LiveUpdateManager.sync(getContext());
        });

        Button deleteButton = root.findViewById(R.id.test_delete_button);
        deleteButton.setOnClickListener(v -> {
            settingsViewModel.clearPortalStatus();
            LiveUpdateManager.reset(getContext(), false);
            settingsViewModel.setPortalStatus("256afd66", "CLEARED");
        });

        TextView profileCartPortalStatus = root.findViewById(R.id.web_status_label);
        TextView helpPortalStatus = root.findViewById(R.id.web_help_status_label);

        settingsViewModel.getPortalStatusText().observe(getViewLifecycleOwner(), portalStatusText -> {
            profileCartPortalStatus.setText(portalStatusText.get("256afd66"));
            helpPortalStatus.setText(portalStatusText.get("256afd66"));
        });

        settingsViewModel.getPortalsLastSyncTime().observe(getViewLifecycleOwner(), portalLastSyncTime -> {
            Log.d("LiveUpdates", "Last sync time for 256afd66 " + portalLastSyncTime.get("256afd66"));
        });

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(() -> {
            try {
                Map<String, LiveUpdate> apps = LiveUpdateManager.getApps();
                for (Map.Entry<String,LiveUpdate> appEntry : apps.entrySet()) {
                    if (getContext() != null) {
                        settingsViewModel.setPortalStatus(appEntry.getKey(), appEntry.getValue().getAppState().toString());
                        settingsViewModel.setPortalLastSyncTime(appEntry.getKey(), LiveUpdateManager.getLastSync(getContext(), appEntry.getKey()));
                    }
                }
            } catch (Exception e) {
                Log.e("LiveUpdates", "Status check failed: " + e.getMessage());
            }
        }, 0, 200, TimeUnit.MILLISECONDS);

        return root;
    }

    private void setContext(MainActivity context) {
        this.context = context;
    }
}