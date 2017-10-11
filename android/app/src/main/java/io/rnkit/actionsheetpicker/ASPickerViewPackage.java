package io.rnkit.actionsheetpicker;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by SimMan on 2016/11/30.
 */

public class ASPickerViewPackage implements ReactPackage {

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        return Arrays.<NativeModule>asList(new ASDatePickerViewModule(reactContext), new ASDataPickerViewModule(reactContext));
    }

    //@Override deprecated in react-native 0.47
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return  Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return  Collections.emptyList();
    }
}
