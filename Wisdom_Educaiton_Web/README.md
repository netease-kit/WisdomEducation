智慧云课堂（PaaS 方案）是网易云信提供的在线互动课堂场景解决方案。基于在线教育的常见场景，网易云信提供智慧云课堂开源项目，为您演示 1 对 1、小班课和大班课的典型方案。您可以直接基于我们的 Demo 修改适配，也可以参考 Demo，自行集成云信 IM SDK、音视频通话 2.0 NERTC SDK 和互动白板 SDK，实现在线教育场景。

本文展示如何编译并运行 Android 平台的智慧云课堂示例项目，体验各个场景的在线课堂。

## 前提条件

在开始运行示例项目之前，请确保您已完成以下操作：

- 在云信控制台创建应用，并获取对应的 AppId 与 AppKey。
- 为此应用开通以下相关服务与抄送：
  - 产品服务：音视频通话 2.0、IM 专业版、聊天室、信令、互动白板、云端录制、点播。
  - 产品功能：
    - 音视频通话 2.0 的云端录制和抄送功能。
    - 互动白板的文档转码和云端录制功能。
  - 音视频通话 2.0 抄送：1-房间启动、2-房间结束、3-房间录制文件下载信息抄送 、4-用户进入房间、5-用户离开房间、8-房间时长抄送。

<note type="note">
<ul><li>开通相关产品与抄送，请联系<a href="https://yunxin.163.com/bizQQWPA.html">联系云信商务经理</a>。</li><li>如果仅需要本地跑通示例项目，简单体验智慧云课堂，您可以使用<a href="https://github.com/netease-kit/WisdomEducation/tree/main/Wisdom_Education_Docs">智慧云课堂体验账号</a>。体验账号已开通相关权限与抄送，课堂时长限制为 30 分钟。</li><li>体验账号仅供开发者体验与测试，请勿在线上环境中使用。</li></ul>
</note>

## 开发环境

在开始运行示例项目之前，请确保开发环境满足以下要求：

- 安全环境：https 环境或者本地连接 localhost/127.0.0.1 环境。
- 浏览器：Chrome 72 及以上版本、Safari 12 及以上版本。

## 示例项目结构


```
├── build                   构建后生成的文件
├── public                  
├── src                      业务代码
│   ├── assets               资源文件
│   ├── component            组件模块
│   ├── hooks                自定义 Hook      
│   ├── lib                  sdk
│   ├── pages                页面
│   ├── services             服务
│   ├── store                mobx
│   └── utils                工具
├── .babelrc                 babelrc模块
├── .env.development         开发环境变量
├── .env.production          生产环境变量
├── package.json             包管理
└── config-overrides.js      配置文件
```

## 运行示例源码

<style>
table th:first-of-type {
    width: 35%;
}
table th:nth-of-type(2) {
    width: 50%;
}
</style>

1. 获取示例项目。

    在 GitHub 的 [WisdomEducation](https://github.com/netease-kit/WisdomEducation) 示例项目 下载 Demo 源码工程。

2. 在示例项目中配置相关字段。

    如果需要基于 Demo 开发自己的应用，在 `.env.development` 或 `.env.production` 中将以下字段改为您的真实信息。

    | 配置项     | 说明                                      |
    | ------------- | ------------------------------------------- |
    | REACT_APP_SDK_APPID        | 应用的 AppId。可以在网易云信控制台中查看。 |
    | REACT_APP_SDK_AUTHORIZATION       | 应用的 AppKey。可以在网易云信控制台中查看。 |
    
    <note type="note">如果仅需要本地跑通示例项目，您可以使用<a href="https://github.com/netease-kit/WisdomEducation/tree/main/Wisdom_Education_Docs">智慧云课堂体验账号</a>。体验账号的课堂时长限制为 30 分钟。</note>

3. 进入 `Wisdom_Educaiton_Web` 目录，安装依赖并启动项目。

    ```
    cd Wisdom_Educaiton_Web
    npm install               // 安装依赖
    npm start             // 开发环境
    ```
4. 构建项目。

    ```
    npm run start:pro         // 线上环境
    npm run build:dev        // 打包测试环境
    npm run build             // 打包线上环境
    ```

