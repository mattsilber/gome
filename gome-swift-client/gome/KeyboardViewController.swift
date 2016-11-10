import UIKit

class KeyboardViewController: UIViewController, Navigable {
    
    private let ACTION__ALT = "ALT"
    private let ACTION__CONTROL = "CTRL"
    private let ACTION__SHIFT = "SHIFT"
    
    @IBOutlet weak var keyAlt: UIButton!
    @IBOutlet weak var keyControl: UIButton!
    @IBOutlet weak var keyShift: UIButton!
    
    private var wrappedValues = [String]()

    func onControllerOpened(_ from: Nav.Route, params: [String: Any]?) {
        self.modalPresentationStyle = .overCurrentContext
    }
    
    func onControllerResult(_ from: Nav.Route, result: Int, with: [String: Any]?) {
        
    }
    
    func getBeforeFilters() -> [Resolvable]{
        return [Resolvable]()
    }
    
    @IBAction func onKeyEnterClicked(_ sender: Any){
        sendValue(text: "\n")
    }
    
    @IBAction func onKeyDeleteClicked(_ sender: Any){
        sendValue(text: "\\b")
    }
    
    @IBAction func onKeyTabClicked(_ sender: Any){
        sendValue(text: "\t")
    }
    
    @IBAction func onKeyEscapeClicked(_ sender: Any){
        sendValue(number: KeycodeCommand.VK_ESCAPE)
    }
    
    @IBAction func onKeyHomeClicked(_ sender: Any){
        sendValue(number: KeycodeCommand.VK_HOME)
    }
    
    @IBAction func onKeyEndClicked(_ sender: Any){
        sendValue(number: KeycodeCommand.VK_END)
    }
    
    @IBAction func onKeyInsertClicked(_ sender: Any){
        sendValue(number: KeycodeCommand.VK_INSERT)
    }
    
    private func sendValue(text: String){
        SocketManager.instance.write(KeyboardCommand(text, wrappedValues: wrappedValues))
        unwrapActions()
    }
    
    private func sendValue(number: Int){
        SocketManager.instance.write(KeycodeCommand(number, wrappedValues: wrappedValues))
        unwrapActions()
    }
    
    private func unwrapActions(){
        wrappedValues = [String]()
        
        keyAlt.backgroundColor = nil
        keyControl.backgroundColor = nil
        keyShift.backgroundColor = nil
    }
    
    @IBAction func onKeyAltClicked(_ sender: Any){
        toggle(ACTION__ALT, keyAlt)
    }
    
    @IBAction func onKeyControlClicked(_ sender: Any){
        toggle(ACTION__CONTROL, keyControl)
    }
    
    @IBAction func onKeyShiftClicked(_ sender: Any){
        toggle(ACTION__SHIFT, keyShift)
    }
    
    private func toggle(_ key: String, _ button: UIButton){
        if wrappedValues.contains(key) {
            wrappedValues.remove(at: wrappedValues.index(of: key)!)
            button.backgroundColor = nil
        } else {
            wrappedValues.append(key)
            button.backgroundColor = UIColor.darkGray
        }
    }
    
    @IBAction func onBackClicked(_ sender: Any){
        Nav.instance.back(Nav.RESULT_CANCELED, with: nil)
    }

}
