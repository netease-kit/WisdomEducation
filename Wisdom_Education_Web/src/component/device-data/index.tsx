import React from 'react';
import { observer } from 'mobx-react';
import logger from '@/lib/logger';
import './index.less';


export type DataType = 'speaker' | 'camera' | 'microphone';

export interface DataList {
  label: string;
  value: string;
}

export interface DeviceDataList {
  title: string;
  type: DataType;
  list: DataList[];
  value?: string;
  onChange: (v: DataList) => void | Promise<void>;
}

export interface IProps {
  data: DeviceDataList[];
  position: {
    left?: number | string;
    right?: number | string;
    top?: number | string;
    bottom?: number | string;
  };
  visible?: boolean;
}

const handleClickNull = (e) => {
  const ev = e || window.Event;
  ev.stopPropagation();
}

const DeviceData:React.FC<IProps> = observer(({data, position, visible = true}) => {
  return visible ? (
    <div className="data-wrapper" style={{ ...position }} onClick={handleClickNull}>
      { data?.map((item) => (
        <div className="data-content" key={item.title}>
          <div className="data-title">{item.title}</div>
          <div>
            { item.list.map((v) => (
              <div className="data-value" key={v.value} onClick={() => { item.onChange({...v})}}>
                <span className="data-text">{v.label}</span>
                {v.value === item.value && <img src={require('@/assets/imgs/select.png').default} alt="select" />}
              </div>
            ) )}
          </div>
        </div>
      )) }
    </div>
  ) : null
})

export default DeviceData;
