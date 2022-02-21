/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

import React from 'react';
import './index.less';
import intl from 'react-intl-universal';

export enum classStatus {
  notStart = 0,
  pause = 1,
}

interface BeforeOrInClassOptions {
  status?: classStatus;
}


const BeforeOrInClass: React.FC<BeforeOrInClassOptions> = ({
  status = classStatus.notStart,
}) => {
  return (
    <div className="beforeorin-class">
      {status === classStatus.notStart && <div className="before-class common">
        <div className="before-class-img beforeorin-class-pic"></div>
        <p className="current-description">{intl.get('课程还未开始，请耐心等待哦')}</p>
      </div>}
      {status === classStatus.pause && <div className="pause-class common">
        <div className="pause-class-img beforeorin-class-pic"></div>
        <p className="current-description">{intl.get('老师暂离，请耐心等待老师回来哦～')}</p>
      </div>}
    </div>
  )
}

export default BeforeOrInClass;
