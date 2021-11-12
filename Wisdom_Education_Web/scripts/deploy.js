/*
 * @Author: lizhaoxuan
 * @Date: 2021-06-17 16:28:35
 * @LastEditTime: 2021-06-17 16:33:00
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: /app_wisdom_education_web/scripts/deploy.js
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
