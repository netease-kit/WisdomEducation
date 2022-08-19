/*
 * @Author: lizhaoxuan
 * @Date: 2021-06-17 16:27:59
 * @LastEditTime: 2021-06-17 16:28:57
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: /app_wisdom_education_web/scripts/utils.ts
 */
const fs = require('fs')


var copyDir=function(src, dst){
  const paths = fs.readdirSync(src); //Read the current directory
  paths.forEach(function(path){
    const _src=src+'/'+path;
    const _dst=dst+'/'+path;
    fs.stat(_src,function(err,stats){  //stats  The object contains the file attributes
      if(err)throw err;
      if(stats.isFile()){ //If it is a file, copy the file
        const  readable=fs.createReadStream(_src);//Create a read stream
        const  writable=fs.createWriteStream(_dst);//Create a write stream
        readable.pipe(writable);
      }else if(stats.isDirectory()){ //If it is a directory, run the recursive implementation
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
