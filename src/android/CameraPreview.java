package com.cordovaplugincamerapreview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.hardware.Camera;
import android.os.Handler;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SizeF;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.List;
import java.util.Arrays;

public class CameraPreview extends CordovaPlugin implements CameraActivity.CameraPreviewListener {

  private static final String TAG = "CameraPreview";

  private static final String COLOR_EFFECT_ACTION = "setColorEffect";
  private static final String SUPPORTED_COLOR_EFFECTS_ACTION = "getSupportedColorEffects";
  private static final String ZOOM_ACTION = "setZoom";
  private static final String GET_ZOOM_ACTION = "getZoom";
  private static final String GET_HFOV_ACTION = "getHorizontalFOV";
  private static final String GET_MAX_ZOOM_ACTION = "getMaxZoom";
  private static final String SUPPORTED_FLASH_MODES_ACTION = "getSupportedFlashModes";
  private static final String GET_FLASH_MODE_ACTION = "getFlashMode";
  private static final String SET_FLASH_MODE_ACTION = "setFlashMode";
  private static final String START_CAMERA_ACTION = "startCamera";
  private static final String STOP_CAMERA_ACTION = "stopCamera";
  private static final String PREVIEW_SIZE_ACTION = "setPreviewSize";
  private static final String PREVIEW_DIMENSIONS_ACTION = "setPreviewDimensions";
  private static final String SWITCH_CAMERA_ACTION = "switchCamera";
  private static final String TAKE_PICTURE_ACTION = "takePicture";
  private static final String START_RECORD_VIDEO_ACTION = "startRecordVideo";
  private static final String STOP_RECORD_VIDEO_ACTION = "stopRecordVideo";
  private static final String TAKE_SNAPSHOT_ACTION = "takeSnapshot";
  private static final String SHOW_CAMERA_ACTION = "showCamera";
  private static final String HIDE_CAMERA_ACTION = "hideCamera";
  private static final String TAP_TO_FOCUS = "tapToFocus";
  private static final String SUPPORTED_PICTURE_SIZES_ACTION = "getSupportedPictureSizes";
  private static final String SUPPORTED_FOCUS_MODES_ACTION = "getSupportedFocusModes";
  private static final String SUPPORTED_WHITE_BALANCE_MODES_ACTION = "getSupportedWhiteBalanceModes";
  private static final String GET_FOCUS_MODE_ACTION = "getFocusMode";
  private static final String SET_FOCUS_MODE_ACTION = "setFocusMode";
  private static final String GET_EXPOSURE_MODES_ACTION = "getExposureModes";
  private static final String GET_EXPOSURE_MODE_ACTION = "getExposureMode";
  private static final String SET_EXPOSURE_MODE_ACTION = "setExposureMode";
  private static final String GET_EXPOSURE_COMPENSATION_ACTION = "getExposureCompensation";
  private static final String SET_EXPOSURE_COMPENSATION_ACTION = "setExposureCompensation";
  private static final String GET_EXPOSURE_COMPENSATION_RANGE_ACTION = "getExposureCompensationRange";
  private static final String GET_WHITE_BALANCE_MODE_ACTION = "getWhiteBalanceMode";
  private static final String SET_WHITE_BALANCE_MODE_ACTION = "setWhiteBalanceMode";
  private static final String SET_BACK_BUTTON_CALLBACK = "onBackButton";
  private static final String GET_CAMERA_CHARACTERISTICS_ACTION = "getCameraCharacteristics";

  private static final int CAM_REQ_CODE = 0;
  private static final int VID_REQ_CODE = 1;
  private String VIDEO_FILE_PATH = "";
  private static final String VIDEO_FILE_EXTENSION = ".mp4";

  private static final String [] permissions = {
    Manifest.permission.CAMERA
  };

  private static final String [] videoPermissions = {
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
  };

  private CameraActivity fragment;
  private CallbackContext takePictureCallbackContext;
  private CallbackContext takeSnapshotCallbackContext;
  private CallbackContext startRecordVideoCallbackContext;
  private CallbackContext stopRecordVideoCallbackContext;
  private CallbackContext setFocusCallbackContext;
  private CallbackContext startCameraCallbackContext;
  private CallbackContext tapBackButtonContext  = null;

  private CallbackContext execCallback;
  private JSONArray execArgs;

  private ViewParent webViewParent;

