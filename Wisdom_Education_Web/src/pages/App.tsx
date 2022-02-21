/*
 * @Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */
import React from 'react';
import './App.less';
import intl from 'react-intl-universal';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        {intl.get('首页')}
      </header>
    </div>
  );
}

export default App;
