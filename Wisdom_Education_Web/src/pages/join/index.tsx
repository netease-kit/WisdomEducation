/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import React, { FC, useState, useEffect } from "react";
import { useHistory } from "react-router-dom";
import { Input, Select, Button, Radio, Form } from "antd";
import logger from "@/lib/logger";
import logo from "@/assets/imgs/book.png";
import android from "@/assets/imgs/android.png";
import ios from "@/assets/imgs/ios.png";
import "./index.less";
import { observer } from "mobx-react";
import { RoomTypes, RoleTypes, isDev } from "@/config";
import { useRoomStore } from "@/hooks/store";
import { GlobalStorage } from "@/utils";

const courseOptions = [
  {
    label: "一对一",
    value: RoomTypes.oneToOne,
    path: "/classroom/one-to-one",
  },
  {
    label: "小班课",
    value: RoomTypes.smallClass,
    path: "/classroom/small-class",
  },
  {
    label: "大班课",
    value: RoomTypes.bigClass,
    path: "/classroom/big-class",
  },
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
  };

  const onFinish = (values) => {
    logger.log("values", values);
    const roomUuid = isNaN(+values.roomUuid)
      ? values.roomUuid.slice(0, -1)
      : values.roomUuid;
    const param = {
      ...values,
      roomUuid: `${roomUuid}${values.sceneType}`,
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
    history.push({
      pathname: path,
    });
    // roomStore.join(param)
  };

  const handleNumFocus = (e) => {
    setRoomNum(e.target.value || "");
  };

  useEffect(() => {
    GlobalStorage.clear();
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
            <a href="https://pgyer.netease.im/s/RqHj">
              <img className="imgAndroid" src={android} alt="android" />
            </a>
            <a href="https://pgyer.netease.im/s/cp7S">
              <img className="imgIos" src={ios} alt="ios" />
            </a>
          </div>
        </div>
        <div className="wrapper-joinForm">
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
                <Radio.Group className="joinForm-radio" options={roleOptions} />
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
            </Form>
            <p className="tips-message">
              *本产品仅用于演示产品功能，课堂最长30分钟，不可商用
            </p>
          </div>
        </div>
      </div>
    </div>
  );
});
export default Join;
