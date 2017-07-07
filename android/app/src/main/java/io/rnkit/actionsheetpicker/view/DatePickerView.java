package io.rnkit.actionsheetpicker.view;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.lib.WheelView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;

import io.rnkit.actionsheetpicker.R;

/**
 * Created by SimMan on 2016/11/30.
 */

public class DatePickerView extends TimePickerView implements DefaultHardwareBackBtnHandler{

    private TextView btnSubmit;
    private TextView btnCancel;
    private TextView tvTitle;
    private WheelView year;
    private WheelView month;
    private WheelView day;
    private WheelView hours;
    private WheelView minutes;


    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";
    private OnTimeCancelListener timeCancelListener;

    public DatePickerView(Builder builder) {
        super(builder);

        btnSubmit = (TextView) findViewById(R.id.btnSubmit);
        btnCancel = (TextView) findViewById(R.id.btnCancel);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        btnCancel.setTextSize(15);
        btnSubmit.setTextSize(15);
        btnSubmit.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        tvTitle.setTextSize(18);

        year =(WheelView) findViewById(R.id.year);
        month = (WheelView) findViewById(R.id.month);
        day = (WheelView) findViewById(R.id.day);
        hours = (WheelView) findViewById(R.id.hour);
        minutes = (WheelView) findViewById(R.id.min);
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(TAG_CANCEL)) {
            dismiss();
            if (timeCancelListener != null) {
                timeCancelListener.onCancel();
            }
            return;
        }
        super.onClick(v);
    }

//    public static int sp2px(Context context, float spValue) {
//        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
//        return (int) (spValue * fontScale + 0.5f);
//    }

    public interface OnTimeCancelListener {
        void onCancel();
    }

    public void setOnTimeCancelListener(OnTimeCancelListener timeCancelListener) {
        this.timeCancelListener = timeCancelListener;
    }

    public TextView getSubmitButton() {
        return this.btnSubmit;
    }

    public TextView getCancelButton() {
        return this.btnCancel;
    }

    public TextView getTitle() {
        return this.tvTitle;
    }

    public WheelView getMinutes() {
        return minutes;
    }

    public WheelView getYear() {
        return year;
    }

    public WheelView getMonth() {
        return month;
    }

    public WheelView getDay() {
        return day;
    }

    public WheelView getHours() {
        return hours;
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        if (this.isShowing()) {
            this.dismissImmediately();
        }
    }
}
