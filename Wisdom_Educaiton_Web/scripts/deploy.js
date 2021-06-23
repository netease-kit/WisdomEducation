/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

/**
 * 打包静态资源到静态资源服务器
 */
 const fs = require('fs-extra')
 const path = require('path')
 const util = require('./utils')

 const env = process.env.NODE_ENV || 'development'

 var srcFolder = path.join(__dirname, `../build`)


 var destFolder = path.join(
   __dirname,
   `../../nim-web-demo-pc/webdemo/yunxin/wisdom-education`
 )

 fs.emptyDirSync(destFolder)

 util.copyDir(srcFolder, destFolder)
