package io.rnkit.actionsheetpicker;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

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

    /* package */ static final String TITLE_TEXT = "titleText";
    /* package */ static final String TITLE_TEXT_COLOR = "titleTextColor";
    /* package */ static final String DONE_TEXT = "doneText";
    /* package */ static final String DONE_TEXT_COLOR = "doneTextColor";
    /* package */ static final String CANCEL_TEXT = "cancelText";
    /* package */ static final String CANCEL_TEXT_COLOR = "cancelTextColor";

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

        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Activity activity = getCurrentActivity();
                if (activity == null) {
                    throw new JSApplicationIllegalArgumentException("Tried to open a Picker dialog while not attached to an Activity");
                }

                Bundle args = createFragmentArguments(options);

                DatePickerView.Type datePickerMode = getDatePickerType(args.getString(DATEPICKER_MODE));

                DatePickerView pvTime = new DatePickerView(activity, datePickerMode);

                pvTime.setRange(args.getInt(MINIMUM_DATE), args.getInt(MAXIMUM_DATE));

                if (args.get(SELECTED_DATE) != null) {
                    pvTime.setTime((Date) args.get(SELECTED_DATE));
                } else {
                    pvTime.setTime(new Date());
                }

                pvTime.setCyclic(false);

                TextView titleView = pvTime.getTitle();
                titleView.setText(args.getString(TITLE_TEXT));
                titleView.setTextColor(args.getInt(TITLE_TEXT_COLOR));

                TextView doneButton = pvTime.getSubmitButton();
                doneButton.setText(args.getString(DONE_TEXT));
                doneButton.setTextColor(args.getInt(DONE_TEXT_COLOR));

                TextView cancelButton = pvTime.getCancelButton();
                cancelButton.setText(args.getString(CANCEL_TEXT));
                cancelButton.setTextColor(args.getInt(CANCEL_TEXT_COLOR));

                pvTime.setCancelable(true);

                pvTime.setOnTimeSelectListener(new DatePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                        format.setTimeZone(TimeZone.getTimeZone("GMT+08"));

                        WritableMap map = Arguments.createMap();
                        map.putString("type", "done");
                        map.putString("selectedDate", format.format(date));

                        callback.invoke(map);
                    }
                });

                pvTime.setOnTimeCancelListener(new DatePickerView.OnTimeCancelListener() {
                    @Override
                    public void onCancel() {

                        WritableMap map = Arguments.createMap();
                        map.putString("type", "cancel");
                        callback.invoke(map);
                    }
                });
                pvTime.show();
            }
        });
    }

    private DatePickerView.Type getDatePickerType(String type) {
        if (type == null) {
            return DatePickerView.Type.YEAR_MONTH_DAY;
        }
        if (type.equals("time")) {
            return DatePickerView.Type.HOURS_MINS;
        }

        if (type.equals("date")) {
            return DatePickerView.Type.YEAR_MONTH_DAY;
        }

        if (type.equals("datetime")) {
            return DatePickerView.Type.ALL;
        }
        return DatePickerView.Type.YEAR_MONTH_DAY;
    }

    private Bundle createFragmentArguments(ReadableMap options) {

        final Bundle args = new Bundle();

        if (options.hasKey(TITLE_TEXT) && !options.isNull(TITLE_TEXT)) {
            args.putString(TITLE_TEXT, options.getString(TITLE_TEXT));
        }
        if (options.hasKey(TITLE_TEXT_COLOR) && !options.isNull(TITLE_TEXT_COLOR)) {
            args.putInt(TITLE_TEXT_COLOR, options.getInt(TITLE_TEXT_COLOR)); // Color.parseColor()
        }

        if (options.hasKey(DONE_TEXT) && !options.isNull(DONE_TEXT)) {
            args.putString(DONE_TEXT, options.getString(DONE_TEXT));
        }
        if (options.hasKey(DONE_TEXT_COLOR) && !options.isNull(DONE_TEXT_COLOR)) {
            args.putInt(DONE_TEXT_COLOR, options.getInt(DONE_TEXT_COLOR)); // Color.parseColor()
        }

        if (options.hasKey(CANCEL_TEXT) && !options.isNull(CANCEL_TEXT)) {
            args.putString(CANCEL_TEXT, options.getString(CANCEL_TEXT));
        }
        if (options.hasKey(CANCEL_TEXT_COLOR) && !options.isNull(CANCEL_TEXT_COLOR)) {
            args.putInt(CANCEL_TEXT_COLOR, options.getInt(CANCEL_TEXT_COLOR)); // Color.parseColor()
        }

        if (options.hasKey(SELECTED_DATE) && !options.isNull(SELECTED_DATE)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                format.setTimeZone(TimeZone.getTimeZone("GMT+08"));
                Date date = format.parse(options.getString(SELECTED_DATE));
                args.putSerializable(SELECTED_DATE, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (options.hasKey(MINIMUM_DATE) && !options.isNull(MINIMUM_DATE)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy", Locale.CHINA);
                format.setTimeZone(TimeZone.getTimeZone("GMT+08"));
                Date date = format.parse(options.getString(MINIMUM_DATE));
                args.putInt(MINIMUM_DATE, Integer.parseInt(format.format(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (options.hasKey(MAXIMUM_DATE) && !options.isNull(MAXIMUM_DATE)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy", Locale.CHINA);
                format.setTimeZone(TimeZone.getTimeZone("GMT+08"));
                Date date = format.parse(options.getString(MAXIMUM_DATE));
                args.putInt(MAXIMUM_DATE, Integer.parseInt(format.format(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (options.hasKey(DATEPICKER_MODE) && !options.isNull(DATEPICKER_MODE)) {
            args.putString(DATEPICKER_MODE, options.getString(DATEPICKER_MODE));
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
