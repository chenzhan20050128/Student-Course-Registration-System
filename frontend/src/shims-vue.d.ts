// .vue文件的类型声明文件
declare module '*.vue' {
  import { DefineComponent } from 'vue';
  const component: DefineComponent<{}, {}, any>;
  export default component;
}

// 对 http.js 模块的类型声明
declare module './http' {
  import { AxiosInstance } from 'axios';
  const instance: AxiosInstance;
  export default instance;
}