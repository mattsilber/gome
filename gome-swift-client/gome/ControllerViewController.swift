import UIKit

class ControllerViewController: UIViewController , Navigable {
    
    public static let PARAM__IP_ADDRESS = "ip_address"
    
    @IBOutlet weak var ipHeaderLabel: UILabel!
    
    @IBOutlet weak var moveTrackpad: TrackpadView!
    
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
        
        moveTrackpad.movementCallback = { SocketManager.instance.write(MouseMoveCommand("move", dx: $0, dy: $1)) }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        SocketManager.instance.connect(ipAddress!, port: "13337")
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        SocketManager.instance.disconnect()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        
    }
    
    @IBAction func onBackClicked(_ sender: Any){
        Nav.instance.back(Nav.RESULT_CANCELED, with: nil)
    }
    
    
}
