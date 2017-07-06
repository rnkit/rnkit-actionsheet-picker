package io.rnkit.actionsheetpicker;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.bigkoo.pickerview.model.IPickerViewData;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.ArrayList;

import io.rnkit.actionsheetpicker.bean.PickerViewData;
import io.rnkit.actionsheetpicker.bean.ProvinceBean;
import io.rnkit.actionsheetpicker.view.DataPickerView;

/**
 * Created by SimMan on 2016/11/30.
 */

public class ASDataPickerViewModule extends ReactContextBaseJavaModule implements Application.ActivityLifecycleCallbacks {

    private static final String REACT_CLASS = "RNKitASDataPicker";
    private static final String ERROR_NO_ACTIVITY = "E_NO_ACTIVITY";
    final ReactApplicationContext reactContext;
    private static Boolean isCallBack = false;

    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<IPickerViewData>>> options3Items = new ArrayList<>();
    private Bundle args;
    private OptionsPickerView.Builder pickerProperty;
    private int option1, option2, option3;
    private String optionStr1, optionStr2, optionStr3;

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


    /* package */ static final String NUMBER_OF_COMPONENTS = "numberOfComponents";
    /* package */ static final String DATA_SOURCE = "dataSource";
    /* package */ static final String DEFAULT_SELECTED = "defaultSelected";


