import React from 'react';
import ReactDOM from 'react-dom';
import './index.less';
import 'antd/dist/antd.less';
// import App from '@/pages/App';
import RoomPage from '@/pages/classRoom';
import Join from '@/pages/join';
import OneToOne from '@/pages/classRoom/one-to-one';
import SmallClass from './pages/classRoom/small-class';
import BigClass from '@/pages/classRoom/big-class';
import EndCourse from '@/pages/endCourse';
import Record from '@/pages/record';
import DeviceCheck from '@/pages/deviceCheck';
import { Provider } from 'mobx-react';
import { AppStore } from '@/store';
import { history } from '@/utils';


import {
  HashRouter as Router,
  Switch,
  Route,
} from "react-router-dom";

const defaultStore = new AppStore()


ReactDOM.render(
  // <React.StrictMode>
  <Provider store={defaultStore}>
    <Router>
      <Switch>
        <Route exact path="/" >
          <Join />
        </Route>
        {/* <Route exact path="/">
          <App />
        </Route> */}
        <Route exact path="/classroom/one-to-one">
          <RoomPage>
            <OneToOne />
          </RoomPage>
        </Route>
        <Route exact path="/classroom/small-class">
          <RoomPage>
            <SmallClass />
          </RoomPage>
        </Route>
        <Route exact path="/classroom/big-class">
          <RoomPage>
            <BigClass />
          </RoomPage>
        </Route>
        <Route exact path="/record">
          <Record />
        </Route>
        <Route exact path="/endCourse">
          <EndCourse />
        </Route>
        <Route exact path="/deviceCheck">
          <DeviceCheck />
        </Route>
      </Switch>
    </Router>
  </Provider>
  // </React.StrictMode>,
  ,document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
