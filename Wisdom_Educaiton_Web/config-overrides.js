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
  // TODO: 建议上发布环境用 'source-map'
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
    key: fs.readFileSync('./cert/key.pem'),
    cert: fs.readFileSync('./cert/cert.pem'),
    port: 3001
  }
}

module.exports = {
  webpack: override(
    useBabelRc(),
    disableEsLint(),
    sourceMap(),
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
          files: ['package.json', 'package-lock.json', 'yarn.lock', '.env', '.env.local', 'env.local'],
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
