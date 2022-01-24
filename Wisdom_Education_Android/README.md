# Wisdom Education Component（Android）

Wisdom Education (PaaS solution) is the online interactive class solution provided by CommsEase. Starting from the common education scenarios, the Wisdom Education is the open source project that you can implement 1v1 tutoring, breakout class, and large class activities. You can also develop your features based on the demo project that incorporates the IM SDK, Audio & Video SDK, and whiteboard SDK.

Visual effects:

Class UI:

<img src="./Images/lesson.png" alt="lesson" style="zoom:50%;" />

Chat UI:

<img src="./Images/chat.png" alt="chat" style="zoom:50%;" />

Participant Management UI:

<img src="./Images/members.png" alt="members" style="zoom:80%;" />

### Features

- Start/end class

- Audio and video calls

- Screen sharing

- Whiteboarding

- Message chats

- Raising hands

- Access control over students（Grant/revoke permissions, including whiteboard, screen sharing, audio and video, and mute all）

This topic illustrates how to compile and run the demo project for Android and use features for online learning.

### Prerequisites

Before you run the demo project, make sure that you have completed the following operations:

- Create a project in the CommsEase console and get the AppKey.

- Activate the following services:
  - Audio & Video Call, IM Pro, Chat room, signaling, whiteboard, cloud recording, and VOD.

- Features:
  - Cloud recording and message delivery.

- File transcoding and cloud recording.

- Message delivery: 1-start room, 2-end room,3-Message deliver for recording downloads, 4-A users join a room, 5- a user leaves a room, 8- Room duration.

```
Note:
```

