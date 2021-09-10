//
//  NERecordNavigationView.swift
//  Pods
//
//  Created by 郭园园 on 2021/7/22.
//

import UIKit

class NERecordNavigationView: UIView {
    public var backButton: UIButton = UIButton.init(type: .custom)
    public var infoButton = UIButton(type: .custom)
    public var lessonNameLabel = UILabel()
    public var netState = UIImageView(image: UIImage.ne_imageName(name: "net_0"))
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        print("NERecordNavigationView frame")
        commonInit()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        print("NERecordNavigationView coder")
        commonInit()
    }
    
    func commonInit() {
        self.translatesAutoresizingMaskIntoConstraints = false
        backButton.translatesAutoresizingMaskIntoConstraints = false
        backButton.setImage(UIImage.ne_imageName(name: "record_back"), for: .normal)
        backButton.addTarget(self, action: #selector(backButtonEvent), for: .touchUpInside)
        self.addSubview(backButton)
        NSLayoutConstraint.activate([
            backButton.topAnchor.constraint(equalTo: self.topAnchor),
            backButton.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: 30),
            backButton.widthAnchor.constraint(equalToConstant: 60),
            backButton.bottomAnchor.constraint(equalTo: self.bottomAnchor)
        ])
        
        let titleLabel = UILabel.init()
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        titleLabel.text = "课堂回放"
        titleLabel.textColor = .white
        titleLabel.textAlignment = .center
        titleLabel.font = UIFont.systemFont(ofSize: 14.0)
        self.addSubview(titleLabel)
        NSLayoutConstraint.activate([
            titleLabel.topAnchor.constraint(equalTo: self.topAnchor),
            titleLabel.centerXAnchor.constraint(equalTo: self.centerXAnchor),
            titleLabel.widthAnchor.constraint(equalToConstant: 120),
            titleLabel.bottomAnchor.constraint(equalTo: self.bottomAnchor)
        ])
        
        netState.translatesAutoresizingMaskIntoConstraints = false
        netState.contentMode = .center
        self.addSubview(netState)
        NSLayoutConstraint.activate([
            netState.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: -36),
            netState.centerYAnchor.constraint(equalTo: self.centerYAnchor),
            netState.widthAnchor.constraint(equalToConstant: 40),
            netState.heightAnchor.constraint(equalToConstant: 40)
        ])
        
        infoButton.translatesAutoresizingMaskIntoConstraints = false
        infoButton.setImage(UIImage.ne_imageName(name: "room_info"), for: .normal)
        infoButton.addTarget(self, action: #selector(backButtonEvent), for: .touchUpInside)
        self.addSubview(infoButton)
        NSLayoutConstraint.activate([
            infoButton.topAnchor.constraint(equalTo: netState.topAnchor),
            infoButton.trailingAnchor.constraint(equalTo: netState.leadingAnchor, constant: -10),
            infoButton.widthAnchor.constraint(equalToConstant: 40),
            infoButton.bottomAnchor.constraint(equalTo: netState.bottomAnchor)
        ])
        
        lessonNameLabel.translatesAutoresizingMaskIntoConstraints = false
        lessonNameLabel.font = UIFont.systemFont(ofSize: 14)
        lessonNameLabel.textAlignment = .right
        lessonNameLabel.textColor = UIColor(red: 180/255.0, green: 191/255.0, blue: 208/255.0, alpha: 1.0)
        addSubview(lessonNameLabel)
        NSLayoutConstraint.activate([
            lessonNameLabel.topAnchor.constraint(equalTo: self.topAnchor),
            lessonNameLabel.trailingAnchor.constraint(equalTo: infoButton.leadingAnchor, constant: -10),
            lessonNameLabel.leadingAnchor.constraint(equalTo: titleLabel.trailingAnchor, constant: 0),
            lessonNameLabel.bottomAnchor.constraint(equalTo: self.bottomAnchor)
        ])
    }
    
//Mark:-
    @objc func backButtonEvent() {
        
    }


}
