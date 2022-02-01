import UIKit
import IonicLiveUpdates

class SyncPageViewController: UIViewController {
    @IBOutlet weak var profileAppUILabel: UILabel!
    @IBOutlet weak var helpAppUILabel: UILabel!
    @IBOutlet weak var lastCheckProfileAppUILabel: UILabel!
    @IBOutlet weak var lastCheckHelpAppUILabel: UILabel!
    @IBOutlet weak var activeChannelUILabel: UILabel!
    
    private var activeChannel: String = "Production"
    private var appIds: [String] = [profileAppId, helpAppId]
    private static let profileAppId: String = "186b544f"
    private static let helpAppId: String = "a81b2440"
    private static let channelKey: String = "test-lu-channel"
    private static let profileUIUpdatedKey: String = "profile-updated-time"
    private static let helpUIUpdatedKey: String = "help-updated-time"
    
    private class LiveUpdateCallbacks: ISyncCallback {
        let profileUI: UILabel
        let helpUI: UILabel
        let lastUpdatedProfileUI: UILabel
        let lastUpdatedHelpUI: UILabel
        
        init(profileUI: UILabel, lastUpdatedProfileUI: UILabel, helpUI: UILabel, lastUpdatedHelpUI: UILabel) {
            self.profileUI = profileUI
            self.lastUpdatedProfileUI = lastUpdatedProfileUI
            self.helpUI = helpUI
            self.lastUpdatedHelpUI = lastUpdatedHelpUI
        }
        
        func onAppComplete(_ liveUpdate: LiveUpdate, _ failStep: FailStep?) {
            let id = liveUpdate.getAppId()
            if (id == SyncPageViewController.profileAppId) {
                DispatchQueue.main.sync {
                    self.profileUI.text = "UPDATED"
                    
                    let time = Date().timeIntervalSince1970.description
                    UserDefaults.standard.set(time, forKey: profileUIUpdatedKey)
                    self.lastUpdatedProfileUI.text = time
                }
            }
            else if (id == SyncPageViewController.helpAppId) {
                DispatchQueue.main.sync {
                    self.helpUI.text = "UPDATED"
                    
                    let time = Date().timeIntervalSince1970.description
                    UserDefaults.standard.set(time, forKey: helpUIUpdatedKey)
                    self.lastUpdatedHelpUI.text = time
                }
            }
        }
        
        func onSyncComplete() {
            print("Sync completed!")
        }
    }
    
    override func viewDidLoad() {
        activeChannel = UserDefaults.standard.string(forKey: SyncPageViewController.channelKey) ?? "Production"
        activeChannelUILabel.text = activeChannel
        
        let profileText = UserDefaults.standard.string(forKey: SyncPageViewController.profileUIUpdatedKey) ?? "N/A"
        lastCheckProfileAppUILabel.text = profileText
        profileAppUILabel.text = profileText == "N/A" ? "NOT UPDATED" : "UPDATED"
        
        let helpText = UserDefaults.standard.string(forKey: SyncPageViewController.helpUIUpdatedKey) ?? "N/A"
        lastCheckHelpAppUILabel.text = helpText
        helpAppUILabel.text = helpText == "N/A" ? "NOT UPDATED" : "UPDATED"
    }
    
    @IBAction func deleteLiveUpdates(_ sender: Any) {
        LiveUpdateManager.reset()
        self.profileAppUILabel.text = "NOT UPDATED"
        self.lastCheckProfileAppUILabel.text = "N/A"
        self.helpAppUILabel.text = "NOT UPDATED"
        self.lastCheckHelpAppUILabel.text = "N/A"
    }
    
    @IBAction func syncLiveUpdates(_ sender: Any) {
        let cb = LiveUpdateCallbacks(
            profileUI: profileAppUILabel,
            lastUpdatedProfileUI: lastCheckProfileAppUILabel,
            helpUI: helpAppUILabel,
            lastUpdatedHelpUI: lastCheckHelpAppUILabel
        )
        LiveUpdateManager.sync(appIds: appIds, channel: activeChannel, isParallel: true, callbacks: cb)
    }
    
    @IBAction func setProductionAsActiveChannel(_ sender: Any) {
        activeChannel = "Production"
        activeChannelUILabel.text = activeChannel
        UserDefaults.standard.set(activeChannel, forKey: SyncPageViewController.channelKey)
    }
    
    @IBAction func setDevelopmentAsActiveChannel(_ sender: Any) {
        activeChannel = "Development"
        activeChannelUILabel.text = activeChannel
        UserDefaults.standard.set(activeChannel, forKey: SyncPageViewController.channelKey)
    }
    
    @IBAction func resetNsUserDefaults(_ sender: Any) {
        UserDefaults.resetStandardUserDefaults()
    }
}
