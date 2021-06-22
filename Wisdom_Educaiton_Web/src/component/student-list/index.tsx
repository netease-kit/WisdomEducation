/*
 * @Author: lizhaoxuan
 * @Date: 2021-05-20 16:09:16
 * @LastEditTime: 2021-05-20 16:19:28
 * @LastEditors: Please set LastEditors
 * @Description: 教师与学生列表
 * @FilePath: /app_wisdom_education_web/src/component/student-list/index.tsx
 */

import React from 'react';
import { observer } from 'mobx-react';
import './index.less';


const StudentList: React.FC = observer(({ children }) => {
  return (
    <div className="room-student-list">
      {children}
    </div>
  )
})

export default StudentList
