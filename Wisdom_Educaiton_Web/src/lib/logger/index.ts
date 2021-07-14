/*
 * @Author: your name
 * @Date: 2021-05-12 17:01:35
 * @LastEditTime: 2021-05-12 17:45:10
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: /app_wisdom_education_web/src/lib/logger/index.ts
 */
import logDebug from 'yunxin-log-debug';
import packageJson from '../../../package.json';

const logger = logDebug({
  appName: 'yunxinEducation',
  version: packageJson.version,
  level: 'trace',
  storeWindow: true,
})

export default logger;

