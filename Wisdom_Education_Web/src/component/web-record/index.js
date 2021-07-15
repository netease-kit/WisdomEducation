(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory(require("react"), require("./lib/RecordPlayer/RecordPlayer_v3.1.2.js"));
	else if(typeof define === 'function' && define.amd)
		define(["react", "./lib/RecordPlayer/RecordPlayer_v3.1.2.js"], factory);
	else if(typeof exports === 'object')
		exports["NERTCReplay"] = factory(require("react"), require("./lib/RecordPlayer/RecordPlayer_v3.1.2.js"));
	else
		root["NERTCReplay"] = factory(root["react"], root["./lib/RecordPlayer/RecordPlayer_v3.1.2.js"]);
})(self, function(__WEBPACK_EXTERNAL_MODULE_react__, __WEBPACK_EXTERNAL_MODULE_RecordPlayer__) {
return /******/ (() => { // webpackBootstrap
/******/ 	var __webpack_modules__ = ({

/***/ "./src/component/Control.tsx":
/*!***********************************!*\
  !*** ./src/component/Control.tsx ***!
  \***********************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (/* binding */ Control)
/* harmony export */ });
/* harmony import */ var react__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! react */ "react");
/* harmony import */ var react__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(react__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var _assets_pause_svg__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../assets/pause.svg */ "./src/assets/pause.svg");
/* harmony import */ var _assets_pause_svg__WEBPACK_IMPORTED_MODULE_1___default = /*#__PURE__*/__webpack_require__.n(_assets_pause_svg__WEBPACK_IMPORTED_MODULE_1__);
/* harmony import */ var _assets_play_svg__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../assets/play.svg */ "./src/assets/play.svg");
/* harmony import */ var _assets_play_svg__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(_assets_play_svg__WEBPACK_IMPORTED_MODULE_2__);
/* harmony import */ var _Control_less__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./Control.less */ "./src/component/Control.less");
/* harmony import */ var _utils__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../utils */ "./src/utils/index.ts");
function _typeof(obj) { "@babel/helpers - typeof"; if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _slicedToArray(arr, i) { return _arrayWithHoles(arr) || _iterableToArrayLimit(arr, i) || _unsupportedIterableToArray(arr, i) || _nonIterableRest(); }

function _nonIterableRest() { throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method."); }

function _unsupportedIterableToArray(o, minLen) { if (!o) return; if (typeof o === "string") return _arrayLikeToArray(o, minLen); var n = Object.prototype.toString.call(o).slice(8, -1); if (n === "Object" && o.constructor) n = o.constructor.name; if (n === "Map" || n === "Set") return Array.from(o); if (n === "Arguments" || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)) return _arrayLikeToArray(o, minLen); }

function _arrayLikeToArray(arr, len) { if (len == null || len > arr.length) len = arr.length; for (var i = 0, arr2 = new Array(len); i < len; i++) { arr2[i] = arr[i]; } return arr2; }

function _iterableToArrayLimit(arr, i) { var _i = arr && (typeof Symbol !== "undefined" && arr[Symbol.iterator] || arr["@@iterator"]); if (_i == null) return; var _arr = []; var _n = true; var _d = false; var _s, _e; try { for (_i = _i.call(arr); !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"] != null) _i["return"](); } finally { if (_d) throw _e; } } return _arr; }

function _arrayWithHoles(arr) { if (Array.isArray(arr)) return arr; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = _getPrototypeOf(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = _getPrototypeOf(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return _possibleConstructorReturn(this, result); }; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Boolean.prototype.valueOf.call(Reflect.construct(Boolean, [], function () {})); return true; } catch (e) { return false; } }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }







var Control = /*#__PURE__*/function (_React$Component) {
  _inherits(Control, _React$Component);

  var _super = _createSuper(Control);

  function Control(props) {
    var _this;

    _classCallCheck(this, Control);

    _this = _super.call(this, props);
    _this.toolUIEl = /*#__PURE__*/(0,react__WEBPACK_IMPORTED_MODULE_0__.createRef)();

    _this.onMouseMove = function (e) {
      var _this$getCurrentXAndT = _this.getCurrentXAndTime(e),
          currentX = _this$getCurrentXAndT.currentX,
          currentTime = _this$getCurrentXAndT.currentTime;

      var _getTime = (0,_utils__WEBPACK_IMPORTED_MODULE_4__.getTime)(currentTime),
          _getTime2 = _slicedToArray(_getTime, 1),
          labelText = _getTime2[0];

      _this.setState({
        labelLeft: currentX,
        labelText: labelText
      });
    };

    _this.pauseOrPlay = function (e) {
      if (_this.props.playing) {
        _this.props.onPause();
      } else {
        _this.props.onPlay();
      }
    };

    _this.onMouseDown = function (e) {
      var role = e.target.getAttribute('role');

      if (role === 'slider') {
        _this.setState({
          showLabel: true,
          drag: true
        });

        _this.onMouseMove(e);
      }
    };

    _this.onMouseUp = function (e) {
      if (_this.state.drag) {
        _this.seekTo(e);
      }

      _this.hidePosition(e);
    };

    _this.handleClickSpeed = function (e) {
      _this.setState({
        speedOptionShow: !_this.state.speedOptionShow
      });
    };

    _this.handleClickSpeedOption = function (ev, speed) {
      ev.stopPropagation();

      _this.setState({
        speedOptionShow: false
      });

      _this.props.onSetSpeed(speed);
    };

    _this.showPosition = function (e) {
      _this.setState({
        showLabel: true
      });

      _this.onMouseMove(e);
    };

    _this.hidePosition = function (e) {
      _this.setState({
        showLabel: false
      });
    };

    _this.seekTo = function (e) {
      var _this$getCurrentXAndT2 = _this.getCurrentXAndTime(e),
          currentX = _this$getCurrentXAndT2.currentX,
          currentTime = _this$getCurrentXAndT2.currentTime;

      _this.props.onSeekTo(currentTime);

      var updator = {
        labelLeft: currentX,
        time: currentTime,
        drag: false
      }; // 如果是鼠标事件，不隐藏光标

      _this.setState(updator);
    };

    _this.getCurrentXAndTime = function (e) {
      var currentX = 0;

      var left = _this.toolUIEl.current.getBoundingClientRect().left;

      if (e.type.search(/mouse|click/) > -1) {
        currentX = e.clientX - left;
      } else {
        currentX = e.touches[0].clientX - left;
      }

      var fullWidth = _this.toolUIEl.current.offsetWidth; // 防止越界

      if (currentX < 0) {
        currentX = 0;
      }

      if (currentX > fullWidth) {
        currentX = fullWidth;
      }

      var currentTime = _this.state.duration * (currentX / fullWidth);
      return {
        currentX: currentX,
        currentTime: currentTime
      };
    };

    _this.renderTimeline = function (widthPercent) {
      var _this$state = _this.state,
          showLabel = _this$state.showLabel,
          labelLeft = _this$state.labelLeft,
          labelText = _this$state.labelText,
          drag = _this$state.drag;
      return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        ref: _this.toolUIEl,
        className: 'player-timeline',
        onClick: _this.seekTo,
        onMouseEnter: _this.showPosition,
        onMouseLeave: _this.hidePosition,
        onMouseDown: _this.onMouseDown,
        onMouseMove: _this.onMouseMove,
        onTouchStart: _this.onMouseDown,
        onTouchMove: _this.onMouseMove,
        onTouchEnd: _this.onMouseUp,
        role: "timeline"
      }, /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("span", {
        role: "currentLabel",
        className: 'player-timeline-label',
        style: {
          display: showLabel ? 'inline-block' : 'none',
          left: "".concat(labelLeft, "px")
        }
      }, labelText), /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("span", {
        role: "label-background",
        className: 'player-timeline-bg',
        style: {
          display: showLabel ? 'inline-block' : 'none',
          height: 20,
          left: "".concat(labelLeft, "px")
        }
      }), /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        role: "blue-line",
        className: 'player-timeline-blueline',
        style: {
          width: "".concat(widthPercent + '%')
        }
      }), /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        role: "slider",
        className: 'player-timeline-slider',
        style: {
          left: "".concat(drag ? labelLeft + 'px' : widthPercent + '%'),
          backgroundClip: "".concat(drag ? 'border-box' : 'content-box')
        }
      }));
    };

    _this.renderPlayOrPause = function () {
      return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        className: 'player-play-icon-wrapper',
        role: "icon",
        onClick: _this.pauseOrPlay
      }, /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("span", {
        className: 'player-play-icon',
        style: {
          backgroundImage: "url(".concat(_this.props.playing ? (_assets_pause_svg__WEBPACK_IMPORTED_MODULE_1___default()) : (_assets_play_svg__WEBPACK_IMPORTED_MODULE_2___default()), ")")
        }
      }));
    };

    var start = props.start,
        end = props.end;
    _this.state = {
      duration: end - start,
      showLabel: false,
      labelLeft: 0,
      labelText: '',
      speedOptionShow: false,
      drag: false
    };
    return _this;
  }

  _createClass(Control, [{
    key: "renderSpeed",
    value: function renderSpeed() {
      var _this2 = this;

      var speedText = this.props.speed === 1 ? '倍数' : "".concat(this.props.speed, "x");
      var speedArr = [2, 1.5, 1.25, 1, 0.75, 0.5];
      return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        className: 'player-speed',
        onClick: this.handleClickSpeed
      }, /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        className: 'player-speed-text'
      }, speedText), this.state.speedOptionShow && /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        className: 'player-speed-options'
      }, speedArr.map(function (speed) {
        return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
          className: 'option',
          key: speed,
          onClick: function onClick(ev) {
            return _this2.handleClickSpeedOption(ev, speed);
          }
        }, speed, "x");
      })));
    }
  }, {
    key: "render",
    value: function render() {
      var duration = this.state.duration;
      var time = this.props.currTime;
      var widthPercent = 0;

      if (time >= duration) {
        widthPercent = 100;
      } else if (time <= 0) {
        widthPercent = 0;
      } else {
        widthPercent = time / duration * 100;
      }

      var timeDuration;

      if (time > duration) {
        var _getTime3 = (0,_utils__WEBPACK_IMPORTED_MODULE_4__.getTime)(duration),
            _getTime4 = _slicedToArray(_getTime3, 1),
            durationStr = _getTime4[0];

        timeDuration = durationStr + '/' + durationStr;
      } else {
        var _getTime5 = (0,_utils__WEBPACK_IMPORTED_MODULE_4__.getTime)(duration),
            _getTime6 = _slicedToArray(_getTime5, 2),
            _durationStr = _getTime6[0],
            durationHour = _getTime6[1];

        var _getTime7 = (0,_utils__WEBPACK_IMPORTED_MODULE_4__.getTime)(time, durationHour > 0),
            _getTime8 = _slicedToArray(_getTime7, 1),
            currentStr = _getTime8[0];

        timeDuration = currentStr + '/' + _durationStr;
      }

      return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        className: "player-container"
      }, this.renderPlayOrPause(), this.renderTimeline(widthPercent), this.renderSpeed(), /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        role: "duration",
        className: 'player-duration'
      }, timeDuration));
    }
  }]);

  return Control;
}((react__WEBPACK_IMPORTED_MODULE_0___default().Component));



/***/ }),

/***/ "./src/component/VideoBox.tsx":
/*!************************************!*\
  !*** ./src/component/VideoBox.tsx ***!
  \************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (/* binding */ VideoBox)
/* harmony export */ });
/* harmony import */ var react__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! react */ "react");
/* harmony import */ var react__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(react__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var _VideoBox_less__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./VideoBox.less */ "./src/component/VideoBox.less");
function _typeof(obj) { "@babel/helpers - typeof"; if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = _getPrototypeOf(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = _getPrototypeOf(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return _possibleConstructorReturn(this, result); }; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Boolean.prototype.valueOf.call(Reflect.construct(Boolean, [], function () {})); return true; } catch (e) { return false; } }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }



