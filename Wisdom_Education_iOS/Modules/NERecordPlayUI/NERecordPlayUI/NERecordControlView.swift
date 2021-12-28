//
//  NERecordControlView.swift
//  NERecordPlayUI
//
//  Created by 郭园园 on 2021/8/17.
//

import UIKit

class NERecordControlView: UIView {
    public var playButton = UIButton(type: .custom)
    public var slider = UISlider()
    public var volumeButton = UIButton(type: .custom)
    public var progressLabel = UILabel()

    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupSubviews()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    func setupSubviews() {
        let bgColor = UIColor(red: 26/255.0, green: 32/255.0, blue: 40/255.0, alpha: 1.0)
        let blueColor = UIColor(red: 64/255.0, green: 118/255.0, blue: 246/255.0, alpha: 1.0)
        backgroundColor = bgColor
        playButton.translatesAutoresizingMaskIntoConstraints = false
        playButton.setImage(UIImage.ne_imageName(name: "record_play"), for: .normal)
        playButton.setImage(UIImage.ne_imageName(name: "record_pause"), for: .selected)
        addSubview(playButton)
        NSLayoutConstraint.activate([
            playButton.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: 30),
            playButton.centerYAnchor.constraint(equalTo: self.centerYAnchor),
            playButton.widthAnchor.constraint(equalToConstant: 42),
            playButton.heightAnchor.constraint(equalToConstant: 42)
        ])
        
        slider.translatesAutoresizingMaskIntoConstraints = false
        slider.minimumValue = 0
        slider.maximumValue = 1
        slider.minimumTrackTintColor = blueColor
        slider.maximumTrackTintColor = .black
        addSubview(slider)
        NSLayoutConstraint.activate([
            slider.leadingAnchor.constraint(equalTo: playButton.trailingAnchor, constant: 24),
            slider.centerYAnchor.constraint(equalTo: self.centerYAnchor),
            slider.heightAnchor.constraint(equalToConstant: 40)
        ])
        
        progressLabel.translatesAutoresizingMaskIntoConstraints = false
        progressLabel.text = "00:00/00:00"
        progressLabel.font = .systemFont(ofSize: 12.0)
        progressLabel.textAlignment = .right
        progressLabel.textColor = .white
        addSubview(progressLabel)
        NSLayoutConstraint.activate([
            progressLabel.trailingAnchor.constraint(equalTo: slider.trailingAnchor, constant:0),
            progressLabel.topAnchor.constraint(equalTo: slider.centerYAnchor, constant: 0),
            progressLabel.widthAnchor.constraint(equalToConstant: 100),
            progressLabel.heightAnchor.constraint(equalToConstant: 22)
        ])
        
        volumeButton.translatesAutoresizingMaskIntoConstraints = false
        volumeButton.setImage(UIImage.ne_imageName(name: "record_volume"), for: .normal)
        volumeButton.setImage(UIImage.ne_imageName(name: "record_volume_mute"), for: .selected)
        addSubview(volumeButton)
        NSLayoutConstraint.activate([
            volumeButton.leadingAnchor.constraint(equalTo: slider.trailingAnchor, constant: 24),
            volumeButton.centerYAnchor.constraint(equalTo: self.centerYAnchor),
            volumeButton.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: -30),
            volumeButton.widthAnchor.constraint(equalToConstant: 42),
            volumeButton.heightAnchor.constraint(equalToConstant: 42)
        ])
    }
}
