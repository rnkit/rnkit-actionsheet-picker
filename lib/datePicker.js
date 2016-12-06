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
  NativeEventEmitter
} from 'react-native';

const { RNKitASDatePicker } = NativeModules;
const nativeEventEmitter = new NativeEventEmitter(RNKitASDatePicker);

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
        if (resp.type === 'done') {
          options.onPickerConfirm && options.onPickerConfirm(resp.selectedDate);
        } else {
          options.onPickerCancel && options.onPickerCancel();
        }
      });
      this.listener && this.listener.remove();
      this.listener = nativeEventEmitter.addListener('DatePickerEvent', event => {
        options.onPickerDidSelect && options.onPickerDidSelect(event.selectedDate);
      });
    }
    catch (e) {
      this.listener && this.listener.remove();
      options.onPickerCancel && options.onPickerCancel();
      return;
    }
  }
};

export default DatePicker;
