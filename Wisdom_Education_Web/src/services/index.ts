/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */

import axios, { AxiosResponse } from "axios";
import qs from "querystring";
import logger from "@/lib/logger";
import { uuid, checkType, history } from "@/utils";
import { message } from "antd";
import { Authorization } from "@/config";
import { AppStore } from "@/store";
import { defaultStore } from "@/index";
import { isElectron } from "@/config";
import sessionStorage from "@/utils/sessionStorage";
import intl from 'react-intl-universal';

export const baseUrl = process.env.REACT_APP_SDK_DOMAIN;

const deviceId = sessionStorage.getItem("edu-deviceId") || uuid();

sessionStorage.setItem("edu-deviceId", deviceId);

const defaultHeaders = {
  "Content-Type": "application/json",
  Authorization: Authorization,
  versionCode: 111,
  clientType: isElectron ? "electron" : "web",
  deviceId,
};

// axios instance and blocker configuration
const req = axios.create({
  baseURL: `https://${baseUrl}/scene/apps/${process.env.REACT_APP_SDK_APPKEY}/`,
  timeout: 30000, // Timeout duration
  responseType: "json", // default
  headers: defaultHeaders,
  validateStatus: (status) => status >= 200 && status < 500,
});

req.interceptors.response.use(
  (res) => {
    // corresponding blocking
    const { status } = res;
    if (/^2\d{2}/.test(status.toString())) {
      // TODO
      const { data } = res;
      const code = parseInt(data.code, 10);
      // if (code === -200) { // Login failed, redirect to the login page
      //   const { dispatch } = store;
      //   dispatch(routerRedux.push(`/login${window.location.search}`));
      //   return Promise.reject(res);
      // }
      switch (code) {
        case 400:
          message.error(intl.get("参数错误"));
          break;
        case 401:
          message.error(intl.get("鉴权失败"));
          defaultStore.roomStore.leave();
          history.push("/");
          break;
        case 415:
          message.error(intl.get("内部异常"));
          break;
        case 1003:
          message.error(intl.get("指定角色未定义"));
          break;
        case 1004:
          message.error(intl.get('房间不存在'));
          defaultStore.roomStore.leave();
          history.push("/");
          break;
        case 1016:
          message.error(intl.get("用户已在房间中"));
          break;
        default:
          break;
      }
      if (code !== 0) {
        return Promise.reject(res?.data);
      } // success code === 0
      if (checkType(res?.data?.data, "array")) {
        return Promise.resolve(data?.data);
      }
      return Promise.resolve({
        ...res?.data?.data,
        timestamp: res?.data?.ts,
      });

      // return Promise.resolve(res && res.data);
    }
    return Promise.reject(res?.data);
  },
  (error) => {
    logger.error("Request failed", error);
    if (axios.isCancel(error)) {
      // If a request was cancelled, the client promise call link
      return Promise.reject(error);
    }
    return Promise.reject(error);
  }
);
const { post, get, put } = req;

const reqDelete = req.delete;

const postForm = async (url: string, data: any): Promise<any> => {
  try {
    const res = await req.post(url, qs.stringify(data), {
      headers: {
        ...defaultHeaders,
        "Content-Type": "application/x-www-form-urlencoded",
      },
    });
    return res;
  } catch (error) {
    // console.log(error);
  }
};

const postFormData = (url: string, data: any) =>
  req.post(url, data, {
    headers: {
      ...defaultHeaders,
      "Content-Type": "multipart/form-data",
    },
  });

export { req, get, post, postForm, postFormData, put, reqDelete };
