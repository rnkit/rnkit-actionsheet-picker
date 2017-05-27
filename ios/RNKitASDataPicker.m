//
//  RNKitASDataPicker.m
//  RNKitASDataPicker
//
//  Created by SimMan on 2016/11/29.
//  Copyright © 2016年 RNKit.io. All rights reserved.
//

#import <Foundation/Foundation.h>

#if __has_include(<React/RCTBridge.h>)
#import <React/RCTEventEmitter.h>
#import <React/RCTUtils.h>
#import <React/RCTConvert.h>
#import <React/RCTEventDispatcher.h>
#else
#import "RCTEventEmitter.h"
#import "RCTUtils.h"
#import "RCTConvert.h"
#import "RCTEventDispatcher.h"
#endif

#import "RNKitASDataPicker.h"
#import "ActionSheetDataPicker.h"

@implementation RNKitASDataPicker
{
    NSHashTable *_dataPickers;
    RCTResponseSenderBlock _callback;
}

@synthesize bridge = _bridge;

RCT_EXPORT_MODULE()

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"DataPickerEvent"];
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_METHOD(showWithArgs:(NSDictionary *)args callback:(RCTResponseSenderBlock)callback)
{
    UIViewController *presentingController = RCTPresentedViewController();
    if (presentingController == nil) {
        RCTLogError(@"Tried to display action sheet picker view but there is no application window.");
        return;
    }

    NSString *titleText                 = [RCTConvert NSString:args[@"titleText"]];
    UIColor *titleTextColor             = [RCTConvert UIColor:args[@"titleTextColor"]];
    NSString *doneText                  = [RCTConvert NSString:args[@"doneText"]];
    UIColor *doneTextColor              = [RCTConvert UIColor:args[@"doneTextColor"]];
    NSString *cancelText                = [RCTConvert NSString:args[@"cancelText"]];
    UIColor *cancelTextColor            = [RCTConvert UIColor:args[@"cancelTextColor"]];
    NSUInteger numberOfComponents       = [RCTConvert NSUInteger:args[@"numberOfComponents"]];
    NSArray *dataSource                 = [RCTConvert NSArray:args[@"dataSource"]];
    NSArray *defaultSelected            = [RCTConvert NSArray:args[@"defaultSelected"]];

    if (!numberOfComponents) {
        RCTLogError(@"numberOfComponents is require.");
        return;
    }

    if (defaultSelected.count && numberOfComponents != defaultSelected.count) {
        RCTLogError(@"numberOfComponents is not equal to defaultSelected count.");
        return;
    }



    _callback = callback;

    // set button
    UIBarButtonItem *doneButton = [[UIBarButtonItem alloc] initWithTitle:doneText style:UIBarButtonItemStyleDone target:nil action:nil];
    if (doneTextColor) {
        [doneButton setTitleTextAttributes:@{NSForegroundColorAttributeName: doneTextColor } forState:UIControlStateNormal];
    }

    UIBarButtonItem *cancelButton = [[UIBarButtonItem alloc] initWithTitle:cancelText style:UIBarButtonItemStyleDone target:nil action:nil];
    if (cancelTextColor) {
        [cancelButton setTitleTextAttributes:@{NSForegroundColorAttributeName: cancelTextColor } forState:UIControlStateNormal];
    }

    __weak __typeof(self) weakSelf = self;
    ActionSheetDataPicker *picker = [[ActionSheetDataPicker alloc] initWithTitle:titleText initialSelection:nil doneBlock:^(ActionSheetDataPicker *picker, NSArray *selectedData, id origin) {

        __typeof(self) strongSelf = weakSelf;
        if (!strongSelf) {
            return;
        }

        NSArray *selectedIndex = [strongSelf getSelctedIndex:picker selectedData:selectedData];
        callback(@[@{@"type": @"done", @"selectedData": selectedData, @"selectedIndex": selectedIndex}]);

    } cancelBlock:^(ActionSheetDataPicker *picker) {
        callback(@[@{@"type": @"cancel"}]);
        NSLog(@"Cancel ...");
    } didSelectBlock:^(ActionSheetDataPicker *picker, id selectedData) {

        __typeof(self) strongSelf = weakSelf;
        if (!strongSelf) {
            return;
        }

        NSArray *selectedIndex = [strongSelf getSelctedIndex:picker selectedData:selectedData];

        [strongSelf sendEventWithName:@"DataPickerEvent" body:@{@"selectedData": selectedData, @"selectedIndex": selectedIndex}];

    } origin:presentingController.view];

    if (titleTextColor) {
        picker.titleTextAttributes = @{ NSForegroundColorAttributeName : titleTextColor };
    }

    [picker setDoneButton:doneButton];
    [picker setCancelButton:cancelButton];
    picker.tapDismissAction = TapActionCancel;

    if (defaultSelected) {
        picker.defaultSelected = defaultSelected;
    } else {
        NSMutableArray *dv = [NSMutableArray arrayWithCapacity:numberOfComponents];

        if (numberOfComponents == 1) {
            NSString *str = dataSource[0];
            [dv addObject:str];
        }

        if (numberOfComponents == 2) {
            NSDictionary *dic = dataSource[0];
            NSString *item1 = dic.allKeys.firstObject;
            [dv addObject:item1];
            NSString *item2 = dic[item1][0];
            [dv addObject: item2];
        }

        if (numberOfComponents == 3) {
            NSDictionary *dic = dataSource[0];
            NSString *item1 = dic.allKeys.firstObject;
            [dv addObject:item1];

            NSArray *arr = [dic objectForKey:item1];
            NSDictionary *d = arr[0];
            NSString *item2 = d.allKeys.firstObject;
            [dv addObject: item2];

            NSArray *item3 = [d objectForKey:item2][0];
            [dv addObject: item3];
        }
        picker.defaultSelected = dv;
    }

    picker.dataSource = dataSource;
    picker.tapDismissAction = TapActionCancel;
    picker.numberOfComponents = numberOfComponents;


    [picker showActionSheetPicker];

    [[self getDataPickers] addObject:picker];
}

- (NSArray *) getSelctedIndex:(ActionSheetDataPicker *)picker selectedData:(NSArray *)selectedData
{
    NSInteger dataCount = selectedData.count;
    NSMutableArray *selectedIndexArray = [NSMutableArray arrayWithCapacity:dataCount];

    __block NSInteger count = 0;
    [picker.userSelectedComponentRow enumerateKeysAndObjectsUsingBlock:^(id  _Nonnull key, id  _Nonnull obj, BOOL * _Nonnull stop) {
        if (dataCount == count) {
            stop = YES;
        } else {
            [selectedIndexArray addObject:obj];
        }
        count ++;
    }];

    return selectedIndexArray;
}

- (NSHashTable *) getDataPickers
{
    if (!_dataPickers) {
        _dataPickers = [NSHashTable weakObjectsHashTable];
    }
    return _dataPickers;
}

- (void) dealloc
{
    for (ActionSheetDataPicker *picker in [self getDataPickers]) {
        if ([picker respondsToSelector:@selector(hidePickerWithCancelAction)]) {
            [picker hidePickerWithCancelAction];
        }
    }
}

@end
