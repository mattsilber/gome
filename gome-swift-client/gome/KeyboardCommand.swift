import UIKit

class KeyboardCommand: Command {
    
    private var value: String!
    private var wrappedValues: [String]?
    
    init(_ value: String){
        self.value = value
    }
    
    init(_ value: String, wrappedValues: [String]?){
        self.value = value
        self.wrappedValues = wrappedValues
    }
    
    func getActionId() -> String {
        return "key"
    }
    
    func getData() -> [String: Any] {
        var data = [String: Any]()
        data["type"] = "string"
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
