//
//  Image+NERecord.swift
//  NERecordPlayUI
//
//  Created by 郭园园 on 2021/8/9.
//

import UIKit

extension UIImage {
    class func ne_imageName(name: String) -> UIImage? {
        let path = Bundle.init(for: NERecordViewController.self).resourcePath
        guard let resourcePath = path else {
            return nil
        }
        let imagePath = resourcePath + "/NERecordPlayUIBundle.bundle"
        return UIImage.init(named: name, in: Bundle.init(path: imagePath), compatibleWith: nil)
    }
}


