import UIKit

class TrackpadView: UIView {
    
    static let MULTI_FACTOR = 5.50 as CGFloat
    
    var movementCallback: ((_ dx: CGFloat, _ dy: CGFloat) -> Void)?
    var clickCallback: (() -> Void)?
    
    var lastTouch: CGPoint?
    var touchDownSeconds: Double = 0
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let touch = touches.first {
            self.lastTouch = touch.location(in: self)
            self.touchDownSeconds = NSDate().timeIntervalSince1970
        }
    }
    
    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let touch = touches.first {
            let current = touch.location(in: self)
            
            if let last = lastTouch {
                if let moveCallback = movementCallback {
                    moveCallback((current.x - last.x) * TrackpadView.MULTI_FACTOR, (current.y - last.y) * TrackpadView.MULTI_FACTOR)
                }
            }
            
            self.lastTouch = current
        }
    }
    
    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.lastTouch = nil
        
        if NSDate().timeIntervalSince1970 - self.touchDownSeconds < 0.270 {
            clickCallback?()
        }
    }
    
}
