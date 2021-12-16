package io.ionic.demo.ecommerce.ui.liveupdates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.ionic.demo.ecommerce.MainActivity;
import io.ionic.demo.ecommerce.R;
import io.ionic.demo.ecommerce.data.model.Product;
import io.ionic.demo.ecommerce.ui.store.ProductAdapter;
import io.ionic.demo.ecommerce.ui.store.StoreFragment;
import io.ionic.demo.ecommerce.ui.store.StoreViewModel;

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

        return root;
    }

    private void setContext(MainActivity context) {
        this.context = context;
    }
}