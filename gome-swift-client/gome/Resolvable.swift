protocol Resolvable {
    
    func isResolutionRequired() -> Bool;
    
    func resolve(success: @escaping (() -> Void), fail: (() -> Void)?) -> Void;
    
}
