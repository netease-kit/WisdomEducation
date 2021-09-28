//
//  NERecordViewController.swift
//  Pods
//
//  Created by 郭园园 on 2021/7/22.
//

import UIKit
import WebKit
import NEWhiteBoard
import NERecordPlay
import Alamofire

@objc
public class NERecordViewController: UIViewController {

    @objc public var recordData: RecordData?
    var player: NEEduRecorderPlayerManager?
    var recordList: [RecordItem]?
    lazy var contentView = UIView()
    var controlView = NERecordControlView()
    var seeking = false
    var screenShareView: UIView = UIView()
    var infoView = NEEduLessonInfoView()
    var navView = NERecordNavigationView()
    private let cellID = "recordCellID"
    
    private var collectionView: UICollectionView?

    public override func viewDidLoad() {
        super.viewDidLoad()
        setupSubviews()
        createPlayer()
        addNetListen()
    }
    
    func setupSubviews() {
        view.backgroundColor = .white
        navView.backgroundColor = UIColor(red: 26/255.0, green: 32/255.0, blue: 40/255.0, alpha: 1.0)
        self.view.addSubview(navView)
        if #available(iOS 13.0, *) {
            NSLayoutConstraint.activate([
                navView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor),
                navView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor),
                navView.heightAnchor.constraint(equalToConstant: 44),
                navView.topAnchor.constraint(equalTo: self.view.safeAreaLayoutGuide.topAnchor)
            ])
        } else {
            NSLayoutConstraint.activate([
                navView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor),
                navView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor),
                navView.heightAnchor.constraint(equalToConstant: 44),
                navView.topAnchor.constraint(equalTo: self.view.topAnchor)
            ])
        }
        navView.backButton.addTarget(self, action: #selector(backButtonEvent), for: .touchUpInside)
        navView.infoButton.addTarget(self, action: #selector(infoButtonEvent), for: .touchUpInside)
        navView.lessonNameLabel.text = recordData?.snapshotDto.snapshot.room.roomName
        
        contentView.translatesAutoresizingMaskIntoConstraints = false
        self.view.addSubview(contentView)
        NSLayoutConstraint.activate([
            contentView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor),
            contentView.topAnchor.constraint(equalTo: navView.bottomAnchor),
        ])
        
        screenShareView.backgroundColor = .gray
        screenShareView.isHidden = true
        screenShareView.translatesAutoresizingMaskIntoConstraints = false
        self.view.addSubview(screenShareView)
        NSLayoutConstraint.activate([
            screenShareView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
            screenShareView.topAnchor.constraint(equalTo: contentView.topAnchor),
            screenShareView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
            screenShareView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor)
        ])
        
        let layout = UICollectionViewFlowLayout()
        layout.itemSize = CGSize(width: 200, height: 150)
        let collectionView = UICollectionView(frame: CGRect.zero, collectionViewLayout: layout)
        collectionView.backgroundColor = .black
        collectionView.showsVerticalScrollIndicator = false
        collectionView.delegate = self
        collectionView.dataSource = self
        collectionView.translatesAutoresizingMaskIntoConstraints = false
        collectionView.register(NERecordCell.self, forCellWithReuseIdentifier: cellID)
        self.collectionView = collectionView
        self.view.addSubview(collectionView)
        NSLayoutConstraint.activate([
            collectionView.topAnchor.constraint(equalTo: navView.bottomAnchor, constant: 0),
            collectionView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor, constant: 10),
            collectionView.leadingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: 10),
            collectionView.widthAnchor.constraint(equalToConstant: 200),
            collectionView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor)
        ])
        
        controlView.translatesAutoresizingMaskIntoConstraints = false
        self.view.addSubview(controlView)
        if #available(iOS 13.0, *) {
            NSLayoutConstraint.activate([
                controlView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor),
                controlView.topAnchor.constraint(equalTo: contentView.bottomAnchor),
                controlView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor),
                controlView.bottomAnchor.constraint(equalTo: self.view.safeAreaLayoutGuide.bottomAnchor),
                controlView.heightAnchor.constraint(equalToConstant: 68)
            ])
        } else {
            NSLayoutConstraint.activate([
                controlView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor),
                controlView.topAnchor.constraint(equalTo: contentView.bottomAnchor),
                controlView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor),
                controlView.bottomAnchor.constraint(equalTo: self.view.bottomAnchor),
                controlView.heightAnchor.constraint(equalToConstant: 68)
            ])
        }
        
        controlView.playButton.addTarget(self, action: #selector(playButtonEvent), for: .touchUpInside)
        controlView.playButton.isEnabled = false
        controlView.volumeButton.addTarget(self, action: #selector(volumeButtonEvent), for: .touchUpInside)
        controlView.slider.addTarget(self, action: #selector(sliderEvent), for: .valueChanged)
    }

    func createPlayer() {
        guard let data = recordData  else {
            print("error:recordData = nil")
            return
        }
        player = NEEduRecorderPlayerManager(data: data, view: contentView)
        player?.delegate = self
        recordList = player?.playingRecordItems
        player?.prepareToPlay()
    }
    func addNetListen() {
        NetworkReachabilityManager.default?.startListening(onUpdatePerforming: { state in
            print("state:\(state)")
            if state == .notReachable {
                self.navView.netState.image = UIImage.ne_imageName(name: "net_0")
            }else {
                self.navView.netState.image = UIImage.ne_imageName(name: "net_3")
            }
        })
    }
    func stopNetListen() {
        NetworkReachabilityManager.default?.stopListening()
    }
    
    // MARK:Event
    @objc func backButtonEvent(button: UIButton) {
        player?.stop()
        self.dismiss(animated: true, completion: nil)
    }
    
    @objc func infoButtonEvent(button: UIButton) {
        showInfoView()
    }
    
    @objc func playButtonEvent(button: UIButton) {
        print(#function,button)
        button.isSelected = !button.isSelected
        button.isSelected ? player?.play() : player?.pause()
    }
    
    @objc func volumeButtonEvent(button: UIButton) {
        button.isSelected = !button.isSelected
        player?.muteAudio(mute: button.isSelected)
    }
    
    @objc func sliderEvent(slider: UISlider, event: UIEvent) {
        guard let touch: UITouch = event.allTouches?.first else {
            return
        }
        switch touch.phase {
        case .began,.moved:
            seeking = true
        case .ended,.cancelled:
            seeking = false
            player?.seekTo(time: Double(slider.value) * player!.duration)
        default:
            return
        }
    }
//    MARK: Event
    func showInfoView() {
        guard let data = recordData else {
            return
        }
        let infoView = NEEduLessonInfoView()
        var roomId: String = data.snapshotDto.snapshot.room.roomUuid
        infoView.lessonName.text = String(roomId[roomId.startIndex ..< roomId.index(before: roomId.endIndex)])
        infoView.lessonItem.titleLabel.text = recordData?.snapshotDto.snapshot.room.roomUuid
        for member in data.snapshotDto.snapshot.members {
            if member.isTeacher {
                infoView.teacherName.text = member.userName
            }
        }
        self.view.addSubview(infoView)
        NSLayoutConstraint.activate([
            infoView.topAnchor.constraint(equalTo: self.view.topAnchor, constant: 0),
            infoView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor, constant: 0),
            infoView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor, constant: 0),
            infoView.bottomAnchor.constraint(equalTo: self.view.bottomAnchor, constant: 0),
        ])
    }
    
    func hideInfoView() {
        infoView.removeFromSuperview()
    }
    
    
    @objc func audioButtonEvent(button: UIButton) {
        button.isSelected = !button.isSelected
    }
    @objc func videoButtonEvent(button: UIButton) {
        button.isSelected = !button.isSelected
    }
    
    // MARK:Orientation
    public override var shouldAutorotate: Bool {
        return false
    }
    public override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        return .landscapeRight
    }
    public override var preferredInterfaceOrientationForPresentation: UIInterfaceOrientation {
        return .landscapeRight
    }
    
    deinit {
        print("dd")
    }

}

