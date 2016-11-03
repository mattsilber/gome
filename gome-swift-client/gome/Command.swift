protocol Command {
    
    func getActionId() -> String;
    
    func getData() -> [String: Any];
    
}
