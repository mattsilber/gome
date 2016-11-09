import UIKit

class MouseMoveCommand: Command {
    
    private var type: String!
    private var dx: Int!
    private var dy: Int!
    
    init(_ type: String, dx: CGFloat, dy: CGFloat){
        self.type = type
        self.dx = Int(dx)
        self.dy = Int(dy)
    }
    
    func getActionId() -> String {
        return "mouse"
    }
    
    func getData() -> [String: Any] {
        var data = [String: Any]()
        data["type"] = type
        data["mouse_x"] = dx
        data["mouse_y"] = dy
        
        return data
    }
    
}
