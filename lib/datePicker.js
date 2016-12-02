/* @flow */

/**
  * datePicker.js
  *
  * @author SimMan (liwei0990#gmail.com)
  * @Time at 2016-12-01 15:04:33
  * Copyright 2011-2016 RNKit.io, Inc.
  */
'use strict';

import {
  NativeModules,
  processColor,
  NativeAppEventEmitter
} from 'react-native';

const { RNKitASDatePicker } = NativeModules;

const datePickerDefaultArgs = {
  titleText: '选择时间',
  titleTextColor: '#393939',
  doneText: '确定',
  doneTextColor: '#269ff7',
  cancelText: '取消',
  cancelTextColor: '#269ff7',
  minimumDate: '1900-01-01 00:00:00',
  maximumDate: '2222-12-12 23:59:59',
  datePickerMode: 'datetime'
};

let nativeAppEventEmitter;

let DatePicker = {
  show: (args) => {
    const options = {...datePickerDefaultArgs, ...args,};
    try {
      RNKitASDatePicker.showWithArgs({
        ...options,
        titleTextColor: processColor(options.titleTextColor),
        doneTextColor: processColor(options.doneTextColor),
        cancelTextColor: processColor(options.cancelTextColor),
      }, (resp) => {
        nativeAppEventEmitter && nativeAppEventEmitter.remove();
        if (resp.type === 'done') {
          options.onPickerConfirm && options.onPickerConfirm(resp.selectedDate);
        } else {
          options.onPickerCancel && options.onPickerCancel();
        }
      });
      nativeAppEventEmitter && nativeAppEventEmitter.remove();
      nativeAppEventEmitter = NativeAppEventEmitter.addListener('DatePickerEvent', event => {
        options.onPickerDidSelect && options.onPickerDidSelect(event.selectedDate);
      });
    }
    catch (e) {
      nativeAppEventEmitter && nativeAppEventEmitter.remove();
      options.onPickerCancel && options.onPickerCancel();
      return;
    }
  }
};

export default DatePicker;
