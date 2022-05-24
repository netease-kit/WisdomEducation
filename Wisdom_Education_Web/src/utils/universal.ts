import intl from 'react-intl-universal';
import enUS from '@/locales/en-US.js';
import zhCN from '@/locales/zh-CN.js';

export const DEFAULT_LOCCALE = "zh-CN"

export const SUPPORT_LOCALES = [
  {
    label: '中文',
    value: 'zh-CN'
  },
  {
    label: 'English',
    value: 'en-US'
  }
]

export const initLocales = async (locale=currentLocale()) => {
  const locales = {
    'zh-CN': zhCN,
    'en-US': enUS
  }
  intl.init({
    currentLocale: locale,
    locales
  })
  if (intl.get("智慧云课堂")) {
    console.log("cc ",intl.get("智慧云课堂"))
    return true
  }
}

export const currentLocale = () => {
  const locale = localStorage.getItem("lang")
  return locale || DEFAULT_LOCCALE
}