var observeAt = '李四';

var VideoBox = /*#__PURE__*/function (_React$Component) {
  _inherits(VideoBox, _React$Component);

  var _super = _createSuper(VideoBox);

  function VideoBox() {
    var _this;

    _classCallCheck(this, VideoBox);

    _this = _super.apply(this, arguments);
    _this.videoRef = /*#__PURE__*/(0,react__WEBPACK_IMPORTED_MODULE_0__.createRef)();
    _this.state = {
      videoOn: true,
      audioOn: true,
      playing: false,
      speed: 1
    };

    _this.handleToggleVideo = function () {
      _this.setState({
        videoOn: !_this.state.videoOn
      });
    };

    _this.handleToggleAudio = function () {
      _this.setState({
        audioOn: !_this.state.audioOn
      }, function () {
        if (_this.videoRef.current) {
          _this.videoRef.current.muted = _this.state.audioOn ? false : true;
        }
      });
    };

    _this.handleWait = function () {
      if (_this.props.name.toString() === observeAt) {
        console.log('李四 wait');
      }

      _this.props.onWait();
    };

    _this.handleCanPlay = function () {
      if (_this.props.name.toString() === observeAt) {
        console.log('李四 play');
      }

      _this.props.onCanPlay();
    };

    _this.printVideoState = function () {
      if (_this.videoRef.current) {
        console.log('李四 currTime', _this.videoRef.current.currentTime);
        console.log('李四 paused', _this.videoRef.current.paused);
        console.log('李四 ended', _this.videoRef.current.ended);
        console.log('李四 readyState', _this.videoRef.current.readyState);
      }
    };

    return _this;
  }

  _createClass(VideoBox, [{
    key: "componentDidMount",
    value: function componentDidMount() {
      if (this.props.playing) {
        this.play();
      }

      console.log(this.props);
      this.setPlaySpeed(this.props.speed);
    }
  }, {
    key: "componentWillReceiveProps",
    value: function componentWillReceiveProps(nextProps) {
      if (nextProps.playing && !this.state.playing) {
        this.play();
      } else if (!nextProps.playing && this.state.playing) {
        this.pause();
      } else if (nextProps.syncTimestamp !== this.props.syncTimestamp) {
        this.calibrate(nextProps);
      }

      if (nextProps.speed !== this.state.speed) {
        this.setPlaySpeed(nextProps.speed);
      }
    }
  }, {
    key: "play",
    value: function play() {
      if (this.props.name.toString() === observeAt) {
        console.log('李四 play', this.props);
      }

      if (this.videoRef.current) {
        this.calibrate(this.props);
        this.videoRef.current.play();
        this.setState({
          playing: true
        });
      }
    }
  }, {
    key: "pause",
    value: function pause() {
      if (this.props.name.toString() === observeAt) {
        console.log('李四 pause', this.props);
      }

      if (this.videoRef.current) {
        this.videoRef.current.pause();
        this.setState({
          playing: false
        });
      }
    }
    /**
     * 调整播放器时间。
     * 在播放，暂停，以及syncTimestamp发生变化时调整播放器时间
     * @param props
     */

  }, {
    key: "calibrate",
    value: function calibrate(props) {
      var playAt = new Date().valueOf() - props.syncTimestamp + props.syncAt;

      if (this.props.name.toString() === observeAt) {
        console.log('李四 calibrate at', playAt / 1000, this.props);
        this.printVideoState();
      }

      if (this.videoRef.current) {
        if (props.playing) {
          this.videoRef.current.play();
        }

        this.videoRef.current.currentTime = playAt / 1000;
      }
    }
  }, {
    key: "setPlaySpeed",
    value: function setPlaySpeed(speed) {
      if (this.state.speed !== speed) {
        this.setState({
          speed: speed
        });

        if (this.videoRef.current) {
          this.videoRef.current.playbackRate = speed;
        }
      }
    }
  }, {
    key: "render",
    value: function render() {
      if (this.props.name.toString() === observeAt) {
        console.log('李四 props');
      }

      var hidden = this.props.hidden || !this.state.videoOn;
      return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        className: 'videobox'
      }, /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("video", {
        ref: this.videoRef,
        onWaiting: this.handleWait,
        onCanPlay: this.handleCanPlay,
        src: this.props.url,
        muted: !this.state.audioOn || hidden,
        style: {
          width: '100%',
          visibility: hidden ? 'hidden' : 'visible'
        }
      }), hidden && /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        className: 'video-black'
      }), this.renderControls());
    }
  }, {
    key: "renderControls",
    value: function renderControls() {
      var name = this.props.name + (this.props.role === 'teacher' ? '(教师)' : '');
      var videoClassNameSuffix = this.state.videoOn ? '' : ' off';
      var audioClassNameSuffix = this.state.audioOn ? '' : ' off';
      return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        className: 'controls'
      }, /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        className: 'name'
      }, name), /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        className: 'ctrl'
      }, /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        className: 'video icon' + videoClassNameSuffix,
        onClick: this.handleToggleVideo
      }), /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        className: 'audio icon' + audioClassNameSuffix,
        onClick: this.handleToggleAudio
      })));
    }
  }]);

  return VideoBox;
}((react__WEBPACK_IMPORTED_MODULE_0___default().Component));



/***/ }),

/***/ "./src/component/VideoGroup.tsx":
/*!**************************************!*\
  !*** ./src/component/VideoGroup.tsx ***!
  \**************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (/* binding */ VideoGroup)
/* harmony export */ });
/* harmony import */ var react__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! react */ "react");
/* harmony import */ var react__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(react__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var _VideoBox__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./VideoBox */ "./src/component/VideoBox.tsx");
function _typeof(obj) { "@babel/helpers - typeof"; if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = _getPrototypeOf(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = _getPrototypeOf(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return _possibleConstructorReturn(this, result); }; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Boolean.prototype.valueOf.call(Reflect.construct(Boolean, [], function () {})); return true; } catch (e) { return false; } }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }




var VideoGroup = /*#__PURE__*/function (_React$Component) {
  _inherits(VideoGroup, _React$Component);

  var _super = _createSuper(VideoGroup);

  function VideoGroup() {
    var _this;

    _classCallCheck(this, VideoGroup);

    _this = _super.apply(this, arguments);
    _this.waitSet = new Set();

    _this.handleWait = function (id) {
      if (!_this.waitSet.has(id)) {
        _this.waitSet.add(id);

        if (_this.waitSet.size === 1) {
          _this.props.onWait();
        }
      }
    };

    _this.handleCanPlay = function (id) {
      if (_this.waitSet.has(id)) {
        _this.waitSet["delete"](id);

        if (_this.waitSet.size === 0) {
          _this.props.onCanPlay();
        }
      }
    };

    return _this;
  }

  _createClass(VideoGroup, [{
    key: "render",
    value: function render() {
      var _this2 = this;

      return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        className: 'main-video',
        style: {
          width: this.props.videoWidth + 20
        }
      }, this.props.videos.map(function (v) {
        return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
          key: v.id,
          style: {
            marginBottom: 10
          }
        }, /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement(_VideoBox__WEBPACK_IMPORTED_MODULE_1__.default, {
          playing: _this2.props.playing,
          url: v.url,
          name: v.name,
          role: v.role,
          hidden: !_this2.props.visibleVIds.includes(v.id),
          speed: _this2.props.speed,
          syncAt: _this2.props.syncAt - (v.start - _this2.props.beginOffset),
          syncTimestamp: _this2.props.syncTimestamp,
          onWait: function onWait() {
            return _this2.handleWait(v.id);
          },
          onCanPlay: function onCanPlay() {
            return _this2.handleCanPlay(v.id);
          }
        }));
      }));
    }
  }]);

  return VideoGroup;
}((react__WEBPACK_IMPORTED_MODULE_0___default().Component));



/***/ }),

/***/ "./src/utils/TickTick.ts":
/*!*******************************!*\
  !*** ./src/utils/TickTick.ts ***!
  \*******************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "DEFAULT_INTERVAL": () => (/* binding */ DEFAULT_INTERVAL),
/* harmony export */   "default": () => (/* binding */ TickTick)
/* harmony export */ });
/* harmony import */ var eventemitter3__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! eventemitter3 */ "./node_modules/eventemitter3/index.js");
/* harmony import */ var eventemitter3__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(eventemitter3__WEBPACK_IMPORTED_MODULE_0__);
function _typeof(obj) { "@babel/helpers - typeof"; if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = _getPrototypeOf(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = _getPrototypeOf(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return _possibleConstructorReturn(this, result); }; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Boolean.prototype.valueOf.call(Reflect.construct(Boolean, [], function () {})); return true; } catch (e) { return false; } }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

/**
 * 滴答滴答报时器。
 *
 * 处于play状态时，每隔一段时间会向外发送tick事件。
 */

var DEFAULT_INTERVAL = 10;

var TickTick = /*#__PURE__*/function (_EventEmitter) {
  _inherits(TickTick, _EventEmitter);

  var _super = _createSuper(TickTick);

  function TickTick() {
    var _this;

    _classCallCheck(this, TickTick);

    _this = _super.apply(this, arguments);
    _this.state = 'pause'; //currTimeStamp是相对于最早Track的起始时间偏移量

    _this.currTimeStamp = 0; //lastRealTime是上一次记录的真实时间

    _this.lastRealTime = undefined;
    _this.rate = 1;

    _this.microTask = function () {
      if (_this.state === 'play') {
        _this.tick();
      }
    };

    return _this;
  }

  _createClass(TickTick, [{
    key: "destory",
    value: function destory() {
      var _this2 = this;

      this.pause();
      this.eventNames().forEach(function (name) {
        return _this2.off(name);
      });
    }
  }, {
    key: "tick",
    value: function tick() {
      var now = Date.now();

      if (this.lastRealTime === undefined) {
        this.lastRealTime = now;
        return;
      } else {
        var passedGap = (now - this.lastRealTime) * this.rate;
        this.currTimeStamp = this.currTimeStamp + passedGap;
        this.lastRealTime = now;
        this.emit('tick', this.currTimeStamp);
      }
    }
  }, {
    key: "pause",
    value: function pause() {
      if (this.timer) {
        window.clearInterval(this.timer);
      }

      this.timer = undefined;
      this.lastRealTime = undefined;
      this.state = 'pause';
      this.emit('pause');
    }
  }, {
    key: "play",
    value: function play() {
      if (!this.timer) {
        this.timer = setInterval(this.microTask, DEFAULT_INTERVAL);
      }

      this.state = 'play';
      this.emit('play');
    }
  }, {
    key: "setCurrentTime",
    value: function setCurrentTime() {
      var timeStamp = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;
      this.currTimeStamp = timeStamp;
      this.lastRealTime = undefined;
      this.emit('tick', this.currTimeStamp);
    }
  }]);

  return TickTick;
}(eventemitter3__WEBPACK_IMPORTED_MODULE_0__.EventEmitter);



/***/ }),

/***/ "./src/utils/index.ts":
/*!****************************!*\
  !*** ./src/utils/index.ts ***!
  \****************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "getTime": () => (/* binding */ getTime)
/* harmony export */ });
var p0 = function p0(n) {
  return n < 10 ? '0' + n : '' + n;
};

var getTime = function getTime(n) {
  var showHour = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
  n = ~~(n / 1000);
  var second = n % 60;
  n -= second;
  n = ~~(n / 60); // 有多少分钟

  var minute = n % 60; // 

  var hour = ~~(n / 60);

  if (hour === 0 && showHour === false) {
    return ["".concat(p0(minute), ":").concat(p0(second)), hour];
  }

  return ["".concat(hour, ":").concat(p0(minute), ":").concat(p0(second)), hour];
};

/***/ }),

/***/ "./node_modules/css-loader/dist/runtime/api.js":
/*!*****************************************************!*\
  !*** ./node_modules/css-loader/dist/runtime/api.js ***!
  \*****************************************************/
