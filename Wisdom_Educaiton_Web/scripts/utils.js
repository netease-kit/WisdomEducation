/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
const fs = require('fs')


var copyDir=function(src, dst){
  const paths = fs.readdirSync(src); //同步读取当前目录
  paths.forEach(function(path){
    const _src=src+'/'+path;
    const _dst=dst+'/'+path;
    fs.stat(_src,function(err,stats){  //stats  该对象 包含文件属性
      if(err)throw err;
      if(stats.isFile()){ //如果是个文件则拷贝
        const  readable=fs.createReadStream(_src);//创建读取流
        const  writable=fs.createWriteStream(_dst);//创建写入流
        readable.pipe(writable);
      }else if(stats.isDirectory()){ //是目录则 递归
        checkDirectory(_src,_dst,copyDir);
      }
    });
  });
}

var checkDirectory=function(src,dst,callback){
  fs.access(dst, fs.constants.F_OK, (err) => {
    if(err){
      fs.mkdirSync(dst, { recursive: true });
      callback(src,dst);
    }else{
      callback(src,dst);
    }
  });
};

module.exports = {
  copyDir: copyDir
}
