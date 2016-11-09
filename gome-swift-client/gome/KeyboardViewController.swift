import UIKit

class KeyboardViewController: UIViewController, Navigable {
    
    private let ACTION__ALT = "ALT"
    private let ACTION__CONTROL = "CTRL"
    private let ACTION__SHIFT = "SHIFT"
    
    private var wrappedValues = [String]()

    func onControllerOpened(_ from: Nav.Route, params: [String: Any]?) {
        
    }
    
    func onControllerResult(_ from: Nav.Route, result: Int, with: [String: Any]?) {
        
    }
    
    func getBeforeFilters() -> [Resolvable]{
        return [Resolvable]()
    }
    
    @IBAction func onKeyEnterClicked(_ sender: Any){
        sendKey("\n")
    }
    
    @IBAction func onKeyDeleteClicked(_ sender: Any){
        sendKey("\\b")
    }
    
    @IBAction func onKeyTabClicked(_ sender: Any){
        sendKey("\t")
    }
    
    private func sendKey(_ key: String){
        SocketManager.instance.write(KeyboardCommand(key, wrappedValues: wrappedValues))
        wrappedValues = [String]()
    }
    
    @IBAction func onKeyAltClicked(_ sender: Any){
        toggle(ACTION__ALT)
    }
    
    @IBAction func onKeyControlClicked(_ sender: Any){
        toggle(ACTION__CONTROL)
    }
    
    @IBAction func onKeyShiftClicked(_ sender: Any){
        toggle(ACTION__SHIFT)
    }
    
    private func toggle(_ key: String){
        if wrappedValues.contains(key) {
            wrappedValues.remove(at: wrappedValues.index(of: key)!)
        } else {
            wrappedValues.append(key)
        }
    }
    
    @IBAction func onBackClicked(_ sender: Any){
        Nav.instance.back(Nav.RESULT_CANCELED, with: nil)
    }

}