/***/ ((module) => {

"use strict";

/*
  MIT License http://www.opensource.org/licenses/mit-license.php
  Author Tobias Koppers @sokra
*/
// css base code, injected by the css-loader
// eslint-disable-next-line func-names

module.exports = function (cssWithMappingToString) {
  var list = []; // return the list of modules as css string

  list.toString = function toString() {
    return this.map(function (item) {
      var content = cssWithMappingToString(item);

      if (item[2]) {
        return "@media ".concat(item[2], " {").concat(content, "}");
      }

      return content;
    }).join("");
  }; // import a list of modules into the list
  // eslint-disable-next-line func-names


  list.i = function (modules, mediaQuery, dedupe) {
    if (typeof modules === "string") {
      // eslint-disable-next-line no-param-reassign
      modules = [[null, modules, ""]];
    }

    var alreadyImportedModules = {};

    if (dedupe) {
      for (var i = 0; i < this.length; i++) {
        // eslint-disable-next-line prefer-destructuring
        var id = this[i][0];

        if (id != null) {
          alreadyImportedModules[id] = true;
        }
      }
    }

    for (var _i = 0; _i < modules.length; _i++) {
      var item = [].concat(modules[_i]);

      if (dedupe && alreadyImportedModules[item[0]]) {
        // eslint-disable-next-line no-continue
        continue;
      }

      if (mediaQuery) {
        if (!item[2]) {
          item[2] = mediaQuery;
        } else {
          item[2] = "".concat(mediaQuery, " and ").concat(item[2]);
        }
      }

      list.push(item);
    }
  };

  return list;
};

/***/ }),

/***/ "./node_modules/css-loader/dist/runtime/cssWithMappingToString.js":
/*!************************************************************************!*\
  !*** ./node_modules/css-loader/dist/runtime/cssWithMappingToString.js ***!
  \************************************************************************/
/***/ ((module) => {

"use strict";


function _slicedToArray(arr, i) {
  return _arrayWithHoles(arr) || _iterableToArrayLimit(arr, i) || _unsupportedIterableToArray(arr, i) || _nonIterableRest();
}

function _nonIterableRest() {
  throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.");
}

function _unsupportedIterableToArray(o, minLen) {
  if (!o) return;
  if (typeof o === "string") return _arrayLikeToArray(o, minLen);
  var n = Object.prototype.toString.call(o).slice(8, -1);
  if (n === "Object" && o.constructor) n = o.constructor.name;
  if (n === "Map" || n === "Set") return Array.from(o);
  if (n === "Arguments" || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)) return _arrayLikeToArray(o, minLen);
}

function _arrayLikeToArray(arr, len) {
  if (len == null || len > arr.length) len = arr.length;

  for (var i = 0, arr2 = new Array(len); i < len; i++) {
    arr2[i] = arr[i];
  }

  return arr2;
}

function _iterableToArrayLimit(arr, i) {
  if (typeof Symbol === "undefined" || !(Symbol.iterator in Object(arr))) return;
  var _arr = [];
  var _n = true;
  var _d = false;
  var _e = undefined;

  try {
    for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) {
      _arr.push(_s.value);

      if (i && _arr.length === i) break;
    }
  } catch (err) {
    _d = true;
    _e = err;
  } finally {
    try {
      if (!_n && _i["return"] != null) _i["return"]();
    } finally {
      if (_d) throw _e;
    }
  }

  return _arr;
}

function _arrayWithHoles(arr) {
  if (Array.isArray(arr)) return arr;
}

module.exports = function cssWithMappingToString(item) {
  var _item = _slicedToArray(item, 4),
      content = _item[1],
      cssMapping = _item[3];

  if (typeof btoa === "function") {
    // eslint-disable-next-line no-undef
    var base64 = btoa(unescape(encodeURIComponent(JSON.stringify(cssMapping))));
    var data = "sourceMappingURL=data:application/json;charset=utf-8;base64,".concat(base64);
    var sourceMapping = "/*# ".concat(data, " */");
    var sourceURLs = cssMapping.sources.map(function (source) {
      return "/*# sourceURL=".concat(cssMapping.sourceRoot || "").concat(source, " */");
    });
    return [content].concat(sourceURLs).concat([sourceMapping]).join("\n");
  }

  return [content].join("\n");
};

/***/ }),

/***/ "./node_modules/css-loader/dist/runtime/getUrl.js":
/*!********************************************************!*\
  !*** ./node_modules/css-loader/dist/runtime/getUrl.js ***!
  \********************************************************/
