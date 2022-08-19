/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

import React, { useEffect, useState } from 'react';
import { observer } from 'mobx-react';
import './index.less';
import { Form, Checkbox, Radio } from 'antd';
import { SUPPORT_LOCALES, currentLocale, initLocales } from '@/utils/universal';
import intl from 'react-intl-universal';

const langOptions = SUPPORT_LOCALES;
const JoinSetting: React.FC = observer(() => {
  const [form] = Form.useForm();
  const formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 },
  };
  const [defaultSettingConfig] = useState({
    chatroom: true,
  })
  const [lang, setLang] = useState("")
  const handleLangChange = (e) => {
    const value = e.target.value
    setLang(value)
    localStorage.setItem('lang', value)
    initLocales(value)
  }
  const handleFormChange = (changeValue, allValue) => {
    localStorage.setItem('room-setting', JSON.stringify(allValue))
    console.log(allValue);
  }
  useEffect(() => {
    const stroageInfo = localStorage.getItem('room-setting')
    const result = stroageInfo? JSON.parse(stroageInfo) : defaultSettingConfig;
    form.setFieldsValue(result)
    setLang(currentLocale())
  }, [])
  return (
    <div className="room-join-setting">
      <Form
        { ...formItemLayout }
        className="room-join-setting-form"
        form={form}
        onValuesChange={handleFormChange}
        labelCol={{span:8}}
      >
        <Form.Item valuePropName="checked" name="chatroom" label={intl.get('开启聊天室')}>
          <Checkbox />
        </Form.Item>
        {/* <Form.Item valuePropName="checked" name="nertsLive" label={intl.get('使用低延时直播')}>
          <Checkbox />
        </Form.Item> */}
        <Form.Item valuePropName="checked" name="teaPlugFlow" label={intl.get('老师端推流')}>
          <Checkbox />
        </Form.Item>
        <Form.Item label={intl.get('切换语言')}>
          <Radio.Group
            options={langOptions}
            value={lang}
            onChange={handleLangChange}
          />
        </Form.Item>
      </Form>
    </div>
  )
})

export default JoinSetting;
