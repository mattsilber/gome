import UIKit

class SocketManager {
    
    public static var instance = SocketManager()
    private init(){ }
    
    private var inputStream: InputStream!
    private var outputStream: OutputStream!
    
    private var connected = false
    
    func connect(_ url: String, port: String){
        if !connected {
            var readStream:  Unmanaged<CFReadStream>?
            var writeStream: Unmanaged<CFWriteStream>?
            
            CFStreamCreatePairWithSocketToHost(nil, url as CFString, UInt32(port)!, &readStream, &writeStream)
            
            self.inputStream = readStream!.takeRetainedValue()
            self.outputStream = writeStream!.takeRetainedValue()
            
            self.inputStream.schedule(in: RunLoop.current, forMode: RunLoopMode.defaultRunLoopMode)
            self.outputStream.schedule(in: RunLoop.current, forMode: RunLoopMode.defaultRunLoopMode)
            
            print("Opening streams to \(url)")
            
            self.inputStream.open()
            self.outputStream.open()
            
            self.connected = true
            
            print("Sending device info")
            
            var deviceInfo = [String: Any]()
            deviceInfo["name"] = "Test"
            
            write(json: deviceInfo)
            
            print("Wrote device info")
        }
    }
    
    func write(json data: [String: Any]){
        write(JSON.convert(fromDictionary: data)!)
    }
    
    func write(_ command: String, data: [String: Any]){
        write(command + ":" + JSON.convert(fromDictionary: data)!)
    }
    
    func write(_ data: String){
        if connected {
            let formattedData = (data + "\n").data(using: .utf8, allowLossyConversion: false)!
            
            let bytesWritten = formattedData.withUnsafeBytes { outputStream.write($0, maxLength: formattedData.count) }
            
            print("Wrote \(data)")
        }
    }
    
    func disconnect(){
        inputStream.close()
        outputStream.close()
        
        connected = false
    }
    
}
