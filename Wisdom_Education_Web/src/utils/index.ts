/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

import { Md5 } from 'ts-md5';
import { createHashHistory } from 'history';

export const history = createHashHistory();

export const uuid = (): string => {
  const key = 'wyyx__education__uuid';
  let res = localStorage.getItem(key);
  if (!res) {
    res = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
      const r = (Math.random() * 16) | 0;
      const v = c === 'x' ? r : (r & 0x3) | 0x8;
      return v.toString(16);
    });
    localStorage.setItem(key, res);
  }
  return res;
};

export const encode = (s: string): Int32Array | string => {
  return Md5.hashStr(s + '@163');
};
export class CustomStorage {

  private storage: Storage;


  constructor() {
    this.storage = window.sessionStorage;
  }

  read(key: string): any {
    try {
      const json = JSON.parse(this.storage.getItem(key) as string);
      return json
    } catch(_) {
      return this.storage.getItem(key);
    }
  }

  save(key: string, val: any): void {
    this.storage.setItem(key, JSON.stringify(val));
  }

  remove(key: string): void {
    this.storage.removeItem(key);
  }
  clear(): void {
    this.storage.clear();
  }
}

export const GlobalStorage = new CustomStorage();

(window as any).GlobalStorage = GlobalStorage;

/**
 * 解析输入的文件大小
 * @param size 文件大小，单位b
 * @param level 递归等级，对应fileSizeMap
 */
export const parseFileSize = (size: number, level = 0): string => {
  const fileSizeMap: { [key: number]: string } = {
    0: 'B',
    1: 'KB',
    2: 'MB',
    3: 'GB',
    4: 'TB',
  };

  const handler = (size: number, level: number): string => {
    if (level >= Object.keys(fileSizeMap).length) {
      return 'the file is too big';
    }
    if (size < 1024) {
      return `${size}${fileSizeMap[level]}`;
    }
    return handler(Math.round(size / 1024), level + 1);
  };
  return handler(size, level);
};

export const addUrlSearch = (url: string, search: string): string => {
  const urlObj = new URL(url);
  urlObj.search += (urlObj.search.startsWith('?') ? '&' : '?') + search;
  return urlObj.href;
};

export const matchExt = (extname: string): string => {
  const regMap: { [key: string]: RegExp } = {
    pdf: /pdf$/i,
    word: /(doc|docx)$/i,
    excel: /(xls|xlsx)$/i,
    ppt: /(ppt|pptx)$/i,
    zip: /(rar|zip|7z|gz)$/i,
  };
  return Object.keys(regMap).find((key) => regMap[key].test(extname)) || '';
};


/**
 * @description: 防抖
 * @param {any} fn
 * @param {*} ms
 * @return {*}
 */
export function debounce(fn: any, ms = 2000): (...rest: any[]) => void {
  let timeoutId
  return  (...rest) => {
    clearTimeout(timeoutId)
    timeoutId = setTimeout(() => {
      // @ts-ignore
      fn.apply(this, rest)
    }, ms)
  }
}

/**
 * @description: 判断类型
 * @param {any} val
 * @param {*} type
 * @return {*}
 */
export function checkType(val: any, type?: string): boolean|string { // 检测类型
  if (type) return Object.prototype.toString.call(val).slice(8, -1).toLowerCase() === type.toLowerCase();
  return Object.prototype.toString.call(val).slice(8, -1).toLowerCase();
}

/**
 * @description: 获取URL参数
 * @param {any} val
 * @param {*} type
 * @return {*}
 */
export const getQueryString = (name) => {
  const reg = new RegExp(`(^|&)${name}=([^&]*)(&|$)`, 'i');
  const r = window.location.search.substr(1).match(reg) || window.location.hash.substring((window.location.hash.search(/\?/)) + 1).match(reg);
  if (r != null) {
    return unescape(r[2]);
  }
  return '';
};