  private int containerViewId = 20; //<- set to random number to prevent conflict with other plugins
  public CameraPreview(){
    super();
    Log.d(TAG, "Constructing");
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

    if (START_CAMERA_ACTION.equals(action)) {
      if (cordova.hasPermission(permissions[0])) {
        return startCamera(args.getInt(0), args.getInt(1), args.getInt(2), args.getInt(3), args.getString(4), args.getBoolean(5), args.getBoolean(6), args.getBoolean(7), args.getString(8), args.getBoolean(9), args.getBoolean(10), args.getBoolean(11), callbackContext);
      } else {
        this.execCallback = callbackContext;
        this.execArgs = args;
        cordova.requestPermissions(this, CAM_REQ_CODE, permissions);
        return true;
      }
    } else if (TAKE_PICTURE_ACTION.equals(action)) {
      return takePicture(args.getInt(0), args.getInt(1), args.getInt(2), callbackContext);
    } else if (TAKE_SNAPSHOT_ACTION.equals(action)) {
      return takeSnapshot(args.getInt(0), callbackContext);
    }else if (START_RECORD_VIDEO_ACTION.equals(action)) {
      if ( cordova.hasPermission(videoPermissions[0]) && cordova.hasPermission(videoPermissions[1])) {
        return startRecordVideo(args.getString(0), args.getInt(1), args.getInt(2), args.getInt(3), args.getBoolean(4), callbackContext);
      } else {
        this.execCallback = callbackContext;
        this.execArgs = args;
        cordova.requestPermissions(this, VID_REQ_CODE, videoPermissions);
        return true;
      }
    } else if (STOP_RECORD_VIDEO_ACTION.equals(action)) {
      return stopRecordVideo(callbackContext);
    } else if (COLOR_EFFECT_ACTION.equals(action)) {
      return setColorEffect(args.getString(0), callbackContext);
    } else if (ZOOM_ACTION.equals(action)) {
      return setZoom(args.getInt(0), callbackContext);
    } else if (GET_ZOOM_ACTION.equals(action)) {
      return getZoom(callbackContext);
    } else if (GET_HFOV_ACTION.equals(action)) {
      return getHorizontalFOV(callbackContext);
    } else if (GET_MAX_ZOOM_ACTION.equals(action)) {
      return getMaxZoom(callbackContext);
    } else if (PREVIEW_SIZE_ACTION.equals(action)) {
      return setPreviewSize(args.getInt(0), args.getInt(1), callbackContext);
    } else if (PREVIEW_DIMENSIONS_ACTION.equals(action)) {
      return setPreviewDimensions(args.getInt(0), args.getInt(1), args.getInt(2), args.getInt(3), callbackContext);
    } else if (SUPPORTED_FLASH_MODES_ACTION.equals(action)) {
      return getSupportedFlashModes(callbackContext);
    } else if (GET_FLASH_MODE_ACTION.equals(action)) {
      return getFlashMode(callbackContext);
    } else if (SET_FLASH_MODE_ACTION.equals(action)) {
      return setFlashMode(args.getString(0), callbackContext);
    } else if (STOP_CAMERA_ACTION.equals(action)){
      return stopCamera(callbackContext);
    } else if (SHOW_CAMERA_ACTION.equals(action)) {
      return showCamera(callbackContext);
    } else if (HIDE_CAMERA_ACTION.equals(action)) {
      return hideCamera(callbackContext);
    } else if (TAP_TO_FOCUS.equals(action)) {
      return tapToFocus(args.getInt(0), args.getInt(1), callbackContext);
    } else if (SWITCH_CAMERA_ACTION.equals(action)) {
      return switchCamera(callbackContext);
    } else if (SUPPORTED_PICTURE_SIZES_ACTION.equals(action)) {
      return getSupportedPictureSizes(callbackContext);
    } else if (GET_EXPOSURE_MODES_ACTION.equals(action)) {
      return getExposureModes(callbackContext);
    } else if (SUPPORTED_FOCUS_MODES_ACTION.equals(action)) {
      return getSupportedFocusModes(callbackContext);
    } else if (GET_FOCUS_MODE_ACTION.equals(action)) {
      return getFocusMode(callbackContext);
    } else if (SET_FOCUS_MODE_ACTION.equals(action)) {
      return setFocusMode(args.getString(0), callbackContext);
    } else if (GET_EXPOSURE_MODE_ACTION.equals(action)) {
      return getExposureMode(callbackContext);
    } else if (SET_EXPOSURE_MODE_ACTION.equals(action)) {
      return setExposureMode(args.getString(0), callbackContext);
    } else if (GET_EXPOSURE_COMPENSATION_ACTION.equals(action)) {
      return getExposureCompensation(callbackContext);
    } else if (SET_EXPOSURE_COMPENSATION_ACTION.equals(action)) {
      return setExposureCompensation(args.getInt(0), callbackContext);
    } else if (GET_EXPOSURE_COMPENSATION_RANGE_ACTION.equals(action)) {
      return getExposureCompensationRange(callbackContext);
    } else if (SUPPORTED_WHITE_BALANCE_MODES_ACTION.equals(action)) {
      return getSupportedWhiteBalanceModes(callbackContext);
    } else if (GET_WHITE_BALANCE_MODE_ACTION.equals(action)) {
      return getWhiteBalanceMode(callbackContext);
    } else if (SET_WHITE_BALANCE_MODE_ACTION.equals(action)) {
      return setWhiteBalanceMode(args.getString(0),callbackContext);
    } else if (SET_BACK_BUTTON_CALLBACK.equals(action)) {
      return setBackButtonListener(callbackContext);
    } else if (SUPPORTED_COLOR_EFFECTS_ACTION.equals(action)) {
      return getSupportedColorEffects(callbackContext);
    } else if (GET_CAMERA_CHARACTERISTICS_ACTION.equals(action)) {
      return getCameraCharacteristics(callbackContext);
    }

    return false;
  }

