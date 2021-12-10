/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import React, { FC, useState, useEffect, useMemo, Fragment } from "react";
import { useHistory } from "react-router-dom";
import { Input, Select, Button, Radio, Form, Modal } from "antd";
import logger from "@/lib/logger";
import logo from "@/assets/imgs/book.png";
import android from "@/assets/imgs/android.png";
import ios from "@/assets/imgs/ios.png";
import "./index.less";
import { observer } from "mobx-react";
import { RoomTypes, RoleTypes, isDev, isElectron } from "@/config";
import { useRoomStore } from "@/hooks/store";
import { GlobalStorage } from "@/utils";
import Icon from "@/component/icon";
import JoinSetting from '@/component/setting';

const courseOptions = [
  {
    label: "一对一教学",
    value: RoomTypes.oneToOne,
    path: "/classroom/one-to-one",
  },
  {
    label: "多人小班课",
    value: RoomTypes.smallClass,
    path: "/classroom/small-class",
  },
  {
    label: "互动大班课",
    value: RoomTypes.bigClass,
    path: "/classroom/big-class",
  },
  {
    label: "直播大班课",
    value: RoomTypes.bigClasLive,
    path: '/classroom/big-class-live',
  }
];
const roleOptions = [
  {
    label: "老师",
    value: RoleTypes.host,
  },
  {
    label: "学生",
    value: RoleTypes.broadcaster,
  },
];

const roomUuidReg = new RegExp(/^\d{1,10}$/);

