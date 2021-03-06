package io.ionic.demo.ecommerce;

import static io.ionic.demo.ecommerce.ui.settings.SettingsFragment.CHANNEL;
import static io.ionic.demo.ecommerce.ui.settings.SettingsFragment.PRODUCTION;
import static io.ionic.demo.ecommerce.ui.settings.SettingsFragment.SETTINGS;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.capacitorjs.plugins.camera.CameraPlugin;

import java.util.Arrays;
import java.util.HashMap;

import io.ionic.demo.ecommerce.data.ShoppingCart;
import io.ionic.demo.ecommerce.plugins.ShopAPIPlugin;
import io.ionic.demo.ecommerce.portals.FadePortalFragment;
import io.ionic.demo.ecommerce.ui.settings.SettingsFragment;
import io.ionic.liveupdates.LiveUpdate;
import io.ionic.liveupdates.LiveUpdateManager;
import io.ionic.portals.PortalManager;

/**
 * The parent Application Class for the E-Commerce app.
 */
public class EcommerceApp extends Application {

    /**
     * A single instance of this class.
     */
    private static EcommerceApp instance;

    /**
     * The active shopping cart used for this shopping session.
     */
    private ShoppingCart shoppingCart;

    /**
     * Get the singleton instance of the app class.
     *
     * @return A singleton instance of the app class.
     */
    public static EcommerceApp getInstance() {
        return instance;
    }

    /**
     * Gets the application context from the singleton instance of the app class.
     *
     * @return The application context.
     */
    public static Context getContext() {
        return instance.getApplicationContext();
    }

    /**
     * Get the active shopping cart state used for this shopping session.
     *
     * @return The shopping cart for the current shopping session.
     */
    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    /**
     * Saves a reference to the application object on app launch and creates a fresh
     * shopping cart to be used for the shopping session.
     */
    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

        // Start app with a fresh shopping cart
        shoppingCart = new ShoppingCart();

        // Register Portals
        // PortalManager.register("YOUR_KEY_HERE");

        // Get channel
        SharedPreferences sharedPrefs = getContext().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        String channel = sharedPrefs.getString(CHANNEL, PRODUCTION);

        Log.d("LiveUpdates", "Loading Portals with Live Update channel: " + channel);

        // Create a Live Updates profile for the Profile and Checkout web app, and the Help web app
        LiveUpdate profileCheckoutConfig = new LiveUpdate("186b544f", channel);
        LiveUpdate helpConfig = new LiveUpdate("a81b2440", channel);

        // Checkout Portal
        PortalManager.newPortal("checkout")
                .setStartDir("webapp")
                .setPlugins(Arrays.asList(ShopAPIPlugin.class))
                .setLiveUpdateConfig(getContext(), profileCheckoutConfig)
                .create();

        // Help Portal
        HashMap<String, String> initialContext = new HashMap<>();
        initialContext.put("startingRoute", "/help");
        PortalManager.newPortal("help")
                .setStartDir("webapp_help")
                .setInitialContext(initialContext)
                .setPlugins(Arrays.asList(ShopAPIPlugin.class))
                .setPortalFragmentType(FadePortalFragment.class)
                .setLiveUpdateConfig(getContext(), helpConfig)
                .create();

        // Profile Portal
        HashMap<String, String> initialContextProfile = new HashMap<>();
        initialContextProfile.put("startingRoute", "/user");
        PortalManager.newPortal("profile")
                .setStartDir("webapp")
                .addPlugin(ShopAPIPlugin.class)
                .addPlugin(CameraPlugin.class)
                .setInitialContext(initialContextProfile)
                .setLiveUpdateConfig(getContext(), profileCheckoutConfig)
                .create();
    }
}