  @Override
  public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
    for(int r:grantResults){
      if(r == PackageManager.PERMISSION_DENIED){
        execCallback.sendPluginResult(new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION));
        return;
      }
    }

    if(requestCode == CAM_REQ_CODE){
      startCamera(this.execArgs.getInt(0), this.execArgs.getInt(1), this.execArgs.getInt(2), this.execArgs.getInt(3), this.execArgs.getString(4), this.execArgs.getBoolean(5), this.execArgs.getBoolean(6), this.execArgs.getBoolean(7), this.execArgs.getString(8), this.execArgs.getBoolean(9), this.execArgs.getBoolean(10), this.execArgs.getBoolean(11), this.execCallback);
    }else if(requestCode == VID_REQ_CODE){
      startRecordVideo(this.execArgs.getString(0), this.execArgs.getInt(1), this.execArgs.getInt(2), this.execArgs.getInt(3), this.execArgs.getBoolean(4),  this.execCallback);
    }
  }

  private boolean hasView(CallbackContext callbackContext) {
    if(fragment == null) {
      callbackContext.error("No preview");
      return false;
    }

    return true;
  }

  private boolean hasCamera(CallbackContext callbackContext) {
    if(this.hasView(callbackContext) == false){
      return false;
    }

    if(fragment.getCamera() == null) {
      callbackContext.error("No Camera");
      return false;
    }

    return true;
  }

  private boolean getSupportedPictureSizes(CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    List<Camera.Size> supportedSizes;
    Camera camera = fragment.getCamera();
    supportedSizes = camera.getParameters().getSupportedPictureSizes();
    if (supportedSizes != null) {
      JSONArray sizes = new JSONArray();
      for (int i=0; i<supportedSizes.size(); i++) {
        Camera.Size size = supportedSizes.get(i);
        int h = size.height;
        int w = size.width;
        JSONObject jsonSize = new JSONObject();
        try {
          jsonSize.put("height", new Integer(h));
          jsonSize.put("width", new Integer(w));
        }
        catch(JSONException e){
          e.printStackTrace();
        }
        sizes.put(jsonSize);
      }
      callbackContext.success(sizes);
      return true;
    }
    callbackContext.error("Camera Parameters access error");
    return true;
  }

  private boolean startCamera(int x, int y, int width, int height, String defaultCamera, Boolean tapToTakePicture, Boolean dragEnabled, final Boolean toBack, String alpha, boolean tapFocus, boolean disableExifHeaderStripping, boolean storeToFile, CallbackContext callbackContext) {
    Log.d(TAG, "start camera action");

    if (fragment != null) {
      callbackContext.error("Camera already started");
      return true;
    }

    final float opacity = Float.parseFloat(alpha);

    fragment = new CameraActivity();
    fragment.setEventListener(this);
    fragment.defaultCamera = defaultCamera;
    fragment.tapToTakePicture = tapToTakePicture;
    fragment.dragEnabled = dragEnabled;
    fragment.tapToFocus = tapFocus;
    fragment.disableExifHeaderStripping = disableExifHeaderStripping;
    fragment.storeToFile = storeToFile;
    fragment.toBack = toBack;

    DisplayMetrics metrics = cordova.getActivity().getResources().getDisplayMetrics();

    // offset
    int computedX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x, metrics);
    int computedY = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, y, metrics);

    // size
    int computedWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, metrics);
    int computedHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, metrics);

    fragment.setRect(computedX, computedY, computedWidth, computedHeight);

    startCameraCallbackContext = callbackContext;

    cordova.getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {

        //create or update the layout params for the container view
        FrameLayout containerView = (FrameLayout)cordova.getActivity().findViewById(containerViewId);
        if(containerView == null){
          containerView = new FrameLayout(cordova.getActivity().getApplicationContext());
          containerView.setId(containerViewId);

          FrameLayout.LayoutParams containerLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
          cordova.getActivity().addContentView(containerView, containerLayoutParams);
        }

        //display camera below the webview
        if(toBack){
          View view = webView.getView();
          ViewParent rootParent = containerView.getParent();
          ViewParent curParent = view.getParent();

          view.setBackgroundColor(0x00000000);

          // If parents do not match look for.
          if(curParent.getParent() != rootParent) {
            while(curParent != null && curParent.getParent() != rootParent) {
              curParent = curParent.getParent();
            }

            if(curParent != null) {
              ((ViewGroup)curParent).setBackgroundColor(0x00000000);
              ((ViewGroup)curParent).bringToFront();
            } else {
              // Do default...
              curParent = view.getParent();
              webViewParent = curParent;
              ((ViewGroup)view).bringToFront();
            }
          }else{
            // Default
            webViewParent = curParent;
            ((ViewGroup)curParent).bringToFront();
          }

        }else{
          //set camera back to front
          containerView.setAlpha(opacity);
          containerView.bringToFront();
        }

        //add the fragment to the container
        FragmentManager fragmentManager = cordova.getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(containerView.getId(), fragment);
        fragmentTransaction.commit();
      }
    });

    return true;
  }

  public void onCameraStarted() {
    Log.d(TAG, "Camera started");

    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "Camera started");
    pluginResult.setKeepCallback(false);
    startCameraCallbackContext.sendPluginResult(pluginResult);
  }

  private boolean takeSnapshot(int quality, CallbackContext callbackContext) {
    if(this.hasView(callbackContext) == false){
      return true;
    }

    takeSnapshotCallbackContext = callbackContext;

    fragment.takeSnapshot(quality);
    return true;
  }

  public void onSnapshotTaken(String originalPicture) {
    Log.d(TAG, "returning snapshot");

    JSONArray data = new JSONArray();
    data.put(originalPicture);

    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, data);
    pluginResult.setKeepCallback(true);
    takeSnapshotCallbackContext.sendPluginResult(pluginResult);
    takeSnapshotCallbackContext = null;
  }

  public void onSnapshotTakenError(String message) {
    Log.d(TAG, "CameraPreview onSnapshotTakenError");
    takeSnapshotCallbackContext.error(message);
    takeSnapshotCallbackContext = null;
  }

  private boolean takePicture(int width, int height, int quality, CallbackContext callbackContext) {
    if(this.hasView(callbackContext) == false){
      return true;
    }
    takePictureCallbackContext = callbackContext;

    fragment.takePicture(width, height, quality);

    return true;
  }

  public void onPictureTaken(String originalPicture) {
    Log.d(TAG, "returning picture");

    JSONArray data = new JSONArray();
    data.put(originalPicture);

    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, data);
    pluginResult.setKeepCallback(fragment.tapToTakePicture);
    takePictureCallbackContext.sendPluginResult(pluginResult);
  }

  public void onPictureTakenError(String message) {
    Log.d(TAG, "CameraPreview onPictureTakenError");
    takePictureCallbackContext.error(message);
  }

  private boolean startRecordVideo(final String camera, final int width, final int height, final int quality, final boolean withFlash, CallbackContext callbackContext) {
    if(this.hasView(callbackContext) == false){
      return true;
    }
    final String filename = "videoTmp";
    VIDEO_FILE_PATH = cordova.getActivity().getCacheDir().toString() + "/";
    startRecordVideoCallbackContext = callbackContext;
     cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        fragment.startRecord(getFilePath(filename), camera, width, height, quality, withFlash);
      }
    });

    return true;
  }

  public void onStartRecordVideo() {
    Log.d(TAG, "onStartRecordVideo started");

    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
    pluginResult.setKeepCallback(true);

    startRecordVideoCallbackContext.sendPluginResult(pluginResult);
  }

  public void onStartRecordVideoError(String message) {
    Log.d(TAG, "CameraPreview onStartRecordVideo");

    startRecordVideoCallbackContext.error(message);
  }

  private boolean stopRecordVideo(CallbackContext callbackContext) {
    if(this.hasView(callbackContext) == false){
      return true;
    }

    stopRecordVideoCallbackContext = callbackContext;

    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        fragment.stopRecord();
      }
    });

    return true;
  }

  public void onStopRecordVideo(String file) {
    Log.d(TAG, "onStopRecordVideo success");

    PluginResult result = new PluginResult(PluginResult.Status.OK, file);
    result.setKeepCallback(true);

    stopRecordVideoCallbackContext.sendPluginResult(result);
  }

  public void onStopRecordVideoError(String err) {
    Log.d(TAG, "onStopRecordVideo error");

    stopRecordVideoCallbackContext.error(err);
  }

  private String getFilePath(String filename) {
    String fileName = filename;

    int i = 1;

    while (new File(VIDEO_FILE_PATH + fileName + VIDEO_FILE_EXTENSION).exists()) {
      // Add number suffix if file exists
      fileName = filename + '_' + i;
      i++;
    }

    return VIDEO_FILE_PATH + fileName + VIDEO_FILE_EXTENSION;
  }

  private boolean setColorEffect(String effect, CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    List<String> supportedColors;
    supportedColors = params.getSupportedColorEffects();

    if(supportedColors.contains(effect)){
      params.setColorEffect(effect);
      fragment.setCameraParameters(params);
      callbackContext.success(effect);
    }else{
      callbackContext.error("Color effect not supported" + effect);
      return true;
    }
    return true;
  }

  private boolean getSupportedColorEffects(CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();
    List<String> supportedColors;
    supportedColors = params.getSupportedColorEffects();
    JSONArray jsonColorEffects = new JSONArray();

    if (supportedColors != null) {
      for (int i=0; i<supportedColors.size(); i++) {
          jsonColorEffects.put(new String(supportedColors.get(i)));
      }
    }

    callbackContext.success(jsonColorEffects);

    return true;
  }

  private boolean getExposureModes(CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    if (camera.getParameters().isAutoExposureLockSupported()) {
      JSONArray jsonExposureModes = new JSONArray();
      jsonExposureModes.put(new String("lock"));
      jsonExposureModes.put(new String("continuous"));
      callbackContext.success(jsonExposureModes);
    } else {
      callbackContext.error("Exposure modes not supported");
    }

    return true;
  }

  private boolean getExposureMode(CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    String exposureMode;

    if (camera.getParameters().isAutoExposureLockSupported()) {
      if (camera.getParameters().getAutoExposureLock()) {
        exposureMode = "lock";
      } else {
        exposureMode = "continuous";
      };
      callbackContext.success(exposureMode);
    } else {
      callbackContext.error("Exposure mode not supported");
    }

    return true;
  }

  private boolean setExposureMode(String exposureMode, CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    if (camera.getParameters().isAutoExposureLockSupported()) {
      params.setAutoExposureLock("lock".equals(exposureMode));
      fragment.setCameraParameters(params);
      callbackContext.success();
    } else {
      callbackContext.error("Exposure mode not supported");
    }

    return true;
  }

  private boolean getExposureCompensation(CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    if (camera.getParameters().getMinExposureCompensation() == 0 && camera.getParameters().getMaxExposureCompensation() == 0) {
      callbackContext.error("Exposure corection not supported");
    } else {
      int exposureCompensation = camera.getParameters().getExposureCompensation();
      callbackContext.success(exposureCompensation);
    }

    return true;
  }

  private boolean setExposureCompensation(int exposureCompensation, CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    int minExposureCompensation = camera.getParameters().getMinExposureCompensation();
    int maxExposureCompensation = camera.getParameters().getMaxExposureCompensation();

    if ( minExposureCompensation == 0 && maxExposureCompensation == 0) {
      callbackContext.error("Exposure corection not supported");
    } else {
      if (exposureCompensation < minExposureCompensation) {
        exposureCompensation = minExposureCompensation;
      } else if (exposureCompensation > maxExposureCompensation) {
        exposureCompensation = maxExposureCompensation;
      }
      params.setExposureCompensation(exposureCompensation);
      fragment.setCameraParameters(params);

      callbackContext.success(exposureCompensation);
    }

    return true;
  }

  private boolean getExposureCompensationRange(CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    int minExposureCompensation = camera.getParameters().getMinExposureCompensation();
    int maxExposureCompensation = camera.getParameters().getMaxExposureCompensation();

    if (minExposureCompensation == 0 && maxExposureCompensation == 0) {
      callbackContext.error("Exposure corection not supported");
    } else {
      JSONObject jsonExposureRange = new JSONObject();
      try {
        jsonExposureRange.put("min", new Integer(minExposureCompensation));
        jsonExposureRange.put("max", new Integer(maxExposureCompensation));
      }
      catch(JSONException e){
        e.printStackTrace();
      }
      callbackContext.success(jsonExposureRange);
    }

    return true;
  }

  private boolean getSupportedWhiteBalanceModes(CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    List<String> supportedWhiteBalanceModes;
    supportedWhiteBalanceModes = params.getSupportedWhiteBalance();

    JSONArray jsonWhiteBalanceModes = new JSONArray();
    if (camera.getParameters().isAutoWhiteBalanceLockSupported()) {
      jsonWhiteBalanceModes.put(new String("lock"));
    }
    if (supportedWhiteBalanceModes != null) {
      for (int i=0; i<supportedWhiteBalanceModes.size(); i++) {
        jsonWhiteBalanceModes.put(new String(supportedWhiteBalanceModes.get(i)));
      }
    }

    callbackContext.success(jsonWhiteBalanceModes);
    return true;
  }

  private boolean getWhiteBalanceMode(CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    String whiteBalanceMode;

    if (camera.getParameters().isAutoWhiteBalanceLockSupported()) {
      if (camera.getParameters().getAutoWhiteBalanceLock()) {
        whiteBalanceMode = "lock";
      } else {
        whiteBalanceMode = camera.getParameters().getWhiteBalance();
      };
    } else {
      whiteBalanceMode = camera.getParameters().getWhiteBalance();
    }
    if (whiteBalanceMode != null) {
      callbackContext.success(whiteBalanceMode);
    } else {
      callbackContext.error("White balance mode not supported");
    }

    return true;
  }

  private boolean setWhiteBalanceMode(String whiteBalanceMode, CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    if (whiteBalanceMode.equals("lock")) {
      if (camera.getParameters().isAutoWhiteBalanceLockSupported()) {
        params.setAutoWhiteBalanceLock(true);
        fragment.setCameraParameters(params);
        callbackContext.success();
      } else {
        callbackContext.error("White balance lock not supported");
      }
    } else if (whiteBalanceMode.equals("auto") ||
               whiteBalanceMode.equals("incandescent") ||
               whiteBalanceMode.equals("cloudy-daylight") ||
               whiteBalanceMode.equals("daylight") ||
               whiteBalanceMode.equals("fluorescent") ||
               whiteBalanceMode.equals("shade") ||
               whiteBalanceMode.equals("twilight") ||
               whiteBalanceMode.equals("warm-fluorescent")) {
      params.setWhiteBalance(whiteBalanceMode);
      fragment.setCameraParameters(params);
      callbackContext.success();
    } else {
      callbackContext.error("White balance parameter not supported");
    }

    return true;
  }

  private boolean getMaxZoom(CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    if (camera.getParameters().isZoomSupported()) {
      int maxZoom = camera.getParameters().getMaxZoom();
      callbackContext.success(maxZoom);
    } else {
      callbackContext.error("Zoom not supported");
    }

    return true;
  }

 private boolean getHorizontalFOV(CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    float horizontalViewAngle = params.getHorizontalViewAngle();

    callbackContext.success(String.valueOf(horizontalViewAngle));
    return true;
  }


  private boolean getZoom(CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    if (camera.getParameters().isZoomSupported()) {
      int getZoom = camera.getParameters().getZoom();
      callbackContext.success(getZoom);
    } else {
      callbackContext.error("Zoom not supported");
    }

    return true;
  }

  private boolean setZoom(int zoom, CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    if (camera.getParameters().isZoomSupported()) {
      params.setZoom(zoom);
      fragment.setCameraParameters(params);

      callbackContext.success(zoom);
    } else {
      callbackContext.error("Zoom not supported");
    }

    return true;
  }

  private boolean setPreviewSize(int width, int height, CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    params.setPreviewSize(width, height);
    fragment.setCameraParameters(params);
    camera.startPreview();

    callbackContext.success();
    return true;
  }

  private boolean setPreviewDimensions(int x, int y, int width, int height, CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    params.setPreviewSize(width, height);
    fragment.setCameraParameters(params);
    camera.startPreview();

    callbackContext.success();
    return true;
  }

  private boolean getSupportedFlashModes(CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();
    List<String> supportedFlashModes;
    supportedFlashModes = params.getSupportedFlashModes();
    JSONArray jsonFlashModes = new JSONArray();

    if (supportedFlashModes != null) {
      for (int i=0; i<supportedFlashModes.size(); i++) {
          jsonFlashModes.put(new String(supportedFlashModes.get(i)));
      }
    }

    callbackContext.success(jsonFlashModes);
    return true;
  }

  private boolean getSupportedFocusModes(CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();
    List<String> supportedFocusModes;
    supportedFocusModes = params.getSupportedFocusModes();

    if (supportedFocusModes != null) {
      JSONArray jsonFocusModes = new JSONArray();
      for (int i=0; i<supportedFocusModes.size(); i++) {
          jsonFocusModes.put(new String(supportedFocusModes.get(i)));
      }

      callbackContext.success(jsonFocusModes);
      return true;
    }

    callbackContext.error("Camera focus modes parameters access error");
    return true;
  }

  private boolean getFocusMode(CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    List<String> supportedFocusModes;
    supportedFocusModes = params.getSupportedFocusModes();

    if (supportedFocusModes != null) {
      String focusMode = params.getFocusMode();
      callbackContext.success(focusMode);
    } else {
      callbackContext.error("FocusMode not supported");
    }

    return true;
  }

  private boolean setFocusMode(String focusMode, CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    List<String> supportedFocusModes;
    List<String> supportedAutoFocusModes = Arrays.asList("auto", "continuous-picture", "continuous-video","macro");
    supportedFocusModes = params.getSupportedFocusModes();
    if (supportedFocusModes.indexOf(focusMode) > -1) {
      params.setFocusMode(focusMode);
      fragment.setCameraParameters(params);
      callbackContext.success(focusMode);
      return true;
    } else {
      callbackContext.error("Focus mode not recognised: " + focusMode);
      return true;
    }
  }

  private boolean getFlashMode(CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    String flashMode = params.getFlashMode();

    if (flashMode != null ) {
      callbackContext.success(flashMode);
    } else {
      callbackContext.error("FlashMode not supported");
    }

    return true;
  }

  private boolean setFlashMode(String flashMode, CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    Camera camera = fragment.getCamera();
    Camera.Parameters params = camera.getParameters();

    List<String> supportedFlashModes;
    supportedFlashModes = camera.getParameters().getSupportedFlashModes();
    if (supportedFlashModes.indexOf(flashMode) > -1) {
      params.setFlashMode(flashMode);
    } else {
      callbackContext.error("Flash mode not recognised: " + flashMode);
      return true;
    }

    fragment.setCameraParameters(params);

    callbackContext.success(flashMode);
    return true;
  }

  private boolean stopCamera(CallbackContext callbackContext) {
    if(webViewParent != null) {
      cordova.getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          ((ViewGroup)webView.getView()).bringToFront();
          webViewParent = null;
        }
      });
    }

    if(this.hasView(callbackContext) == false){
      return true;
    }

    FragmentManager fragmentManager = cordova.getActivity().getFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.remove(fragment);
    fragmentTransaction.commit();
    fragment = null;

    callbackContext.success();
    return true;
  }

  private boolean showCamera(CallbackContext callbackContext) {
    if(this.hasView(callbackContext) == false){
      return true;
    }

    FragmentManager fragmentManager = cordova.getActivity().getFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.show(fragment);
    fragmentTransaction.commit();

    callbackContext.success();
    return true;
  }

  private boolean hideCamera(CallbackContext callbackContext) {
    if(this.hasView(callbackContext) == false){
      return true;
    }

    FragmentManager fragmentManager = cordova.getActivity().getFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.hide(fragment);
    fragmentTransaction.commit();

    callbackContext.success();
    return true;
  }

  private boolean tapToFocus(final int pointX, final int pointY, CallbackContext callbackContext) {
    if(this.hasView(callbackContext) == false){
      return true;
    }

    setFocusCallbackContext = callbackContext;

    fragment.setFocusArea(pointX, pointY, new Camera.AutoFocusCallback() {
      public void onAutoFocus(boolean success, Camera camera) {
        if (success) {
          onFocusSet(pointX, pointY);
        } else {
          onFocusSetError("fragment.setFocusArea() failed");
        }
      }
    });

    return true;
  }

  public void onFocusSet(final int pointX, final int pointY) {
    Log.d(TAG, "Focus set, returning coordinates");

    JSONObject data = new JSONObject();
    try {
      data.put("x", pointX);
      data.put("y", pointY);
    } catch (JSONException e) {
      Log.d(TAG, "onFocusSet failed to set output payload");
    }

    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, data);
    pluginResult.setKeepCallback(true);
    setFocusCallbackContext.sendPluginResult(pluginResult);
  }

  public void onFocusSetError(String message) {
    Log.d(TAG, "CameraPreview onFocusSetError");
    setFocusCallbackContext.error(message);
  }

  private boolean switchCamera(CallbackContext callbackContext) {
    if(this.hasView(callbackContext) == false){
      return true;
    }

    fragment.switchCamera();

    callbackContext.success();

    return true;
  }

  public boolean setBackButtonListener(CallbackContext callbackContext) {
    tapBackButtonContext = callbackContext;
    return true;
  }

  public void onBackButton() {
    if(tapBackButtonContext == null) {
      return;
    }

    Log.d(TAG, "Back button tapped, notifying");

    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "Back button pressed");
    tapBackButtonContext.sendPluginResult(pluginResult);
  }

  private boolean getCameraCharacteristics(CallbackContext callbackContext) {
    if(this.hasCamera(callbackContext) == false){
      return true;
    }

    JSONObject data = new JSONObject();
    JSONArray cameraCharacteristicsArray = new JSONArray();

    // Get the CameraManager
    CameraManager cManager = (CameraManager) this.cordova.getActivity().getApplicationContext().getSystemService(Context.CAMERA_SERVICE);

    try {
      for (String cameraId : cManager.getCameraIdList()) {
        CameraCharacteristics characteristics = cManager.getCameraCharacteristics(cameraId);

	JSONObject cameraData = new JSONObject();

	// INFO_SUPPORTED_HARDWARE_LEVEL
	Integer supportLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
	cameraData.put("INFO_SUPPORTED_HARDWARE_LEVEL", supportLevel);

	// LENS_FACING
	Integer lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
	cameraData.put("LENS_FACING", lensFacing);

	// SENSOR_INFO_PHYSICAL_SIZE
	SizeF sensorInfoPhysicalSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
	cameraData.put("SENSOR_INFO_PHYSICAL_SIZE_WIDTH", new Double(sensorInfoPhysicalSize.getWidth()));
	cameraData.put("SENSOR_INFO_PHYSICAL_SIZE_HEIGHT", new Double(sensorInfoPhysicalSize.getHeight()));

	// SENSOR_INFO_PIXEL_ARRAY_SIZE
	Size sensorInfoPixelSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
	cameraData.put("SENSOR_INFO_PIXEL_ARRAY_SIZE_WIDTH", new Integer(sensorInfoPixelSize.getWidth()));
	cameraData.put("SENSOR_INFO_PIXEL_ARRAY_SIZE_HEIGHT", new Integer(sensorInfoPixelSize.getHeight()));

	// LENS_INFO_AVAILABLE_FOCAL_LENGTHS
	float[] focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
	JSONArray focalLengthsArray = new JSONArray();
	for (int focusId=0; focusId<focalLengths.length; focusId++) {
	  JSONObject focalLengthsData = new JSONObject();
	  focalLengthsData.put("FOCAL_LENGTH", new Double(focalLengths[focusId]));
	  focalLengthsArray.put(focalLengthsData);
	}
	cameraData.put("LENS_INFO_AVAILABLE_FOCAL_LENGTHS", focalLengthsArray);

	// add camera data to result list
	cameraCharacteristicsArray.put(cameraData);
      }

      data.put("CAMERA_CHARACTERISTICS", cameraCharacteristicsArray);

    } catch (CameraAccessException e) {
      Log.e(TAG, e.getMessage(), e);
    } catch (JSONException e) {
      Log.d(TAG, "getCameraSensorInfo failed to set output payload");
    }

    callbackContext.success(data);
    return true;
  }

}
