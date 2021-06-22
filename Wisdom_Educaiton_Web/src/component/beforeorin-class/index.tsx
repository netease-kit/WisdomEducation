/*
 * @Author: lizhaoxuan
 * @Date: 2021-05-17 14:10:16
 * @LastEditTime: 2021-05-17 16:03:10
 * @LastEditors: Please set LastEditors
 * @Description: 课程未开始
 * @FilePath: /app_wisdom_education_web/src/component/before-class/index.tsx
 */

import React from 'react';
import './index.less';

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
        <p className="current-description">课程还未开始，请耐心等待哦</p>
      </div>}
      {status === classStatus.pause && <div className="pause-class common">
        <div className="pause-class-img beforeorin-class-pic"></div>
        <p className="current-description">老师暂离，请耐心等待老师回来哦～</p>
      </div>}
    </div>
  )
}

export default BeforeOrInClass;
