import UIKit

class ControllerViewController: UIViewController , Navigable {
    
    public static let PARAM__IP_ADDRESS = "ip_address"
    
    @IBOutlet weak var ipHeaderLabel: UILabel!
    
    private var ipAddress: String!
    
    func onControllerOpened(_ from: Nav.Route, params: [String: Any]?) {
        self.ipAddress = params![ControllerViewController.PARAM__IP_ADDRESS] as! String
    }
    
    func onControllerResult(_ from: Nav.Route, result: Int, with: [String: Any]?) {
        
    }
    
    func getBeforeFilters() -> [Resolvable]{
        return [Resolvable]()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        ipHeaderLabel.text = ipAddress
        
        SocketManager.instance.connect(ipAddress, port: "1337")
        SocketManager.instance.write("{}")
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        
    }
    
    @IBAction func onBackClicked(_ sender: Any){
        Nav.instance.back(Nav.RESULT_CANCELED, with: nil)
    }
    
    
}
