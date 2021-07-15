/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import React from 'react';
import { Link, useHistory, useLocation } from 'react-router-dom';
import logger from '@/lib/logger';
import { observer } from 'mobx-react';
import { Button } from 'antd';
import Header from '@/component/header';
import { GlobalStorage } from '@/utils'
import './index.less';

const EndCourse = observer(() => {
  const useQuery = () => {
    return new URLSearchParams(useLocation().search);
  }
  const query = useQuery();

  return (
    <div className="endCourse-wrapper">
      <Header isHave={true} />
      <div className="endCourse-container">
        <div className="endCourse-content">
          <p className="title">课程已结束</p>
          <Button type="ghost" className="playback"><Link to={`/record?roomUuid=${query.get('roomUuid')}&rtcCid=${query.get('rtcCid')}`}>查看课程回放</Link></Button>
          <Button type="ghost"><Link to="/">返回</Link></Button>
        </div>
      </div>
    </div>
  )
});

export default EndCourse;
