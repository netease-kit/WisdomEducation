/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
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

