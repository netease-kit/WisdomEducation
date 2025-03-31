const { app, BrowserWindow, screen, ipcMain, desktopCapturer, systemPreferences} = require("electron");
const path = require("path")

const isLocal = process.env.MODE === 'local';
let [hasbind, mainWindow] = [false];

function handleWindowClose() {
  app.isClosedByCode = true;
  mainWindow.close()
  mainWindow.destroy()
}

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

  app.isClosedByCode = false
  app.isQuiting = false

  // and load the index.html of the app.
  if (isLocal) {
    mainWindow.loadURL("https://localhost:3001/");
    // Open the DevTools.
    mainWindow.webContents.openDevTools();
  } else {
    mainWindow.loadFile(path.join(__dirname, "../index.html"));
  }

  if (mainWindow.listenerCount('close') === 0) {
    console.log('\n添加close监听')
    mainWindow.on('close', (event) => {
      if(app.isClosedByCode) {
        console.log('\nclose-直接关闭')
        mainWindow.removeListener('close', handleWindowClose);
      } else {
        console.log('\nclose-触发二次确认')
        event.preventDefault()
        mainWindow.webContents.send('main-close-before')
      }
    });
  }
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

  app.on("before-quit", (event) =>{
    console.log('\n监听到quit')
    app.isQuiting = true
  })

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
  ipcMain.on('allow-to-close', (event, arg)=>{
    console.log('\n监听到allow-to-close')
    handleWindowClose()
  })
});

app.on('window-all-closed', function () {
  console.log('\n监听到window-all-closed ',app.isQuiting)
  if (process.platform !== 'darwin' || app.isQuiting) app.quit()
});