/***/ ((module) => {

"use strict";


module.exports = function (url, options) {
  if (!options) {
    // eslint-disable-next-line no-param-reassign
    options = {};
  } // eslint-disable-next-line no-underscore-dangle, no-param-reassign


  url = url && url.__esModule ? url.default : url;

  if (typeof url !== "string") {
    return url;
  } // If url is already wrapped in quotes, remove them


  if (/^['"].*['"]$/.test(url)) {
    // eslint-disable-next-line no-param-reassign
    url = url.slice(1, -1);
  }

  if (options.hash) {
    // eslint-disable-next-line no-param-reassign
    url += options.hash;
  } // Should url be wrapped?
  // See https://drafts.csswg.org/css-values-3/#urls


  if (/["'() \t\n]/.test(url) || options.needQuotes) {
    return "\"".concat(url.replace(/"/g, '\\"').replace(/\n/g, "\\n"), "\"");
  }

  return url;
};

/***/ }),

/***/ "./node_modules/eventemitter3/index.js":
/*!*********************************************!*\
  !*** ./node_modules/eventemitter3/index.js ***!
  \*********************************************/
/***/ ((module) => {

"use strict";


var has = Object.prototype.hasOwnProperty,
    prefix = '~';
/**
 * Constructor to create a storage for our `EE` objects.
 * An `Events` instance is a plain object whose properties are event names.
 *
 * @constructor
 * @private
 */

function Events() {} //
// We try to not inherit from `Object.prototype`. In some engines creating an
// instance in this way is faster than calling `Object.create(null)` directly.
// If `Object.create(null)` is not supported we prefix the event names with a
// character to make sure that the built-in object properties are not
// overridden or used as an attack vector.
//


if (Object.create) {
  Events.prototype = Object.create(null); //
  // This hack is needed because the `__proto__` property is still inherited in
  // some old browsers like Android 4, iPhone 5.1, Opera 11 and Safari 5.
  //

  if (!new Events().__proto__) prefix = false;
}
/**
 * Representation of a single event listener.
 *
 * @param {Function} fn The listener function.
 * @param {*} context The context to invoke the listener with.
 * @param {Boolean} [once=false] Specify if the listener is a one-time listener.
 * @constructor
 * @private
 */


function EE(fn, context, once) {
  this.fn = fn;
  this.context = context;
  this.once = once || false;
}
/**
 * Add a listener for a given event.
 *
 * @param {EventEmitter} emitter Reference to the `EventEmitter` instance.
 * @param {(String|Symbol)} event The event name.
 * @param {Function} fn The listener function.
 * @param {*} context The context to invoke the listener with.
 * @param {Boolean} once Specify if the listener is a one-time listener.
 * @returns {EventEmitter}
 * @private
 */


function addListener(emitter, event, fn, context, once) {
  if (typeof fn !== 'function') {
    throw new TypeError('The listener must be a function');
  }

  var listener = new EE(fn, context || emitter, once),
      evt = prefix ? prefix + event : event;
  if (!emitter._events[evt]) emitter._events[evt] = listener, emitter._eventsCount++;else if (!emitter._events[evt].fn) emitter._events[evt].push(listener);else emitter._events[evt] = [emitter._events[evt], listener];
  return emitter;
}
/**
 * Clear event by name.
 *
 * @param {EventEmitter} emitter Reference to the `EventEmitter` instance.
 * @param {(String|Symbol)} evt The Event name.
 * @private
 */


function clearEvent(emitter, evt) {
  if (--emitter._eventsCount === 0) emitter._events = new Events();else delete emitter._events[evt];
}
/**
 * Minimal `EventEmitter` interface that is molded against the Node.js
 * `EventEmitter` interface.
 *
 * @constructor
 * @public
 */


function EventEmitter() {
  this._events = new Events();
  this._eventsCount = 0;
}
/**
 * Return an array listing the events for which the emitter has registered
 * listeners.
 *
 * @returns {Array}
 * @public
 */


EventEmitter.prototype.eventNames = function eventNames() {
  var names = [],
      events,
      name;
  if (this._eventsCount === 0) return names;

  for (name in events = this._events) {
    if (has.call(events, name)) names.push(prefix ? name.slice(1) : name);
  }

  if (Object.getOwnPropertySymbols) {
    return names.concat(Object.getOwnPropertySymbols(events));
  }

  return names;
};
/**
 * Return the listeners registered for a given event.
 *
 * @param {(String|Symbol)} event The event name.
 * @returns {Array} The registered listeners.
 * @public
 */


EventEmitter.prototype.listeners = function listeners(event) {
  var evt = prefix ? prefix + event : event,
      handlers = this._events[evt];
  if (!handlers) return [];
  if (handlers.fn) return [handlers.fn];

  for (var i = 0, l = handlers.length, ee = new Array(l); i < l; i++) {
    ee[i] = handlers[i].fn;
  }

  return ee;
};
/**
 * Return the number of listeners listening to a given event.
 *
 * @param {(String|Symbol)} event The event name.
 * @returns {Number} The number of listeners.
 * @public
 */


EventEmitter.prototype.listenerCount = function listenerCount(event) {
  var evt = prefix ? prefix + event : event,
      listeners = this._events[evt];
  if (!listeners) return 0;
  if (listeners.fn) return 1;
  return listeners.length;
};
/**
 * Calls each of the listeners registered for a given event.
 *
 * @param {(String|Symbol)} event The event name.
 * @returns {Boolean} `true` if the event had listeners, else `false`.
 * @public
 */


EventEmitter.prototype.emit = function emit(event, a1, a2, a3, a4, a5) {
  var evt = prefix ? prefix + event : event;
  if (!this._events[evt]) return false;
  var listeners = this._events[evt],
      len = arguments.length,
      args,
      i;

  if (listeners.fn) {
    if (listeners.once) this.removeListener(event, listeners.fn, undefined, true);

    switch (len) {
      case 1:
        return listeners.fn.call(listeners.context), true;

      case 2:
        return listeners.fn.call(listeners.context, a1), true;

      case 3:
        return listeners.fn.call(listeners.context, a1, a2), true;

      case 4:
        return listeners.fn.call(listeners.context, a1, a2, a3), true;

      case 5:
        return listeners.fn.call(listeners.context, a1, a2, a3, a4), true;

      case 6:
        return listeners.fn.call(listeners.context, a1, a2, a3, a4, a5), true;
    }

    for (i = 1, args = new Array(len - 1); i < len; i++) {
      args[i - 1] = arguments[i];
    }

    listeners.fn.apply(listeners.context, args);
  } else {
    var length = listeners.length,
        j;

    for (i = 0; i < length; i++) {
      if (listeners[i].once) this.removeListener(event, listeners[i].fn, undefined, true);

      switch (len) {
        case 1:
          listeners[i].fn.call(listeners[i].context);
          break;

        case 2:
          listeners[i].fn.call(listeners[i].context, a1);
          break;

        case 3:
          listeners[i].fn.call(listeners[i].context, a1, a2);
          break;

        case 4:
          listeners[i].fn.call(listeners[i].context, a1, a2, a3);
          break;

        default:
          if (!args) for (j = 1, args = new Array(len - 1); j < len; j++) {
            args[j - 1] = arguments[j];
          }
          listeners[i].fn.apply(listeners[i].context, args);
      }
    }
  }

  return true;
};
/**
 * Add a listener for a given event.
 *
 * @param {(String|Symbol)} event The event name.
 * @param {Function} fn The listener function.
 * @param {*} [context=this] The context to invoke the listener with.
 * @returns {EventEmitter} `this`.
 * @public
 */


EventEmitter.prototype.on = function on(event, fn, context) {
  return addListener(this, event, fn, context, false);
};
/**
 * Add a one-time listener for a given event.
 *
 * @param {(String|Symbol)} event The event name.
 * @param {Function} fn The listener function.
 * @param {*} [context=this] The context to invoke the listener with.
 * @returns {EventEmitter} `this`.
 * @public
 */


EventEmitter.prototype.once = function once(event, fn, context) {
  return addListener(this, event, fn, context, true);
};
/**
 * Remove the listeners of a given event.
 *
 * @param {(String|Symbol)} event The event name.
 * @param {Function} fn Only remove the listeners that match this function.
 * @param {*} context Only remove the listeners that have this context.
 * @param {Boolean} once Only remove one-time listeners.
 * @returns {EventEmitter} `this`.
 * @public
 */


EventEmitter.prototype.removeListener = function removeListener(event, fn, context, once) {
  var evt = prefix ? prefix + event : event;
  if (!this._events[evt]) return this;

  if (!fn) {
    clearEvent(this, evt);
    return this;
  }

  var listeners = this._events[evt];

  if (listeners.fn) {
    if (listeners.fn === fn && (!once || listeners.once) && (!context || listeners.context === context)) {
      clearEvent(this, evt);
    }
  } else {
    for (var i = 0, events = [], length = listeners.length; i < length; i++) {
      if (listeners[i].fn !== fn || once && !listeners[i].once || context && listeners[i].context !== context) {
        events.push(listeners[i]);
      }
    } //
    // Reset the array, or remove it completely if we have no more listeners.
    //


    if (events.length) this._events[evt] = events.length === 1 ? events[0] : events;else clearEvent(this, evt);
  }

  return this;
};
/**
 * Remove all listeners, or those of the specified event.
 *
 * @param {(String|Symbol)} [event] The event name.
 * @returns {EventEmitter} `this`.
 * @public
 */


EventEmitter.prototype.removeAllListeners = function removeAllListeners(event) {
  var evt;

  if (event) {
    evt = prefix ? prefix + event : event;
    if (this._events[evt]) clearEvent(this, evt);
  } else {
    this._events = new Events();
    this._eventsCount = 0;
  }

  return this;
}; //
// Alias methods names because people roll like that.
//


EventEmitter.prototype.off = EventEmitter.prototype.removeListener;
EventEmitter.prototype.addListener = EventEmitter.prototype.on; //
// Expose the prefix.
//

EventEmitter.prefixed = prefix; //
// Allow `EventEmitter` to be imported as module namespace.
//

EventEmitter.EventEmitter = EventEmitter; //
// Expose the module.
//

if (true) {
  module.exports = EventEmitter;
}

/***/ }),

/***/ "./node_modules/style-loader/dist/runtime/injectStylesIntoStyleTag.js":
/*!****************************************************************************!*\
  !*** ./node_modules/style-loader/dist/runtime/injectStylesIntoStyleTag.js ***!
  \****************************************************************************/
/***/ ((module, __unused_webpack_exports, __webpack_require__) => {

"use strict";


var isOldIE = function isOldIE() {
  var memo;
  return function memorize() {
    if (typeof memo === 'undefined') {
      // Test for IE <= 9 as proposed by Browserhacks
      // @see http://browserhacks.com/#hack-e71d8692f65334173fee715c222cb805
      // Tests for existence of standard globals is to allow style-loader
      // to operate correctly into non-standard environments
      // @see https://github.com/webpack-contrib/style-loader/issues/177
      memo = Boolean(window && document && document.all && !window.atob);
    }

    return memo;
  };
}();

var getTarget = function getTarget() {
  var memo = {};
  return function memorize(target) {
    if (typeof memo[target] === 'undefined') {
      var styleTarget = document.querySelector(target); // Special case to return head of iframe instead of iframe itself

      if (window.HTMLIFrameElement && styleTarget instanceof window.HTMLIFrameElement) {
        try {
          // This will throw an exception if access to iframe is blocked
          // due to cross-origin restrictions
          styleTarget = styleTarget.contentDocument.head;
        } catch (e) {
          // istanbul ignore next
          styleTarget = null;
        }
      }

      memo[target] = styleTarget;
    }

    return memo[target];
  };
}();

var stylesInDom = [];

function getIndexByIdentifier(identifier) {
  var result = -1;

  for (var i = 0; i < stylesInDom.length; i++) {
    if (stylesInDom[i].identifier === identifier) {
      result = i;
      break;
    }
  }

  return result;
}

function modulesToDom(list, options) {
  var idCountMap = {};
  var identifiers = [];

  for (var i = 0; i < list.length; i++) {
    var item = list[i];
    var id = options.base ? item[0] + options.base : item[0];
    var count = idCountMap[id] || 0;
    var identifier = "".concat(id, " ").concat(count);
    idCountMap[id] = count + 1;
    var index = getIndexByIdentifier(identifier);
    var obj = {
      css: item[1],
      media: item[2],
      sourceMap: item[3]
    };

    if (index !== -1) {
      stylesInDom[index].references++;
      stylesInDom[index].updater(obj);
    } else {
      stylesInDom.push({
        identifier: identifier,
        updater: addStyle(obj, options),
        references: 1
      });
    }

    identifiers.push(identifier);
  }

  return identifiers;
}

function insertStyleElement(options) {
  var style = document.createElement('style');
  var attributes = options.attributes || {};

  if (typeof attributes.nonce === 'undefined') {
    var nonce =  true ? __webpack_require__.nc : 0;

    if (nonce) {
      attributes.nonce = nonce;
    }
  }

  Object.keys(attributes).forEach(function (key) {
    style.setAttribute(key, attributes[key]);
  });

  if (typeof options.insert === 'function') {
    options.insert(style);
  } else {
    var target = getTarget(options.insert || 'head');

    if (!target) {
      throw new Error("Couldn't find a style target. This probably means that the value for the 'insert' parameter is invalid.");
    }

    target.appendChild(style);
  }

  return style;
}

function removeStyleElement(style) {
  // istanbul ignore if
  if (style.parentNode === null) {
    return false;
  }

  style.parentNode.removeChild(style);
}
/* istanbul ignore next  */


var replaceText = function replaceText() {
  var textStore = [];
  return function replace(index, replacement) {
    textStore[index] = replacement;
    return textStore.filter(Boolean).join('\n');
  };
}();

function applyToSingletonTag(style, index, remove, obj) {
  var css = remove ? '' : obj.media ? "@media ".concat(obj.media, " {").concat(obj.css, "}") : obj.css; // For old IE

  /* istanbul ignore if  */

  if (style.styleSheet) {
    style.styleSheet.cssText = replaceText(index, css);
  } else {
    var cssNode = document.createTextNode(css);
    var childNodes = style.childNodes;

    if (childNodes[index]) {
      style.removeChild(childNodes[index]);
    }

    if (childNodes.length) {
      style.insertBefore(cssNode, childNodes[index]);
    } else {
      style.appendChild(cssNode);
    }
  }
}

function applyToTag(style, options, obj) {
  var css = obj.css;
  var media = obj.media;
  var sourceMap = obj.sourceMap;

  if (media) {
    style.setAttribute('media', media);
  } else {
    style.removeAttribute('media');
  }

  if (sourceMap && typeof btoa !== 'undefined') {
    css += "\n/*# sourceMappingURL=data:application/json;base64,".concat(btoa(unescape(encodeURIComponent(JSON.stringify(sourceMap)))), " */");
  } // For old IE

  /* istanbul ignore if  */


  if (style.styleSheet) {
    style.styleSheet.cssText = css;
  } else {
    while (style.firstChild) {
      style.removeChild(style.firstChild);
    }

    style.appendChild(document.createTextNode(css));
  }
}

var singleton = null;
var singletonCounter = 0;

function addStyle(obj, options) {
  var style;
  var update;
  var remove;

  if (options.singleton) {
    var styleIndex = singletonCounter++;
    style = singleton || (singleton = insertStyleElement(options));
    update = applyToSingletonTag.bind(null, style, styleIndex, false);
    remove = applyToSingletonTag.bind(null, style, styleIndex, true);
  } else {
    style = insertStyleElement(options);
    update = applyToTag.bind(null, style, options);

    remove = function remove() {
      removeStyleElement(style);
    };
  }

  update(obj);
  return function updateStyle(newObj) {
    if (newObj) {
      if (newObj.css === obj.css && newObj.media === obj.media && newObj.sourceMap === obj.sourceMap) {
        return;
      }

      update(obj = newObj);
    } else {
      remove();
    }
  };
}

module.exports = function (list, options) {
  options = options || {}; // Force single-tag solution on IE6-9, which has a hard limit on the # of <style>
  // tags it will allow on a page

  if (!options.singleton && typeof options.singleton !== 'boolean') {
    options.singleton = isOldIE();
  }

  list = list || [];
  var lastIdentifiers = modulesToDom(list, options);
  return function update(newList) {
    newList = newList || [];

    if (Object.prototype.toString.call(newList) !== '[object Array]') {
      return;
    }

    for (var i = 0; i < lastIdentifiers.length; i++) {
      var identifier = lastIdentifiers[i];
      var index = getIndexByIdentifier(identifier);
      stylesInDom[index].references--;
    }

    var newLastIdentifiers = modulesToDom(newList, options);

    for (var _i = 0; _i < lastIdentifiers.length; _i++) {
      var _identifier = lastIdentifiers[_i];

      var _index = getIndexByIdentifier(_identifier);

      if (stylesInDom[_index].references === 0) {
        stylesInDom[_index].updater();

        stylesInDom.splice(_index, 1);
      }
    }

    lastIdentifiers = newLastIdentifiers;
  };
};

/***/ }),

/***/ "./node_modules/css-loader/dist/cjs.js??ruleSet[1].rules[3].use[1]!./node_modules/less-loader/dist/cjs.js!./src/component/Control.less":
/*!*********************************************************************************************************************************************!*\
  !*** ./node_modules/css-loader/dist/cjs.js??ruleSet[1].rules[3].use[1]!./node_modules/less-loader/dist/cjs.js!./src/component/Control.less ***!
  \*********************************************************************************************************************************************/
/***/ ((module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony import */ var _node_modules_css_loader_dist_runtime_cssWithMappingToString_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../node_modules/css-loader/dist/runtime/cssWithMappingToString.js */ "./node_modules/css-loader/dist/runtime/cssWithMappingToString.js");
/* harmony import */ var _node_modules_css_loader_dist_runtime_cssWithMappingToString_js__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_node_modules_css_loader_dist_runtime_cssWithMappingToString_js__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var _node_modules_css_loader_dist_runtime_api_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../node_modules/css-loader/dist/runtime/api.js */ "./node_modules/css-loader/dist/runtime/api.js");
/* harmony import */ var _node_modules_css_loader_dist_runtime_api_js__WEBPACK_IMPORTED_MODULE_1___default = /*#__PURE__*/__webpack_require__.n(_node_modules_css_loader_dist_runtime_api_js__WEBPACK_IMPORTED_MODULE_1__);
// Imports


var ___CSS_LOADER_EXPORT___ = _node_modules_css_loader_dist_runtime_api_js__WEBPACK_IMPORTED_MODULE_1___default()((_node_modules_css_loader_dist_runtime_cssWithMappingToString_js__WEBPACK_IMPORTED_MODULE_0___default()));
// Module
___CSS_LOADER_EXPORT___.push([module.id, ".player-container {\n  display: block;\n  position: relative;\n  height: 30px;\n  background: rgba(113, 113, 113, 0.23);\n  user-select: none;\n  display: flex;\n  padding: 0 10px 0 5px;\n}\n.player-container .player-play-icon-wrapper {\n  cursor: pointer;\n  position: relative;\n  width: 20px;\n  height: 20px;\n  padding: 5px;\n}\n.player-container .player-play-icon-wrapper .player-play-icon {\n  display: inline-block;\n  height: inherit;\n  width: inherit;\n  background-size: cover;\n}\n.player-container .player-speed {\n  padding: 5px;\n  height: 20px;\n  font-size: 14px;\n  font-family: sans-serif;\n  cursor: pointer;\n}\n.player-container .player-speed .player-speed-text {\n  text-align: center;\n}\n.player-container .player-speed .player-speed-options {\n  position: absolute;\n  bottom: 27px;\n  background: rgba(0, 0, 0, 0.8);\n  padding: 0 10px;\n  color: white;\n  text-align: center;\n}\n.player-container .player-speed .player-speed-options .option {\n  margin: 10px 0;\n}\n.player-container .player-duration {\n  height: 20px;\n  font-size: 14px;\n  font-family: sans-serif;\n  padding: 5px;\n}\n.player-container .player-timeline {\n  flex: 1;\n  height: 6px;\n  cursor: pointer;\n  background: rgba(0, 0, 0, 0.12);\n  margin: 12px;\n  position: relative;\n}\n.player-container .player-timeline-label {\n  position: absolute;\n  height: 20px;\n  background: white;\n  border-radius: 3px;\n  bottom: 10px;\n  padding: 4px 6px;\n  transform: translateX(-50%);\n}\n.player-container .player-timeline-bg {\n  position: absolute;\n  height: 20px;\n  background: 'white';\n  width: 2px;\n  bottom: -5px;\n  pointer-events: none;\n}\n.player-container .player-timeline-blueline {\n  position: absolute;\n  background: rgba(24, 121, 226, 0.7);\n  height: inherit;\n  pointer-events: none;\n}\n.player-container .player-timeline-slider {\n  position: absolute;\n  top: -5px;\n  bottom: -5px;\n  background: white;\n  box-sizing: content-box;\n  padding: 0 4px;\n  width: 4px;\n  transform: translateX(-6px);\n}\n", "",{"version":3,"sources":["webpack://./src/component/Control.less"],"names":[],"mappings":"AAAA;EACI,cAAA;EACA,kBAAA;EACA,YAAA;EACA,qCAAA;EACA,iBAAA;EACA,aAAA;EACA,qBAAA;AACJ;AARA;EAUQ,eAAA;EACA,kBAAA;EACA,WAAA;EACA,YAAA;EACA,YAAA;AACR;AAfA;EAiBY,qBAAA;EACA,eAAA;EACA,cAAA;EACA,sBAAA;AACZ;AArBA;EAyBQ,YAAA;EACA,YAAA;EACA,eAAA;EACA,uBAAA;EACA,eAAA;AADR;AA5BA;EAgCY,kBAAA;AADZ;AA/BA;EAoCY,kBAAA;EACA,YAAA;EACA,8BAAA;EACA,eAAA;EACA,YAAA;EACA,kBAAA;AAFZ;AAvCA;EA4CgB,cAAA;AAFhB;AA1CA;EAkDQ,YAAA;EACA,eAAA;EACA,uBAAA;EACA,YAAA;AALR;AAhDA;EAyDQ,OAAA;EACA,WAAA;EACA,eAAA;EACA,+BAAA;EACA,YAAA;EACA,kBAAA;AANR;AAxDA;EAkEQ,kBAAA;EACA,YAAA;EACA,iBAAA;EACA,kBAAA;EACA,YAAA;EACA,gBAAA;EACA,2BAAA;AAPR;AAjEA;EA4EQ,kBAAA;EACA,YAAA;EACA,mBAAA;EACA,UAAA;EACA,YAAA;EACA,oBAAA;AARR;AAzEA;EAqFQ,kBAAA;EACA,mCAAA;EACA,eAAA;EACA,oBAAA;AATR;AA/EA;EA4FQ,kBAAA;EACA,SAAA;EACA,YAAA;EACA,iBAAA;EACA,uBAAA;EACA,cAAA;EACA,UAAA;EACA,2BAAA;AAVR","sourcesContent":[".player-container {\n    display: block;\n    position: relative;\n    height: 30px;\n    background: rgba(113, 113, 113, 0.23);\n    user-select: none;\n    display: flex;\n    padding: 0 10px 0 5px;\n\n    .player-play-icon-wrapper {\n        cursor: pointer;\n        position: relative;\n        width: 20px;\n        height: 20px;\n        padding: 5px;\n\n        .player-play-icon {\n            display:inline-block;\n            height:inherit;\n            width:inherit;\n            background-size:cover;\n        }\n    }\n\n    .player-speed {\n        padding: 5px;\n        height: 20px;\n        font-size: 14px;\n        font-family: sans-serif;\n        cursor: pointer;\n\n        .player-speed-text {\n            text-align: center;\n        }\n\n        .player-speed-options {\n            position: absolute;\n            bottom: 27px;\n            background: rgba(0,0,0,.8);\n            padding: 0 10px;\n            color: white;\n            text-align: center;\n\n            .option {\n                margin: 10px 0;\n            }\n        }\n    }\n\n    .player-duration {\n        height:20px;\n        font-size:14px;\n        font-family:sans-serif;\n        padding: 5px;\n    }\n\n    .player-timeline {\n        flex: 1;\n        height: 6px;\n        cursor: pointer;\n        background: rgba(0,0,0,0.12);\n        margin: 12px;\n        position: relative;\n    }\n\n    .player-timeline-label {\n        position: absolute;\n        height: 20px;\n        background: white;\n        border-radius: 3px;\n        bottom: 10px;\n        padding: 4px 6px;\n        transform: translateX(-50%);\n    }\n\n    .player-timeline-bg {\n        position: absolute;\n        height: 20px;\n        background: 'white';\n        width: 2px;\n        bottom: -5px;\n        pointer-events: none;\n    }\n\n    .player-timeline-blueline {\n        position: absolute;\n        background: rgba(24, 121, 226, 0.7);\n        height: inherit;\n        pointer-events: none;\n    }\n\n    .player-timeline-slider {\n        position: absolute;\n        top: -5px;\n        bottom: -5px;\n        background: white;\n        box-sizing: content-box;\n        padding:0 4px;\n        width: 4px;\n        transform:translateX(-6px);\n    }\n}"],"sourceRoot":""}]);
// Exports
/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (___CSS_LOADER_EXPORT___);


/***/ }),

/***/ "./node_modules/css-loader/dist/cjs.js??ruleSet[1].rules[3].use[1]!./node_modules/less-loader/dist/cjs.js!./src/component/Replay.less":
/*!********************************************************************************************************************************************!*\
  !*** ./node_modules/css-loader/dist/cjs.js??ruleSet[1].rules[3].use[1]!./node_modules/less-loader/dist/cjs.js!./src/component/Replay.less ***!
  \********************************************************************************************************************************************/
/***/ ((module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony import */ var _node_modules_css_loader_dist_runtime_cssWithMappingToString_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../node_modules/css-loader/dist/runtime/cssWithMappingToString.js */ "./node_modules/css-loader/dist/runtime/cssWithMappingToString.js");
/* harmony import */ var _node_modules_css_loader_dist_runtime_cssWithMappingToString_js__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_node_modules_css_loader_dist_runtime_cssWithMappingToString_js__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var _node_modules_css_loader_dist_runtime_api_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../node_modules/css-loader/dist/runtime/api.js */ "./node_modules/css-loader/dist/runtime/api.js");
/* harmony import */ var _node_modules_css_loader_dist_runtime_api_js__WEBPACK_IMPORTED_MODULE_1___default = /*#__PURE__*/__webpack_require__.n(_node_modules_css_loader_dist_runtime_api_js__WEBPACK_IMPORTED_MODULE_1__);
// Imports


var ___CSS_LOADER_EXPORT___ = _node_modules_css_loader_dist_runtime_api_js__WEBPACK_IMPORTED_MODULE_1___default()((_node_modules_css_loader_dist_runtime_cssWithMappingToString_js__WEBPACK_IMPORTED_MODULE_0___default()));
// Module
___CSS_LOADER_EXPORT___.push([module.id, ".replay-div {\n  height: 100%;\n}\n.replay-div div {\n  box-sizing: border-box;\n}\n.replay-div .main {\n  height: calc(100% - 45px);\n}\n.replay-div .main .main-wb {\n  padding: 10px;\n  height: 100%;\n  display: inline-block;\n}\n.replay-div .main .main-video {\n  overflow: auto;\n  padding: 10px;\n  height: 100%;\n  display: inline-flex;\n  flex-direction: column;\n  vertical-align: top;\n}\n.replay-div .footer {\n  width: 100%;\n  height: 34px;\n}\n", "",{"version":3,"sources":["webpack://./src/component/Replay.less"],"names":[],"mappings":"AAAA;EACI,YAAA;AACJ;AAFA;EAIQ,sBAAA;AACR;AALA;EAQQ,yBAAA;AAAR;AARA;EAWY,aAAA;EACA,YAAA;EACA,qBAAA;AAAZ;AAbA;EAiBY,cAAA;EACA,aAAA;EACA,YAAA;EACA,oBAAA;EACA,sBAAA;EACA,mBAAA;AADZ;AArBA;EA2BQ,WAAA;EACA,YAAA;AAHR","sourcesContent":[".replay-div {\n    height: 100%;\n\n    div {\n        box-sizing: border-box;\n    }\n\n    .main {\n        height: calc(100% - 45px);\n\n        .main-wb {\n            padding: 10px;\n            height: 100%;\n            display: inline-block;\n        }\n\n        .main-video {\n            overflow: auto;\n            padding: 10px;\n            height: 100%;\n            display: inline-flex;\n            flex-direction: column;\n            vertical-align: top;\n        }\n    }\n\n    .footer {\n        width: 100%;\n        height: 34px;\n    }\n}"],"sourceRoot":""}]);
// Exports
/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (___CSS_LOADER_EXPORT___);


/***/ }),

