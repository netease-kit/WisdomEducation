# 云信智慧云课堂组件（iOS）

智慧云课堂（PaaS 方案）是网易云信提供的在线互动课堂场景解决方案。基于在线教育的常见场景，网易云信提供智慧云课堂开源项目，为您演示 1 对 1、小班课和大班课的典型方案。您可以直接基于我们的 Demo 修改适配，也可以参考 Demo，自行集成云信 IM SDK、音视频通话 2.0 NERTC SDK 和互动白板 SDK，实现在线教育场景。

效果展示：

课堂页面：

<img src="https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2FiOS%2FEdu%2Flesson.PNG" alt="IMG_4826" style="zoom: 40%;" />

聊天页面：

<img src="https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2FiOS%2FEdu%2F%20chat.PNG" alt="IMG_4825" style="zoom:40%;" />

课堂成员管理页面：

<img src="https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2FiOS%2FEdu%2Fmembers.png" alt="screenshot-20210705-161251" style="zoom:47%;" />

## 功能介绍

- 开始/结束上课
- 音视频通话
- 屏幕共享
- 白板绘制
- 聊天互动
- 举手上台
- 教师对学生权限控制（授予/收回白板编辑权限、授予/收回屏幕共享权限、开关音视频、全体禁言）

## 环境准备

