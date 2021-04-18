declare module 'cordova-plugin-camera-preview' {
  type CameraPreviewErrorHandler = (err: any) => any;
  type CameraPreviewSuccessHandler = (data: any) => any;

  type CameraPreviewCameraDirection = 'back'|'front';
  type CameraPreviewColorEffect = 'aqua'|'blackboard'|'mono'|'negative'|'none'|'posterize'|'sepia'|'solarize'|'whiteboard';
  type CameraPreviewExposureMode = 'lock'|'auto'|'continuous'|'custom';
  type CameraPreviewFlashMode = 'off'|'on'|'auto'|'red-eye'|'torch';
  type CameraPreviewFocusMode = 'fixed'|'auto'|'continuous'|'continuous-picture'|'continuous-video'|'edof'|'infinity'|'macro';
  type CameraPreviewWhiteBalanceMode = 'lock'|'auto'|'continuous'|'incandescent'|'cloudy-daylight'|'daylight'|'fluorescent'|'shade'|'twilight'|'warm-fluorescent';

  interface CameraPreviewStartCameraOptions {
    alpha?: number;
    camera?: CameraPreviewCameraDirection|string;
    height?: number;
    previewDrag?: boolean;
    tapFocus?: boolean;
    tapPhoto?: boolean;
    toBack?: boolean;
    width?: number;
    x?: number;
    y?: number;
    disableExifHeaderStripping?: boolean;
    storeToFile?: boolean;
  }

  interface CameraPreviewTakePictureOptions {
    height?: number;
    quality?: number;
    width?: number;
  }

  interface CameraPreviewTakeSnapshotOptions {
    quality?: number;
  }

  interface CameraPreviewPreviewSizeDimension {
    x?: number;
    y?: number;
    height?: number;
    width?: number;
  }

  interface CameraPreview {
    startCamera(options?: CameraPreviewStartCameraOptions, onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    stopCamera(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    switchCamera(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    hide(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    show(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    takePicture(options?: CameraPreviewTakePictureOptions|CameraPreviewSuccessHandler, onSuccess?: CameraPreviewSuccessHandler|CameraPreviewErrorHandler, onError?: CameraPreviewErrorHandler): void;
    takeSnapshot(options?: CameraPreviewTakeSnapshotOptions|CameraPreviewSuccessHandler, onSuccess?: CameraPreviewSuccessHandler|CameraPreviewErrorHandler, onError?: CameraPreviewErrorHandler): void;
    setColorEffect(effect: CameraPreviewColorEffect|string, onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    setZoom(zoom?: number, onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    startRecordVideo(options?:any|CameraPreviewSuccessHandler, onSuccess?:CameraPreviewSuccessHandler|CameraPreviewErrorHandler, onError?:CameraPreviewErrorHandler):void;
    stopRecordVideo(onSuccess?:CameraPreviewSuccessHandler, onError?:CameraPreviewErrorHandler):void;
    getMaxZoom(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    getSupportedFocusMode(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    getZoom(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    getHorizontalFOV(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    setPreviewSize(dimensions?: CameraPreviewPreviewSizeDimension|string, onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    setPreviewDimensions(dimensions?: CameraPreviewPreviewSizeDimension|string, onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    getSupportedPictureSizes(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    getSupportedFlashModes(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    getSupportedColorEffects(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    setFlashMode(flashMode: CameraPreviewFlashMode|string, onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    getSupportedFocusModes(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    getFocusMode(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    setFocusMode(focusMode?: CameraPreviewFocusMode|string, onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    tapToFocus(xPoint?: number, yPoint?: number, onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    getExposureModes(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    getExposureMode(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    setExposureMode(exposureMode?: CameraPreviewExposureMode, onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    getExposureCompensation(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    setExposureCompensation(exposureCompensation?: number, onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    getExposureCompensationRange(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    getSupportedWhiteBalanceModes(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    getSupportedWhiteBalanceMode(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    setWhiteBalanceMode(whiteBalanceMode?: CameraPreviewWhiteBalanceMode|string, onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    onBackButton(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    getBlob(path: string, onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
    getCameraCharacteristics(onSuccess?: CameraPreviewSuccessHandler, onError?: CameraPreviewErrorHandler): void;
  }
}
