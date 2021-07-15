/*
* @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
* Use of this source code is governed by a MIT license that can be found in the LICENSE file
*/
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */


import axios, { AxiosResponse } from 'axios';
import qs from 'querystring';
import logger from '@/lib/logger';
import { uuid, checkType } from '@/utils';
import { message } from 'antd';


export const baseUrl = process.env.REACT_APP_SDK_DOMAIN;

const deviceId = localStorage.getItem('edu-deviceId') || uuid();

localStorage.setItem('edu-deviceId', deviceId);

const defaultHeaders = {
  'Content-Type': 'application/json',
  'Authorization': `Basic ${process.env.REACT_APP_SDK_AUTHORIZATION}`,
  'clientType': 'web',
  versionCode: 10,
  deviceId,
};

// axios的实例及拦截器配置
const req = axios.create({
  baseURL: `${window.location.protocol}//${baseUrl}/scene/apps/${process.env.REACT_APP_SDK_APPKEY}/`,
  timeout: 30000, // 超时时间
  responseType: 'json', // default
  headers: defaultHeaders,
  validateStatus: (status) => status >= 200 && status < 500,
});

req.interceptors.response.use((res) => {
  // 相应拦截
  const {
    status,
  } = res;
  if (/^2\d{2}/.test(status.toString())) {
    // TODO
    const {
      data,
    } = res;
    const code = parseInt(data.code, 10);
    // if (code === -200) { // 登录失效，跳转至登录
    //   const { dispatch } = store;
    //   dispatch(routerRedux.push(`/login${window.location.search}`));
    //   return Promise.reject(res);
    // }
    switch (code) {
      case 400:
        message.error('参数非法');
        break;
      // case 403:
      //   message.error('无操作权限');
      //   break;
      case 415:
        message.error('内部异常');
        break;
      case 1003:
        message.error('指定角色未定义');
        break;
      default:
        break;
    }
    if (code !== 0) {
      return Promise.reject(res?.data);
    } // 成功 code === 0
    if (checkType(res?.data?.data, 'array')) {
      return Promise.resolve(data?.data);
    }
    return Promise.resolve({
      ...res?.data?.data,
      timestamp: res?.data?.ts,
    });

    // return Promise.resolve(res && res.data);
  }
  return Promise.reject(res?.data);
}, (error) => {
  logger.error('请求失败', error);
  if (axios.isCancel(error)) { // 取消请求的情况下，终端Promise调用链
    return Promise.reject(error);
  }
  return Promise.reject(error);
});
const { post, get, put } = req;

const reqDelete = req.delete;

const postForm = async (url: string, data: any): Promise<any> => {
  try {
    const res = await req.post(url, qs.stringify(data), {
      headers: {
        ...defaultHeaders,
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    });
    return res;
  } catch (error) {
    // console.log(error);
  }
};

const postFormData = (url: string, data: any) => req.post(url, data, {
  headers: {
    ...defaultHeaders,
    'Content-Type': 'multipart/form-data',
  },
});

export {
  req,
  get,
  post,
  postForm,
  postFormData,
  put,
  reqDelete,
};
