package io.rnkit.actionsheetpicker;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.listener.OnDismissListener;
import com.bigkoo.pickerview.OptionsPickerView;
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
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.rnkit.actionsheetpicker.bean.PickerViewData;
import io.rnkit.actionsheetpicker.bean.ProvinceBean;
import io.rnkit.actionsheetpicker.view.DataPickerView;
import io.rnkit.actionsheetpicker.view.DatePickerView;

/**
 * Created by SimMan on 2016/11/30.
 */

public class ASDataPickerViewModule extends ReactContextBaseJavaModule implements Application.ActivityLifecycleCallbacks {

    private static final String REACT_CLASS = "RNKitASDataPicker";
    private static final String ERROR_NO_ACTIVITY = "E_NO_ACTIVITY";
    final ReactApplicationContext reactContext;

    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<IPickerViewData>>> options3Items = new ArrayList<>();
    private Bundle args;
    private int option1, option2, option3;
    private String optionStr1, optionStr2, optionStr3;

    /* package */ static final String TITLE_TEXT = "titleText";
    /* package */ static final String TITLE_TEXT_COLOR = "titleTextColor";
    /* package */ static final String DONE_TEXT = "doneText";
    /* package */ static final String DONE_TEXT_COLOR = "doneTextColor";
    /* package */ static final String CANCEL_TEXT = "cancelText";
    /* package */ static final String CANCEL_TEXT_COLOR = "cancelTextColor";
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

        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Activity activity = getCurrentActivity();
                if (activity == null) {
                    throw new JSApplicationIllegalArgumentException("Tried to open a Picker dialog while not attached to an Activity");
                }

                args = createFragmentArguments(options);

                ReadableArray dataSource = options.getArray("dataSource");
                DataPickerView pvOptions = new DataPickerView(activity);

                if (options.hasKey(DEFAULT_SELECTED) && !options.isNull(DEFAULT_SELECTED)) {
                    ReadableArray defaultSelected = options.getArray("defaultSelected");

                    if (defaultSelected.size() != args.getInt(NUMBER_OF_COMPONENTS)) {
                        throw new JSApplicationIllegalArgumentException("numberOfComponents is not equal to defaultSelected count.");
                    }

                    if (defaultSelected != null && defaultSelected.size() > 0) {

                        for (int i = 0; i < defaultSelected.size(); i++) {
                            if (i < 3) {
                                if (i == 0) {
                                    optionStr1 = defaultSelected.getString(0);
                                } else if (i == 1) {
                                    optionStr2 = defaultSelected.getString(1);
                                } else if (i == 2) {
                                    optionStr3 = defaultSelected.getString(2);
                                }
                            }
                        }

                    }
                }

                setPickerDataSource(dataSource, pvOptions);

                pvOptions.setCyclic(false, false, false);

                pvOptions.setSelectOptions(option1, option2, option3);

                TextView titleView = pvOptions.getTitle();
                titleView.setText(args.getString(TITLE_TEXT));
                titleView.setTextColor(args.getInt(TITLE_TEXT_COLOR));

                TextView doneButton = pvOptions.getSubmitButton();
                doneButton.setText(args.getString(DONE_TEXT));
                doneButton.setTextColor(args.getInt(DONE_TEXT_COLOR));

                TextView cancelButton = pvOptions.getCancelButton();
                cancelButton.setText(args.getString(CANCEL_TEXT));
                cancelButton.setTextColor(args.getInt(CANCEL_TEXT_COLOR));

                pvOptions.setCancelable(true);

                pvOptions.setOnoptionsSelectListener(new DataPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int option1, int option2, int option3) {

                        WritableMap map = Arguments.createMap();
                        map.putString("type", "done");

                        int numberOfComponents = args.getInt(NUMBER_OF_COMPONENTS);

                        WritableArray selectedData = Arguments.createArray();
                        WritableArray selectedIndex = Arguments.createArray();

                        switch (numberOfComponents) {
                            case 3:
                                selectedData.pushString(options3Items.get(option1).get(option2).get(option3).getPickerViewText());
                                selectedIndex.pushInt(option3);
                            case 2:
                                selectedData.pushString(options2Items.get(option1).get(option2));
                                selectedIndex.pushInt(option2);
                            case 1:
                                selectedData.pushString(options1Items.get(option1).getName());
                                selectedIndex.pushInt(option1);
                            default:
                                break;

                        }

                        map.putArray("selectedData", selectedData);
                        map.putArray("selectedIndex", selectedIndex);

                        callback.invoke(map);
                    }
                });

                pvOptions.setOnTimeCancelListener(new DataPickerView.OnTimeCancelListener() {
                    @Override
                    public void onCancel() {

                        WritableMap map = Arguments.createMap();
                        map.putString("type", "cancel");
                        callback.invoke(map);
                    }
                });

                pvOptions.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(Object o) {
                        WritableMap map = Arguments.createMap();
                        map.putString("type", "cancel");
                        callback.invoke(map);
                    }
                });
                pvOptions.show();
            }
        });
    }

    private void setPickerDataSource(ReadableArray dataSource, DataPickerView pvOptions) {
        options2Items.clear();
        options1Items.clear();
        options3Items.clear();

        for (int i = 0; i < dataSource.size(); i++) {
            try {
                String type = dataSource.getType(i).name();

                if (type.equals("String")) {
                    String v = dataSource.getString(i);
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
                pvOptions.setPicker(options1Items, options2Items, true);
                break;
            case 3:
                pvOptions.setPicker(options1Items, options2Items, options3Items, true);
            default:
                break;

        }


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

        if (options.hasKey(NUMBER_OF_COMPONENTS) && !options.isNull(NUMBER_OF_COMPONENTS)) {
            args.putInt(NUMBER_OF_COMPONENTS, options.getInt(NUMBER_OF_COMPONENTS));
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
