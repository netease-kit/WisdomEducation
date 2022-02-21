/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import React from 'react';
import './index.less';
import intl from 'react-intl-universal';

const ScreenSharing: React.FC = () => {
  return (
    <div className="screen-sharing">
      <span>{intl.get('您正在进行屏幕共享')}</span>
    </div>
  )
}

export default ScreenSharing;
