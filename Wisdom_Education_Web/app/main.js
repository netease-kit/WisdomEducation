const { app, BrowserWindow, screen, ipcMain, desktopCapturer, systemPreferences} = require("electron");
const path = require("path")

const isLocal = process.env.MODE === 'local';
let [hasbind, mainWindow] = [false];

function createWindow() {
  // Create the browser window.
  const { width, height } = screen.getPrimaryDisplay().workAreaSize;
  mainWindow = new BrowserWindow({
    width,
    height,
    webPreferences: {
      contextIsolation: false,
      nodeIntegration: true,
      enableRemoteModule: true,
      visualEffectState: 'active',
      preload: path.join(__dirname, "./preload.js"),
      title: '智慧云课堂'
    },
    backgroundColor: '#EEF1FB'
  });

  // and load the index.html of the app.
  if (isLocal) {
    mainWindow.loadURL("https://localhost:3001/");
    // Open the DevTools.
    mainWindow.webContents.openDevTools();
  } else {
    mainWindow.loadFile(path.join(__dirname, "../index.html"));
  }

  mainWindow.webContents.on('did-finish-load', () => {
    if (hasbind) {
      return;
    }
    mainWindow.on('close', () => {
      mainWindow.webContents.send('main-close-before')
    });
    hasbind = true;
    // mainWindow.on('closed', () => {
    //   mainWindow.webContents.send('main-closed')
    // })
  });
}

if (isLocal) {
  app.commandLine.appendSwitch('ignore-certificate-errors')
}


app.whenReady().then(() => {
  createWindow();

  app.on("activate", function () {
    // On macOS it's common to re-create a window in the app when the
    // dock icon is clicked and there are no other windows open.
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });

  ipcMain.on('hasJoinClass', (event, arg) => {
    try {
      event.reply('onWindowCreate', {
        shareStatus: systemPreferences.getMediaAccessStatus('screen')
      })
    } catch (error) {
      console.error('Exception thrown', error);
    }
    // event.sender.send('onWindowCreate', {})
  })
  ipcMain.on('hasRender', (event, arg) => {
    try {
      event.reply('onWindowRender', {
        logPath: app.getPath('logs')
      })
    } catch (error) {
      console.error('Exception thrown', error);
    }
  })
});

app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') app.quit()
});


