import UIKit

class Nav {
    
    static var instance = Nav()
    private init(){ }
    
    enum Route: String {
        case HOME = "/"
        case ALERT = "/alert"
        case CONTROLLER = "/controller"
    }
    
    static let RESULT_CANCELED = 0;
    static let RESULT_OK = 1;
    static let RESULT_FORCE_CLOSE = 2;
    
    private var stack = [Routable]()
    
    func takeRootOwnership(_ view: UIViewController) {
        stack = [Routable]()
        
        stack.append(Routable(.HOME, controller: view, params: nil))
    }
    
    func open(_ route: Route, params: [String: Any]?, onLoad: ((_ controller: UIViewController) -> Void)?) {
        if stack.count < 1 {
            preconditionFailure("A root controller must call takeRootOwnership before opening!")
        }
        
        let controller = buildController(route)
        
        if let navigable = (controller as? Navigable) {
            navigable.onControllerOpened(stack[stack.count - 1].route, params: params)
            
            ActionResolver.resolve(navigable.getBeforeFilters(), success: {
                self.stack.append(Routable(route, controller: controller, params: params))
                
                self.stack[self.stack.count - 2].controller.present(controller, animated: true, completion: {
                    onLoad?(controller)
                })
                }, fail: nil)
        } else {
            preconditionFailure("Loaded views must implement Navigable whether they will use the nav methods or not")
        }
    }
    
    func open(_ view: UIViewController, fromUrl: String){
        // Handle that later for deep linking to match with route regex
    }
    
    func back(_ result: Int, with: [String: Any]?) {
        self.back(result, with: with, animated: true)
    }
    
    func back(_ result: Int, with: [String: Any]?, animated: Bool) {
        if 1 < stack.count {
            popLastRoutable(animated, onRemoved: { last in
                DispatchQueue.main.async {
                    (self.stack[self.stack.count - 1].controller as! Navigable).onControllerResult(last.route, result: result, with: with)
                }
            })
        } else {
            preconditionFailure("Why would you try to pop the root, bro?")
        }
    }
    
    func backTo(_ to: Route, result: Int, with: [String: Any]?) {
        if 1 < stack.count {
            let last = popLastRoutable(false, onRemoved: { last in
                self.popUntil(to, onRemoved: {
                    DispatchQueue.main.async {
                        (self.stack[self.stack.count - 1].controller as! Navigable).onControllerResult(last.route, result: result, with: with)
                    }
                })
            })
        } else {
            preconditionFailure("Why would you try to pop the root, bro?")
        }
    }
    
    private func popUntil(_ to: Route, onRemoved: @escaping (() -> Void)){
        if 1 < stack.count && stack[stack.count - 1].route != to {
            popLastRoutable(false, onRemoved: { last in
                self.popUntil(to, onRemoved: onRemoved)
            })
        } else {
            onRemoved()
        }
    }
    
    private func popLastRoutable(_ animated: Bool, onRemoved: @escaping ((_ routable: Routable) -> Void)) {
        let last = stack[stack.count - 1]
        
        stack.removeLast()
        
        print("Just popped \(last.route.rawValue)")
        
        last.controller.dismiss(animated: animated, completion: { onRemoved(last) })
    }
    
    private func buildController(_ route: Route) -> UIViewController {
        switch route {
        case .CONTROLLER:
            return ControllerViewController(nibName: "ControllerView", bundle: nil)
//        case .EDUCATION:
//            return buildStoryboard("Education", controllerName: "EducationViewController")
        default:
            preconditionFailure("No route defined for: \(route.rawValue)")
        }
    }
    
    private func buildStoryboard(_ title: String, controllerName: String) -> UIViewController {
        let storyboard = UIStoryboard(name: title, bundle: nil)
        return storyboard.instantiateViewController(withIdentifier: controllerName)
    }
    
    private struct Routable {
        internal var route: Route!
        internal var controller: UIViewController!
        internal var params: [String: Any]?
        
        init(_ route: Route, controller: UIViewController, params: [String: Any]?){
            self.route = route
            self.controller = controller
            self.params = params
        }
    }
    
    
    
}