    public ASDataPickerViewModule(ReactApplicationContext reactContext) {
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
                pickerProperty = new OptionsPickerView.Builder(activity, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int selectOptions1, int selectOptions2, int selectOptions3, View v) {
                        isCallBack = true;
                        WritableMap map = Arguments.createMap();
                        map.putString("type", "done");
                        int numberOfComponents = args.getInt(NUMBER_OF_COMPONENTS);

                        WritableArray selectedData = Arguments.createArray();
                        WritableArray selectedIndex = Arguments.createArray();
                        switch (numberOfComponents){
                            case 3:
                                selectedData.pushString(options3Items.get(selectOptions1).get(selectOptions2).get(selectOptions3).getPickerViewText());
                                selectedIndex.pushInt(selectOptions3);
                            case 2:
                                selectedData.pushString(options2Items.get(selectOptions1).get(selectOptions2));
                                selectedIndex.pushInt(selectOptions2);
                            case 1:
                                selectedData.pushString(options1Items.get(selectOptions1).getName());
                                selectedIndex.pushInt(selectOptions1);
                            default:
                                break;
                        }
                        System.out.println("selectedData:  "+selectedData+"  selectIndex: "+selectedIndex+"  ++++");
                        map.putArray("selectedData", selectedData);
                        map.putArray("selectedIndex", selectedIndex);

                        callback.invoke(map);
                    }
                });
                args = createFragmentArguments(options);
                optionStr1 = optionStr2 = optionStr3 = "";
                ReadableArray dataSource = options.getArray(DATA_SOURCE);
                if (options.hasKey(DEFAULT_SELECTED) && !options.isNull(DEFAULT_SELECTED)) {
                    ReadableArray defaultSelected = options.getArray("defaultSelected");

                    if (defaultSelected.size() != args.getInt(NUMBER_OF_COMPONENTS)) {
                        throw new JSApplicationIllegalArgumentException("numberOfComponents is not equal to defaultSelected count.");
                    }

                    if (defaultSelected != null && defaultSelected.size() > 0) {
                        if (!TextUtils.isEmpty(defaultSelected.getString(0)))
                            optionStr1 = defaultSelected.getString(0);
                        if (defaultSelected.size() > 1 && !defaultSelected.getString(1).isEmpty())
                            optionStr2 = defaultSelected.getString(1);
                        if (defaultSelected.size() > 2 && !defaultSelected.getString(2).isEmpty())
                            optionStr3 = defaultSelected.getString(2);
                    }
                }

                DataPickerView pvOptions = new DataPickerView(pickerProperty);
                setPickerDataSource(dataSource, pvOptions);
                pvOptions.setSelectOptions(option1, option2, option3);

                pvOptions.setOnTimeCancelListener(new DataPickerView.OnTimeCancelListener() {
                    @Override
                    public void onCancel() {
                        isCallBack = true;

                        WritableMap map = Arguments.createMap();
                        map.putString("type", "cancel");
                        callback.invoke(map);
                    }
                });

                pvOptions.setOnDismissListener(new OnDismissListener() {
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

                pvOptions.show();
            }
        });
    }


    private void setPickerDataSource(ReadableArray dataSource, OptionsPickerView pvOptions) {
        options2Items.clear();
        options1Items.clear();
        options3Items.clear();


        for (int i = 0; i < dataSource.size(); i++) {
            try {
                String type = dataSource.getType(i).name();
                if (type.equals("String")) {
                    String v = dataSource.getString(i);
                     if (v.equals(optionStr1)) {
                        option1 = i;
                    }
                    options1Items.add(new ProvinceBean(i, v, v, v));
                } else if (type.equals("Map")) {

                    ReadableMap map = dataSource.getMap(i);
                    ReadableMapKeySetIterator park = map.keySetIterator();

                    String kkk = park.nextKey();

                    ReadableArray cccclist = map.getArray(kkk);

                    if (cccclist.getType(0).name().equals("String")) {

                        ReadableMapKeySetIterator paramsKey = map.keySetIterator();
                        while (paramsKey.hasNextKey()) {

                            String key = paramsKey.nextKey();

                            if (key.equals(optionStr1)) {
                                option1 = i;
                            }

                            options1Items.add(new ProvinceBean(i, key, key, key));
                            ArrayList<String> list = new ArrayList<>();

                            ReadableArray citys = map.getArray(key);

                            for (int k = 0; k < citys.size(); k++) {
                                String area = citys.getString(k);
                                list.add(area);
                                if (area.equals(optionStr2)) {
                                    option2 = k;
                                }
                            }
                            options2Items.add(list);
                        }
                    } else {
                        ReadableMapKeySetIterator paramsKey = map.keySetIterator();
                        while (paramsKey.hasNextKey()) {

                            String key = paramsKey.nextKey();

                            if (key.equals(optionStr1)) {
                                option1 = i;
                            }

                            options1Items.add(new ProvinceBean(i, key, key, key));
                            ArrayList<String> list = new ArrayList<>();

                            ReadableArray citys = map.getArray(key);
                            ArrayList<ArrayList<IPickerViewData>> options3Items_01 = new ArrayList<>();
                            for (int k = 0; k < citys.size(); k++) {
                                ReadableMap p = citys.getMap(k);
                                ReadableMapKeySetIterator p2 = p.keySetIterator();

                                while (p2.hasNextKey()) {
                                    String city = p2.nextKey();

                                    if (city.equals(optionStr2)) {
                                        option2 = k;
                                    }

                                    list.add(city);
                                    ReadableArray areas = p.getArray(city);
                                    ArrayList<IPickerViewData> options3Items_01_01 = new ArrayList<>();
                                    for (int j = 0; j < areas.size(); j++) {

                                        String area = areas.getString(j);

                                        if (area.equals(optionStr3)) {
                                            option3 = j;
                                        }

                                        options3Items_01_01.add(new PickerViewData(area));
                                    }
                                    options3Items_01.add(options3Items_01_01);

                                }

                            }
                            options3Items.add(options3Items_01);
                            options2Items.add(list);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int numberOfComponents = args.getInt(NUMBER_OF_COMPONENTS);

        switch (numberOfComponents) {
            case 1:
                pvOptions.setPicker(options1Items);
                break;
            case 2:
                pvOptions.setPicker(options1Items, options2Items);
                break;
            case 3:
                pvOptions.setPicker(options1Items, options2Items, options3Items);
            default:
                break;

        }

    }

    private Bundle createFragmentArguments(ReadableMap options) {

        final Bundle args = new Bundle();

        if (options.hasKey(TITLE_TEXT) && !options.isNull(TITLE_TEXT)) {
            pickerProperty.setTitleText(options.getString(TITLE_TEXT));
        }
        if (options.hasKey(TITLE_TEXT_COLOR) && !options.isNull(TITLE_TEXT_COLOR)) {
            pickerProperty.setTitleColor(options.getInt(TITLE_TEXT_COLOR));
        }
        if (options.hasKey(DONE_TEXT) && !options.isNull(DONE_TEXT)) {
            pickerProperty.setSubmitText(options.getString(DONE_TEXT));
        }
        if (options.hasKey(DONE_TEXT_COLOR) && !options.isNull(DONE_TEXT_COLOR)) {
            pickerProperty.setSubmitColor(options.getInt(DONE_TEXT_COLOR));
        }
        if (options.hasKey(CANCEL_TEXT) && !options.isNull(CANCEL_TEXT)) {
            pickerProperty.setCancelText( options.getString(CANCEL_TEXT));
        }
        if (options.hasKey(CANCEL_TEXT_COLOR) && !options.isNull(CANCEL_TEXT_COLOR)) {
            pickerProperty.setCancelColor(options.getInt(CANCEL_TEXT_COLOR));
        }
        if (options.hasKey(WHEEL_BG_COLOR) && !options.isNull(WHEEL_BG_COLOR)) {
            pickerProperty.setBgColor(options.getInt(WHEEL_BG_COLOR));
        }
        if (options.hasKey(TITLE_BG_COLOR) && !options.isNull(TITLE_BG_COLOR)) {
            pickerProperty.setTitleBgColor(options.getInt(TITLE_BG_COLOR));
        }
        if (options.hasKey(DONE_CANCEL_SIZE) && !options.isNull(DONE_CANCEL_SIZE)) {
            pickerProperty.setSubCalSize(options.getInt(DONE_CANCEL_SIZE));
        }
        if (options.hasKey(TITLE_SIZE) && !options.isNull(TITLE_SIZE)) {
            pickerProperty.setTitleSize(options.getInt(TITLE_SIZE));
        }
        if (options.hasKey(CONTENT_SIZE) && !options.isNull(CONTENT_SIZE)) {
            pickerProperty.setContentTextSize(options.getInt(CONTENT_SIZE));
        }
        if (options.hasKey(CANCEL_ENABLE) && !options.isNull(CANCEL_ENABLE)) {
            pickerProperty.setOutSideCancelable(options.getBoolean(CANCEL_ENABLE));
        }
        if (options.hasKey(IS_CENTER_LABEL) && !options.isNull(IS_CENTER_LABEL)) {
            pickerProperty.isCenterLabel(options.getBoolean(IS_CENTER_LABEL));
        }
        if (options.hasKey(OUT_TEXT_COLOR) && !options.isNull(OUT_TEXT_COLOR)) {
            pickerProperty.setTextColorOut(options.getInt(OUT_TEXT_COLOR));
        }
        if (options.hasKey(CENTER_TEXT_COLOR) && !options.isNull(CENTER_TEXT_COLOR)) {
            pickerProperty.setTextColorCenter(options.getInt(CENTER_TEXT_COLOR));
        }
        if (options.hasKey(DIVIDER_COLOR) && !options.isNull(DIVIDER_COLOR)) {
            pickerProperty.setDividerColor(options.getInt(DIVIDER_COLOR));
        }
        if (options.hasKey(SHADE_BG_COLOR) && !options.isNull(SHADE_BG_COLOR)) {
            pickerProperty.setBackgroundId(options.getInt(SHADE_BG_COLOR));
        }
        if (options.hasKey(LINE_SPACING_MULTIPLIER) && !options.isNull(LINE_SPACING_MULTIPLIER)) {
            pickerProperty.setLineSpacingMultiplier((float) options.getDouble(LINE_SPACING_MULTIPLIER));
        }
        if (options.hasKey(DIVIDER_TYPE) && !options.isNull(DIVIDER_TYPE)) {
            if ("fill".equals(options.getString(DIVIDER_TYPE)))
                pickerProperty.setDividerType(WheelView.DividerType.FILL);
            else if ("wrap".equals(options.getString(DIVIDER_TYPE)))
                pickerProperty.setDividerType(WheelView.DividerType.WRAP);
        }
        if (options.hasKey(NUMBER_OF_COMPONENTS)&&!options.isNull(NUMBER_OF_COMPONENTS))
            args.putInt(NUMBER_OF_COMPONENTS,options.getInt(NUMBER_OF_COMPONENTS));
        if (options.hasKey(IS_CYCLIC)&&!options.isNull(IS_CYCLIC)){
            pickerProperty.setCyclic(options.getBoolean(IS_CYCLIC),options.getBoolean(IS_CYCLIC),options.getBoolean(IS_CYCLIC));
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
