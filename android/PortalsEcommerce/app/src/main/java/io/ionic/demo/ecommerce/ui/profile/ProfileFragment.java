package io.ionic.demo.ecommerce.ui.profile;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.ionic.liveupdates.AppState;
import io.ionic.liveupdates.LiveUpdate;
import io.ionic.portals.Portal;
import io.ionic.portals.PortalFragment;
import io.ionic.portals.PortalManager;

public class ProfileFragment extends PortalFragment {

    public static ProfileFragment newInstance() {
        return new ProfileFragment(PortalManager.getPortal("profile"));
    }

    public ProfileFragment() {
        super();
    }

    public ProfileFragment(Portal portal) {
        super(portal);
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(false);

        Portal profilePortal = getPortal();
        if (profilePortal != null) {
            LiveUpdate liveUpdate = profilePortal.getLiveUpdateConfig();
            if (liveUpdate != null) {
                AppState appState = liveUpdate.getAppState();
                if (appState != AppState.FAILED && appState != AppState.CANCELED && appState != AppState.UPDATED) {
                    reloadOnFinish(liveUpdate);
                }
            }
        }
    }

    private void reloadOnFinish(LiveUpdate liveUpdate) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(() -> {
            AppState appState = liveUpdate.getAppState();
            if (appState == AppState.UPDATED) {
                // App is finished updating, reload the portal
                if (getContext() != null) {
                    // Force reload portal
                    Log.d("LiveUpdates", "RELOADING!!");
                    Log.d("LiveUpdates", "RELOADING!!");
                    reload();
                }
                throw new RuntimeException("Successful");
            } else if (appState == AppState.FAILED || appState == AppState.CANCELED) {
                Log.e("LiveUpdates", "Live update failed or canceled, reload not triggered.");
                throw new RuntimeException("Failed or Canceled");
            } else if (appState == AppState.CHECKED) {
                Log.d("LiveUpdates", "No app update!");
                throw new RuntimeException("Checked");
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }
}