const Join: FC = observer(() => {
  const [disabled, setDisabled] = useState(true);
  const [form] = Form.useForm();
  const roomStore = useRoomStore();
  const history = useHistory();
  const [roomNum, setRoomNum] = useState<undefined | string>(undefined);
  const recordUrl = useMemo(() => localStorage.getItem('record-url'), []);
  const [showSetting, setShowSetting] = useState(false);
  const [sceneType, setSceneType] = useState<RoomTypes>()

  const handleFormChange = (changedValues, allValues) => {
    const formValue = Object.keys(allValues).some(
      (item) =>
        !["uuid", "token"].includes(item) &&
        (allValues[item] === undefined || allValues[item] === "")
    );
    const verifyNum =
      !roomUuidReg.test(allValues["roomUuid"]) &&
      allValues.roomUuid?.length === 1;
    if (changedValues.roomUuid !== undefined) {
      if (
        roomUuidReg.test(changedValues.roomUuid) ||
        changedValues.roomUuid === ""
      ) {
        setRoomNum(changedValues.roomUuid);
      }
    }
    setDisabled(formValue || verifyNum);
    setSceneType(allValues['sceneType'])
  };

  const onFinish = (values) => {
    logger.log("values", values);

    /**
     * http://jira.netease.com/browse/YYTX-3445
     * roomUuid应该为纯数字
     */
    const roomUuid = (values.roomUuid || '').match(/\d+/g).join('')
    if (roomUuid.length === 0) {
      return
    }

    const param = {
      ...values,
      roomUuid,
      // roomUuid: `${roomUuid}${values.sceneType}`,
      roomName: `${values.userName}的课堂`,
      role:
        values.sceneType === RoomTypes.bigClass &&
        values.role === RoleTypes.broadcaster
          ? RoleTypes.audience
          : values.role,
    };
    param.userUuid = `${param.userName}${param.role}`;
    GlobalStorage.save("user", param);
    let path = "";
    courseOptions.some((item) => {
      if (item.value === param.sceneType) {
        path = item.path;
        return true;
      }
    });
    if (param.sceneType === RoomTypes.bigClasLive) {
      if (param.role === RoleTypes.host) {
        path += '-tea'
      } else {
        path += '-stu'
      }
    }
    history.push({
      pathname: path,
    });
    // roomStore.join(param)
  };

  const handleNumFocus = (e) => {
    setRoomNum(e.target.value || "");
  };

  useEffect(() => {
    /**
     * 进入join页面后，先清空之前的房间状态
     * http://jira.netease.com/browse/YYTX-3439
     */
    roomStore.leave()
    GlobalStorage.clear();
    roomStore.setClassDuration(0);
  }, []);

  return (
    <div className="wrapper">
      <div className="wrapper-join">
        <div className="wrapper-joinText">
          <div className="joinText-header">
            欢迎使用
            <br />
            智慧云课堂
          </div>
          <div className="joinText-desc">
            网易云信在线教育场景方案助您快速搭建专属在线课堂，
            <br />
            支持一对一、小班课、大班课多种教育场景。
            <br />
            <br />
            基于新一代音视频通话2.0产品，为您提供超低延时、高清稳定的音视频体验。
          </div>
          <div className="joinText-download">
            安装包
            <a href="https://www.pgyer.com/AvhN">
              <img className="imgAndroid" src={android} alt="android" />
            </a>
            <a href="https://www.pgyer.com/gRZh">
              <img className="imgIos" src={ios} alt="ios" />
            </a>
          </div>
        </div>
        <div className="wrapper-joinForm">
          <Icon className="join-setting" type="iconyx-tv-settingx" onClick={() => setShowSetting(true)} />
          <div className="wrapper-joinForm-content">
            <div className="joinForm-title-outer">
              <img src={logo} alt="" className="joinForm-book" />
              <div className="joinForm-title">智慧云课堂</div>
            </div>
            <div className="joinForm-desc">若课堂不存在则会创建课堂</div>
            <Form
              className="joinForm-form"
              form={form}
              onFinish={onFinish}
              onValuesChange={handleFormChange}
            >
              {isDev && (
                <>
                  <Form.Item
                    name="uuid"
                    // rules={[{ required: true, message: '课堂号格式错误', pattern: roomUuidReg }]}
                  >
                    <div>
                      <Input
                        className="joinForm-input"
                        placeholder="请输入id"
                      />
                    </div>
                  </Form.Item>
                  <Form.Item name="token">
                    <div>
                      <Input
                        className="joinForm-input"
                        placeholder="请输入token"
                      />
                    </div>
                  </Form.Item>
                </>
              )}
              {/* <Form.Item name="roomName">
                <Input
                  className="joinForm-input"
                  placeholder="请输入课堂名称"
                  maxLength={20}
                  autoComplete="off"
                />
              </Form.Item> */}
              <Form.Item name="roomUuid">
                <div>
                  <Input
                    className="joinForm-input"
                    placeholder="请输入课堂号"
                    autoComplete="off"
                    maxLength={10}
                    value={roomNum}
                    onFocus={handleNumFocus}
                  />
                </div>
              </Form.Item>
              <Form.Item name="userName">
                <Input
                  className="joinForm-input"
                  placeholder="请输入昵称"
                  maxLength={20}
                  autoComplete="off"
                />
              </Form.Item>
              <Form.Item name="sceneType">
                <Select
                  className="courseType"
                  placeholder="请输入课堂类型"
                  options={courseOptions}
                  dropdownClassName="select"
                />
              </Form.Item>
              <Form.Item name="role">
                <Radio.Group className="joinForm-radio">
                  {roleOptions.map((item) => (
                    RoomTypes.bigClasLive === sceneType && item.value === RoleTypes.host && isElectron ?
                      <Fragment key={item.value}></Fragment> :
                      <Radio key={item.value} value={item.value}>{item.label}</Radio>
                  ))}
                </Radio.Group>
              </Form.Item>
              <Form.Item>
                <Button
                  type="primary"
                  htmlType="submit"
                  className={disabled ? "joinForm-btn active" : "joinForm-btn"}
                >
                  加入课堂
                </Button>
              </Form.Item>
              {recordUrl && isElectron && <Form.Item>
                <Button
                  onClick={() => history.push(`${recordUrl}`)}
                  className="joinForm-btn record-btn"
                >
                  查看回放
                </Button>
              </Form.Item>}
            </Form>
            <p className="tips-message">
              *本产品仅用于演示产品功能，课堂最长30分钟，不可商用
            </p>
          </div>
        </div>
      </div>
      <Modal
        visible={showSetting}
        title="设置"
        centered
        footer={null}
        onCancel={() => setShowSetting(false)}
      >
        <JoinSetting />
      </Modal>
    </div>
  );
});
export default Join;
