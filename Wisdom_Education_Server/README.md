# 使用姿势
```
服务端api可以站在应用的角度来操作账号和房间，可以执行例如创建账号，创建房间，结束房间等。
接口以Restful Api形式提供，鉴权方式采用签名方式。
```

# 接口调用概述
## AppKey/AppSecret：
[联系云信商务经理](https://yunxin.163.com/bizQQWPA.html) 开通功能
## Host：yiyong-xedu-v2.netease.im
## Headers
### CurTime：当前时间，毫秒时间戳
### Nonce：随机串，长度不超过64
### CheckSum：hex.lowercase(sha1(utf8.decode(AppSecret+Nonce+CurTime)))
> 例子：<br>
> Secret=abcdefghigk <br>
> Nonce=ABCDEFG1234567abcdefg7654321GFEDCBA<br>
> CurTime=1234567890123<br>
> <b>计算可得</b>: CheckSum=0990f3d1615b5fa19fcbcea6b105270053fdf6a7<br>

>若调用端是java语言，则可直接使用[示例代码](CheckSumBuilder.java)计算checksum, 注意参数顺序

# 接口列表
```
以下接口Url中的AppKey和Host见接口调用概述
```
## 用户:
### 创建用户
#### Request:
- Url: https://{Host}/apps/{appKey}/v1/users/{userUuid}
- HttpMethod: PUT
- Auth: CHECKSUM
- Body:

| 参数名称| 	是否必选| 	描述|
|----|----|----|
| userUuid	| 必选	| 用户唯一ID|
| userToken	| 必选	| 用户令牌|
| imKey	| 必选	| IM 应用标识符: 一般等同于AppKey|
| imToken	| 必选	| IM 应用中用户token: 一般等同于userToken|
| rtcKey	| 必选	| RTC 应用标识符: 一般等同于AppKey|

- Sample
```json
{
  "userToken": "",
  "imToken": "",
  "assertNotExist": false,
  "updateOnConflict": true
}
```

#### Response:
- Body:
- Sample
```json
{
    "code": 0,
    "msg": "Success",
    "data": {
        "userUuid": "abc",
        "userToken": "",
        "imToken": "LJWV80X8PLEPM0VDFKHVBMTU",
        "imKey": "616711ef810ede22919a1a68463fbd0a",
        "rtcKey": "616711ef810ede22919a1a68463fbd0a"
    },
    "ts": 1619068087795,
    "requestId": "6e507107d1f4447ea731f651dc6d2432",
    "cost": "66ms"
}
```


## 房间:
### 创建房间
#### Request:
- Url: https://{Host}/apps/{appKey}/v1/rooms/{roomUuid}
- HttpMethod: PUT
- Auth: CHECKSUM
- Body:

| 参数名称| 	是否必选| 	描述|
|----|----|----|
| roomName	| 必选| 	房间名称| 
| configId	| 必选	| 房间配置模版id| 
| properties	| 可选	| 房间初始化时可选的一些房间属性, 可携带的属性需要参考配置模版.| 
| config.resource.live	| 可选: 默认false	| 房间是否打开直播| 
| config.resource.rtc	|可选: 默认true| 房间是否打开rtc房间| 
| config.resource.chatroom| 可选: 默认true| 房间是否打开聊天时| 
| config.resource.whiteboard| 可选: 默认true| 房间是否打开白板| 

- Sample
```json
{
  "roomName": "9999",
  "configId": 5,
  "properties": {},
  "config": {"resource": {"live": true}}
}
```

#### Response:
- 响应体:

| 参数名称 | 是否必选 | 描述 |
|----|----|----|
|roomName|必选|房间名称|
|roomUuid|必选|房间唯一标识符|
|config|必选|房间配置, 参见房间配置说明|
|properties|必选|房间属性集, 参见房间属性集说明|
|states|必选|房间状态集, 参见房间状态集说明|

### 获取房间配置
#### Request:
- Url: https://{Host}/apps/{appKey}/v1/rooms/{roomUuid}/config
- HttpMethod: GET
- Auth: CHECKSUM
- Body:  None

#### Response:
- Body:

| 参数名称| 	是否必选| 	描述|
|----|----|----|
|sceneType	|必选|	场景类型|
|permissions.room.states	|必选	|房间状态角色权限配置|
|permissions.room.properties	|必选	|房间属性角色权限配置|
|permissions.room.streams	|必选	|房间成员流的角色权限配置, 配置什么角色可以操作谁的什么类型的流|
|permissions.member.properties	|必选	|房间成员属性的角色权限配置, 配置什么角色可以操作谁的什么类型的属性|
|roleConfigs.<roleName>.limit	|必选	|房间中某个角色的数量|
|roleConfigs.<roleName>.superRole	|必选	|房间中某个角色是否为超级角色, 超级角色可以踢人和结束房间.|
- Sample
```json
{
  "code": 0,
  "msg": "Success",
  "data": {
    "sceneType": "EDU.SMALL",
    "permissions": {
      "room": {
        "states": {
          "step": {
            "roles": [
              "host"
            ]
          },
          "pause": {
            "roles": [
              "host"
            ]
          }
        },
        "properties": {
          "whiteboard": {
            "roles": [
              "host"
            ]
          },
          "screenShare": {
            "roles": [
              "host"
            ],
            "memberGrant": {
              "screenShare": {
                "value": 1
              }
            },
            "exclusive": true
          }
        }
      },
      "member": {
        "properties": {
          "whiteboard": {
            "roles": [
              "host"
            ]
          },
          "screenShare": {
            "roles": [
              "host"
            ]
          },
          "avHandsUp": {
            "roles": [
              "host.self",
              "broadcaster.self"
            ]
          }
        },
        "streams": {
          "audio": {
            "roles": [
              "host",
              "host.self",
              "broadcaster:self"
            ]
          },
          "video": {
            "roles": [
              "host",
              "host.self",
              "broadcaster.self"
            ]
          },
          "subVideo": {
            "roles": [
              "host",
              "host.self",
              "broadcaster.self"
            ]
          }
        }
      }
    },
    "roleConfigs": {
      "host": {
        "limit": 1
      },
      "broadcaster": {
        "limit": 16
      }
    }
  },
  "ts": 1620461806819,
  "requestId": "7fbfc4956ed34eba9c93bb1f905030a0",
  "cost": "5ms"
}
```
  
### 房间关闭

#### Request:
- Url: https://{Host}/apps/{appKey}/v1/rooms/{roomUuid}
- HttpMethod: DELETE
- Auth: CHECKSUM
- Body: None
#### Response:
- Body: None
    