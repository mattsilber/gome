import UIKit

class Debouncer {
    
    var runnable: (() -> Void)?
    
    private var timer: Timer?
    private let interval: TimeInterval
    
    init(_ interval: TimeInterval) {
        self.interval = interval
    }
    
    init(_ interval: TimeInterval, _ runnable: @escaping () -> Void) {
        self.interval = interval
        self.runnable = runnable
    }
    
    func trigger() {
        timer?.invalidate()
        timer = Timer.scheduledTimer(timeInterval: interval, target: self, selector: #selector(handleTimer), userInfo: nil, repeats: false)
    }
    
    @objc private func handleTimer(_ timer: Timer) {
        runnable?()
    }
    
}
