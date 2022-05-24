/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

import { Md5 } from 'ts-md5';
import { createHashHistory } from 'history';
import sessionStorage from './sessionStorage';


export const history = createHashHistory();

export const uuid = (): string => {
  const res = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    const r = (Math.random() * 16) | 0;
    const v = c === 'x' ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
  return res
};

export const encode = (s: string): Int32Array | string => {
  return Md5.hashStr(s + '@163');
};

/**
 * all data must be stored to sessionStorage.wisState to prevent the data in sessionStorage from deleting when calling the clear method */
export class CustomStorage {

  private storage: Storage;


  constructor() {
    this.storage = sessionStorage;
  }

  read(key: string): any {
    const appStateObj = this._getAppState()
    return appStateObj[key] || null
  }

  save(key: string, val: any): void {
    const appStateObj = this._getAppState()
    appStateObj[key] = val

    this.storage.setItem('wisState', JSON.stringify(appStateObj));
  }

  remove(key: string): void {
    const appStateObj = this._getAppState()
    delete appStateObj[key]
    this.storage.setItem('wisState', JSON.stringify(appStateObj))
  }

  clear(): void {
    this.storage.setItem('wisState', JSON.stringify({}))
  }

  _getAppState(): any {
    const appStateStr = this.storage.getItem('wisState') || ''
    let appStateObj = {}
    try {
      appStateObj = JSON.parse(appStateStr)
    } catch(err) {
      appStateObj = {}
    }
    return appStateObj
  }
}

export const GlobalStorage = new CustomStorage();

(window as any).GlobalStorage = GlobalStorage;

/**
 * Parse the file size
 * @param size file size in bytes
 * @param level incursive level for fileSizeMap
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
 * @description: debounce
 * @param {any} fn
 * @param {*} ms
 * @return {*}
 */
// export function debounce(fn: any, ms = 2000): (...rest: any[]) => void {
//   let timeoutId
//   return  (...rest) => {
//     clearTimeout(timeoutId)
//     timeoutId = setTimeout(() => {
//       // @ts-ignore
//       fn.apply(this, rest)
//     }, ms)
//   }
// }

let timeout: any;
export function debounce(fn, wait = 2000) { // debounce function
  if(timeout !== null) clearTimeout((timeout as any));timeout = null;
  timeout = setTimeout(fn, wait) as any
}

/**
 * @description: check types
 * @param {any} val
 * @param {*} type
 * @return {*}
 */
export function checkType(val: any, type?: string): boolean|string { // check types
  if (type) return Object.prototype.toString.call(val).slice(8, -1).toLowerCase() === type.toLowerCase();
  return Object.prototype.toString.call(val).slice(8, -1).toLowerCase();
}

/**
 * @description: Get URL parameters
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

/**
 * @description: remove spaces at the beginning and the end of a string
 * @param {string} str
 * @return {string}
 */
export function trimStr(str: string): string {
  return str.replace(/(^\s*)|(\s*$)/g,"");
}
