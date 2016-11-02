protocol Navigable {
    
    // Will be called when Nav opens a new controller, right before it's been presented. Any data the controller needs can be passed in before hand, and, by convention that I'm defining, we should define those keys as static Strings within the specific UIViewController subclass
    func onControllerOpened(_ from: Nav.Route, params: [String: Any]?)
    
    // Will be called when Nav returns to a ViewController. One very important note: do NOT alter any Views or navigate from here. Events touching the UI Thread must be postponed until the view actually appears (i.e. viewDidAppear ). See PendingEvents for easy trigger solution.
    func onControllerResult(_ from: Nav.Route, result: Int, with: [String: Any]?)
    
    // Gives the Nav a chance to check required Resolvables before actually launching
    func getBeforeFilters() -> [Resolvable]
    
    
}
