import React from 'react';
import { Button, message } from 'antd';
import copy from 'copy-to-clipboard';
import { Link } from 'react-router-dom';
import './index.less';

const Empty: React.FC = () => {

  const copyToClip = () => {
    copy( window.location.href);
    message.success('复制成功')
  }

  return (
    <div className="empty-wrap">
      <div className="content">
        <div className="process">
          <span></span>
        </div>
        <h1 className="title">课程回放文件转码中，预计20分钟完成，请稍后</h1>
        <div className="desc">您可以收藏回放地址，晚点再来查看哦</div>
        <div className="btn-wrap">
          <Button type="ghost" className="playback" onClick={copyToClip}>复制回放地址</Button>
          <Button type="ghost"><Link to="/">返回</Link></Button>
        </div>
      </div>
    </div>
  )
}

export default Empty;
