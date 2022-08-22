
Pod::Spec.new do |spec|
  spec.name         = "NERecordPlay"
  spec.version      = "0.0.1"
  spec.summary      = "Play multiple videos"
  spec.description  = "Play multiple videos"
  spec.homepage     = "http://g.hz.netease.com/yunxin-app/app_wisdom_education_ios.git"
  spec.license      = "MIT"
  spec.author    = "yuanyuan"
  spec.ios.deployment_target = "10.0"
  spec.source       = { :git => "ssh://git@g.hz.netease.com:22222/yunxin-app/app_wisdom_education_ios.git", :tag => "#{spec.version}" }
  spec.source_files  = "NERecordPlay", "NERecordPlay/**/*.{swift,h,m}"
  spec.exclude_files = "NERecordPlay/Exclude"
  spec.public_header_files = "NERecordPlay/**/*.h"

  spec.dependency "NELivePlayer/LivePlayer", "3.2.1"
  spec.dependency "Alamofire", "~> 5.4.3"
  spec.dependency "NEWhiteBoard", "~> 0.0.1"
  

end
