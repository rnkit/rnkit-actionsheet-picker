//
//  KCDataPicker.m
//  ActionSheetPickerDemo
//
//  Created by Kevin on 16/3/17.
//  Copyright © 2016年 Kevin. All rights reserved.
//

#import "ActionSheetDataPicker.h"

@implementation ActionSheetDataPicker

+(instancetype)showPickerWithTitle:(NSString *)title initialSelection:(NSArray *)initialSelection doneBlock:(ActionDataDoneBlock)doneBlock cancelBlock:(ActionDataCancelBlock)cancelBlock didSelectBlock:(ActionDataDidSelectBlock)didSelectBlock origin:(UIView *)view
{
    ActionSheetDataPicker *picker = [[ActionSheetDataPicker alloc] initWithTitle:title initialSelection:initialSelection doneBlock:doneBlock cancelBlock:cancelBlock didSelectBlock:didSelectBlock origin:view];

    return picker;
}

-(instancetype)initWithTitle:(NSString *)title initialSelection:(NSArray *)initialSelection doneBlock:(ActionDataDoneBlock)doneBlock cancelBlock:(ActionDataCancelBlock)cancelBlock didSelectBlock:(ActionDataDidSelectBlock)didSelectBlock origin:(UIView *)view
{
    self = [self initWithTitle:title initialSelection:initialSelection target:nil action:nil origin:view cancelAction:nil];
    if (self) {
        self.onActionSheetDone = doneBlock;
        self.onActionSheetCancel = cancelBlock;
        self.onActionSheetDidSelect = didSelectBlock;
    }
    return self;
}

- (instancetype)initWithTitle:(NSString *)title initialSelection:(NSArray *)initialSelection target:(id)target action:(SEL)action origin:(id)origin cancelAction:(SEL)cancelAction
{
    self = [super initWithTarget:target successAction:action cancelAction:cancelAction origin:origin];
    if (self) {
        _defaultSelected = initialSelection;
        self.title = title;
    }
    return self;
}


#pragma mark - Private method

