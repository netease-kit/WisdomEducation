const { contextBridge, ipcRenderer, remote, app } = require('electron');
const path = require('path');

window.addEventListener('DOMContentLoaded', () => {
  //TODO
})

// contextBridge.exposeInMainWorld('NERtcSDK', require('nertc-electron-sdk').default)
window.NERtcSDK = require('nertc-electron-sdk').default;
window.platform = process.platform;
window.ipcRenderer = ipcRenderer;
window.eleRemote = remote;
window.eleProcess = process;
