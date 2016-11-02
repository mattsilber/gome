import UIKit

class ConnectViewController: UIViewController, Navigable {
    
    @IBOutlet weak var ipInputView: UITextView!
    
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
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        
    }
    
    @IBAction func onConnectClicked(_ sender: Any){
        var params = [String: Any]()
        params[ControllerViewController.PARAM__IP_ADDRESS] = ipInputView.text
        
        Nav.instance.open(.CONTROLLER, params: params, onLoad: nil)
    }


}