-(NSString *)getDefaultValue: (NSDictionary *)dictionary dept:(NSInteger)dept index:(NSInteger)index component:(NSInteger)component row:(NSInteger)row{
    NSEnumerator * enumeratorValue = [dictionary objectEnumerator];
    NSString *value = @"";
    if (dept == 0) {
        int i = 0;
        for (id obj in enumeratorValue) {
            if (i < index) {
                i++;
                continue;
            }
            if ([obj isKindOfClass:[NSString class]] || [obj isKindOfClass:[NSNumber class]]) {
                value = obj;
            }else if ([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *tmpDict = (NSDictionary *)obj;
                NSArray *tmpKeys = tmpDict.allKeys;
                if (tmpKeys.count > 0) {
                    value = tmpKeys[0];
                }
            }else if ([obj isKindOfClass:[NSArray class]]){
                NSArray *tmpKeys = (NSArray *)obj;
                if (tmpKeys.count > 0 && tmpKeys.count > index) {
                    value = tmpKeys[index];
                }
            }
            break;
        }
        return value;
    }

    dept -= 1;
    id obj = enumeratorValue.nextObject;
    if ([obj isKindOfClass:[NSString class]] || [obj isKindOfClass:[NSNumber class]]) {
        value = obj;
    }else if ([obj isKindOfClass:[NSDictionary class]]){
        NSDictionary *tmpDict = (NSDictionary *)obj;
        NSArray *tmpKeys = tmpDict.allKeys;
        NSString *key = @"";
        if (tmpKeys.count > 0) {
            key = tmpKeys[0];
        }
        NSDictionary *dictObj = [tmpDict objectForKey:key];
        value = [self getDefaultValue:dictObj dept:dept index:index component:component row:row];
    }


    return value;
}

-(NSInteger)getChildNumberOfRowsInComponent:(NSInteger)component dictionary:(NSDictionary *)dictionary dept:(NSInteger)dept
{
    NSInteger number = 0;
    NSEnumerator * enumeratorValue = [dictionary objectEnumerator];
    if (dept == 0) {
        id obj = enumeratorValue.nextObject;
        if ([obj isKindOfClass:[NSString class]] || [obj isKindOfClass:[NSNumber class]]) {
            number = 1;
        }else if ([obj isKindOfClass:[NSDictionary class]]){
            NSDictionary *tmpDict = (NSDictionary *)obj;
            NSArray *tmpKeys = tmpDict.allKeys;
            NSString *key = @"";
            if (tmpKeys.count > 0) {
                key = tmpKeys[0];
            }
            NSDictionary *dictObj = [tmpDict objectForKey:key];
            if ([dictObj isKindOfClass:[NSDictionary class]] || [dictObj isKindOfClass:[NSArray class]]) {
                number = [dictObj count];
            }
        }else if ([obj isKindOfClass:[NSArray class]]){
            NSArray *tmpKeys = (NSArray *)obj;
            number = tmpKeys.count;
        }
    }
    dept--;
    if (dept >= 0) {
        id obj = enumeratorValue.nextObject;
        if ([obj isKindOfClass:[NSString class]] || [obj isKindOfClass:[NSNumber class]]) {
            number = 1;
        }else if ([obj isKindOfClass:[NSDictionary class]]){
            NSDictionary *tmpDict = (NSDictionary *)obj;
            NSArray *tmpKeys = tmpDict.allKeys;
            NSString *key = @"";
            if (tmpKeys.count > 0) {
                key = tmpKeys[0];
            }
            NSDictionary *dictObj = [tmpDict objectForKey:key];
            if ([dictObj isKindOfClass:[NSDictionary class]] || [dictObj isKindOfClass:[NSArray class]]) {
                number = [self getChildNumberOfRowsInComponent:component dictionary:dictObj dept:dept];
            }
        }else if ([obj isKindOfClass:[NSArray class]]){
            NSArray *tmpKeys = (NSArray *)obj;
            number = tmpKeys.count;
        }
    }

    return number;
}

-(NSInteger)getDictionaryAndRowByValue:(id )value component:(NSInteger)component orgDictionary:(NSDictionary *)orgDictionary rowDictionary:(NSMutableDictionary **)rowDictionary
{
    NSInteger row = 0;
    if ([orgDictionary isKindOfClass:[NSArray class]]) {
        NSArray *array = (NSArray *)orgDictionary;
        for (int i = 0; i < array.count; i++) {
            id obj = [array objectAtIndex:i];
            if ([obj isKindOfClass:[NSString class]]) {
                if ([obj isEqual:value]) {
                    row = i;
                    *rowDictionary = nil;
                    break;
                }
            }else if ([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dict = (NSDictionary *)obj;
                NSArray *tmpKeys = dict.allKeys;
                NSString *key = @"";
                if (tmpKeys.count > 0) {
                    key = tmpKeys[0];
                }
                if ([key isEqual:value]) {
                    row = i;
                    NSMutableDictionary *childArray = [dict objectForKey:key];
                    *rowDictionary = childArray;
                    break;
                }
            }
        }
    }

    return row;
}

-(id)getDirectionaryInCurrentComponent:(NSInteger)currentComponent parentComponent:(NSInteger)parentComponent dictionary:(NSDictionary *)dictionary selectRow:(NSInteger)row
{
    id data = nil;
    if (currentComponent ==parentComponent) {
        if ([dictionary isKindOfClass:[NSString class]] || [dictionary isKindOfClass:[NSNumber class]]) {

        }else if ([dictionary isKindOfClass:[NSDictionary class]]){
            NSDictionary *tmpDict = (NSDictionary *)dictionary;
            NSArray *tmpKeys = tmpDict.allKeys;
            NSString *key = @"";
            if (tmpKeys.count > 0) {
                key = tmpKeys[0];
            }
            NSArray *dictObj = [tmpDict objectForKey:key];
            if ([dictObj isKindOfClass:[NSArray class]] && dictObj.count > row) {
                data = [dictObj objectAtIndex:row];
                [self setSelectedValue:data component:currentComponent];
            }
        }else if ([dictionary isKindOfClass:[NSArray class]]){
            NSArray *tmpKeys = (NSArray *)dictionary;
            if ([tmpKeys isKindOfClass:[NSArray class]] && tmpKeys.count > 0) {
                NSDictionary *firstDcit = [tmpKeys objectAtIndex:row];
                if ([firstDcit isKindOfClass:[NSDictionary class]] && firstDcit.count > 0) {
                    NSArray *tmpKeys = firstDcit.allKeys;
                    NSString *key = @"";
                    if (tmpKeys.count > 0) {
                        key = tmpKeys[0];
                    }
                    [_userSelected replaceObjectAtIndex:currentComponent withObject:key];
                    NSDictionary *dictObj = [firstDcit objectForKey:key];
                    if ([dictObj isKindOfClass:[NSDictionary class]] || [dictObj isKindOfClass:[NSArray class]]) {
                        data = dictObj;
                    }
                }else if ([firstDcit isKindOfClass:[NSString class]] || [firstDcit isKindOfClass:[NSNumber class]]){
                    data = firstDcit;
                    [_userSelected replaceObjectAtIndex:currentComponent withObject:firstDcit];
                }
            }
        }
    }
    NSInteger parentRow = [[_userSelectedComponentRow objectForKey:@(parentComponent)] integerValue];
    if (parentComponent < currentComponent) {
        parentComponent++;
        if ([dictionary isKindOfClass:[NSArray class]]) {
            NSArray *array = (NSArray *)dictionary;
            if (array.count > 0 && array.count > parentRow) {
                NSDictionary *dictObj = [array objectAtIndex:parentRow];
                data = [self getDirectionaryInCurrentComponent:currentComponent parentComponent:parentComponent dictionary:dictObj selectRow:row];
            }
        }else if ([dictionary isKindOfClass:[NSDictionary class]] && dictionary.count > 0){
            NSArray *tmpKeys = dictionary.allKeys;
            NSString *key = @"";
            if (tmpKeys.count > 0) {
                key = tmpKeys[0];
            }
            NSArray *array = [dictionary objectForKey:key];
            if ([array isKindOfClass:[NSArray class]] && array.count > parentRow) {
                NSDictionary *dictObj = [array objectAtIndex:parentRow];
                data = [self getDirectionaryInCurrentComponent:currentComponent parentComponent:parentComponent dictionary:dictObj selectRow:row];
            }
        }

    }

    return data;
}

-(void)reloadChildComponent:(UIPickerView *)pickerView dictionary:(NSDictionary *)dictionary reloadComponent:(NSInteger)reloadComponent
{
    NSMutableArray *array = [NSMutableArray array];
    if ([dictionary isKindOfClass:[NSString class]] || [dictionary isKindOfClass:[NSNumber class]]) {
        array = dictionary;
    }else if ([dictionary isKindOfClass:[NSDictionary class]]){
        NSDictionary *tmpDict = (NSDictionary *)dictionary;
        NSArray *tmpKeys = tmpDict.allKeys;
        NSString *key = @"";
        if (tmpKeys.count > 0) {
            key = tmpKeys[0];
        }
        array = [tmpDict objectForKey:key];
    }else if ([dictionary isKindOfClass:[NSArray class]]){
        array = (NSMutableArray *)dictionary;
    }
    if (reloadComponent < _numberOfComponents) {
        [_childShowDictionary setObject:array forKey:@(reloadComponent)];
        [pickerView reloadComponent:reloadComponent];
        [pickerView selectRow:0 inComponent:reloadComponent animated:YES];
        if ([array isKindOfClass:[NSString class]]) {
            [self setSelectedValue:@"" component:reloadComponent];
        }else{
            [self setSelectedValue:array component:reloadComponent];
        }

        reloadComponent++;
        if ([array isKindOfClass:[NSArray class]] && array.count > 0 && reloadComponent < _numberOfComponents) {
            NSDictionary *child = [array objectAtIndex:0];
            if ([child isKindOfClass:[NSString class]] || [child isKindOfClass:[NSNumber class]]) {
                [self reloadChildComponent:pickerView dictionary:child reloadComponent:reloadComponent];
            }else if ([child isKindOfClass:[NSDictionary class]]){
                NSEnumerator * enumeratorValue = [child objectEnumerator];
                id next = enumeratorValue.nextObject;
                [self reloadChildComponent:pickerView dictionary:next reloadComponent:reloadComponent];
            }
        }else if ((([array isKindOfClass:[NSArray class]] && [array count]==0) || [array isKindOfClass:[NSString class]]) && reloadComponent < _numberOfComponents){

            while (reloadComponent < _numberOfComponents) {
                [_childShowDictionary setObject:array forKey:@(reloadComponent)];
                [pickerView reloadComponent:reloadComponent];
                [self setSelectedValue:@"" component:reloadComponent];
                reloadComponent++;
            }
        }
    }
}

-(void)setSelectedValue:(id)value component:(NSInteger)component
{
    if ([value isKindOfClass:[NSString class]] || [value isKindOfClass:[NSNumber class]]) {
        [_userSelected replaceObjectAtIndex:component withObject:value];
    }else if ([value isKindOfClass:[NSDictionary class]]){
        NSDictionary *firstDict = (NSDictionary *)value;
        NSArray *tmpKeys = firstDict.allKeys;
        NSString *key = @"";
        if (tmpKeys.count > 0) {
            key = tmpKeys[0];
        }
        [_userSelected replaceObjectAtIndex:component withObject:key];
    }else if ([value isKindOfClass:[NSArray class]]){
        NSArray *array = (NSArray *)value;
        if ([array count] > 0) {
            NSDictionary *firstDict = [array objectAtIndex:0];
            if ([firstDict isKindOfClass:[NSDictionary class]]) {
                NSArray *tmpKeys = firstDict.allKeys;
                NSString *key = @"";
                if (tmpKeys.count > 0) {
                    key = tmpKeys[0];
                }
                [_userSelected replaceObjectAtIndex:component withObject:key];
            }else if ([firstDict isKindOfClass:[NSArray class]]){

            }else if ([firstDict isKindOfClass:[NSString class]]){
                [_userSelected replaceObjectAtIndex:component withObject:firstDict];
            }
        }else{
            [_userSelected replaceObjectAtIndex:component withObject:@""];
        }
    }

}


#pragma mark - AbstractActionSheetPicker fulfilment

- (UIView *)configuredPickerView
{
    _userSelectedComponentRow = [NSMutableDictionary dictionary];
    _childShowDictionary = [NSMutableDictionary dictionary];
    _userSelected = [NSMutableArray array];
    CGRect pickerFrame = CGRectMake(0, 40, self.viewSize.width, 216);
    UIPickerView *pv = [[UIPickerView alloc] initWithFrame:pickerFrame];
    self.pickerView = pv;
    pv.delegate = self;
    pv.dataSource = self;
    pv.showsSelectionIndicator = YES;

    for (int i = 0; i < _numberOfComponents; i++) {
        NSString *defaultStr = [self getDefaultValue:_dataSource dept:i index:0 component:i row:0];
        [_userSelected addObject:defaultStr];
        [_userSelectedComponentRow setObject:@(0) forKey:@(i)];
    }
    if (_defaultSelected!=nil && [_defaultSelected isKindOfClass:[NSArray class]] && _defaultSelected.count > 0) {
        NSDictionary *orgDict = nil;
        NSMutableDictionary *rowDict = nil;
        NSInteger index = 0;
        while (index < _numberOfComponents) {
            [_userSelected replaceObjectAtIndex:index withObject:@""];
            index++;
        }
        for (NSUInteger i = 0; i < [_defaultSelected count]; i++){
            NSString *defaultStr = [_defaultSelected objectAtIndex:i];
            [_userSelected replaceObjectAtIndex:i withObject:defaultStr];
            if (orgDict == nil) {
                orgDict = _dataSource;
            }
            NSInteger row = [self getDictionaryAndRowByValue:defaultStr component:i orgDictionary:orgDict rowDictionary:&rowDict];
            if ([rowDict isKindOfClass:[NSDictionary class]] || [rowDict isKindOfClass:[NSArray class]]) {
                orgDict = rowDict;
            }
            if (rowDict != nil) {
                [_childShowDictionary setObject:rowDict forKey:@(i+1)];
            }
            [_userSelectedComponentRow setObject:@(row) forKey:@(i)];
            [pv selectRow:row inComponent:i animated:YES];
            [pv reloadAllComponents];
        }
    } else {
        NSDictionary *orgDict = nil;
        NSMutableDictionary *rowDict = nil;
        NSInteger index = 0;
        while (index < _numberOfComponents) {
            [_userSelected replaceObjectAtIndex:index withObject:@""];
            index++;
        }
        for (NSUInteger i = 0; i < [_defaultSelected count]; i++){
            NSString *defaultStr = [_defaultSelected objectAtIndex:i];
            [_userSelected replaceObjectAtIndex:i withObject:defaultStr];
            if (orgDict == nil) {
                orgDict = _dataSource;
            }
            NSInteger row = [self getDictionaryAndRowByValue:defaultStr component:i orgDictionary:orgDict rowDictionary:&rowDict];
            if ([rowDict isKindOfClass:[NSDictionary class]] || [rowDict isKindOfClass:[NSArray class]]) {
                orgDict = rowDict;
            }
            if (rowDict != nil) {
                [_childShowDictionary setObject:rowDict forKey:@(i+1)];
            }
            [_userSelectedComponentRow setObject:@(row) forKey:@(i)];
            [pv selectRow:row inComponent:i animated:YES];
            [pv reloadAllComponents];
        }
    }
    return pv;
}

- (void)notifyTarget:(id)target didSucceedWithAction:(SEL)action origin:(id)origin
{
    if (self.onActionSheetDone)
    {
        self.onActionSheetDone(self, _userSelected, origin);
        return;
    }
}

- (void)notifyTarget:(id)target didCancelWithAction:(SEL)cancelAction origin:(id)origin
{
    if (self.onActionSheetCancel)
    {
        self.onActionSheetCancel(self);
        return;
    }
}

-(NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    if (_numberOfComponents < 0) {
        _numberOfComponents = 0;
    }
    return _numberOfComponents;
}

-(NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    NSInteger number = 0;
    if (component == 0) {
        number = [_dataSource count];
    }else{
        if (_childShowDictionary.count <= 0) {
            number = [self getChildNumberOfRowsInComponent:component dictionary:_dataSource dept:component -1];
        }else{
            NSArray *array = [_childShowDictionary objectForKey:@(component)];
            if ([array isKindOfClass:[NSArray class]]) {
                number = array.count;
            }
        }
    }

    return number;
}

- (UIView *)pickerView:(UIPickerView *)pickerView viewForRow:(NSInteger)row forComponent:(NSInteger)component reusingView:(UIView *)view {
    UILabel *pickerLabel = (UILabel *)view;
    if (pickerLabel == nil) {
        CGRect frame = CGRectZero;
        CGFloat totalWidth = pickerView.frame.size.width - 20;
        CGFloat otherSize = (totalWidth )/(self.numberOfComponents);
        frame = CGRectMake(0.0, 0.0, otherSize, 32);

        pickerLabel = [[UILabel alloc] initWithFrame:frame];
        [pickerLabel setTextAlignment:NSTextAlignmentCenter];
        if ([pickerLabel respondsToSelector:@selector(setMinimumScaleFactor:)])
            [pickerLabel setMinimumScaleFactor:0.5];
        [pickerLabel setAdjustsFontSizeToFitWidth:YES];
        [pickerLabel setBackgroundColor:[UIColor clearColor]];
        [pickerLabel setFont:[UIFont systemFontOfSize:20]];
    }

    NSString *text = @"";
    NSArray *array = [_childShowDictionary objectForKey:@(component)];
    if (_childShowDictionary.count <= 0 || component == 0 || array == nil) {
        text = [self getDefaultValue:_dataSource dept:component index:row component:component row:row];
    }else{
        if (array.count > row) {
            NSString *value = [array objectAtIndex:row];
            if ([value isKindOfClass:[NSString class]]) {
                text = value;
            }else if ([value isKindOfClass:[NSDictionary class]]){
                NSDictionary *tmpDict = (NSDictionary *)value;
                if (tmpDict.count > 0) {
                    NSArray *keysrr = tmpDict.allKeys;
                    if ([keysrr isKindOfClass:[NSArray class]] && keysrr.count > 0) {
                        text = [keysrr objectAtIndex:0];
                    }
                }
            }
        }
    }
    if ([text isKindOfClass:[NSString class]] && text.length > 0) {
        pickerLabel.text = text;
    }

    return pickerLabel;
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    id data = [self getDirectionaryInCurrentComponent:component parentComponent:0 dictionary:_dataSource selectRow:row];
    [self reloadChildComponent:pickerView dictionary:data reloadComponent:component+1];
    [_userSelectedComponentRow setObject:@(row) forKey:@(component)];

    if (self.onActionSheetDidSelect)
    {
        self.onActionSheetDidSelect(self, _userSelected);
        return;
    }
}

@end
