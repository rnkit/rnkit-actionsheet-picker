package io.rnkit.actionsheetpicker.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;

import io.rnkit.actionsheetpicker.R;

/**
 * Created by SimMan on 2016/11/30.
 */

public class DataPickerView extends OptionsPickerView implements DefaultHardwareBackBtnHandler{

    private TextView btnSubmit, btnCancel, tvTitle;
    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";
    private OnTimeCancelListener timeCancelListener;

    public DataPickerView(Context context) {
        super(context);

        btnSubmit = (TextView) findViewById(R.id.btnSubmit);
        btnCancel = (TextView) findViewById(R.id.btnCancel);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        btnCancel.setTextSize(15);

        btnSubmit.setTextSize(15);
        btnSubmit.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);

        tvTitle.setTextSize(18);
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

    public TextView getSubmitButton() {
        return this.btnSubmit;
    }

    public TextView getCancelButton() {
        return this.btnCancel;
    }

    public TextView getTitle() {
        return this.tvTitle;
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        if (this.isShowing()) {
            this.dismissImmediately();
        }
    }
}
