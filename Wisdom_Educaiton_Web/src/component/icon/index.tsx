/*
 * @Author: lizhaoxuan
 * @Date: 2021-05-28 15:12:06
 * @LastEditTime: 2021-05-28 15:12:35
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: /app_wisdom_education_web/src/component/icon/index.tsx
 */

import React from 'react';
import styled from 'styled-components';
import { createFromIconfontCN } from '@ant-design/icons';

const MyIcon = createFromIconfontCN({
  scriptUrl: '//at.alicdn.com/t/font_2183559_08q78brd4gls.js',
});

const Icon = styled(MyIcon)`
  width: ${(props) => props.width || '24'}px;
  height: ${(props) => props.height || '24'}px;
  color: ${(props) => props.color || 'inherit'};
  & > svg {
    width: ${(props) => props.width || '24'}px;
    height: ${(props) => props.height || '24'}px;
  }
`;

export default Icon;