/***/ "./node_modules/css-loader/dist/cjs.js??ruleSet[1].rules[3].use[1]!./node_modules/less-loader/dist/cjs.js!./src/component/VideoBox.less":
/*!**********************************************************************************************************************************************!*\
  !*** ./node_modules/css-loader/dist/cjs.js??ruleSet[1].rules[3].use[1]!./node_modules/less-loader/dist/cjs.js!./src/component/VideoBox.less ***!
  \**********************************************************************************************************************************************/
/***/ ((module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony import */ var _node_modules_css_loader_dist_runtime_cssWithMappingToString_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../node_modules/css-loader/dist/runtime/cssWithMappingToString.js */ "./node_modules/css-loader/dist/runtime/cssWithMappingToString.js");
/* harmony import */ var _node_modules_css_loader_dist_runtime_cssWithMappingToString_js__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_node_modules_css_loader_dist_runtime_cssWithMappingToString_js__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var _node_modules_css_loader_dist_runtime_api_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../node_modules/css-loader/dist/runtime/api.js */ "./node_modules/css-loader/dist/runtime/api.js");
/* harmony import */ var _node_modules_css_loader_dist_runtime_api_js__WEBPACK_IMPORTED_MODULE_1___default = /*#__PURE__*/__webpack_require__.n(_node_modules_css_loader_dist_runtime_api_js__WEBPACK_IMPORTED_MODULE_1__);
/* harmony import */ var _node_modules_css_loader_dist_runtime_getUrl_js__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../node_modules/css-loader/dist/runtime/getUrl.js */ "./node_modules/css-loader/dist/runtime/getUrl.js");
/* harmony import */ var _node_modules_css_loader_dist_runtime_getUrl_js__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(_node_modules_css_loader_dist_runtime_getUrl_js__WEBPACK_IMPORTED_MODULE_2__);
/* harmony import */ var _assets_video_svg__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../assets/video.svg */ "./src/assets/video.svg");
/* harmony import */ var _assets_video_svg__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(_assets_video_svg__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var _assets_audio_svg__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../assets/audio.svg */ "./src/assets/audio.svg");
/* harmony import */ var _assets_audio_svg__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(_assets_audio_svg__WEBPACK_IMPORTED_MODULE_4__);
// Imports





