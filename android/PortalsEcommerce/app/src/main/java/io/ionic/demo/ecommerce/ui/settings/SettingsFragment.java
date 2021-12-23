package io.ionic.demo.ecommerce.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
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
import io.ionic.liveupdates.network.FailStep;
import io.ionic.liveupdates.network.SyncCallback;

/**
 * Displays products to be purchased.
 */
public class SettingsFragment extends Fragment {

    public static final String SETTINGS = "settings";
    public static final String CHANNEL = "channel";
    public static final String PRODUCTION = "production";
    public static final String DEVELOPMENT = "development";

    String channel = "production";

    private SettingsViewModel settingsViewModel;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);

    public static SettingsFragment newInstance() {
        SettingsFragment settingsFragment = new SettingsFragment();
        return settingsFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        SharedPreferences sharedPrefs = getContext().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        channel = sharedPrefs.getString(CHANNEL, PRODUCTION);

        RadioGroup radioGroup = root.findViewById(R.id.channel_options);
        if (channel.equalsIgnoreCase(PRODUCTION)) {
            radioGroup.check(R.id.radio_channel_production);
        } else {
            radioGroup.check(R.id.radio_channel_development);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_channel_production) {
                sharedPrefs.edit().putString(CHANNEL, PRODUCTION).apply();
            } else {
                sharedPrefs.edit().putString(CHANNEL, DEVELOPMENT).apply();
            }
        });

        Button syncButton = root.findViewById(R.id.test_sync_button);
        syncButton.setOnClickListener(v -> {
            LiveUpdateManager.sync(getContext(), new String[] {"186b544f", "a81b2440"} ,true, new SyncCallback() {

                @Override
                public void onSyncComplete() {
                    settingsViewModel.setResetProfile(true);
                }

                @Override
                public void onAppComplete(@NonNull LiveUpdate liveUpdate, @Nullable FailStep failStep) {
                    Log.d("LiveUpdates", "App " + liveUpdate.getAppId() + " finished syncing");
                }
            });
        });

        Button deleteButton = root.findViewById(R.id.test_delete_button);
        deleteButton.setOnClickListener(v -> {
            settingsViewModel.clearPortalStatus();
            LiveUpdateManager.reset(getContext(), false);
            settingsViewModel.setPortalStatus("186b544f", "CLEARED");
            settingsViewModel.setPortalStatus("a81b2440", "CLEARED");
            settingsViewModel.setResetProfile(true);
        });

        TextView profileCartPortalStatus = root.findViewById(R.id.web_status_label);
        TextView helpPortalStatus = root.findViewById(R.id.web_help_status_label);

        settingsViewModel.getPortalStatusText().observe(getViewLifecycleOwner(), portalStatusText -> {
            profileCartPortalStatus.setText(portalStatusText.get("186b544f"));
            helpPortalStatus.setText(portalStatusText.get("a81b2440"));
        });

        TextView profileCartPortalSyncTime = root.findViewById(R.id.web_time_label);
        TextView helpPortalSyncTime = root.findViewById(R.id.web_help_time_label);

        settingsViewModel.getPortalsLastSyncTime().observe(getViewLifecycleOwner(), portalLastSyncTime -> {
            Long webPortalLastSyncTime = portalLastSyncTime.get("186b544f");
            if (webPortalLastSyncTime != null && webPortalLastSyncTime != -1) {
                profileCartPortalSyncTime.setText(formatter.format(webPortalLastSyncTime));
            }

            Long helpPortalLastSyncTime = portalLastSyncTime.get("a81b2440");
            if (helpPortalLastSyncTime != null && helpPortalLastSyncTime != -1) {
                helpPortalSyncTime.setText(formatter.format(helpPortalLastSyncTime));
            }
        });

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(() -> {
            try {
                Map<String, LiveUpdate> apps = LiveUpdateManager.getApps();
                for (Map.Entry<String,LiveUpdate> appEntry : apps.entrySet()) {
                    if (getContext() != null) {
                        String appState = appEntry.getValue().getAppState().toString();
                        if (!profileCartPortalStatus.getText().toString().equalsIgnoreCase(appState)
                                || !helpPortalStatus.getText().toString().equalsIgnoreCase(appState)) {
                            settingsViewModel.setPortalStatus(appEntry.getKey(), appState);
                            settingsViewModel.setPortalLastSyncTime(appEntry.getKey(), LiveUpdateManager.getLastSync(getContext(), appEntry.getKey()));
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("LiveUpdates", "Status check failed: " + e.getMessage());
            }
        }, 0, 200, TimeUnit.MILLISECONDS);

        return root;
    }
}