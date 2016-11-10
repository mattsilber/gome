import UIKit

class KeycodeCommand: Command {
    
    public static let VK_INSERT = 155
    public static let VK_HOME = 36
    public static let VK_END = 35
    public static let VK_ESCAPE = 27
    
    private var value: Int!
    private var wrappedValues: [String]?
    
    init(_ value: Int){
        self.value = value
    }
    
    init(_ value: Int, wrappedValues: [String]?){
        self.value = value
        self.wrappedValues = wrappedValues
    }
    
    func getActionId() -> String {
        return "key"
    }
    
    func getData() -> [String: Any] {
        var data = [String: Any]()
        data["type"] = "action"
        data["value"] = value
        
        if let wrapped = wrappedValues {
            var wrappedData = [String]()
            
            for item in wrapped {
                wrappedData.append(item)
            }
            
            data["wrapped"] = wrappedData
        }
        
        return data
    }
    
}
