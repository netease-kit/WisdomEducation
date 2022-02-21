/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import React from 'react';
import { Button, message } from 'antd';
import copy from 'copy-to-clipboard';
import { Link } from 'react-router-dom';
import { isElectron } from '@/config';
import './index.less';
import intl from 'react-intl-universal';

const Empty: React.FC = () => {

  const copyToClip = () => {
    copy( window.location.href);
    message.success(intl.get('复制成功'))
  }

  return (
    <div className="empty-wrap">
      <div className="content">
        <div className="process">
          <span></span>
        </div>
        <h1 className="title">{intl.get('课程结束后，需进行文件转码，预计20分钟后可观看回放')}</h1>
        {!isElectron && <div className="desc">{intl.get('您可以收藏回放地址，晚点再来查看哦')}</div>}
        <div className={`btn-wrap ${isElectron && 'for-ele'}`}>
          {!isElectron && <Button type="ghost" className="playback" onClick={copyToClip}>{intl.get('复制回放地址')}</Button>}
          <Button type="ghost"><Link to="/">{intl.get('返回')}</Link></Button>
        </div>
      </div>
    </div>
  )
}

export default Empty;
