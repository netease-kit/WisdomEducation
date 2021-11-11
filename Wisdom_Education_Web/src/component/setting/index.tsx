/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

import React, { useEffect, useState } from 'react';
import { observer } from 'mobx-react';
import './index.less';
import { Form, Checkbox } from 'antd';

const JoinSetting: React.FC = observer(() => {
  const [form] = Form.useForm();
  const formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 },
  };
  const [defaultSettingConfig] = useState({
    chatroom: true,
  })
  const handleFormChange = (changeValue, allValue) => {
    localStorage.setItem('room-setting', JSON.stringify(allValue))
    console.log(allValue);
  }
  useEffect(() => {
    const stroageInfo = localStorage.getItem('room-setting')
    const result = stroageInfo? JSON.parse(stroageInfo) : defaultSettingConfig;
    form.setFieldsValue(result)
  }, [])
  return (
    <div className="room-join-setting">
      <Form
        { ...formItemLayout }
        className="room-join-setting-form"
        form={form}
        onValuesChange={handleFormChange}
      >
        <Form.Item valuePropName="checked" name="chatroom" label="开启聊天室">
          <Checkbox />
        </Form.Item>
        <Form.Item valuePropName="checked" name="nertsLive" label="使用低延时直播">
          <Checkbox />
        </Form.Item>
      </Form>
    </div>
  )
})

export default JoinSetting;
