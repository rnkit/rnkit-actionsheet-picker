//
//  RNKitASDatePicker.m
//  RNKitASDatePicker
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

#import "RNKitASDatePicker.h"
#import "ActionSheetDatePicker.h"

@implementation RCTConvert (RNKitASDatePickerModeDate)

RCT_ENUM_CONVERTER(UIDatePickerMode, (@{
    @"time": @(UIDatePickerModeTime),
    @"date": @(UIDatePickerModeDate),
    @"datetime": @(UIDatePickerModeDateAndTime),
    @"countdown": @(UIDatePickerModeCountDownTimer), // not supported yet
}), UIDatePickerModeDate, integerValue)

@end

@implementation RNKitASDatePicker
{
    NSHashTable *_datePickers;
    RCTResponseSenderBlock _callback;
}

@synthesize bridge = _bridge;


RCT_EXPORT_MODULE()

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"DatePickerEvent"];
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_METHOD(showWithArgs:(NSDictionary *)args callback:(RCTResponseSenderBlock)callback)
{
    // ---
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

    NSDate *selectedDate                = [self getDateFromString:[RCTConvert NSString:args[@"selectedDate"]]];
    NSDate *minimumDate                 = [self getDateFromString:[RCTConvert NSString:args[@"minimumDate"]]];
    NSDate *maximumDate                 = [self getDateFromString:[RCTConvert NSString:args[@"maximumDate"]]];

    UIDatePickerMode datePickerMode    = [RCTConvert UIDatePickerMode:args[@"datePickerMode"]];

    _callback = callback;

    // default value
    selectedDate    = selectedDate ? selectedDate : [NSDate new];
//    datePickerMode  = datePickerMode ? datePickerMode : UIDatePickerModeDate;

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
    ActionSheetDatePicker *picker = [[ActionSheetDatePicker alloc]
                                     initWithTitle:titleText
                                     datePickerMode:datePickerMode
                                     selectedDate:selectedDate
                                     doneBlock:^(ActionSheetDatePicker *picker, id selectedDate, id origin) {
                                         __typeof(self) strongSelf = weakSelf;
                                         if (!strongSelf) {
                                             return;
                                         }
                                         NSString *selectedDateString = [strongSelf getStringFromDate:selectedDate withDatePickerMode:datePickerMode];

                                         callback(@[@{@"type": @"done", @"selectedDate": selectedDateString}]);
    } cancelBlock:^(ActionSheetDatePicker *picker) {
        callback(@[@{@"type": @"cancel"}]);
    } origin:presentingController.view];

    if (titleTextColor) {
        picker.titleTextAttributes = @{ NSForegroundColorAttributeName : titleTextColor };
    }

    [picker setDoneButton:doneButton];
    [picker setCancelButton:cancelButton];
    picker.tapDismissAction = TapActionCancel;

    if (minimumDate) {
        picker.minimumDate = minimumDate;
    }

    if (maximumDate) {
        picker.maximumDate = maximumDate;
    }

    [picker showActionSheetPicker];

    if (picker.pickerView) {
        UIDatePicker *datePicker = (UIDatePicker *)picker.pickerView;
        [datePicker addTarget:self action:@selector(eventForDatePicker:) forControlEvents:UIControlEventValueChanged];
    }

    [[self getDatePickers] addObject:picker];
}


- (NSDate *) getDateFromString:(NSString *)json
{
    if (!json) {
        return nil;
    }
    static NSDateFormatter *formatter;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        formatter = [NSDateFormatter new];
        formatter.dateFormat = @"yyyy-MM-dd HH:mm:ss";
    });

    NSDate *date = [formatter dateFromString:json];
    return date;
}

- (void)eventForDatePicker:(id)sender
{
    if (!sender || ![sender isKindOfClass:[UIDatePicker class]])
        return;
    UIDatePicker *datePicker = (UIDatePicker *)sender;

    NSString *selectedDateString = [self getStringFromDate:datePicker.date withDatePickerMode:datePicker.datePickerMode];

    [self sendEventWithName:@"DatePickerEvent" body:@{@"selectedDate": selectedDateString}];
}

- (NSString *) getStringFromDate: (NSDate *)date withDatePickerMode: (UIDatePickerMode) mode
{
    if (!date) {
        return @"";
    }

    static NSDateFormatter *formatter;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        formatter = [NSDateFormatter new];

        if (mode == UIDatePickerModeDate) {

        }
        switch (mode) {
            case UIDatePickerModeDate: {
                formatter.dateFormat = @"yyyy-MM-dd";
            }
                break;
            case UIDatePickerModeTime: {
                formatter.dateFormat = @"HH:mm:ss";
            }
                break;
            case UIDatePickerModeDateAndTime: {
                formatter.dateFormat = @"yyyy-MM-dd HH:mm:ss";
            }
                break;
            default:
                formatter.dateFormat = @"yyyy-MM-dd HH:mm:ss";
                break;
        }

    });

    NSString *dateString = [formatter stringFromDate:date];

    return dateString;
}

- (NSHashTable *) getDatePickers
{
    if (!_datePickers) {
        _datePickers = [NSHashTable weakObjectsHashTable];
    }
    return _datePickers;
}

- (void) dealloc
{
    for (ActionSheetDatePicker *picker in [self getDatePickers]) {
        if ([picker respondsToSelector:@selector(hidePickerWithCancelAction)]) {
            [picker hidePickerWithCancelAction];
        }
    }
}

@end
