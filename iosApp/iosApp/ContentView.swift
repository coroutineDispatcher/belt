import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let viewController = MainViewControllerKt.MainViewController()
        let swipeBackGesture = UISwipeGestureRecognizer(target: viewController, action: #selector(viewController.handleSwipe(_:)))
        swipeBackGesture.direction = .right
        viewController.view.addGestureRecognizer(swipeBackGesture)
        return viewController
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

extension UIViewController {
    @objc func handleSwipe(_ gesture: UISwipeGestureRecognizer) {
        if gesture.direction == .right {
            BackGestureListenerKt.backGesture()
        }
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}