extension NERecordViewController: NERecordCellDelegate {
    
    func audioMute(mute: Bool, url: String) {
        guard let player = self.player?.playerDic[url] else {
            return
        }
        player.muteAudio(mute: mute)
    }
    
    func videoClose(cell: NERecordCell, url: String) {
        guard let player = self.player?.playerDic[url] else {
            return
        }
        if cell.videoButton.isSelected {
            player.view?.removeFromSuperview()
        }else {
            guard let view = player.view else {
                return
            }
            view.frame = CGRect.init(x: 0, y: 0, width: 200, height: 150)
            cell.videoView.addSubview(view)
        }
    }
}

// MARK: NEEduRecordPlayEvent
extension NERecordViewController:NEEduRecordPlayerDelegate {
    
    public func onPrepared(playerItem: Any) {
        print("[VC]:onPrepared")
        controlView.playButton.isEnabled = true
    }
    
    public func onPlay(player: Any) {
        
    }
    
    public func onPause(player: Any) {
        
    }

    public func onSeeked(player: Any, time: Double, errorCode: Int) {
        print("onSeeked:\(time)")
    }
    
    public func onFinished(player: Any) {
        controlView.playButton.isSelected = false
        controlView.slider.setValue(0, animated: false)
        recordList = self.player?.playingRecordItems
        collectionView?.reloadData()
    }
    public func onResetPlayer(player: NEEduRecordPlayerProtocol) {
        screenShareView.isHidden = true
        recordList = self.player?.playingRecordItems
        collectionView?.reloadData()
    }
    
