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
  }
};