- To activate services and message delivery, contact [your account manager](https://yunxin.163.com/bizQQWPA.html).
- To run the demo project in the local environment, you can use trial accounts that are provided with the required services. The class duration for trials is 30 minutes.
- Trial accounts are used for trials and testing only. Do not log in to the trial accounts when your application is deployed for service.

### Development environment

Before you start running the demo project, make sure that the following environment is ready:

| Requirement         | Description                                                        |
| ---------------- | ------------------------------------------------------------ |
| JDK version         | 1.8.0 or later                                             |
| Android API version | API 23 and Android 6.0 or later                               |
| CPU          | ARM64、ARMV7                                                 |
| IDE              | Android Studio                                               |
| Miscellaneous              | Androidx without support library. Devices with Android OS 4.3 or later |

### Running the demo project

1. Get the demo project

Download the demo project or the source code on the Wisdom Education page.

1. Enable the Developer Options on the Android device and connect the device with your computer using the USB interface.
2. Open the demo project in Android Studio.
3. Configure required settings for the demo project.

If you want to develop your application based on the demo project, edit the following fields in `config.properties` for your purpose.

| Field        | Description                                       |
| ------------- | ------------------------------------------- |
| APP_KEY       | The AppKey that you can view in the CommsEase console. |
| AUTHORIZATION | The verification parameter in the request header when you call server APIs.      |

```
Note: If you want to run the demo project in your local environment, use a trial account. The class duration for a trial account is 30 minutes long.
```

1. You can directly run the demo on your device.

### Configure features

Demo project structure:

```
├── app                      Shell project
├── base                     Public basic component
├── edu-logic                Education core business module
│   ├── cmd                  IM pass-through notification
│   ├── model                data source
│   ├── net.service          API requests      
│   ├── option               Basic configuration
│   └── service              Business service
├── edu-ui                   UIKit component
│   ├── clazz                Activity
├── edu-model                Education model
├── im                       IM service component
├── rtc                      Audio and video call component
├── whiteboard               Whiteboard component
├── recordplay-logic         Recording playback logic
├── recordplay-model         Recording playback module
├── recordplay-ui            Recording playback UI module
├── rvadapter                adapter tool module
├── viewbinding              viewbinding tool module
└── config.properties        Custom configuration
```

If the UI of the demo project does not meet your business requirements, you can develop your UI component and use the capabilities of audio and video call provided by CommsEase.

Modules:

<img src="./Images/layer.svg" alt="layer" style="zoom:100%;" />

**EduUI:** includes the component UI, ViewController、View and model for 1v1 tutoring, breakout class, and large class.

**EduLogic:** implements the education logic with NEEduRtcService, NEEduIMService, and NEEduBoardService supported separately by CommsEase Audio & Video Call, IM SDK, and Whiteboard SDK.

Integrate the component

### Integrate the component into the project

1. Create an Android project
   a. Open Android Studio and create a new project by selecting File > New > New Project from the main menu.
   b. Select Phone and Tablet > Empty Activity and click Next.
   c. Configure the project settings.

Note: Minimum API Level uses API 21.

   d. If you are ready to create your project, click Finish.

2. Add dependencies  
   a. Copy Modules, config.gradle and config.properties of the project to the current folder.
   b. Import modules in settings.gradle.

```
include ':edu-ui'
include ':edu-logic'
include ':whiteboard'
include ':im'
include ':rtc'
include ':base'
```

   c. Edit the 'app/build.gradle' file and add dependencies of Wisdom Education.

```
allprojects {
    repositories {
        google()
        jcenter()
        maven{
            url 'https://oss.sonatype.org/content/repositories/snapshots/'
        }
    }
}



// If the message appears that More than one file was found with OS independent path 'lib/arm64-v8a/libc++_shared.so',

// you can add the following configurations in packageOptions in the android closure in the build.gradle file of the module
android{
    //......
    packagingOptions {
      pickFirst 'lib/arm64-v8a/libc++_shared.so'
      pickFirst 'lib/armeabi-v7a/libc++_shared.so'
    }
}





dependencies {
    //......
    // Add EduUI dependency
    implementation project(':edu-ui')
}
```

d. Create a project by selecting 'Build -> Make Project' and download dependencies.

After the download is complete, import classes and methods provided by Wisdom Education.

3. Permissions configuration

The Wisdom Education SDK requires the following permissions:

The previous permissions are declared in the SDK. Developers do not need to declare the permissions in `AndroidManifest.xml`. However, the permissions request is needed when the app is running. For more information, see [Android permissions request example](https://developer.android.google.cn/guide/topics/permissions/overview)。

```
<!-- Network -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- Multimedia -->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.Manifest.permission.READ_PHONE_STATE"/>

<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

### 2 Initialize the component

Procedure:

1. Configure global settings

Configure global settings using `NEEduOptions`, and pass `config` in `NEEduOptions`.`NEEduOptions` includes the following parameters:

| Parameter       | Description                                                         |
| ------------- | ------------------------------------------------------------ |
| APP_KEY       | The AppKey that can be viewed in the CommsEase console.               |
| BASE_URL      | The URL of the app server. Replace the URL for on-premises deployment.           |
| AUTHORIZATION | The verification parameter in the request header for server APIs.                       |
| reuseIM       | Specify whether to reuse the persistent connection provided by the NIM-SDK. By default, the parameter is disabled. You can enable the parameter if separate NIM-SDK is used. Otherwise, ignore this parameter. |

```
NEEduUiKit.config(
    this,
    NEEduOptions(
        BuildConfig.APP_KEY,
        BuildConfig.AUTHORIZATION,
        BuildConfig.API_BASE_URL,
        reuseIM
    )
)
```

2. Initialize an instance

When the configuration is complete, create a `NEEduUiKit` instance and initialize the instance using `init` with the following parameters:

| Parameter | Description                                            |
| ------ | ----------------------------------------------- |
| uuid   | The userUuid for authentication. Set the value to "" for anonymous login. |
| token  | The userToken used for authentication. Set the value to "" for anonymous login. |

Example code:

```
NEEduUiKit.init(uuid, token).observeOnce(viewLifecycleOwner, initObserver)
```

### Students or teacher join the class

When a participant joins a class, the client create the class by calling `NEEduClassOptions`. If the class with the specified class ID already exists, then join the class. `NEEduClassOptions` contains the following parameters:

| Parameter    | Description                                                          |
| --------- | ------------------------------------------------------------ |
| classId   | The unique identifier of the class                                         |
| className | The class name                                                    |
| nickName  | The display name in the class                                          |
| sceneType | Class type: 1v1, breakout class, and large class                 |
| roleType  | Role type. host: teacher and broadcaster: student |

Example code:

```
eduManager.enterClass(neEduClassOptions).map {
    if (it.success()) {
        if (neEduClassOptions.roleType == NEEduRoleType.HOST) {
            when (neEduClassOptions.sceneType) {
                NEEduSceneType.ONE_TO_ONE -> {
                    OneToOneTeacherActivity.start(context)
                }
                NEEduSceneType.SMALL -> {
                    SmallClazzTeacherActivity.start(context)
                }
                NEEduSceneType.BIG -> {
                    BigClazzTeacherActivity.start(context)
                }
            }
        } else {
            when (neEduClassOptions.sceneType) {
                NEEduSceneType.ONE_TO_ONE -> {
                    OneToOneStudentActivity.start(context)
                }
                NEEduSceneType.SMALL -> {
                    SmallClazzStudentActivity.start(context)
                }
                NEEduSceneType.BIG -> {
                    BigClazzStudentActivity.start(context)
                }
            }

        }

    }

    it

}
```

### Interaction

When all participants join a class, the teacher can start the class. Wisdom Education offers a variety of features for engaging interactions during the class. For example, whiteboard, share screen, raising hands, audio and video calls.

1. Start class

The teacher client starts class. Example code:

```
// The teacher starts class
eduManager.getRoomService().startClass(roomUuid = eduRoom.roomUuid)
    .observe(this@BaseClassActivity, {
        ALog.i(tag, "startClazz")
    })
```

2. The teacher manages students  
   a. The teacher can control microphones and cameras of students by calling remoteUserVideoEnable and remoteUserAudioEnable. To invite specified students to speak, unmute the camera. Example code:

```
// Control the cameras of the students

eduManager.roomConfig.memberStreamsPermission()?.apply {
    val self = entryMember
    video?.let { it ->
        // Check the permissions status
        if (it.hasAllPermission(self.role)) {
            // Turn on the cameras of students with specified userUuid by calling remoteUserVideoEnable
            eduManager.getRtcService().remoteUserVideoEnable(userUuid, true)
                .observe(this@BaseClassActivity, {
                    // Handle the callback result
                    ALog.i(tag, "switchRemoteUserVideo")
                    ToastUtil.showShort(R.string.operation_successful)
                })
        }
    }
}



// Control the microphones of the students

eduManager.roomConfig.memberStreamsPermission()?.apply {
    val self = entryMember
    audio?.let { it ->
        // Check whether the permissions are granted
        if (it.hasAllPermission(self.role)) {
            // unmute the specified student by calling remoteUserAudioEnable
            eduManager.getRtcService().remoteUserAudioEnable(member.userUuid, !member.hasAudio())
                .observe(this@BaseClassActivity, {
                    // handle callback result
                    ALog.i(tag, "switchRemoteUserAudio")
                    toastOperateSuccess()
                })
        }
    }
}
```

   b. Grant whiteboard or screen sharing permissions by calling grantPermission. Example code:

```
// Grant whiteboard permissions to students
eduManager.roomConfig.memberPropertiesPermission()?.apply {
    val self = entryMember
    whiteboard?.let { it ->
        // Check the permissions status
        if (it.hasAllPermission(self.role)) {
            // Grant the white permissions by calling grantPermission
            eduManager.getBoardService().grantPermission(member.userUuid, !member.isGrantedWhiteboard())
                .observe(this@BaseClassActivity, {
                    // Handle callbacl result
                    ALog.i(tag, "grantWhiteboardPermission")
                })
        }
    }
}
```

3. Screen sharing

Start screen sharing by calling startScreenCapture. Example code:

```
// Start screen sharing
// Create the configuration instance for screen sharing
val config = NERtcScreenConfig().apply {
    contentPrefer = NERtcScreenConfig.NERtcSubStreamContentPrefer.CONTENT_PREFER_DETAILS
    videoProfile = RTCVideoProfile.kVideoProfileHD1080p
}
// Share the local screen
eduManager.getShareScreenService().startScreenCapture(config, data, object :
    MediaProjection.Callback() {
    override fun onStop() {
        // Handle callback result
        runOnUiThread { stopLocalShareScreen() }
    }
})
```

1. Chat room

Messages can be sent and received in the chat room in 1v1 tutoring and interactive large class. Students and teachers can communicate with each other using text and image messages. Teachers can mute or unmute the class.

Teachers and students join the class by calling enterChatRoom and send text and image messages by calling sendMessage. Example code:

```
// Start chat room
// Create the EnterChatRoomData instance
val data = EnterChatRoomData(activity.eduRoom?.chatRoomId())
// Join the chat room using EnterChatRoomData
imService.enterChatRoom(data).observe(this, { it ->
    // handle the callback result
    if (it.success()) roomInfo = it.data!!.roomInfo
    it
}



// Sent text messages
// Create text messages
val chatMessage = ChatRoomMessageBuilder.createChatRoomTextMessage(it.roomId, text)
// Continue sending messages
imService.sendMessage(chatMessage)





// Send image messages
// Create image messages
val chatMessage =
    ChatRoomMessageBuilder.createChatRoomImageMessage(it.roomId, file, file?.name)
// Continue sending image messages
imService.sendMessage(chatMessage)
```

### EduLogic API

**APIs supported by the NEEduLogic component are described in the following table:**

- `**NEEduUiKit**`**Singleton class that provides SDK configuration and initializes the SDK and gets NEEduManager.**

| Interface                                                  | Description              |
| ------------------------------------------------------ | ----------------- |
| config(context: Application, eduOptions: NEEduOptions) | Configure the SDK |
| init()                                                 | Initialize the component        |
| enterClass(neEduClassOptions: NEEduClassOptions)       | Join a class          |

- `**NEEduManager**`**singleton class. The SDK provides services.**

| interface                  | Description              |
| --------------------- | ---------------- |
| getRoomService        | Get the room service |
| getMemberService      | Get the member service |
| getRtcService         | Get the rtc service  |
| getIMService          | Get the IM service |
| getShareScreenService | Get the screen share service |
| getBoardService       | Get the whiteboard service   |
| getHandsUpService     | Get the raise hand service |
| destroy()             | Release the instance       |

- `**NEEduRoomService: Class management class**`

| Interface                   | Description                    |
| ----------------------- | ---------------------- |
| startClass              | Start a class           |
| finishClass:            | End a class           |
|                         |                        |
| Callback                | Description                    |
| onCurrentRoomInfo       | Notifications for class profile changes |
| onRoomStatesChange:     | Notifications for Room state changes       |
| onNetworkQualityChange: | Notifications for network changes          |

- `**NEEduMemberService: **``***Participants***``**management class**`

| Interface                     | Description                      |
| ------------------------ | ------------------------ |
| getMemberList            | Get the list of participants in the class |
| getLocalUser             | Get the current participant          |
|                          |                          |
| Callback                 | Description                      |
| onMemberJoin             | Get notified when online member status changes   |
| onMemberLeave            | Get notified when a member leaves the class       |
| onMemberPropertiesChange | Get notified when member properties change       |

- `**NEEduRtcService**`**: Audio & video management class**

| Interface                   | Description                                   |
| ---------------------- | -------------------------------------- |
| muteAllAudio:          | Mute all audio                         |
| updateRtcAudio:        | Set audio by enabling or disabling hardware                   |
| enableLocalVideo:      | Enable or disable video by turning on or off hardware               |
| updateRtcVideo:        | Set member video view                     |
| updateRtcSubVideo:     | Set substream                           |
| localUserVideoEnable:  | Enable or disable local video without turning on or off hardware          |
| localUserAudioEnable:  | Enable or disable local audio without turning on or off hardware    |
| remoteUserVideoEnable: | Enable or disable remote video       |
| remoteUserAudioEnable: | Enable or disable remote audio         |
| destroy                | Leave the room.                       |
|                        |                                        |
| Callback                | Description                                    |
| onMuteAllAudio:        | Notification for the mute all event                           |
| onStreamChange:        | notifications for stream state changes (audio, video and substream)|

- `**NEEduIMService**`**: Chat room management class**

| Interface                       | Description                         |
| --------------------------- | --------------------------- |
| sendMessage:                | Send a message               |
| muteAllChat:                | Mute all members          |
| enterChatRoom:              | Join a chat room                  |
| exitChatRoom:               | Leave a chat room                  |
|                             |                             |
| Callback                    | Description                         |
| onReceiveMessage:           | Notification for receiving messages             |
| onMessageStatusChange:      | Notifications for image message state changes      |
| onAttachmentProgressChange: | Notifications for progress of message attachment upload or download progress changes |

- `**NEEduShareScreenService**`**: Screen sharing management class**

| Interface                | Description                              |
| ------------------- | -------------------------------- |
| grantPermission:    | Grant or revoke screen sharing permissions |
| shareScreen         | Send screen sharing request without screenshot         |
| finishShareScreen   | Cancel screen sharing without screenshot  |
| startScreenCapture  | Start screen sharing                     |
| stopScreenCapture   | Stop screen sharing                     |
|                     |                                  |
| Callback            | Description                              |
| onPermissionGranted | Screen sharing permissions change        |
| onScreenShareChange | Screen sharing state changes             |

- `**NEEduBoardService**`**: Whiteboard management class**

| Interface                 | Description                          |
| -------------------- | ---------------------------- |
| grantPermission:     | Grant or revoke the whiteboard permissions for students |
| initBoard            | Initialize whiteboard              |
| setEnableDraw        | Enable or disable drawing        |
|                      |                              |
| Callback            | Description                          |
| onPermissionGranted: | Notification for whiteboard permissions change         |

- `**NEEduHandsUpService**`**: Raise hand management class（apply to large classes**

| Interface                  | Description                     |
| --------------------- | ------------------------ |
| getHandsUpApplyList:  | Get the list of members who raise their hands |
| getOnStageMemberList  | Details of members as speaker  |
| handsUpStateChange    | Change the raise hand state         |
|                       |                          |
| Callback              | Description                    |
| onHandsUpStateChange: | The student state changes   |