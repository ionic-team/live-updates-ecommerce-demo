import UIKit
import IonicPortals
import IonicLiveUpdates

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.

        // Register Portals
        PortalManager.register(PORTALS_KEY)

        let profileId = "186b544f"
        let helpId = "a81b2440"

        // Checkout Live Update
        let checkoutLiveUpdate = LiveUpdate(appId: profileId)
        let checkoutPortal = PortalManager.newPortal("checkout")
            .setStartDir("portals/shopwebapp")
            .setLiveUpdateConfig(liveUpdateConfig: checkoutLiveUpdate, updateOnAppLoad: false)
            .create()
        PortalManager.addPortal(checkoutPortal)
        
        // Help Live Update
        let helpLiveUpdate = LiveUpdate(appId: helpId)
        let helpPortal = PortalManager.newPortal("help")
            .setStartDir("portals/shopwebapphelp")
            .setLiveUpdateConfig(liveUpdateConfig: helpLiveUpdate, updateOnAppLoad: false)
            .create()
        PortalManager.addPortal(helpPortal)
        
        // User Live Update
        let userLiveUpdate = LiveUpdate(appId: profileId)
        let userPortal = PortalManager.newPortal("user")
            .setStartDir("portals/shopwebapp")
            .setLiveUpdateConfig(liveUpdateConfig: userLiveUpdate, updateOnAppLoad: false)
            .create()
        PortalManager.addPortal(userPortal)
        
        return true
    }

    // MARK: UISceneSession Lifecycle

    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }
}