var ___CSS_LOADER_EXPORT___ = _node_modules_css_loader_dist_runtime_api_js__WEBPACK_IMPORTED_MODULE_1___default()((_node_modules_css_loader_dist_runtime_cssWithMappingToString_js__WEBPACK_IMPORTED_MODULE_0___default()));
var ___CSS_LOADER_URL_REPLACEMENT_0___ = _node_modules_css_loader_dist_runtime_getUrl_js__WEBPACK_IMPORTED_MODULE_2___default()((_assets_video_svg__WEBPACK_IMPORTED_MODULE_3___default()));
var ___CSS_LOADER_URL_REPLACEMENT_1___ = _node_modules_css_loader_dist_runtime_getUrl_js__WEBPACK_IMPORTED_MODULE_2___default()((_assets_audio_svg__WEBPACK_IMPORTED_MODULE_4___default()));
// Module
___CSS_LOADER_EXPORT___.push([module.id, ".videobox {\n  position: relative;\n}\n.videobox .video-black {\n  background: black;\n  position: absolute;\n  top: 0;\n  width: 100%;\n  bottom: 32px;\n}\n.videobox .controls {\n  height: 30px;\n  background-color: rgba(0, 0, 0, 0.4);\n  position: relative;\n  top: -5px;\n  color: white;\n}\n.videobox .controls .name {\n  position: absolute;\n  left: 3px;\n  line-height: 30px;\n}\n.videobox .controls .ctrl {\n  position: absolute;\n  line-height: 30px;\n  right: 3px;\n}\n.videobox .controls .ctrl .icon {\n  width: 30px;\n  height: 30px;\n  display: inline-block;\n  background-repeat: no-repeat;\n  background-position: center;\n  position: relative;\n}\n.videobox .controls .ctrl .icon:hover {\n  cursor: pointer;\n}\n.videobox .controls .ctrl .off::after {\n  content: \"\";\n  background-image: url(data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBzdGFuZGFsb25lPSJubyI/PjwhRE9DVFlQRSBzdmcgUFVCTElDICItLy9XM0MvL0RURCBTVkcgMS4xLy9FTiIgImh0dHA6Ly93d3cudzMub3JnL0dyYXBoaWNzL1NWRy8xLjEvRFREL3N2ZzExLmR0ZCI+PHN2ZyB0PSIxNjE5NDIwODAxNzQ4IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjExMzQiIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCI+PGRlZnM+PHN0eWxlIHR5cGU9InRleHQvY3NzIj48L3N0eWxlPjwvZGVmcz48cGF0aCBkPSJNMjIyLjYzNDY2NyAyNzIuMjk4NjY3QTM4Mi40IDM4Mi40IDAgMCAwIDEzOC42NjY2NjcgNTEyYzAgMjEyLjExNzMzMyAxNzEuOTA0IDM4NCAzODQgMzg0IDkwLjY2NjY2NyAwIDE3NC4wMTYtMzEuNDI0IDIzOS43MDEzMzMtODMuOTY4bC01MzkuNzMzMzMzLTUzOS43MzMzMzN6IG02MC4zMzA2NjYtNjAuMzMwNjY3bDUzOS43MzMzMzQgNTM5LjczMzMzM0EzODIuNCAzODIuNCAwIDAgMCA5MDYuNjY2NjY3IDUxMmMwLTIxMi4xMTczMzMtMTcxLjkwNC0zODQtMzg0LTM4NC05MC42NjY2NjcgMC0xNzQuMDE2IDMxLjQyNC0yMzkuNzAxMzM0IDgzLjk2OHpNNTMuMzMzMzMzIDUxMmMwLTI1OS4yMjEzMzMgMjEwLjA5MDY2Ny00NjkuMzMzMzMzIDQ2OS4zMzMzMzQtNDY5LjMzMzMzMyAyNTkuMjIxMzMzIDAgNDY5LjMzMzMzMyAyMTAuMDkwNjY3IDQ2OS4zMzMzMzMgNDY5LjMzMzMzMyAwIDI1OS4yMjEzMzMtMjEwLjA5MDY2NyA0NjkuMzMzMzMzLTQ2OS4zMzMzMzMgNDY5LjMzMzMzMy0yNTkuMjIxMzMzIDAtNDY5LjMzMzMzMy0yMTAuMDkwNjY3LTQ2OS4zMzMzMzQtNDY5LjMzMzMzM3oiIHAtaWQ9IjExMzUiPjwvcGF0aD48L3N2Zz4=);\n  position: absolute;\n  width: 18px;\n  height: 18px;\n  left: 50%;\n  top: 50%;\n  transform: translate(-50%, -50%);\n  background-size: contain;\n  background-position: center;\n  background-repeat: no-repeat;\n}\n.videobox .controls .ctrl .video {\n  background-image: url(" + ___CSS_LOADER_URL_REPLACEMENT_0___ + ");\n}\n.videobox .controls .ctrl .audio {\n  background-image: url(" + ___CSS_LOADER_URL_REPLACEMENT_1___ + ");\n}\n", "",{"version":3,"sources":["webpack://./src/component/VideoBox.less"],"names":[],"mappings":"AAEA;EACI,kBAAA;AADJ;AAAA;EAIQ,iBAAA;EACA,kBAAA;EACA,MAAA;EACA,WAAA;EACA,YAAA;AADR;AAPA;EAYQ,YAAA;EACA,oCAAA;EACA,kBAAA;EACA,SAAA;EACA,YAAA;AAFR;AAdA;EAmBY,kBAAA;EACA,SAAA;EACA,iBAAA;AAFZ;AAnBA;EAyBY,kBAAA;EACA,iBAAA;EACA,UAAA;AAHZ;AAxBA;EA6BgB,WAAA;EACA,YAAA;EACA,qBAAA;EACA,4BAAA;EACA,2BAAA;EACA,kBAAA;AAFhB;AAGgB;EACI,eAAA;AADpB;AAnCA;EAyCgB,WAAA;EACA,izCAAA;EACA,kBAAA;EACA,WAAA;EACA,YAAA;EACA,SAAA;EACA,QAAA;EACA,gCAAA;EACA,wBAAA;EACA,2BAAA;EACA,4BAAA;AAHhB;AAhDA;EAuDgB,yDAAA;AAJhB;AAnDA;EA2DgB,yDAAA;AALhB","sourcesContent":["@controlHeight: 30px;\n\n.videobox {\n    position: relative;\n\n    .video-black {\n        background: black;\n        position: absolute;\n        top: 0;\n        width: 100%;\n        bottom: 32px;\n    }\n\n    .controls {\n        height: @controlHeight;\n        background-color: rgba(0,0,0,.4);\n        position: relative;\n        top: -5px;\n        color: white;\n\n        .name {\n            position: absolute;\n            left: 3px;\n            line-height: @controlHeight;\n        }\n\n        .ctrl {\n            position: absolute;\n            line-height: @controlHeight;\n            right: 3px;\n            .icon {\n                width: @controlHeight;\n                height: @controlHeight;\n                display: inline-block;\n                background-repeat: no-repeat;\n                background-position: center;\n                position: relative;\n                &:hover {\n                    cursor: pointer;\n                }\n            }\n\n            .off::after {\n                content: \"\";\n                background-image: url(data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBzdGFuZGFsb25lPSJubyI/PjwhRE9DVFlQRSBzdmcgUFVCTElDICItLy9XM0MvL0RURCBTVkcgMS4xLy9FTiIgImh0dHA6Ly93d3cudzMub3JnL0dyYXBoaWNzL1NWRy8xLjEvRFREL3N2ZzExLmR0ZCI+PHN2ZyB0PSIxNjE5NDIwODAxNzQ4IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjExMzQiIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCI+PGRlZnM+PHN0eWxlIHR5cGU9InRleHQvY3NzIj48L3N0eWxlPjwvZGVmcz48cGF0aCBkPSJNMjIyLjYzNDY2NyAyNzIuMjk4NjY3QTM4Mi40IDM4Mi40IDAgMCAwIDEzOC42NjY2NjcgNTEyYzAgMjEyLjExNzMzMyAxNzEuOTA0IDM4NCAzODQgMzg0IDkwLjY2NjY2NyAwIDE3NC4wMTYtMzEuNDI0IDIzOS43MDEzMzMtODMuOTY4bC01MzkuNzMzMzMzLTUzOS43MzMzMzN6IG02MC4zMzA2NjYtNjAuMzMwNjY3bDUzOS43MzMzMzQgNTM5LjczMzMzM0EzODIuNCAzODIuNCAwIDAgMCA5MDYuNjY2NjY3IDUxMmMwLTIxMi4xMTczMzMtMTcxLjkwNC0zODQtMzg0LTM4NC05MC42NjY2NjcgMC0xNzQuMDE2IDMxLjQyNC0yMzkuNzAxMzM0IDgzLjk2OHpNNTMuMzMzMzMzIDUxMmMwLTI1OS4yMjEzMzMgMjEwLjA5MDY2Ny00NjkuMzMzMzMzIDQ2OS4zMzMzMzQtNDY5LjMzMzMzMyAyNTkuMjIxMzMzIDAgNDY5LjMzMzMzMyAyMTAuMDkwNjY3IDQ2OS4zMzMzMzMgNDY5LjMzMzMzMyAwIDI1OS4yMjEzMzMtMjEwLjA5MDY2NyA0NjkuMzMzMzMzLTQ2OS4zMzMzMzMgNDY5LjMzMzMzMy0yNTkuMjIxMzMzIDAtNDY5LjMzMzMzMy0yMTAuMDkwNjY3LTQ2OS4zMzMzMzQtNDY5LjMzMzMzM3oiIHAtaWQ9IjExMzUiPjwvcGF0aD48L3N2Zz4=);\n                position: absolute;\n                width: 18px;\n                height: 18px;\n                left: 50%;\n                top: 50%;\n                transform: translate(-50%, -50%);\n                background-size: contain;\n                background-position: center;\n                background-repeat: no-repeat;\n            }\n\n            .video {\n                background-image: url(../assets/video.svg);\n            }\n\n            .audio {\n                background-image: url(../assets/audio.svg);\n            }\n        }\n    }\n}"],"sourceRoot":""}]);
// Exports
/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (___CSS_LOADER_EXPORT___);


/***/ }),

/***/ "./src/component/Control.less":
/*!************************************!*\
  !*** ./src/component/Control.less ***!
  \************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony import */ var _node_modules_style_loader_dist_runtime_injectStylesIntoStyleTag_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! !../../node_modules/style-loader/dist/runtime/injectStylesIntoStyleTag.js */ "./node_modules/style-loader/dist/runtime/injectStylesIntoStyleTag.js");
/* harmony import */ var _node_modules_style_loader_dist_runtime_injectStylesIntoStyleTag_js__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_node_modules_style_loader_dist_runtime_injectStylesIntoStyleTag_js__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var _node_modules_css_loader_dist_cjs_js_ruleSet_1_rules_3_use_1_node_modules_less_loader_dist_cjs_js_Control_less__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! !!../../node_modules/css-loader/dist/cjs.js??ruleSet[1].rules[3].use[1]!../../node_modules/less-loader/dist/cjs.js!./Control.less */ "./node_modules/css-loader/dist/cjs.js??ruleSet[1].rules[3].use[1]!./node_modules/less-loader/dist/cjs.js!./src/component/Control.less");

            

var options = {};

options.insert = "head";
options.singleton = false;

