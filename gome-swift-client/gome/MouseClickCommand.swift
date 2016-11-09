import UIKit

class MouseClickCommand: Command {
    
    private var type: String!
    
    init(_ type: String){
        self.type = type
    }
    
    func getActionId() -> String {
        return "mouse"
    }
    
    func getData() -> [String: Any] {
        var data = [String: Any]()
        data["type"] = type
        
        return data
    }
    
}
