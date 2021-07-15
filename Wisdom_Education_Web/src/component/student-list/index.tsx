/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
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
