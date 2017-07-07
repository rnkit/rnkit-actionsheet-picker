package io.rnkit.actionsheetpicker;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.rnkit.actionsheetpicker.view.DatePickerView;

/**
 * Created by SimMan on 2016/11/30.
 */

public class ASDatePickerViewModule extends ReactContextBaseJavaModule implements Application.ActivityLifecycleCallbacks {

    private static final String REACT_CLASS = "RNKitASDatePicker";
    private static final String ERROR_NO_ACTIVITY = "E_NO_ACTIVITY";
    final ReactApplicationContext reactContext;
    private static Boolean isCallBack = false;

    private TimePickerView.Builder pickerBuilder;

    /* package */ static final String TITLE_TEXT = "titleText";
    /* package */ static final String TITLE_TEXT_COLOR = "titleTextColor";
    /* package */ static final String DONE_TEXT = "doneText";
    /* package */ static final String DONE_TEXT_COLOR = "doneTextColor";
    /* package */ static final String CANCEL_TEXT = "cancelText";
    /* package */ static final String CANCEL_TEXT_COLOR = "cancelTextColor";

    /* package */ static final String WHEEL_BG_COLOR = "wheelBgColor";
    /* package */ static final String TITLE_BG_COLOR = "titleBgColor";
    /* package */ static final String DONE_CANCEL_SIZE = "doneCancelSize";
    /* package */ static final String TITLE_SIZE = "titleSize";
    /* package */ static final String CONTENT_SIZE = "contentSize";
    /* package */ static final String CANCEL_ENABLE = "cancelEnabel";
    /* package */ static final String IS_CENTER_LABEL = "isCenterLable";
    /* package */ static final String OUT_TEXT_COLOR = "outTextColor";
    /* package */ static final String CENTER_TEXT_COLOR = "centerTextColor";
    /* package */ static final String DIVIDER_COLOR = "dividerColor";
    /* package */ static final String SHADE_BG_COLOR = "shadeBgColor";
    /* package */ static final String LINE_SPACING_MULTIPLIER = "lineSpacingMultiplier";
    /* package */ static final String DIVIDER_TYPE = "dividerType";
    /* package */ static final String IS_CYCLIC = "isCyclic";

    /* package */ static final String YEAR_TEXT = "yearText";
    /* package */ static final String MONTH_TEXT = "monthText";
    /* package */ static final String DAY_TEXT = "dayText";
    /* package */ static final String HOURS_TEXT = "hoursText";
    /* package */ static final String MINUTES_TEXT = "minutesText";
    /* package */ static final String SECONDS_TEXT = "secondsText";

    /* package */ static final String SELECTED_DATE = "selectedDate";
    /* package */ static final String MINIMUM_DATE = "minimumDate";
    /* package */ static final String MAXIMUM_DATE = "maximumDate";
    /* package */ static final String DATEPICKER_MODE = "datePickerMode";

