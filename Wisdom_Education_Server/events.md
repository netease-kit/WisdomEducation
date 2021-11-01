# 为什么需要应用级事件
* 客户的应用系统在某些场景，比如排课系统的列表页等，需要展示房间状态，当前成员数量等信息，可以通过订阅应用事件来实现。
* 可以订阅的事件包括创建房间，结束房间，成员进/出，成员属性修改。
* 订阅方式是带密钥签名的http(s)协议。


# 使用姿势

```
智慧云课堂房间内的任何事件，包括创建房间，结束房间，成员进/出，成员属性修改，都可以订阅，订阅的方式是带密钥签名的http(s)
```

# 抄送订阅方式

[联系云信商务经理](https://yunxin.163.com/bizQQWPA.html) 开通抄送

## Headers

### CurTime：当前时间，毫秒时间戳

### Nonce：随机串，长度不超过64

### CheckSum：hex.lowercase(sha1(utf8.decode(AppSecret+Nonce+CurTime+hex.lowercase(md5(body)))))

> 若调用端是java语言，则可直接使用[示例代码](toolkit/src/main/java/com/netease/util/CheckSumBuilder.java)计算checksum, 注意参数顺序

# 通知格式

| 名称|    类型|    描述|
|----|----|----|
| sequence    | Long|事件序号|
| appKey    | String    | 应用唯一标识|
| roomUuid    | String    | 房间唯一标识|
| type    | String    | R：房间事件，RM：房间自定义消息|
| cmd    | Integer    | [命令号](#命令号)|
| version    | Integer    |目前1|
| data.operatorMember.role    | String    |操作者角色 可选|
| data.operatorMember.userUuid    | String    |操作者账号 可选|
| data.operatorMember.userName    | String    |操作者名称 可选|
| data.operatorMember.rtcUuid   | Long    |操作者rtcUid 可选|


```json
{
  "sequence": 1,
  "appKey": "ed73b42de1b66244337a4cd54a9534cc",
  "roomUuid": "163551",
  "type": "R",
  "cmd": 30,
  "version": 1,
  "data": {
    "members": [
      {
        "properties": {
          "screenShare": {
            "value": 0,
            "time": 1632299997927
          }
        },
        "streams": {
          "audio": {
            "value": 1,
            "time": 1632299997927
          },
          "video": {
            "value": 1,
            "time": 1632299997927
          }
        },
        "role": "host",
        "userName": "tea",
        "userUuid": "anonymous_2_26",
        "rtcUid": 2137
      }
    ],
    "operatorMember": {
      "role": "host",
      "userName": "tea",
      "userUuid": "anonymous_2_26",
      "rtcUid": 2137
    }
  },
  "timestamp": 1632299998307
}
```

## 命令号
|事件cmdId|含义|触发场景|
|----|----|----|
|1|课堂状态变更通知|课堂全体静音;开始上课|
|2|课堂状态删除通知|取消全体静音|
|10|设置房间属性通知|暂无|
|11|删除房间属性通知|暂无|
|20|成员属性修改通知|成员举手, 手放下, 举手同意等.|
|21|成员属性删除通知|删除举手属性.|
|30|成员加入通知|成员加入|
|31|成员离开通知|成员离开|
|40|成员流变更通知|成员打开音频流, 视频流, 共享屏幕.|
|41|成员流删除通知|成员删除流.|
|50|房间创建通知, 仅存在应用通知抄送|房间创建|
|51|房间删除通知|房间关闭后, 主动关闭或全部成员退出|
|99|自定义消息通知|对成员发送自定义消息后|

### 房间状态变更: CmdId=1:
| 名称|    类型|    描述|
|----|----|----|
| data.states.xxx | JsonObject| 被修改的属性|
```json
{
  "sequence": 21,
  "appKey": "app_id_test",
  "type": "R",
  "roomUuid": "12314111",
  "cmd": 1,
  "version": 1,
  "data": {
    "states": {
      "step": {
        "value": 1,
        "time": 1619095819608
      }
    },
    "operatorMember": {
      "role": "host",
      "userName": "HELLO",
      "userUuid": "abc",
      "rtcUid": 3
    }
  }
}
```

### 房间状态删除: cmdId=2:
| 名称|    类型|    描述|
|----|----|----|
| data.key | String| 被删除的属性key|
```json
{
  "sequence": 21,
  "appKey": "app_id_test",
  "type": "R",
  "roomUuid": "12314111",
  "cmd": 1,
  "version": 1,
  "data": {
    "key": "step",
    "operatorMember": {
      "role": "host",
      "userName": "HELLO",
      "userUuid": "abc",
      "rtcUid": 3
    }
  }
}

```

### 房间属性变更: cmdId=10:

| 名称|    类型|    描述|
|----|----|----|
| data.properties.xxx | JsonObject| 被修改的房间属性|
```json
{
  "sequence": 22,
  "appKey": "app_id_test",
  "type": "R",
  "roomUuid": "12314111",
  "cmd": 10,
  "version": 1,
  "data": {
    "properties": {
      "whiteboard": {
        "value": 1,
        "time": 1619095819608
      }
    },
    "operatorMember": {
      "role": "host",
      "userName": "HELLO",
      "userUuid": "abc",
      "rtcUid": 3
    }
  }
}

```

### 房间属性删除: cmdId=11:

| 名称|    类型|    描述|
|----|----|----|
| data.key | String| 被删除的房间属性key|
```json
{
  "sequence": 22,
  "appKey": "app_id_test",
  "type": "R",
  "roomUuid": "12314111",
  "cmd": 10,
  "version": 1,
  "data": {
    "key": "whiteboard",
    "operatorMember": {
      "role": "host",
      "userName": "HELLO",
      "userUuid": "abc",
      "rtcUid": 3
    }
  }
}
```

### 房间成员属性变更: cmdId=20:

| 名称|    类型|    描述|
|----|----|----|
| data.properties.xxx | JsonObject| 被修改的房间成员属性|
| data.member.xxx | JsonObject| 被修改的房间成员|
```json
{
  "sequence": 62,
  "appKey": "app_id_test",
  "type": "R",
  "roomUuid": "12314111",
  "cmd": 20,
  "version": 1,
  "data": {
    "properties": {
      "avHandsUp": {
        "value": 1,
        "time": 1619095819608
      }
    },
    "member": {
      "role": "host",
      "userName": "HELLO",
      "userUuid": "abcbbb1111",
      "rtcUid": 5
    },
    "operatorMember": {
      "role": "host",
      "userName": "HELLO",
      "userUuid": "abcbbb1111",
      "rtcUid": 5
    }
  }
}

```

### 房间成员属性删除: cmdId=21:

| 名称|    类型|    描述|
|----|----|----|
| data.key | String| 被删除的房间成员属性key|

```json
{
  "sequence": 62,
  "appKey": "app_id_test",
  "type": "R",
  "roomUuid": "12314111",
  "cmd": 20,
  "version": 1,
  "data": {
    "key": "avHandsUp",
    "member": {
      "role": "host",
      "userName": "HELLO",
      "userUuid": "abcbbb1111",
      "rtcUid": 5
    },
    "operatorMember": {
      "role": "host",
      "userName": "HELLO",
      "userUuid": "abcbbb1111",
      "rtcUid": 5
    }
  }
}

```

### 用户加入: cmdId=30:

| 名称|    类型|    描述|
|----|----|----|
| data.members | JsonArray| 加入的成员列表|

```json
{
  "sequence": 17,
  "appKey": "app_id_test",
  "type": "R",
  "roomUuid": "12314111",
  "cmd": 30,
  "version": 1,
  "data": {
    "members": [
      {
        "time": 1619095814241,
        "properties": {},
        "streams": {
          "audio": {
            "value": 1,
            "time": 1619095814239
          },
          "video": {
            "video": 0,
            "time": 1619095814239
          }
        },
        "role": "host",
        "userName": "HELLO",
        "userUuid": "abc",
        "rtcUid": 3
      }
    ],
    "operatorMember": {
      "time": 1619095814241,
      "properties": {},
      "streams": {
        "audio": {
          "value": 1,
          "time": 1619095814239
        },
        "video": {
          "value": 0,
          "time": 1619095814239
        }
      },
      "role": "host",
      "userName": "HELLO",
      "userUuid": "abc",
      "rtcUid": 3
    }
  }
}

```

### 用户离开: cmdId=31:

| 名称|    类型|    描述|
|----|----|----|
| data.members | JsonArray| 离开的成员列表|
```json
{
  "sequence": 17,
  "appKey": "app_id_test",
  "type": "R",
  "roomUuid": "12314111",
  "cmd": 30,
  "version": 1,
  "data": {
    "members": [
      {
        "time": 1619095814241,
        "properties": {},
        "streams": {
          "audio": {
            "value": 1,
            "time": 1619095814239
          },
          "video": {
            "video": 0,
            "time": 1619095814239
          }
        },
        "role": "host",
        "userName": "HELLO",
        "userUuid": "abc",
        "rtcUid": 3
      }
    ],
    "operatorMember": {
      "time": 1619095814241,
      "properties": {},
      "streams": {
        "audio": {
          "value": 1,
          "time": 1619095814239
        },
        "video": {
          "value": 0,
          "time": 1619095814239
        }
      },
      "role": "host",
      "userName": "HELLO",
      "userUuid": "abc",
      "rtcUid": 3
    }
  }
}

```

### 房间成员流变更: cmdId=40:

| 名称|    类型|    描述|
|----|----|----|
| data.streams.xxx | JsonObject| 被修改的房间成员流 video，audio，subVideo|
```json
{
  "sequence": 18,
  "appKey": "app_id_test",
  "type": "R",
  "roomUuid": "12314111",
  "cmd": 40,
  "version": 1,
  "data": {
    "streams": {
      "video": {
        "value": 0,
        "time": 1619095814239
      }
    },
    "member": {
      "role": "host",
      "userName": "HELLO",
      "userUuid": "abc",
      "rtcUid": 3
    }
  }
}

```

### 房间成员流移除: cmdId=41:

| 名称|    类型|    描述|
|----|----|----|
| data.key | String| 被删除的房间成员流类型|

```json
{
  "sequence": 20,
  "appKey": "app_id_test",
  "type": "R",
  "roomUuid": "12314111",
  "cmd": 41,
  "version": 1,
  "data": {
    "streamType": "subVideo",
    "member": {
      "role": "host",
      "userName": "HELLO",
      "userUuid": "abc",
      "rtcUid": 3
    },
    "operatorMember": {
      "role": "host",
      "userName": "HELLO",
      "userUuid": "abc",
      "rtcUid": 3
    }
  }
}
```

### 消息: cmdId=99:

```json
{
  "cmd": 99,
  "appKey": "app_id_test",
  "type": "RM",
  "roomUuid": "12314111",
  "version": 1,
  "data": {
    "message": {
      "body": {
        "a": 1
      },
      "type": 1
    },
    "operatorMember": {
      "role": "broadcaster",
      "userName": "HELLO",
      "userUuid": "anonymous_1_9"
    }
  }
}
```
