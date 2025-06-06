// 声明 Vanta.js 模块类型
declare module 'vanta/dist/vanta.net.min.js' {
  interface VantaNetOptions {
    el: HTMLElement;
    THREE?: any;
    mouseControls?: boolean;
    touchControls?: boolean;
    gyroControls?: boolean;
    minHeight?: number;
    minWidth?: number;
    scale?: number;
    scaleMobile?: number;
    color?: number;
    backgroundColor?: number;
    points?: number;
    maxDistance?: number;
    spacing?: number;
  }

  interface VantaEffect {
    destroy(): void;
  }

  function NET(options: VantaNetOptions): VantaEffect;
  export default NET;
}