var update = _node_modules_style_loader_dist_runtime_injectStylesIntoStyleTag_js__WEBPACK_IMPORTED_MODULE_0___default()(_node_modules_css_loader_dist_cjs_js_ruleSet_1_rules_3_use_1_node_modules_less_loader_dist_cjs_js_Control_less__WEBPACK_IMPORTED_MODULE_1__.default, options);



/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (_node_modules_css_loader_dist_cjs_js_ruleSet_1_rules_3_use_1_node_modules_less_loader_dist_cjs_js_Control_less__WEBPACK_IMPORTED_MODULE_1__.default.locals || {});

/***/ }),

/***/ "./src/component/Replay.less":
/*!***********************************!*\
  !*** ./src/component/Replay.less ***!
  \***********************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony import */ var _node_modules_style_loader_dist_runtime_injectStylesIntoStyleTag_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! !../../node_modules/style-loader/dist/runtime/injectStylesIntoStyleTag.js */ "./node_modules/style-loader/dist/runtime/injectStylesIntoStyleTag.js");
/* harmony import */ var _node_modules_style_loader_dist_runtime_injectStylesIntoStyleTag_js__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_node_modules_style_loader_dist_runtime_injectStylesIntoStyleTag_js__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var _node_modules_css_loader_dist_cjs_js_ruleSet_1_rules_3_use_1_node_modules_less_loader_dist_cjs_js_Replay_less__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! !!../../node_modules/css-loader/dist/cjs.js??ruleSet[1].rules[3].use[1]!../../node_modules/less-loader/dist/cjs.js!./Replay.less */ "./node_modules/css-loader/dist/cjs.js??ruleSet[1].rules[3].use[1]!./node_modules/less-loader/dist/cjs.js!./src/component/Replay.less");

            

var options = {};

options.insert = "head";
options.singleton = false;

var update = _node_modules_style_loader_dist_runtime_injectStylesIntoStyleTag_js__WEBPACK_IMPORTED_MODULE_0___default()(_node_modules_css_loader_dist_cjs_js_ruleSet_1_rules_3_use_1_node_modules_less_loader_dist_cjs_js_Replay_less__WEBPACK_IMPORTED_MODULE_1__.default, options);



/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (_node_modules_css_loader_dist_cjs_js_ruleSet_1_rules_3_use_1_node_modules_less_loader_dist_cjs_js_Replay_less__WEBPACK_IMPORTED_MODULE_1__.default.locals || {});

/***/ }),

/***/ "./src/component/VideoBox.less":
/*!*************************************!*\
  !*** ./src/component/VideoBox.less ***!
  \*************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (__WEBPACK_DEFAULT_EXPORT__)
/* harmony export */ });
/* harmony import */ var _node_modules_style_loader_dist_runtime_injectStylesIntoStyleTag_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! !../../node_modules/style-loader/dist/runtime/injectStylesIntoStyleTag.js */ "./node_modules/style-loader/dist/runtime/injectStylesIntoStyleTag.js");
/* harmony import */ var _node_modules_style_loader_dist_runtime_injectStylesIntoStyleTag_js__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_node_modules_style_loader_dist_runtime_injectStylesIntoStyleTag_js__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var _node_modules_css_loader_dist_cjs_js_ruleSet_1_rules_3_use_1_node_modules_less_loader_dist_cjs_js_VideoBox_less__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! !!../../node_modules/css-loader/dist/cjs.js??ruleSet[1].rules[3].use[1]!../../node_modules/less-loader/dist/cjs.js!./VideoBox.less */ "./node_modules/css-loader/dist/cjs.js??ruleSet[1].rules[3].use[1]!./node_modules/less-loader/dist/cjs.js!./src/component/VideoBox.less");

            

var options = {};

options.insert = "head";
options.singleton = false;

var update = _node_modules_style_loader_dist_runtime_injectStylesIntoStyleTag_js__WEBPACK_IMPORTED_MODULE_0___default()(_node_modules_css_loader_dist_cjs_js_ruleSet_1_rules_3_use_1_node_modules_less_loader_dist_cjs_js_VideoBox_less__WEBPACK_IMPORTED_MODULE_1__.default, options);



/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = (_node_modules_css_loader_dist_cjs_js_ruleSet_1_rules_3_use_1_node_modules_less_loader_dist_cjs_js_VideoBox_less__WEBPACK_IMPORTED_MODULE_1__.default.locals || {});

/***/ }),

/***/ "./src/assets/audio.svg":
/*!******************************!*\
  !*** ./src/assets/audio.svg ***!
  \******************************/
/***/ ((module) => {

module.exports = "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIGNsYXNzPSJzdmctaWNvbiIgdmlld0JveD0iNjQgNjQgODk2IDg5NiIgd2lkdGg9IjIwIiBoZWlnaHQ9IjIwIiBzdHlsZT0iZmlsbDogcmdiKDI1NSwgMjU1LCAyNTUpOyI+PHBhdGggZD0iTTUxMiA2MjRjOTMuOSAwIDE3MC03NS4yIDE3MC0xNjhWMjMyYzAtOTIuOC03Ni4xLTE2OC0xNzAtMTY4cy0xNzAgNzUuMi0xNzAgMTY4djIyNGMwIDkyLjggNzYuMSAxNjggMTcwIDE2OHptMzMwLTE3MGMwLTQuNC0zLjYtOC04LThoLTYwYy00LjQgMC04IDMuNi04IDggMCAxNDAuMy0xMTMuNyAyNTQtMjU0IDI1NFMyNTggNTk0LjMgMjU4IDQ1NGMwLTQuNC0zLjYtOC04LThoLTYwYy00LjQgMC04IDMuNi04IDggMCAxNjguNyAxMjYuNiAzMDcuOSAyOTAgMzI3LjZWODg0SDMyNi43Yy0xMy43IDAtMjQuNyAxNC4zLTI0LjcgMzJ2MzZjMCA0LjQgMi44IDggNi4yIDhoNDA3LjZjMy40IDAgNi4yLTMuNiA2LjItOHYtMzZjMC0xNy43LTExLTMyLTI0LjctMzJINTQ4Vjc4Mi4xYzE2NS4zLTE4IDI5NC0xNTggMjk0LTMyOC4xeiI+PC9wYXRoPjwvc3ZnPg=="

/***/ }),

/***/ "./src/assets/pause.svg":
/*!******************************!*\
  !*** ./src/assets/pause.svg ***!
  \******************************/
/***/ ((module) => {

module.exports = "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBzdGFuZGFsb25lPSJubyI/PjwhRE9DVFlQRSBzdmcgUFVCTElDICItLy9XM0MvL0RURCBTVkcgMS4xLy9FTiIgImh0dHA6Ly93d3cudzMub3JnL0dyYXBoaWNzL1NWRy8xLjEvRFREL3N2ZzExLmR0ZCI+PHN2ZyB0PSIxNjE0MTUxOTUxOTYxIiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjI5MDgiIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCI+PGRlZnM+PHN0eWxlIHR5cGU9InRleHQvY3NzIj48L3N0eWxlPjwvZGVmcz48cGF0aCBkPSJNMjU5Ljk0NzkyOTA5IDEzMy45MjE4OTNoMTI2LjAyNjAzNDgxYzIzLjMzODE1NDczIDAgNDIuMDA4Njc4MjcgMTguNjcwNTIzNTMgNDIuMDA4Njc5NTcgNDIuMDA4Njc4MjZ2NjcyLjEzODg1NzQ4YzAgMjMuMzM4MTU0NzMtMTguNjcwNTIzNTMgNDIuMDA4Njc4MjctNDIuMDA4Njc5NTcgNDIuMDA4Njc4MjZIMjU5Ljk0NzkyOTA5Yy0yMy4zMzgxNTQ3MyAwLTQyLjAwODY3ODI3LTE4LjY3MDUyMzUzLTQyLjAwODY3OTU2LTQyLjAwODY3ODI2VjE3NS45MzA1NzEyNmMwLTIzLjMzODE1NDczIDE4LjY3MDUyMzUzLTQyLjAwODY3ODI3IDQyLjAwODY3OTU2LTQyLjAwODY3ODI2ek02MzguMDI2MDM2MSAxMzMuOTIxODkzaDEyNi4wMjYwMzQ4MWMyMy4zMzgxNTQ3MyAwIDQyLjAwODY3ODI3IDE4LjY3MDUyMzUzIDQyLjAwODY3OTU2IDQyLjAwODY3ODI2djY3Mi4xMzg4NTc0OGMwIDIzLjMzODE1NDczLTE4LjY3MDUyMzUzIDQyLjAwODY3ODI3LTQyLjAwODY3OTU2IDQyLjAwODY3ODI2aC0xMjYuMDI2MDM0ODFjLTIzLjMzODE1NDczIDAtNDIuMDA4Njc4MjctMTguNjcwNTIzNTMtNDIuMDA4Njc5NTctNDIuMDA4Njc4MjZWMTc1LjkzMDU3MTI2YzAtMjMuMzM4MTU0NzMgMTguNjcwNTIzNTMtNDIuMDA4Njc4MjcgNDIuMDA4Njc5NTctNDIuMDA4Njc4MjZ6IiBmaWxsPSIjZmZmZmZmIiBwLWlkPSIyOTA5Ij48L3BhdGg+PC9zdmc+"

/***/ }),

/***/ "./src/assets/play.svg":
/*!*****************************!*\
  !*** ./src/assets/play.svg ***!
  \*****************************/
/***/ ((module) => {

module.exports = "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBzdGFuZGFsb25lPSJubyI/PjwhRE9DVFlQRSBzdmcgUFVCTElDICItLy9XM0MvL0RURCBTVkcgMS4xLy9FTiIgImh0dHA6Ly93d3cudzMub3JnL0dyYXBoaWNzL1NWRy8xLjEvRFREL3N2ZzExLmR0ZCI+PHN2ZyB0PSIxNjE0MTUxOTI0MjE2IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjIxMzEiIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCI+PGRlZnM+PHN0eWxlIHR5cGU9InRleHQvY3NzIj48L3N0eWxlPjwvZGVmcz48cGF0aCBkPSJNOTEyLjcyNDg4NCA0MjkuMzU1NjgxTDIwOC43OTc1NDUgMTMuMTk4NjM4QzE1MS42MDM0NDktMjAuNTk3ODc0IDY0LjAxMjQ5IDEyLjE5ODc0MSA2NC4wMTI0OSA5NS43OTAxMTJWOTI3LjkwNDIxOWMwIDc0Ljk5MjI1OSA4MS4zOTE1OTkgMTIwLjE4NzU5NCAxNDQuNzg1MDU1IDgyLjU5MTQ3NWw3MDMuOTI3MzM5LTQxNS45NTcwNjRjNjIuNzkzNTE4LTM2Ljk5NjE4MSA2Mi45OTM0OTgtMTI4LjE4Njc2OCAwLTE2NS4xODI5NDl6IiBmaWxsPSIjZmZmZmZmIiBwLWlkPSIyMTMyIj48L3BhdGg+PC9zdmc+"

/***/ }),

/***/ "./src/assets/video.svg":
/*!******************************!*\
  !*** ./src/assets/video.svg ***!
  \******************************/
/***/ ((module) => {

module.exports = "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIGNsYXNzPSJzdmctaWNvbiIgdmlld0JveD0iNjQgNjQgODk2IDg5NiIgd2lkdGg9IjIwIiBoZWlnaHQ9IjIwIiBzdHlsZT0iZmlsbDogcmdiKDI1NSwgMjU1LCAyNTUpOyI+PHBhdGggZD0iTTkxMiAzMDIuM0w3ODQgMzc2VjIyNGMwLTM1LjMtMjguNy02NC02NC02NEgxMjhjLTM1LjMgMC02NCAyOC43LTY0IDY0djU3NmMwIDM1LjMgMjguNyA2NCA2NCA2NGg1OTJjMzUuMyAwIDY0LTI4LjcgNjQtNjRWNjQ4bDEyOCA3My43YzIxLjMgMTIuMyA0OC0zLjEgNDgtMjcuNlYzMzBjMC0yNC42LTI2LjctNDAtNDgtMjcuN3pNMzI4IDM1MmMwIDQuNC0zLjYgOC04IDhIMjA4Yy00LjQgMC04LTMuNi04LTh2LTQ4YzAtNC40IDMuNi04IDgtOGgxMTJjNC40IDAgOCAzLjYgOCA4djQ4em01NjAgMjczbC0xMDQtNTkuOFY0NTguOUw4ODggMzk5djIyNnoiPjwvcGF0aD48L3N2Zz4="

/***/ }),

