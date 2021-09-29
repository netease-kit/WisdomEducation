let sessionStorage: any = window.sessionStorage
if (!window.sessionStorage ||
  typeof window.sessionStorage.getItem !== 'function' ||
  typeof window.sessionStorage.setItem !== 'function' ||
  typeof window.sessionStorage.removeItem !== 'function') {
  sessionStorage = {
    privateObj: {},
    setItem: function (key: string, value: string) {
      sessionStorage.privateObj[key] = value
    },
    getItem: function (key: string) {
      return sessionStorage.privateObj[key]
    },
    removeItem: function (key: string) {
      delete sessionStorage.privateObj[key]
    },
    getKeys: function () {
      return Object.keys(sessionStorage.privateObj)
    }
  }
}

export default sessionStorage