const fs = require('fs-extra')
const path = require('path')
const util = require('./utils')

const env = process.env.NODE_ENV || 'development'

var srcFolder = path.join(__dirname, `../app`)


var destFolder = path.join(
  __dirname,
  `../build/app`
)

fs.emptyDirSync(destFolder)

util.copyDir(srcFolder, destFolder)
