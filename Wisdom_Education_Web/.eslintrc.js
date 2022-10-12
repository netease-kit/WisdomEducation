/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
module.exports = {
  root: true,
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 6,
    sourceType: 'module',
    ecmaFeatures: {
      "jsx": true,
    }
  },
  env: {
    browser: true,
  },
  plugins: [
    '@typescript-eslint',
    'react',
  ],
  extends: [
    'eslint:recommended',
    "plugin:react/recommended",
    'plugin:@typescript-eslint/recommended',
  ],
  rules: {
    "no-unused-vars": 0,
    "@typescript-eslint/no-unused-vars": ["off"],
    'react/prop-types': 'off',
    "@typescript-eslint/no-explicit-any": "off",
    "no-debugger": 1,
    '@typescript-eslint/no-var-requires': 0,
    '@typescript-eslint/ban-ts-comment': 1,
    '@typescript-eslint/indent': 0,
    "indent": ["error", 2, { "SwitchCase": 1 }],
    // "semi": [2, "always"],
    "react/no-unknown-property": ['error', { ignore: ['x-webkit-airplay', 'webkit-playsinline'] }]
  }
};
