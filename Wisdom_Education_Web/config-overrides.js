/* eslint-disable react-hooks/rules-of-hooks */
/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

const {
  override,
  addWebpackExternals,
  useBabelRc,
  fixBabelImports,
  addWebpackModuleRule,
  addWebpackPlugin,
  disableEsLint,
  babelInclude,
  babelExclude,
  addBundleVisualizer,
  getBabelLoader,
  addWebpackAlias,
  overrideDevServer,
  watchAll,
  addLessLoader,
  addDecoratorsLegacy,
  // addWebpackTarget,
} = require('customize-cra')

const SimpleProgressWebpackPlugin = require('simple-progress-webpack-plugin')
const HardSourceWebpackPlugin = require('hard-source-webpack-plugin')


const {DefinePlugin} = require('webpack')
const path = require('path')
const fs = require('fs-extra')
const isProd = process.env.REACT_APP_ENV === 'production'


const sourceMap = () => config => {
  // TODO: Please use 'source-map' in production environment
  // TODO: 'source-map' is recommended for the production environment
  // config.devtool = 'source-map'
  config.devtool = isProd ? false : 'source-map'
  return config;
}

const useOptimizeBabelConfig = () => config => {
  const rule = {
    test: /\.(ts|js)x?$/i,
    include: [
      path.resolve("src")
    ],
    use: [
      'thread-loader', 'cache-loader', getBabelLoader(config).loader
    ],
    exclude: [
      path.resolve("node_modules"),
    ]
  }

  for (let _rule of config.module.rules) {
    if (_rule.oneOf) {
      _rule.oneOf.unshift(rule);
      break;
    }
  }
  return config;
}

const devServerConfig = () => config => {
  return {
    ...config,
    https: true,
    key: fs.readFileSync('请替换为您申请的https证书路径'),
    cert: fs.readFileSync('请替换为您申请的https证书路径'),
    port: 3001
  }
}

module.exports = {
  webpack: override(
    useBabelRc(),
    disableEsLint(),
    sourceMap(),
    addWebpackExternals({
      RecordPlayer: 'RecordPlayer',
      ToolCollection: 'ToolCollection',
      WhiteBoard: 'WhiteBoardSDK'
    }),
    addWebpackModuleRule({
      test: /\.worker\.js$/,
      use: { loader: 'worker-loader' },
    }),
    addWebpackModuleRule({
      test: /\.tsx?$/,
      loader: 'esbuild-loader',
      options: {
        loader: 'tsx',
        target: 'es2015',
        tsconfigRaw: require('./tsconfig.json')
      }
    }),
    addDecoratorsLegacy(),
    fixBabelImports("import", [
      {
        libraryName: "antd",
        libraryDirectory: "es",
        style: true,
      }
    ]),
    addWebpackPlugin(
      new SimpleProgressWebpackPlugin()
    ),
    babelInclude([
      path.resolve("src")
    ]),
    babelExclude([
      path.resolve("node_modules")
    ]),
    addWebpackPlugin(
      new HardSourceWebpackPlugin({
        root: process.cwd(),
        directories: [],
        environmentHash: {
          root: process.cwd(),
          directories: [],
          files: ['package.json', 'package-lock.json', 'yarn.lock', '.env', '.env.local', 'env.local', '.env.development', '.env.production', 'src/config.js'],
        }
      })
    ),
    // addBundleVisualizer({
    //   // "analyzerMode": "static",
    //   // "reportFilename": "report.html"
    // }, true),
    useOptimizeBabelConfig(),
    addWebpackAlias({
      '@': path.resolve(__dirname, 'src')
    }),
    // addWebpackPlugin(new DefinePlugin({
    //   'REACT_APP_SDK_DOMAIN': JSON.stringify(process.env.REACT_APP_SDK_DOMAIN),
    // })),
    addLessLoader({
      lessOptions: {
        modifyVars: {
          '@primary-color': '#5174F6',
        },
        javascriptEnabled: true,
      }
    })
  ),
  devServer: overrideDevServer(
    devServerConfig(),
    watchAll()
  ),
}
