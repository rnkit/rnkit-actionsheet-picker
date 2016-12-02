/* @flow */

/**
  * dataPicker.js
  *
  * @author SimMan (liwei0990#gmail.com)
  * @Time at 2016-12-01 15:04:33
  * Copyright 2011-2016 RNKit.io, Inc.
  */
'use strict';

import {
  NativeModules,
  processColor,
  Platform,
  NativeAppEventEmitter
} from 'react-native';

const { RNKitASDataPicker } = NativeModules;

const dataPickerDefaultArgs = {
  titleText: '',
  titleTextColor: '#393939',
  doneText: '确定',
  doneTextColor: '#269ff7',
  cancelText: '取消',
  cancelTextColor: '#269ff7',
  numberOfComponents: 1,
};

let nativeAppEventEmitter;

let DataPicker = {
  show: (args) => {
    const options = {...dataPickerDefaultArgs, ...args,};
    try {
      RNKitASDataPicker.showWithArgs({
        ...options,
        titleTextColor: processColor(options.titleTextColor),
        doneTextColor: processColor(options.doneTextColor),
        cancelTextColor: processColor(options.cancelTextColor),
      }, (resp) => {
        nativeAppEventEmitter && nativeAppEventEmitter.remove();
        if (resp.type === 'done') {
          options.onPickerConfirm && options.onPickerConfirm(
            Platform.OS ==='android' ? resp.selectedData.reverse() : resp.selectedData,
            Platform.OS ==='android' ? resp.selectedIndex.reverse() : resp.selectedIndex
          );
        } else {
          options.onPickerCancel && options.onPickerCancel();
        }
      });
      nativeAppEventEmitter && nativeAppEventEmitter.remove();
      nativeAppEventEmitter = NativeAppEventEmitter.addListener('PickerEvent', event => {
        options.onPickerDidSelect && options.onPickerDidSelect(event.selectedData, event.selectedIndex);
      });
    }
    catch (e) {
      console.log(e);
      nativeAppEventEmitter && nativeAppEventEmitter.remove();
      options.onPickerCancel && options.onPickerCancel();
      return;
    }
  }
};

export default DataPicker;
