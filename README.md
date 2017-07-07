[![npm][npm-badge]][npm]
[![react-native][rn-badge]][rn]
[![MIT][license-badge]][license]
[![bitHound Score][bithound-badge]][bithound]
[![Downloads](https://img.shields.io/npm/dm/rnkit-actionsheet-picker.svg)](https://www.npmjs.com/package/rnkit-actionsheet-picker)

The best DatePicker and DataPicker for [React Native][rn].

[**Support me with a Follow**](https://github.com/simman/followers)


![](https://raw.githubusercontent.com/rnkit/rnkit-actionsheet-picker/master/android.gif)
![](https://github.com/rnkit/rnkit-actionsheet-picker/blob/master/ios.gif?raw=true)

[npm-badge]: https://img.shields.io/npm/v/rnkit-actionsheet-picker.svg
[npm]: https://www.npmjs.com/package/rnkit-actionsheet-picker
[rn-badge]: https://img.shields.io/badge/react--native-v0.28-05A5D1.svg
[rn]: https://facebook.github.io/react-native
[license-badge]: https://img.shields.io/dub/l/vibe-d.svg
[license]: https://raw.githubusercontent.com/rnkit/rnkit-actionsheet-picker/master/LICENSE
[bithound-badge]: https://www.bithound.io/github/rnkit/rnkit-actionsheet-picker/badges/score.svg
[bithound]: https://www.bithound.io/github/rnkit/rnkit-actionsheet-picker

## Getting Started

First, `cd` to your RN project directory, and install RNMK through [rnpm](https://github.com/rnpm/rnpm) . If you don't have rnpm, you can install RNMK from npm with the command `npm i -S rnkit-actionsheet-picker` and link it manually (see below).

### iOS

* ####React Native < 0.29 (Using rnpm)

  `rnpm install rnkit-actionsheet-picker`

* ####React Native >= 0.29
  `$npm install -S rnkit-actionsheet-picker`

  `$react-native link rnkit-actionsheet-picker`



#### Manually
1. Add `node_modules/rnkit-actionsheet-picker/ios/RNKitASPickerView.xcodeproj` to your xcode project, usually under the `Libraries` group
1. Add `libRNKitASPickerView.a` (from `Products` under `RNKitASPickerView.xcodeproj`) to build target's `Linked Frameworks and Libraries` list



### Android

* ####React Native < 0.29 (Using rnpm)

  `rnpm install rnkit-actionsheet-picker`

* ####React Native >= 0.29
  `$npm install -S rnkit-actionsheet-picker`

  `$react-native link rnkit-actionsheet-picker`

#### Manually
1. JDK 7+ is required
1. Add the following snippet to your `android/settings.gradle`:
  ```gradle
include ':rnkit-actionsheet-picker'
project(':rnkit-actionsheet-picker').projectDir = new File(rootProject.projectDir, '../node_modules/rnkit-actionsheet-picker/android/app')

  ```
1. Declare the dependency in your `android/app/build.gradle`
  ```gradle
  dependencies {
      ...
      compile project(':rnkit-actionsheet-picker')
  }

  ```
1. Import `import io.rnkit.actionsheetpicker.ASPickerViewPackage;` and register it in your `MainActivity` (or equivalent, RN >= 0.32 MainApplication.java):

  ```java
  @Override
  protected List<ReactPackage> getPackages() {
      return Arrays.asList(
              new MainReactPackage(),
              new ASPickerViewPackage()
      );
  }
  ```

Finally, you're good to go, feel free to require `rnkit-actionsheet-picker` in your JS files.

Have fun! :metal:

## Basic Usage

Import library

```
import { DatePicker, DataPicker } from 'rnkit-actionsheet-picker';
```

### DatePicker

```jsx
DatePicker.show({
      onPickerConfirm: (selectedDate) => {
        console.log(selectedDate);
        this.setState({
          date: selectedDate
        })
      },
      onPickerCancel: () => {
        console.log('date picker canceled');
      },
      onPickerDidSelect: (selectedDate) => {
        console.log(selectedDate);
      }
    })
    
```

#### Params

| Key | Type | Default | Description |
| --- | --- | --- | --- |
| titleText | string | '选择时间' | |
| titleTextColor | string | '#393939' |  |
| doneText | string | '确定' | |
| doneTextColor | string | '#269ff7' |  |
| cancelText | string | '选择时间' | |
| cancelTextColor | string | '#269ff7' |  |
| minimumDate | string | '1900-01-01 00:00:00' |  |
| maximumDate | string | '2222-12-12 23:59:59' |  |
| selectedDate | string |  |  |
| datePickerMode | string | 'datetime' | time、date、datetime |
| onPickerConfirm | Fun |  | return selectedDate |
| onPickerCancel | Fun |  |  |
| onPickerDidSelect | Fun |  | iOS only |

### DataPicker

```jsx
DataPicker.show({
      // dataSource: ["男", "女"],
      // dataSource: [{"北京": ["123123", "ssssss"]}, {"广东省": ["深圳"]}],
      // dataSource: [{"北京": [{"北京x": ["123123", "ssssss"]}, {"北京xasdfasdf": ["123123", "ssssss"]}]},{"广东省": [{"深圳": ["福田区", "宝安区"]}]}],
      dataSource: require('./area.json'),
      defaultSelected: ["广东", '深圳市', '福田区'],
      numberOfComponents: 3,
      onPickerConfirm: (selectedData, selectedIndex) => {
        console.log(selectedData, selectedIndex);
        this.setState({
          data: JSON.stringify(selectedData) + ' -- ' + JSON.stringify(selectedIndex)
        })
      },
      onPickerCancel: () => {
        console.log('data picker canceled');
      },
      onPickerDidSelect: (selectedData, selectedIndex) => {
        console.log(selectedData, selectedIndex);
      }
    })

```

#### Params

| Key | Type | Default | Description |
| --- | --- | --- | --- |
| titleText | string | '选择时间' | |
| titleTextColor | string | '#393939' |  |
| doneText | string | '确定' | |
| doneTextColor | string | '#269ff7' |  |
| cancelText | string | '选择时间' | |
| cancelTextColor | string | '#269ff7' |  |
| yearText | string | '年' | android only |
| monthText | string | '月' | android only |
| dayText | string | '日' | android only |
| hoursText | string | '时' | android only |
| minutesText | string | '分' | android only |
| secondsText | string | '秒' | android only |
| wheelBgColor | string |  | '滚轮背景颜色' android only |
| titleBgColor | string |  | '标题栏背景颜色'android only |
| doneCancelSize | int | 17 | '确定取消按钮大小'android only |
| titleSize | int | 18 | '标题文字大小'android only |
| contentSize | int | 18 | '内容文字大小'android only |
| cancelEnabel | boolean | true | '是否能取消'android only |
| isCenterLable | boolean | true | '是否只显示中间的label'android only |
| outTextColor | string |  | '分割线以外的文字颜色'android only |
| centerTextColor | string |  | '分割线之间的文字颜色'android only |
| dividerColor | string |  | '分割线的颜色'android only |
| shadeBgColor | string |  | '遮罩层背景色'android only |
| lineSpacingMultiplier | float | 1.6f | '条目间距倍数'android only |
| dividerType | string |  | '分割线类型：fill or wrap'android only |
| isCyclic | boolean | false | '滚轮是否循环' android only |
| numberOfComponents | int | 1 |  |
| dataSource | array |  |  |
| defaultSelected | array |  |  |
| onPickerConfirm | Fun |  | return selectedData、selectedIndex |
| onPickerCancel | Fun |  |  |
| onPickerDidSelect | Fun |  | selectedData、selectedIndex （iOS only） |

## Contribution

- [@simamn](mailto:liwei0990@gmail.com) The main author.

## Thanks

[@saiwu-bigkoo](https://github.com/saiwu-bigkoo) - [Android-PickerView](https://github.com/saiwu-bigkoo/Android-PickerView) 仿iOS的PickerView控件，有时间选择和选项选择并支持一二三级联动效果
[@skywinder](https://github.com/skywinder) - [ActionSheetPicker-3.0](https://github.com/skywinder/ActionSheetPicker-3.0) Quickly reproduce the dropdown UIPickerView / ActionSheet functionality on iOS.

## Questions

Feel free to [contact me](mailto:liwei0990@gmail.com) or [create an issue](https://github.com/rnkit/rnkit-actionsheet-picker/issues/new)

> made with ♥
