import UIKit

class ConnectViewController: UIViewController, Navigable {
    
    private let KEY__LAST_IP = "last_ip_address"
    
    @IBOutlet weak var ipInputView: UITextView!
    
    var preferences = UserDefaults.standard
    
    func onControllerOpened(_ from: Nav.Route, params: [String: Any]?) {
        
    }
    
    func onControllerResult(_ from: Nav.Route, result: Int, with: [String: Any]?) {
        
    }
    
    func getBeforeFilters() -> [Resolvable]{
        return [Resolvable]()
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        
        Nav.instance.takeRootOwnership(self)
        
        if let lastIp = preferences.object(forKey: KEY__LAST_IP) as? String {
            ipInputView.text = lastIp
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        
    }
    
    @IBAction func onConnectClicked(_ sender: Any){
        preferences.set(ipInputView.text, forKey: KEY__LAST_IP)
        preferences.synchronize()
        
        var params = [String: Any]()
        params[ControllerViewController.PARAM__IP_ADDRESS] = ipInputView.text
        
        Nav.instance.open(.CONTROLLER, params: params, onLoad: nil)
    }


}

