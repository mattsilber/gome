import SocketIO

class SocketManager {
    
    public static var instance = SocketManager()
    private init(){ }
    
    private var client: SocketIOClient!
//    private var socket: 
    private var connected = false
    
    func connect(_ url: String, port: String){
        if !connected {
            self.client = SocketIOClient(socketURL: URL(string: "\(url):\(port)")!, config: [.log(true), .forcePolling(true)])
            
            self.client.on("connect") { data, ack in
                print("Socket connected")
            }
            
            self.client.connect()
            self.connected = true
        }
    }
    
    func write(_ data: String){
        if connected {
            client.emit("data", data)
        }
    }
    
    func disconnect(){
        client.disconnect()
        connected = false
    }
    
}
