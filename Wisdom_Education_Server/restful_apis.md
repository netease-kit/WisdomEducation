# 为什么需要服务端api
* 和客户账号系统对接：[创建账号](#创建用户)，此接口返回的userUuid和userToken用于开源客户端的账号登陆
* 和客户排课系统对接：[创建房间](#创建房间)，此接口创建房间后可将房间和排课系统对接。
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

>若调用端是java语言，则可直接使用[示例代码](toolkit/src/main/java/com/netease/util/CheckSumBuilder.java)计算checksum, 注意参数顺序

# 基本概念
| 名称| 	类型| 	描述|
|----|----|----|
| userUuid	| String	| 用户唯一ID，即用户账号，加入房间时的用户身份|
| userToken	| String	| 用户令牌，账号对应的密码，加入房间等需要USER_TOKEN鉴权的接口使用|
| imKey	| String	| IM 应用标识符: 一般等同于AppKey，此应用绑定的im app key，一般情况下开发者无需关心此参数|
| imToken	| String	| IM 应用中用户token。此应用绑定的im token，一般情况下开发者无需关心此参数|
| rtcKey	| String	| RTC 应用标识符: 一般等同于AppKey，此应用绑定的rtc app key，一般情况下开发者无需关心此参数|
|roomName|String|房间名称|
|roomUuid|String|房间唯一标识符，用于标识应用下的一个唯一房间，创建房间时若指定的roomUuid存在，则返回报错|
| configId	| Integer	| 房间的配置模版id，1对1：5；小班课：6；大班课：7；直播大班课：20|

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
| userToken	| 否	| 用户令牌  |
| imToken	| 否	| imToken，复用Im账号时使用 |
| assertNotExist	| 否	| 是否断言该账号不存在，若true时并且账号已经存在则报错 |
| updateOnConflict	| 否	| 当账号存在时是否更新 |


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
| config.resource.live	| 可选: 默认以模版中的设置为准 | 房间是否打开直播| 
| config.resource.rtc	|可选: 默认以模版中的设置为准 | 房间是否打开rtc房间| 
| config.resource.chatroom| 可选: 默认以模版中的设置为准 | 房间是否打开聊天时| 
| config.resource.whiteboard| 可选: 默认以模版中的设置为准 | 房间是否打开白板| 
| config.resource.seat| 可选: 默认以模版中的设置为准 | 房间是否打开麦位| 
| liveStreamName| 可选 | 只有在云信appKey开通了直播2.0时才有效，用于指定自定义流名，具体咨询商务经理| 
| livePushOff| 可选 | true/false，默认false。表示是否关闭直播大班课中的托管推流。用于客户自定义推流。具体使用方式请联系云信技术支持。| 

- Sample
```json
{
  "roomName": "9999",
  "configId": 5,
  "properties": {},
  "config": {"resource": {"live": true,"rtc": true,"chatroom": true,"whiteboard": true,"seat": true}}
}
```

#### Response:
- 响应体:

| 参数名称 | 是否必选 | 描述 |
|----|----|----|
|roomName|必选|房间名称|
|roomUuid|必选|房间唯一标识符|
|roomArchiveId|必选|房间唯一id|
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

### 成员踢出

#### Request:
- Url: https://{Host}/apps/{appKey}/v1/rooms/{roomUuid}/members/{userUuid}
- HttpMethod: DELETE
- Auth: CHECKSUM
- Body: None
#### Response:
- Body: None
### 房间录制记录查询

#### Request:
- Url: https://{Host}/apps/v2/room-media?roomArchiveId={roomArchiveId}
- HttpMethod: GET
- Auth: CHECKSUM
- Url请求参数:

| 参数名称           | 	是否必选| 	描述     |
|----------------|----|---------|
| roomArchiveId	 | 必选| 	房间唯一Id |
#### Response:
- Body:

  | 参数名称 | 	类型|描述|
  |----|-----------|----|
  | fileInfos| List| 录制文件信息列表。|
  | fileInfos.vid| String| 云端录制文件的标识。|
  | fileInfos.objectName| String| <ul><li>云端录制生成的文件名。混合录制文件的objectName 带有"-mix"标记，不带则是单人录制文件。辅流录制文件带有"-substream"标记<br/><li>文件的类型，即文件扩展名包括：<ul><li>aac：实时音频录制文件。<li>mp4：实时视频录制文件。<li>flv：互动直播视频录制文件。<li>gz: 白板录制文件|
  | fileInfos.url| String| 云端录制文件的对应地址，获取此地址后可以通过播放器 SDK 播放。 云端录制文件的对应地址，获取此地址后可以通过播放器 SDK 播放。   <note type="note">如果点播域名开启了 URL 鉴权，您需要手工拼接防盗链 URL ，即在原始地址的最后手工增加 wsSecret 和 wsTime 相关的值，否则用户会无法访问相应的资源。拼接防盗链 URL 的方法请参见<a href="https://doc.yunxin.163.com/jY3NDM4Nzc/docs/DM5MzI2OTI?platform=server#url-鉴权的功能原理" target="_blank">防盗链URL构成</a>。</note> |
  | fileInfos.createTime|Long| 云端录制文件的生成时间。 |
- Sample
```json
{
  "code": 0,
  "cost": "91ms",
  "requestId": "9c04d29dd20c4b9ca4e79f9018c1de54",
  "msg": "Success",
  "data": {
    "fileInfos": [
      {
        "vid": 4934894395,
        "objectName": "19237-1345278913991800-1665455883302-0.mp4",
        "url": "http://sample.domain/jdvodwzsxohf4/19237-1345278913991800-1665455883302-0.mp4",
        "createTime": 1665455897302
      },
      {
        "vid": 4934894396,
        "objectName": "19237-1345278913991800-1665455883302-0-substream.mp4",
        "url": "http://sample.domain/jdvodwzsxohf4/19237-1345278913991800-1665455883302-0-substream.mp4",
        "createTime": 1665455897302
      },
      {
        "vid": 4934892596,
        "objectName": "0-1345273313871842-1665455897302-0-mix.mp4",
        "url": "http://sample.domain/jdvodwzsxohf4/19237-1345278913991800-1665455883302-0-mix.mp4",
        "createTime": 1665455897302
      }
    ]
  },
  "ts": 1665475539147
}
```

### 直播流名查询

#### Request:
- Url: https://{Host}/apps/v2/live-stream-name?roomArchiveId={roomArchiveId}
- HttpMethod: GET
- Auth: CHECKSUM
- Url请求参数:

| 参数名称           | 类型     | 	是否必选| 	描述     |
|----------------|--------|----|---------|
| roomArchiveId	 | String | 必选   | 	房间唯一Id |
#### Response:
- Body:

  | 参数名称 | 	类型     | 	描述   |
  |---------|-------|----|
  | streamName	   | String  | 	直播流名 |
- Sample

```json
{
  "code": 0,
  "cost": "91ms",
  "requestId": "80903990363247d8af4d2152f0c45cc2",
  "msg": "Success",
  "data": {
    "streamName":"12345678_1232324345"
  },
  "ts": 1665475539147
}
```
### 在线成员列表查询

#### Request:
- Url: https://{Host}/apps/v2/online-user-list?roomArchiveId={roomArchiveId}
- HttpMethod: GET
- Auth: CHECKSUM
- Url请求参数:

| 参数名称           | 类型      | 	是否必选 | 	描述             |
|----------------|---------|-------|-----------------|
| roomArchiveId	 | String  | 必选    | 	房间唯一Id         |
| pageNumber	    | Integer | 可选    | 	页码，默认1         |
| pageSize	      | Integer | 可选    | 	分页大小，默认10，最大50 |
#### Response:
- Body:

## 返回参数
| 参数名称                | 类型      | 示例                             | 描述                                                                        |
|---------------------|---------|--------------------------------|---------------------------------------------------------------------------|
| code                | int     | 0                              | 状态码，0表示成功                                                                 |
| msg                 | String  | Success                        | 业务结果描述，Success 表示成功。                                                      |
| ts                  | Long    | 1648021056815                  | 服务器处理该请求的完成时间。该时间为 Unix 时间戳，即从 1970 年 1 月 1 日 0 点 0 分 0 秒开始到现在的秒数。 |
| requestId           | String  | 7c4b6d9c3e9d42*****cc6e3a4d995 | 请求的唯一标识。                                                                  |
| cost                | String  | 48ms                           | 处理该请求所消耗的时间。                                                              |
| data                | Object  | -                              | 在线成员列表。                                                                   |
| data.totalCount     | Integer | 100                            | 房间人数总数。                                                                   |
| data.pageTotal      | Long    | 10                             | 总页数 。                                                                     |
| data.users          | List    | -                              | 用户列表。                                                                     |
| data.users.userUuid | String  | user01                         | 用户 ID。                                                                    |
| data.users.name     | String  | userName01                     | 用户名称。                                                                     |
| data.users.role     | String  | host                           | 成员对应角色。                                                                   |
| data.users.state    | Integer | 2                              | 成员状态，2表示在房间中                                                              |

- Sample

```json
{
  "code":0,
  "msg":"Success",
  "ts":1619068087795,
  "requestId":"6e507107d1f4447ea731f651dc6d2432",
  "cost":"66ms",
  "data": {
    "totalCount":1000,
    "pageTotal":100,
    "users":[
      {
        "userUuid":"user01",
        "name":"Name***",
        "role":"host",
        "state":"2"
      },
      {
        "userUuid":"user02",
        "name":"Name***",
        "role":"host",
        "state":"2"
      }
    ]
  }
}
```
### 用户Id查询

#### Request:
- Url: https://{Host}/apps/v2/user-info?rtcUid={rtcUid}
- HttpMethod: GET
- Auth: CHECKSUM
- Url请求参数:

| 参数名称    | 类型   | 	是否必选 | 	描述     |
|---------|------|-------|---------|
| rtcUid	 | Long | 必选    | 	rtcUid |
#### Response:
- Body:

## 返回参数
| 参数名称          | 类型     | 示例                             | 描述                                                                        |
|---------------|--------|--------------------------------|---------------------------------------------------------------------------|
| code          | int    | 0                              | 状态码，0表示成功                                                                 |
| msg           | String | Success                        | 业务结果描述，Success 表示成功。                                                      |
| ts            | Long   | 1648021056815                  | 服务器处理该请求的完成时间。该时间为 Unix 时间戳，即从 1970 年 1 月 1 日 0 点 0 分 0 秒开始到现在的秒数。 |
| requestId     | String | 7c4b6d9c3e9d42*****cc6e3a4d995 | 请求的唯一标识。                                                                  |
| cost          | String | 48ms                           | 处理该请求所消耗的时间。                                                              |
| data          | Object | -                              | 用户信息。         |
| data.userUuid | String | ayreu3534                      | 用户Id。  |
- Sample

```json
{
  "code":0,
  "msg":"Success",
  "ts":1619068087795,
  "requestId":"6e507107d1f4447ea731f651dc6d2432",
  "cost":"66ms",
  "data": {
     "userUuid":"user01"
  }
}
```
## Error Code
|错误码|错误消息|描述｜
|----|----|----|
|304|Not Modified||
|400|Bad Request|参数非法|
|401|Unauthorized|鉴权失败|
|403|Forbidden|房间操作权限禁止|
|404|Not Found||
|405|Method Not Allowed|method不支持|
|409|Target Already Exists|创建房间时，房间号已经存在|
|415|Unsupported Media Type|不支持的MediaType，比如非Json的body|
|500|Internal Server Error|内部异常，一般是内部服务出现问题|
|503|Service Busy||
|1001|Room Not Prepared|房间内操作时，房间configId不存在，或者configId对应的config不存在，或有格式或内容有误；课堂开始 如step=1时，rtc房间未创建，因为此时录制没发开始|
|1002|Room Role Exceed|加入房间时，角色数量超限|
|1003|Room Role Undefined|加入房间时，指定角色未定义|
|1004|Room Not Found|任何和房间强关联的操作，指定的roomUuid查不到对应的活的房间|
|1005|Bad Room Config|创建房间时，config不存在或无法使用|
|1006|Room Property Exists|房间属性exclusive时put改属性，该属性已经存在|
|1007|Room Member Property Exists|成员属性exclusive时put改属性，该属性已经存在|
|1008|Room Sit Conflict|超大房间设置的座位号已经存在|
|1009|Room Sits Full|超大房间设置座位时，座位已满|
|1010|Room Sit User Conflict|超大房间设置座位时，目标用户已经在座位上|
|1011|Room Sit Not Exist|超大房间设置座位时，座位号不存在|
|1012|Member Property or Stream Out of Currency Limit|put:member.stream|property时，该属性的并发超限，并发超限，如屏幕共享同时只能一人|
|1014|Room Sits Bad|坐席配置不正确|
|1015|Destination Member Server Error|被操作的成员不存在|
|1016|Member Exist|房间成员已存在|
|1017|Bad Room Config: Conflict|创建房间时房间已经存在且config冲突|
|700|Nim Create User Error|创建IM账户失败|
|701|Nim User NOT exist|指定IM账户不存在|
|702|Nim Bad Im Service|IM服务异常|
|703|Nim User exist|IM账户已存在|
|704|Nim User Bad Token|IM账户 Token错误|
|800|Record does not exist| 录制记录不存在|
|801|Live does not exist| 直播不存在| 
|802|Stream name does not exist| 直播流名不存在|