    public func onError(player: Any, errorCode: Int) {
        
    }
    
    public func onPlayTime(player: NEEduRecordPlayerProtocol, time: Double) {
        if seeking {return}
        let progress = Float(time/player.duration)
        controlView.slider.setValue(progress, animated: false)
    }
    public func onSubStreamStart(player: NEEduRecordPlayerProtocol, videoView: UIView?) {
        guard let view = videoView else {
            return
        }
        view.frame = CGRect(x: 0, y: 0, width: screenShareView.bounds.size.width, height: screenShareView.bounds.size.height)
        view.backgroundColor = .red
        screenShareView.isHidden = false
        screenShareView.addSubview(view)
    }
    
    public func onSubStreamStop(player: NEEduRecordPlayerProtocol, videoView: UIView?) {
        guard let view = videoView else {
            return
        }
        screenShareView.isHidden = true
        view.removeFromSuperview()
    }
    
    public func userEnter(item: RecordItem) {
        recordList = player?.playingRecordItems
        self.collectionView?.reloadData()
    }
    
    public func userLeave(item: RecordItem) {
        print("用户离开：\(item.url) 剩余用户：\(player?.playingRecordItems.count)")
        recordList = player?.playingRecordItems
        self.collectionView?.reloadData()
    }
}


// MARK: UICollectionViewDelegate
extension NERecordViewController: UICollectionViewDelegate,UICollectionViewDataSource {
    
    public func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return recordList?.count ?? 0
    }
    
    public func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        let item = (recordList?[indexPath.row])! as RecordItem
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: cellID, for: indexPath) as! NERecordCell
        let role = item.isTeacher() ? "(老师)" : "(学生)"
        cell.nameLabel.text = item.userName == nil ? role : item.userName! + role
        cell.delegate = self
        //view
        let playerItem = player?.playerDic[item.url]
        if cell.url != item.url {
            cell.url = item.url
            if cell.videoView.subviews.first != nil {
                cell.videoView.subviews.first?.removeFromSuperview()
            }
            guard let view = playerItem?.view else {
                return cell
            }
            
            view.frame = CGRect.init(x: 0, y: 0, width: 200, height: 150)
            cell.videoView.addSubview(view)
        }else {
            guard let view = playerItem?.view else {
                return cell
            }
            view.frame = CGRect.init(x: 0, y: 0, width: 200, height: 150)
            cell.videoView.addSubview(view)
        }
        return cell
    }
    
    
}

