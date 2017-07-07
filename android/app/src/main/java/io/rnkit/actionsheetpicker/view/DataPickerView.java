package io.rnkit.actionsheetpicker.view;

import android.view.View;

import com.bigkoo.pickerview.OptionsPickerView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;

/**
 * Created by SimMan on 2016/11/30.
 */

public class DataPickerView extends OptionsPickerView implements DefaultHardwareBackBtnHandler{

    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";
    private OnTimeCancelListener timeCancelListener;

    public DataPickerView(Builder builder) {
        super(builder);
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

    public interface OnTimeCancelListener {
        void onCancel();
    }

    public void setOnTimeCancelListener(OnTimeCancelListener timeCancelListener) {
        this.timeCancelListener = timeCancelListener;
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        if (this.isShowing()) {
            this.dismissImmediately();
        }
    }
}