1. 登录[网易云控制台](https://app.yunxin.163.com/index?clueFrom=nim&from=nim#/)，点击【应用】>【创建】创建自己的App，在【功能管理】以下功能：

   1. 产品服务：音视频通话 2.0、IM 专业版、聊天室、信令、互动白板、云端录制、点播。
   2. 产品功能：
      1. 音视频通话 2.0 的云端录制和抄送功能。
      2. 互动白板的文档转码和云端录制功能。
   3. 音视频通话 2.0 抄送：1-房间启动、2-房间结束、3-房间录制文件下载信息抄送 、4-用户进入房间、5-用户离开房间、8-房间时长抄送。

2. 在控制台中【App Key管理】获取App Key。

3. 下载[本项目](https://github.com/netease-kit/WisdomEducation)替换`KeyCenter.h`中的AppKey为您申请的AppKey。

   **说明：**

   开通相关产品与抄送，请联系[联系云信商务经理](https://yunxin.163.com/bizQQWPA.html)。如果仅需要本地跑通示例项目，简单体验智慧云课堂，您可以使用[智慧云课堂体验账号](https://github.com/netease-kit/WisdomEducation/tree/main/Wisdom_Education_Docs)。体验账号已开通相关权限与抄送，课堂时长限制为 30 分钟。体验账号仅供开发者体验与测试，请勿在线上环境中使用。

## 运行示例项目

1. 在 GitHub 的 [WisdomEducation](https://github.com/netease-kit/WisdomEducation) 示例项目 下载 Demo 源码工程。

2. 在 Podfile 所在文件夹中打开终端，执行命令 `pod install`。

3. 完成安装后，通过 Xcode 打开工程 `WisdomEducation.xcworkspace`。

4. 在示例项目中配置相关字段。

   如果需要基于 Demo 开发自己的应用，在 `keyCenter.m` 中将以下字段改为您的真实信息。

   | 配置项        | 说明                                       |
   | ------------- | ------------------------------------------ |
   | appId         | 应用的 AppId。可以在网易云信控制台中查看。 |
   | authorization | 调用服务端接口时，请求头中的校验参数。     |

   **说明：**如果仅需要本地跑通示例项目，您可以使用[智慧云课堂体验账号](https://github.com/netease-kit/WisdomEducation/tree/main/Wisdom_Education_Docs)。体验账号的课堂时长限制为 30 分钟。

## 功能实现

示例项目结构：

| 文件夹/文件               | 说明              |
| ------------------------- | ----------------- |
| WisdomEducation/keyCenter | 配置AppKey        |
| WisdomEducation/Enter     | 教师\学生登录页面 |
| EduLogic                  | 课堂页面逻辑模块  |
| EduUI                     | 课堂页面UI模块    |
| NEWhiteBoard              | 白板功能模块      |

教育组件功能模块：

<img src="https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2FiOS%2FEdu%2Flayer.jpg" alt="functionLayer(iOS)" style="zoom:50%;" />

**EduUI：**包含教育组件的UI的实现，包括1v1、小班课、大班课场景的ViewController、View以及model部分。

**EduLogic：**是依赖云信的音视频SDK、IMSDK以及白板SDK对于教育逻辑的实现，分别对应NEEduVideoService、NEEduIMService、。

集成组件

### 复用UI：

#### 步骤1:集成组件

1. 使用Xcode创建工程，进入`工程名.xcodeproj`同级目录，复制示例项目中的Modules至当前目录。

2. 执行`pod init`，创建Podfile文件。

3. 编辑Podfile文件并执行`pod install`：

   ```objc
   platform :ios, '10.0'
   target '工程名' do
     use_frameworks!
     pod 'EduUI', :path => 'Modules/EduUI/EduUI.podspec'
     pod 'EduLogic', :path => 'Modules/EduLogic/EduLogic.podspec'
     pod 'NEWhiteBoard', :path => 'Modules/NEWhiteBoard/NEWhiteBoard.podspec'
     pod 'NEScreenShareBroadcaster', '~> 0.1.0'
   ```

4. 以创建一对一场景老师页面为例，导入`EduUI/Room/Viewcontroller/NEEduOneMemberTeacherVC.h`,创建`NEEduOneMemberTeacherVC`对象即可，具体使用方式可参考示例项目。

### 自定义UI：


#### 步骤1:集成组件

1. 使用Xcode创建工程，进入`工程名.xcodeproj`同级目录，复制示例项目中的Modules至当前目录。

2. 执行`pod init`，创建Podfile文件。

3. 编辑Podfile文件并执行`pod install`：

   ```objc
   platform :ios, '10.0'
   target '工程名' do
     use_frameworks!
     pod 'EduLogic', :path => 'Modules/EduLogic/EduLogic.podspec'
     pod 'NEWhiteBoard', :path => 'Modules/NEWhiteBoard/NEWhiteBoard.podspec'
     pod 'NEScreenShareBroadcaster', '~> 0.1.0'
   ```

#### 步骤2:添加权限

1. 在`Info.plist`文件中添加相机、麦克风访问权限：

   ```
   Privacy - Camera Usage Description
   Privacy - Microphone Usage Description
   ```

2. 在工程的`Signing&Capabilities`添加`Background Modes`，并勾选`Audio、Airplay、and Picture in Picture`。


#### 步骤3:初始化EduLogic组件

```objc
NEEduKitOptions *option = [[NEEduKitOptions alloc] init];
option.authorization = [KeyCenter authorization];
option.baseURL = [KeyCenter baseURL];
[[EduManager shared] setupAppKey:[KeyCenter appKey] options:option];
```

#### 步骤4:用户登录

```objc
__weak typeof(self)weakSelf = self;
[[EduManager shared] login:nil success:^(NEEduUser * _Nonnull user) {
  __strong typeof(self)strongSelf = weakSelf;
  //登录成功，创建房间
  [strongSelf createRoom];
} failure:^(NSError * _Nonnull error) {
        //登录失败处理逻辑
}];
```

#### 步骤5:创建房间

```objc
NEEduRoom *room = [[NEEduRoom alloc] init];
    room.roomName = [NSString stringWithFormat:@"%@的课堂",self.nicknameView.text];
    room.sceneType = self.lessonType;
    switch (room.sceneType) {
        case NEEduSceneType1V1:
        {
            room.configId = 5;
        }
            break;
        case NEEduSceneTypeSmall:
        {
            room.configId = 6;
        }
            break;
        case NEEduSceneTypeBig:
        {
            room.configId = 7;
        }
            break;
        default:
            break;
    }
    room.roomUuid = [NSString stringWithFormat:@"%@%d",self.lessonIdView.text,room.configId];
    __weak typeof(self)weakSelf = self;
    [[EduManager shared].roomService createRoom:room completion:^(NEEduCreateRoomRequest *result,NSError * _Nonnull error) {
       // 创建结果处理
    }];
```

#### 步骤6:加入房间

```objc
NEEduEnterRoomParam *room = [[NEEduEnterRoomParam alloc] init];
    room.autoPublish = YES;
    room.autoSubscribeVideo = YES;
    room.autoSubscribeAudio = YES;
    room.roomUuid = resRoom.roomUuid;
    room.roomName = resRoom.roomName;
    room.sceneType = self.lessonType;
    room.role = NEEduRoleTypeStudent;
    room.userName = @"";
    __weak typeof(self)weakSelf = self;
    [[EduManager shared] enterClassroom:room success:^(NEEduRoomProfile * _Nonnull roomProfile) {
        // 进入成功处理
    } failure:^(NSError * _Nonnull error) {
        // 进入失败处理
}
```



## EduLogic API

**EduLogic**组件的 API 接口列表如下：

- `EduManager`单例类，设置SDK的配置信息，持有子功能对象。

| 接口                            | 备注       |
| ------------------------------- | ---------- |
| setupAppKey:options:            | 初始化组件 |
| login:success:failure:          | 登录账户   |
| enterClassroom:success:failure: | 加入课堂   |
| setCanvasView:forMember:        | 设置画布   |
| leaveClassroom                  | 离开课堂   |
| destoryClassroom                | 销毁对象   |

- `NEEduVideoService`：音视频管理类。

| 接口                       | 备注                                                         |
| -------------------------- | ------------------------------------------------------------ |
| setupAppkey:               | 设置音视频SDK的Appkey，EduManager的setupAppKey:options:方法中已调用。 |
| joinChannel: completion:   | 加入音视频房间，EduManager的enterClassroom:success:failure:中已调用 |
| setupLocalVideo:           | 设置本地视频画布，EduManager的setCanvasView:forMember:方法中已调用。 |
| setupRemoteVideo:          | 设置远端视频画布，EduManager的setCanvasView:forMember:方法中已调用。 |
| setupSubStreamVideo:       | 设置辅流视频画布                                             |
| enableLocalAudio:          | 开关音频                                                     |
| enableLocalVideo:          | 开关视频                                                     |
| muteLocalVideo:            | 取消/发送视频，不开关硬件                                    |
| muteLocalAudio:            | 取消/发送音频，不开关硬件                                    |
| subscribeVideo: forUserID: | 订阅用户的视频                                               |
| subscribeAudio: forUserID: | 订阅用户的音频                                               |
| leaveChannel               | 离开音视频房间，EduManager的leaveClassroom方法中已调用。     |
| destroy                    | 销毁对象，EduManager的destoryClassroom方法中已调用。         |

- `NEEduIMService`：聊天、信令管理类。

  | 接口                                     | 备注                                                         |
  | ---------------------------------------- | ------------------------------------------------------------ |
  | setupAppkey:                             | 设置IMSDK的AppKey，EduManager的setupAppKey:options:方法中已调用。 |
  | login: token: completion:                | 登录IMSDK，EduManager的login:success:failure:方法中已调用。  |
  | logoutWithCompletion:                    | 登出IMSDK。                                                  |
  | enterChatRoomWithParam: success: failed: | 加入聊天室，EduManager的enterClassroom:success:failure:方法中已调用。 |
  | exitChatroom: completion:                | 退出聊天室。                                                 |
  | sendChatroomTextMessage:                 | 发送聊天文字消息                                             |
  | sendChatroomImageMessage:                | 发送聊天图片消息                                             |
  | fetchChatroomInfo:                       | 获取聊天室信息                                               |
  | destroy                                  | 销毁对象                                                     |
  |                                          |                                                              |
  | 回调方法                                 | 备注                                                         |
  | didSendMessage: error:                   | 消息已发送                                                   |
  | didRecieveChatMessages:                  | 接受新消息                                                   |
  | didRecieveSignalMessage: fromUser:       | 接受信令通知                                                 |

- `NEEduMessageService`：信令通知分发类。

  | 接口                                    | 备注                        |
  | --------------------------------------- | --------------------------- |
  | updateProfile:                          | 初始化课堂快照数据          |
  |                                         |                             |
  | 回调方法                                | 备注                        |
  | onUserInWithUser: members:              | 用户进入课堂                |
  | onUserOutWithUser: members:             | 用户离开课堂                |
  | onVideoStreamEnable: user:              | 用户视频开启/关闭           |
  | onAudioStreamEnable: user:              | 用户音频频开启/关闭         |
  | onSubVideoStreamEnable:user:            | 用户辅流开启/关闭           |
  | onAudioAuthorizationEnable: user：      | 用户音频被授权开启/关闭     |
  | onVideoAuthorizationEnable: user：      | 用户视频被授权开启/关闭     |
  | onWhiteboardAuthorizationEnable: user:  | 用户被授予/取消白板编辑权限 |
  | onScreenShareAuthorizationEnable: user: | 用户被授予/取消屏幕共享权限 |
  | onHandsupStateChange: user:             | 用户举手状态变更            |
  | onLessonStateChange: roomUuid:          | 课堂状态变更                |
  | onLessonMuteAllAudio: roomUuid:         | 全体静音回调                |
  | onLessonMuteAllText: roomUuid:          | 全体禁言回调                |

  



