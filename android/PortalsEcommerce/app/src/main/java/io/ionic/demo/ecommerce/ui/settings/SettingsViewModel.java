package io.ionic.demo.ecommerce.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class SettingsViewModel extends ViewModel {

    /**
     * Status for updating portals.
     */
    private final MutableLiveData<HashMap<String, String>> portalStatusText;

    /**
     * Last sync time for portals.
     */
    private final MutableLiveData<HashMap<String, Long>> portalsLastSyncTime;

    private final MutableLiveData<Boolean> resetProfile = new MutableLiveData<>(false);

    /**
     * Constructs a settings view model.
     */
    public SettingsViewModel() {
        portalStatusText = new MutableLiveData<>();
        portalsLastSyncTime = new MutableLiveData<>();
        portalStatusText.setValue(new HashMap<>());
        portalsLastSyncTime.setValue(new HashMap<>());
    }

    public void setPortalStatus(String appId, String status) {
        HashMap<String, String> updateMap = portalStatusText.getValue();
        updateMap.put(appId, status);
        portalStatusText.postValue(updateMap);

    }

    public void setPortalLastSyncTime(String appId, Long time) {
        HashMap<String, Long> updateMap = portalsLastSyncTime.getValue();
        updateMap.put(appId, time);
        portalsLastSyncTime.postValue(updateMap);
    }

    public void setResetProfile(boolean reset) {
        resetProfile.postValue(reset);
    }

    public void clearPortalStatus() {
        HashMap<String, String> updateMap = portalStatusText.getValue();
        for (Map.Entry<String,String> appEntry : updateMap.entrySet()) {
            appEntry.setValue("Cleared");
        }
        portalStatusText.postValue(updateMap);
    }

    public void clearPortalLastSync() {
        HashMap<String, Long> updateMap = portalsLastSyncTime.getValue();
        for (Map.Entry<String,Long> appEntry : updateMap.entrySet()) {
            appEntry.setValue(-1L);
        }
        portalsLastSyncTime.postValue(updateMap);
    }

    public LiveData<HashMap<String, String>> getPortalStatusText() {
        return portalStatusText;
    }

    public LiveData<HashMap<String, Long>> getPortalsLastSyncTime() {
        return portalsLastSyncTime;
    }

    public LiveData<Boolean> getResetProfile() {
        return resetProfile;
    }

}
