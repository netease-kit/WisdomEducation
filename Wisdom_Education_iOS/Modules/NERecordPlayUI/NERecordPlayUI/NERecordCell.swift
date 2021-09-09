//
//  NERecordCell.swift
//  NERecordPlayUI
//
//  Created by 郭园园 on 2021/8/12.
//

import UIKit
import NERecordPlay

protocol NERecordCellDelegate {
    func audioMute(mute: Bool,url: String)
    func videoClose(cell: NERecordCell,url: String)
}

class NERecordCell: UICollectionViewCell {
    var delegate: NERecordCellDelegate?
    var videoView = UIView()
    var greyView = UIView()
    var nameLabel = UILabel()
    var videoButton = UIButton()
    var audioButton = UIButton()
    var url: String?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupSubviews()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    
    func setupSubviews() {
        videoView.backgroundColor = .gray
        videoView.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(videoView)
        NSLayoutConstraint.activate([
            videoView.topAnchor.constraint(equalTo: contentView.topAnchor),
            videoView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
            videoView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
            videoView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),
        ])
        
        greyView.backgroundColor = .gray
        greyView.alpha = 0.6
        greyView.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(greyView)
        NSLayoutConstraint.activate([
            greyView.heightAnchor.constraint(equalToConstant: 30),
            greyView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
            greyView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
            greyView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),
        ])
        
        nameLabel.textColor = .white
        nameLabel.font = UIFont.systemFont(ofSize: 12)
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        greyView.addSubview(nameLabel)
        NSLayoutConstraint.activate([
            nameLabel.topAnchor.constraint(equalTo: greyView.topAnchor),
            nameLabel.leadingAnchor.constraint(equalTo: greyView.leadingAnchor),
            nameLabel.bottomAnchor.constraint(equalTo: greyView.bottomAnchor)
        ])
        
        videoButton.setImage(UIImage.ne_imageName(name: "room_video"), for: .normal)
        videoButton.setImage(UIImage.ne_imageName(name: "room_video_off"), for: .selected)
        videoButton.translatesAutoresizingMaskIntoConstraints = false
        greyView.addSubview(videoButton)
        NSLayoutConstraint.activate([
            videoButton.topAnchor.constraint(equalTo: greyView.topAnchor,constant: 0),
            videoButton.trailingAnchor.constraint(equalTo: greyView.trailingAnchor,constant: -10),
            videoButton.widthAnchor.constraint(equalToConstant: 40),
            videoButton.bottomAnchor.constraint(equalTo: greyView.bottomAnchor)
        ])
        videoButton.addTarget(self, action: #selector(videoButtonEvent), for: .touchUpInside)
        
        audioButton.setImage(UIImage.ne_imageName(name: "room_audio"), for: .normal)
        audioButton.setImage(UIImage.ne_imageName(name: "room_audio_off"), for: .selected)
        audioButton.translatesAutoresizingMaskIntoConstraints = false
        greyView.addSubview(audioButton)
        NSLayoutConstraint.activate([
            audioButton.topAnchor.constraint(equalTo: greyView.topAnchor,constant: 0),
            audioButton.trailingAnchor.constraint(equalTo: videoButton.leadingAnchor,constant: 10),
            audioButton.widthAnchor.constraint(equalToConstant: 40),
            audioButton.bottomAnchor.constraint(equalTo: greyView.bottomAnchor),
            audioButton.leadingAnchor.constraint(equalTo: nameLabel.trailingAnchor)
        ])
        audioButton.addTarget(self, action: #selector(audioButtonEvent), for: .touchUpInside)
    }
    
    @objc func videoButtonEvent(button: UIButton){
        button.isSelected = !button.isSelected
        delegate?.videoClose(cell: self, url: self.url ?? "")
    }
    
    @objc func audioButtonEvent(button: UIButton){
        button.isSelected = !button.isSelected
        delegate?.audioMute(mute: button.isSelected, url: self.url ?? "")
    }
}
