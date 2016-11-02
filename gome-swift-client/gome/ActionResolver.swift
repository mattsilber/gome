class ActionResolver {
    
    class func resolve(_ items: [Resolvable], success: @escaping () -> Void, fail: (() -> Void)?){
        for item in items {
            if item.isResolutionRequired() {
                item.resolve(success: { () -> Void in
                    ActionResolver.resolve(items, success: success, fail: fail)
                    }, fail: { () -> Void in
                        if fail != nil {
                            fail!()
                        }
                })
                
                return;
            }
        }
        
        success()
    }
    
}
