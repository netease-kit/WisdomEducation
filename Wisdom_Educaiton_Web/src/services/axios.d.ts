/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

import * as axios from 'axios'

declare module 'axios' {
  interface AxiosInstance {
    (config: AxiosRequestConfig): Promise<any>;
    request<T = any, R = T> (config: AxiosRequestConfig): Promise<R>;
    get<T = any, R = T>(url: string, config?: AxiosRequestConfig): Promise<R>;
    delete<T = any, R = T>(url: string, config?: AxiosRequestConfig): Promise<R>;
    head<T = any, R = T>(url: string, config?: AxiosRequestConfig): Promise<R>;
    options<T = any, R = T>(url: string, config?: AxiosRequestConfig): Promise<R>;
    post<T = any, R = T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<R>;
    put<T = any, R = T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<R>;
    patch<T = any, R = T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<R>;
  }
}