/***/ "RecordPlayer":
/*!************************************************************!*\
  !*** external "./lib/RecordPlayer/RecordPlayer_v3.1.2.js" ***!
  \************************************************************/
/***/ ((module) => {

"use strict";
module.exports = __WEBPACK_EXTERNAL_MODULE_RecordPlayer__;

/***/ }),

/***/ "react":
/*!************************!*\
  !*** external "react" ***!
  \************************/
/***/ ((module) => {

"use strict";
module.exports = __WEBPACK_EXTERNAL_MODULE_react__;

/***/ })

/******/ 	});
/************************************************************************/
/******/ 	// The module cache
/******/ 	var __webpack_module_cache__ = {};
/******/ 	
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/ 		// Check if module is in cache
/******/ 		var cachedModule = __webpack_module_cache__[moduleId];
/******/ 		if (cachedModule !== undefined) {
/******/ 			return cachedModule.exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = __webpack_module_cache__[moduleId] = {
/******/ 			id: moduleId,
/******/ 			// no module.loaded needed
/******/ 			exports: {}
/******/ 		};
/******/ 	
/******/ 		// Execute the module function
/******/ 		__webpack_modules__[moduleId](module, module.exports, __webpack_require__);
/******/ 	
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/ 	
/************************************************************************/
/******/ 	/* webpack/runtime/compat get default export */
/******/ 	(() => {
/******/ 		// getDefaultExport function for compatibility with non-harmony modules
/******/ 		__webpack_require__.n = (module) => {
/******/ 			var getter = module && module.__esModule ?
/******/ 				() => (module['default']) :
/******/ 				() => (module);
/******/ 			__webpack_require__.d(getter, { a: getter });
/******/ 			return getter;
/******/ 		};
/******/ 	})();
/******/ 	
/******/ 	/* webpack/runtime/define property getters */
/******/ 	(() => {
/******/ 		// define getter functions for harmony exports
/******/ 		__webpack_require__.d = (exports, definition) => {
/******/ 			for(var key in definition) {
/******/ 				if(__webpack_require__.o(definition, key) && !__webpack_require__.o(exports, key)) {
/******/ 					Object.defineProperty(exports, key, { enumerable: true, get: definition[key] });
/******/ 				}
/******/ 			}
/******/ 		};
/******/ 	})();
/******/ 	
/******/ 	/* webpack/runtime/hasOwnProperty shorthand */
/******/ 	(() => {
/******/ 		__webpack_require__.o = (obj, prop) => (Object.prototype.hasOwnProperty.call(obj, prop))
/******/ 	})();
/******/ 	
/******/ 	/* webpack/runtime/make namespace object */
/******/ 	(() => {
/******/ 		// define __esModule on exports
/******/ 		__webpack_require__.r = (exports) => {
/******/ 			if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 				Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 			}
/******/ 			Object.defineProperty(exports, '__esModule', { value: true });
/******/ 		};
/******/ 	})();
/******/ 	
/************************************************************************/
var __webpack_exports__ = {};
// This entry need to be wrapped in an IIFE because it need to be in strict mode.
(() => {
"use strict";
/*!**********************************!*\
  !*** ./src/component/Replay.tsx ***!
  \**********************************/
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "default": () => (/* binding */ Replay)
/* harmony export */ });
/* harmony import */ var react__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! react */ "react");
/* harmony import */ var react__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(react__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var RecordPlayer__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! RecordPlayer */ "RecordPlayer");
/* harmony import */ var RecordPlayer__WEBPACK_IMPORTED_MODULE_1___default = /*#__PURE__*/__webpack_require__.n(RecordPlayer__WEBPACK_IMPORTED_MODULE_1__);
/* harmony import */ var _utils_TickTick__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../utils/TickTick */ "./src/utils/TickTick.ts");
/* harmony import */ var _Control__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./Control */ "./src/component/Control.tsx");
/* harmony import */ var _Replay_less__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./Replay.less */ "./src/component/Replay.less");
/* harmony import */ var _VideoGroup__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./VideoGroup */ "./src/component/VideoGroup.tsx");
function _typeof(obj) { "@babel/helpers - typeof"; if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = _getPrototypeOf(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = _getPrototypeOf(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return _possibleConstructorReturn(this, result); }; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Boolean.prototype.valueOf.call(Reflect.construct(Boolean, [], function () {})); return true; } catch (e) { return false; } }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }








var Replay = /*#__PURE__*/function (_React$Component) {
  _inherits(Replay, _React$Component);

  var _super = _createSuper(Replay);

  function Replay(props) {
    var _this;

    _classCallCheck(this, Replay);

    _this = _super.call(this, props);
    /**
     * 白板的起始时间，seekTo的时候要减去这个时间。因为白板seekTo 0对应的位置为整体播放时的wbBeginOffset的位置
     */

    _this.wbBegin = 0;
    _this.wbEnd = 0;

    _this.handlePlay = function () {
      var _a;

      _this.setState({
        playing: true,
        syncAt: _this.state.currTime,
        syncTimestamp: new Date().valueOf()
      });

      (_a = _this.ticktick) === null || _a === void 0 ? void 0 : _a.play();
    };

    _this.handlePause = function () {
      var _a;

      _this.setState({
        playing: false,
        syncAt: _this.state.currTime,
        syncTimestamp: new Date().valueOf()
      });

      (_a = _this.ticktick) === null || _a === void 0 ? void 0 : _a.pause();
    };

    _this.handleSetSpeed = function (speed) {
      _this.setState({
        speed: speed
      });

      if (_this.ticktick) {
        _this.ticktick.rate = speed;
      }
    };

    _this.handleSeekTo = function (time) {
      if (_this.ticktick) {
        _this.ticktick.setCurrentTime(time);
      }

      _this.setState({
        currTime: time,
        syncAt: time,
        syncTimestamp: new Date().valueOf()
      });
    };
    /**
     * 视频文件缓冲中
     */


    _this.handleWait = function () {
      var _a;

      _this.setState({
        wait: true
      });

      (_a = _this.ticktick) === null || _a === void 0 ? void 0 : _a.pause();
    };
    /**
     * 缓冲数据加载了一部分。如果目前正在等待缓冲数据，且处于播放状态，则改为播放
     */


    _this.handleCanPlay = function () {
      var _a;

      if (_this.state.wait === true) {
        _this.setState({
          wait: false
        });

        if (_this.state.playing) {
          (_a = _this.ticktick) === null || _a === void 0 ? void 0 : _a.play();
        }
      }
    };

    var store = props.store;
    var beginAt = store.wbTracks.concat(store.videoTracks).reduce(function (prev, track) {
      return Math.min(prev, track.start);
    }, Number.POSITIVE_INFINITY);
    var endAt = store.wbTracks.concat(store.videoTracks).reduce(function (prev, track) {
      return Math.max(prev, track.end);
    }, Number.NEGATIVE_INFINITY);

    if (store.wbTracks.length > 0) {
      _this.wbBegin = store.wbTracks[0].start - beginAt;
      _this.wbEnd = store.wbTracks[0].end - beginAt;
    }

    store.videoTracks.forEach(function (t, index) {
      console.log('视频开始时间', index, t.start - beginAt);
      console.log('视频结束时间', index, t.end - beginAt);
    });
    console.log('白板开始时间', _this.wbBegin);
    console.log('白板结束时间', _this.wbEnd);
    _this.state = {
      playing: false,
      currTime: 0,
      beginAt: beginAt,
      endAt: endAt,
      syncAt: 0,
      syncTimestamp: new Date().valueOf(),
      speed: 1,
      wait: false
    };
    return _this;
  }

  _createClass(Replay, [{
    key: "componentDidMount",
    value: function componentDidMount() {
      var _this2 = this;

      var store = this.props.store;
      var wbUrls = store.wbTracks.map(function (t) {
        return t.url;
      });
      this.ticktick = new _utils_TickTick__WEBPACK_IMPORTED_MODULE_2__.default();
      this.ticktick.on('tick', function (time) {
        if(_this2.player) {
          _this2.player.seekTo(time - _this2.wbBegin);
          _this2.setState({
            currTime: time
          });
        }
        
      });
      RecordPlayer__WEBPACK_IMPORTED_MODULE_1___default().getInstance({
        whiteboardParams: {
          urlArr: wbUrls,
          container: document.getElementById('whiteboard-container')
        }
      }).then(function (_ref) {
        var player = _ref.player;
        _this2.player = player;
      });
    }
  }, {
    key: "componentWillUnmount",
    value: function componentWillUnmount() {
      var _a;

      (_a = this.ticktick) === null || _a === void 0 ? void 0 : _a.destory();
    }
    /**
     * 获取时间time时，正在播放，且根据各种事件，应该被播放的视频文件
     * @param time
     */

  }, {
    key: "getVideosOfMoment",
    value: function getVideosOfMoment(time) {
      var store = this.props.store;
      var visibleVIds = store.videoTracks.filter(function (t) {
        var inRange = time >= t.start && time <= t.end;

        if (!inRange) {
          return false;
        } else {
          //看看是否因为进出事件，导致视频不应该播放
          var events = store.events.filter(function (ev) {
            return ev.userId === t.userId && ev.timestamp <= time;
          });

          if (events.length > 0) {
            return events[events.length - 1].action === 'show';
          } else {
            return true;
          }
        }
      }).map(function (t) {
        return t.id;
      });
      return visibleVIds;
    }
  }, {
    key: "render",
    value: function render() {
      return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        className: 'replay-div'
      }, /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        className: 'main'
      }, this.renderWhiteboard(), this.renderVideoGroup()), this.renderControls());
    }
  }, {
    key: "renderWhiteboard",
    value: function renderWhiteboard() {
      return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        className: 'main-wb',
        id: 'whiteboard-container',
        style: {
          width: "calc(100% - ".concat(this.props.config.videoWidth + 20, "px)")
        }
      });
    }
  }, {
    key: "renderVideoGroup",
    value: function renderVideoGroup() {
      var visibleVIds = this.getVideosOfMoment(this.state.currTime + this.state.beginAt);
      return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement(_VideoGroup__WEBPACK_IMPORTED_MODULE_5__.default, {
        beginOffset: this.state.beginAt,
        videoWidth: this.props.config.videoWidth,
        videos: this.props.store.videoTracks,
        currTime: this.state.currTime,
        playing: this.state.playing,
        onWait: this.handleWait,
        onCanPlay: this.handleCanPlay,
        syncAt: this.state.syncAt,
        syncTimestamp: this.state.syncTimestamp,
        speed: this.state.speed,
        visibleVIds: visibleVIds
      });
    }
  }, {
    key: "renderControls",
    value: function renderControls() {
      return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement("div", {
        className: 'footer'
      }, /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement(_Control__WEBPACK_IMPORTED_MODULE_3__.default, {
        playing: this.state.playing,
        currTime: this.state.currTime,
        speed: this.state.speed,
        start: this.state.beginAt,
        end: this.state.endAt,
        onPlay: this.handlePlay,
        onPause: this.handlePause,
        onSetSpeed: this.handleSetSpeed,
        onSeekTo: this.handleSeekTo
      }));
    }
  }]);

  return Replay;
}((react__WEBPACK_IMPORTED_MODULE_0___default().Component));


})();

/******/ 	return __webpack_exports__;
/******/ })()
;
});
//# sourceMappingURL=index.js.map