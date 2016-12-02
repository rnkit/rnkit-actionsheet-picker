package io.rnkit.actionsheetpicker.bean;

import com.bigkoo.pickerview.model.IPickerViewData;

/**
 * Created by SimMan on 2016/12/1.
 */
public class PickerViewData implements IPickerViewData {
    private String content;

    public PickerViewData(String content) {
        this.content = content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getPickerViewText() {
        return content;
    }
}