    public ASDatePickerViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactMethod
    public void showWithArgs(@Nullable final ReadableMap options, @Nullable final Callback callback) {
        isCallBack = false;
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Activity activity = getCurrentActivity();
                if (activity == null) {
                    throw new JSApplicationIllegalArgumentException("Tried to open a Picker dialog while not attached to an Activity");
                }
                final String datePickerMode = options.getString(DATEPICKER_MODE);
                pickerBuilder = new TimePickerView.Builder(activity, new DatePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        isCallBack = true;
                        SimpleDateFormat format = null;
                        if (datePickerMode==null){
                            format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                        } else if (datePickerMode.equals("date")) {
                            format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                        } else if (datePickerMode.equals("time")) {
                            format = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
                        } else if (datePickerMode.equals("dateTime")) {
                            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                        }

                        format.setTimeZone(TimeZone.getTimeZone("GMT+08"));

                        WritableMap map = Arguments.createMap();
                        map.putString("type", "done");
                        map.putString("selectedDate", format.format(date));

                        callback.invoke(map);
                    }
                });
                Bundle args = createFragmentArguments(options);
                pickerBuilder.setLabel(args.getString(YEAR_TEXT),args.getString(MONTH_TEXT),args.getString(DAY_TEXT)
                                ,args.getString(HOURS_TEXT),args.getString(MINUTES_TEXT),args.getString(SECONDS_TEXT));
                pickerBuilder.setType(getDatePickerType(datePickerMode));

                DatePickerView pvTime = new DatePickerView(pickerBuilder);



                pvTime.setOnTimeCancelListener(new DatePickerView.OnTimeCancelListener() {
                    @Override
                    public void onCancel() {
                        isCallBack = true;
                        WritableMap map = Arguments.createMap();
                        map.putString("type", "cancel");
                        callback.invoke(map);
                    }
                });

                pvTime.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(Object o) {
                        if (!isCallBack) {
                            isCallBack = true;
                            WritableMap map = Arguments.createMap();
                            map.putString("type", "cancel");
                            callback.invoke(map);
                        }
                    }
                });

                pvTime.show();
            }
        });
    }

    private boolean[] getDatePickerType(String type) {
        if (type==null)
            return new boolean[]{true,true,true,true,true,true};
        else if (type.equals("time")) {
            return new boolean[]{false,false,false,true,true,true};
        }else if (type.equals("date")) {
            return new boolean[]{true,true,true,false,false,false};
        }else if (type.equals("datetime")) {
            return new boolean[]{true,true,true,true,true,true};
        }
        return new boolean[]{true,true,true,true,true,true};
    }

    private Bundle createFragmentArguments(ReadableMap options) {

        final Bundle args = new Bundle();
        Calendar startDate = null,endDate=null;

        if (options.hasKey(TITLE_TEXT) && !options.isNull(TITLE_TEXT)) {
            pickerBuilder.setTitleText(options.getString(TITLE_TEXT));
        }
        if (options.hasKey(TITLE_TEXT_COLOR) && !options.isNull(TITLE_TEXT_COLOR)) {
            pickerBuilder.setTitleColor(options.getInt(TITLE_TEXT_COLOR));
        }

        if (options.hasKey(DONE_TEXT) && !options.isNull(DONE_TEXT)) {
            args.putString(DONE_TEXT, options.getString(DONE_TEXT));
            pickerBuilder.setSubmitText(options.getString(DONE_TEXT));
        }
        if (options.hasKey(DONE_TEXT_COLOR) && !options.isNull(DONE_TEXT_COLOR)) {
            pickerBuilder.setSubmitColor(options.getInt(DONE_TEXT_COLOR));
        }

        if (options.hasKey(CANCEL_TEXT) && !options.isNull(CANCEL_TEXT)) {
            args.putString(CANCEL_TEXT, options.getString(CANCEL_TEXT));
            pickerBuilder.setCancelText(options.getString(CANCEL_TEXT));
        }
        if (options.hasKey(CANCEL_TEXT_COLOR) && !options.isNull(CANCEL_TEXT_COLOR)) {
            pickerBuilder.setCancelColor(options.getInt(CANCEL_TEXT_COLOR));
        }

        if (options.hasKey(SELECTED_DATE) && !options.isNull(SELECTED_DATE)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                format.setTimeZone(TimeZone.getTimeZone("GMT+08"));
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
                calendar.setTime(format.parse(options.getString(SELECTED_DATE)));
                pickerBuilder.setDate(calendar);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (options.hasKey(MINIMUM_DATE) && !options.isNull(MINIMUM_DATE)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy", Locale.CHINA);
                format.setTimeZone(TimeZone.getTimeZone("GMT+08"));
                Date date = format.parse(options.getString(MINIMUM_DATE));
                startDate = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
                startDate.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (options.hasKey(MAXIMUM_DATE) && !options.isNull(MAXIMUM_DATE)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy", Locale.CHINA);
                format.setTimeZone(TimeZone.getTimeZone("GMT+08"));
                Date date = format.parse(options.getString(MAXIMUM_DATE));
                endDate = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
                endDate.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        pickerBuilder.setRangDate(startDate,endDate);

        if (options.hasKey(YEAR_TEXT) && !options.isNull(YEAR_TEXT)) {
            args.putString(YEAR_TEXT, options.getString(YEAR_TEXT));
        }

        if (options.hasKey(MONTH_TEXT) && !options.isNull(MONTH_TEXT)) {
            args.putString(MONTH_TEXT, options.getString(MONTH_TEXT));
        }
        if (options.hasKey(DAY_TEXT) && !options.isNull(DAY_TEXT)) {
            args.putString(DAY_TEXT, options.getString(DAY_TEXT));
        }
        if (options.hasKey(HOURS_TEXT) && !options.isNull(HOURS_TEXT)) {
            args.putString(HOURS_TEXT, options.getString(HOURS_TEXT));
        }
        if (options.hasKey(MINUTES_TEXT)&&!options.isNull(MINUTES_TEXT)){
            args.putString(MINUTES_TEXT,options.getString(MINUTES_TEXT));
        }
        if (options.hasKey(SECONDS_TEXT)&&!options.isNull(SECONDS_TEXT)){
            args.putString(SECONDS_TEXT,options.getString(SECONDS_TEXT));
        }
        if (options.hasKey(WHEEL_BG_COLOR) && !options.isNull(WHEEL_BG_COLOR)) {
            pickerBuilder.setBgColor(options.getInt(WHEEL_BG_COLOR));
        }
        if (options.hasKey(TITLE_BG_COLOR) && !options.isNull(TITLE_BG_COLOR)) {
            pickerBuilder.setTitleBgColor(options.getInt(TITLE_BG_COLOR));
        }
        if (options.hasKey(DONE_CANCEL_SIZE) && !options.isNull(DONE_CANCEL_SIZE)) {
            pickerBuilder.setSubCalSize(options.getInt(DONE_CANCEL_SIZE));
        }
        if (options.hasKey(TITLE_SIZE) && !options.isNull(TITLE_SIZE)) {
            pickerBuilder.setTitleSize(options.getInt(TITLE_SIZE));
        }
        if (options.hasKey(CONTENT_SIZE) && !options.isNull(CONTENT_SIZE)) {
            pickerBuilder.setContentSize(options.getInt(CONTENT_SIZE));
        }
        if (options.hasKey(CANCEL_ENABLE) && !options.isNull(CANCEL_ENABLE)) {
            pickerBuilder.setOutSideCancelable(options.getBoolean(CANCEL_ENABLE));
        }
        if (options.hasKey(IS_CENTER_LABEL) && !options.isNull(IS_CENTER_LABEL)) {
            pickerBuilder.isCenterLabel(options.getBoolean(IS_CENTER_LABEL));
        }
        if (options.hasKey(OUT_TEXT_COLOR) && !options.isNull(OUT_TEXT_COLOR)) {
            pickerBuilder.setTextColorOut(options.getInt(OUT_TEXT_COLOR));
        }
        if (options.hasKey(CENTER_TEXT_COLOR) && !options.isNull(CENTER_TEXT_COLOR)) {
            pickerBuilder.setTextColorCenter(options.getInt(CENTER_TEXT_COLOR));
        }
        if (options.hasKey(DIVIDER_COLOR) && !options.isNull(DIVIDER_COLOR)) {
            pickerBuilder.setDividerColor(options.getInt(DIVIDER_COLOR));
        }
        if (options.hasKey(SHADE_BG_COLOR) && !options.isNull(SHADE_BG_COLOR)) {
            pickerBuilder.setBackgroundId(options.getInt(SHADE_BG_COLOR));
        }
        if (options.hasKey(LINE_SPACING_MULTIPLIER) && !options.isNull(LINE_SPACING_MULTIPLIER)) {
            pickerBuilder.setLineSpacingMultiplier((float) options.getDouble(LINE_SPACING_MULTIPLIER));
        }
        if (options.hasKey(DIVIDER_TYPE) && !options.isNull(DIVIDER_TYPE)) {
            if ("fill".equals(options.getString(DIVIDER_TYPE)))
                pickerBuilder.setDividerType(WheelView.DividerType.FILL);
            else if ("wrap".equals(options.getString(DIVIDER_TYPE)))
                pickerBuilder.setDividerType(WheelView.DividerType.WRAP);
        }
        if (options.hasKey(IS_CYCLIC)&&!options.isNull(IS_CYCLIC)){
            pickerBuilder.isCyclic(options.getBoolean(IS_CYCLIC));
        }

        return args;
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
