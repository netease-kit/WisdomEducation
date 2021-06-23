/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
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
