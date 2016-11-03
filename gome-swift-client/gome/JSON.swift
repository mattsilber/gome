import UIKit

class JSON {
    
    class func convert(fromData value: Data) -> [String: Any]? {
        do {
            return try JSONSerialization.jsonObject(with: value, options: []) as? [String: Any]
        } catch let error as NSError {
            print(error)
        }
        
        return nil
    }
    
    class func convert(fromJsonString value: String) -> [String: Any]? {
        if let data = value.data(using: String.Encoding.utf8) {
            return JSON.convert(fromData: data)
        }
        
        return nil
    }
    
    class func convert(fromDictionary value: [String: Any]) -> String? {
        do {
            let data = try JSONSerialization.data(withJSONObject: value, options: [])
            
            return String(data: data, encoding: String.Encoding.utf8)
        } catch let error as NSError {
            print(error)
        }
        
        return nil
    }
    
}
