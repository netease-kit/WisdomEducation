
Pod::Spec.new do |spec|

  spec.name         = "NERecordPlayUI"
  spec.version      = "0.0.1"
  spec.summary      = "A user interface module of record video"
  spec.description  = "A user interface module of record video"
  spec.homepage     = "http://g.hz.netease.com/yunxin-app/app_wisdom_education_ios.git"
  spec.license      = "MIT"
  spec.author    = "yuanyuan"
  spec.platform     = :ios, "10.0"
  spec.source       = { :git => "ssh://git@g.hz.netease.com:22222/yunxin-app/app_wisdom_education_ios.git", :tag => "#{spec.version}" }
  spec.source_files  = "NERecordPlayUI", "NERecordPlayUI/**/*.{swift,h,m}"
  spec.exclude_files = "NERecordPlayUI/Exclude"
  spec.public_header_files = "NERecordPlayUI/**/*.h"
  spec.resource_bundles = {
     'NERecordPlayUIBundle' => ['NERecordPlayUI/**/*.{xib,png,xcassets}']
   }
  
  spec.dependency "NERecordPlay", "~> 0.0.1"

end
