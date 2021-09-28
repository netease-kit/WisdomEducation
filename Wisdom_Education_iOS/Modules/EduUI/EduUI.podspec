
Pod::Spec.new do |spec|
  spec.name         = "EduUI"
  spec.version      = "0.0.1"
  spec.summary      = "A user interface module of educational business."
  spec.description  = "A user interface module of educational business."
  spec.homepage     = "http://g.hz.netease.com/yunxin-app/app_wisdom_education_ios.git"
  spec.license      = "MIT"
  spec.author             = { "yuanyuan" => "guoyuanyuan02@corp.netease.com" }
  spec.ios.deployment_target = "10.0"
  spec.source       = { :git => "ssh://git@g.hz.netease.com:22222/yunxin-app/app_wisdom_education_ios.git", :tag => "#{spec.version}" }
  spec.source_files  = "EduUI", "EduUI/**/*.{h,m}"
  spec.public_header_files = "EduUI/**/*.h"
  spec.resource_bundles = {
     'EduUIBundle' => ['EduUI/**/*.{xib,png,xcassets}']
   }
  
  spec.dependency "EduLogic", "~> 0.0.1"
  spec.dependency "NEWhiteBoard", "~> 0.0.1"
  spec.dependency "NEScreenShareHost", '~> 0.1.0'
  spec.dependency "SDWebImage", '~> 5.11.1 '
  spec.dependency "NELivePlayer", "~> 2.9.0"
  



end
