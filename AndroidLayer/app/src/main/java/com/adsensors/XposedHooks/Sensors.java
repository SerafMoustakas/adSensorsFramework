package com.adsensors.XposedHooks;



import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.camera2.CameraDevice;
import android.net.http.SslError;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Random;
import java.util.concurrent.Executor;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findClass;

public class Sensors implements IXposedHookLoadPackage{
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //check loaddataurl also!!
                XposedHelpers.findAndHookMethod("android.webkit.WebView", lpparam.classLoader, "loadUrl", String.class, new XC_MethodHook() {
                    @SuppressLint("JavascriptInterface")
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " +" + lpparam.packageName + "a webview loaded " + param.args[0].toString());
                        final WebView hooked = (WebView) param.thisObject;
                        XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + "a webview loaded testing" + hooked.getUrl()); //get original url also!!
                        hooked.setWebViewClient(new WebViewClient() {
                            @Override
                            public void onReceivedSslError(WebView v, SslErrorHandler handler, SslError er) {
                                handler.proceed();
                            }
                        });
                        XposedBridge.log("OsLog: "+"Reaper_sensor :" + lpparam.packageName + "i found a WebView");
                    }
                });
            }
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                XposedHelpers.findAndHookMethod("android.os.Vibrator", lpparam.classLoader, "vibrate", "long", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (lpparam.packageName.equals("com.android.chrome")) {
                            XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " called Vibrator");
                        }
                    }
                });
            }
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                XposedHelpers.findAndHookMethod(Class.forName("android.webkit.WebChromeClient"),"onConsoleMessage", ConsoleMessage.class,new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param)
                            throws Throwable {
                        ConsoleMessage x = ((ConsoleMessage)param.args[0]);
                        XposedBridge.log("OsLog: "+"WebView Xposed - "+ AndroidAppHelper.currentPackageName() +" : " + x.message());
                        return true;
                    }

                });
            }
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                XposedHelpers.findAndHookMethod(Class.forName("android.webkit.WebView"),"setWebChromeClient", WebChromeClient.class,new XC_MethodHook(){
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedHelpers.findAndHookMethod(param.args[0].getClass(),"onConsoleMessage", ConsoleMessage.class,new XC_MethodHook(){
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                ConsoleMessage x = ((ConsoleMessage)param.args[0]);
                                XposedBridge.log("OsLog: "+"WebView Xposed - "+ AndroidAppHelper.currentPackageName() +" : " +x.message());
                            }

                        });
                    }

                });
            }
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                XposedHelpers.findAndHookMethod(Class.forName("android.webkit.WebChromeClient"),"onConsoleMessage", String.class,int.class,String.class,new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param)
                            throws Throwable {
                        XposedBridge.log("OsLog: "+"WebView Xposed- "+ AndroidAppHelper.currentPackageName() +" : " + param.args[0]+" "+param.args[1]+" "+param.args[2]);
                        return true;
                    }

                });
            }
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                XposedHelpers.findAndHookMethod(Class.forName("android.webkit.WebView"),"setWebChromeClient", WebChromeClient.class,new XC_MethodHook(){
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedHelpers.findAndHookMethod(param.args[0].getClass(),"onConsoleMessage", String.class,int.class,String.class,new XC_MethodHook(){
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                XposedBridge.log("OsLog: "+"WebView Xposed- "+ AndroidAppHelper.currentPackageName() +" : " + param.args[0]+" "+param.args[1]+" "+param.args[2]);
                            }

                        });
                    }

                });
            }
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }


        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                XposedHelpers.findAndHookMethod("android.media.AudioRecord", lpparam.classLoader, "getActiveMicrophones", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (lpparam.packageName.equals("com.android.chrome")) {
                            XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " called Microphone");
                        }
                    }
                });
            }
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                XposedHelpers.findAndHookMethod("android.media.AudioRecord", lpparam.classLoader, "getMinBufferSize", "int", "int", "int", new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (lpparam.packageName.equals("com.android.chrome")) {
                            XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " called Microphone " + param.thisObject.getClass().toString());
                            XposedBridge.log("OsLog: "+Binder.getCallingUid() + " - " + Binder.getCallingPid() + " - " + AndroidAppHelper.currentPackageName());
                        }
                    }
                });
            }
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                XposedHelpers.findAndHookMethod("android.media.AudioRecord", lpparam.classLoader, "startRecording", new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (lpparam.packageName.equals("com.android.chrome")) {
                            XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " called Microphone " + param.thisObject.getClass().toString());
                            XposedBridge.log("OsLog: "+Binder.getCallingUid() + " - " + Binder.getCallingPid() + " - " + AndroidAppHelper.currentPackageName());
                        }
                    }
                });
            }
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                XposedHelpers.findAndHookMethod("android.media.MediaRecorder", lpparam.classLoader, "setAudioEncoder", "int", new XC_MethodHook() {


                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (lpparam.packageName.equals("com.android.chrome")) {
                            XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " called Microphone ");
                        }
                    }
                });
            }
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                XposedHelpers.findAndHookMethod("android.media.MediaRecorder", lpparam.classLoader, "setAudioSource", "int", new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (lpparam.packageName.equals("com.android.chrome")) {
                            XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " called Microphone ");
                        }
                    }
                });
            }
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                XposedHelpers.findAndHookMethod("android.hardware.camera2.CameraManager", lpparam.classLoader, "openCamera", String.class, CameraDevice.StateCallback.class, Handler.class, new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (lpparam.packageName.equals("com.android.chrome")) {
                            XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " called camera ");
                        }
                    }
                });
            }
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                XposedHelpers.findAndHookMethod("android.hardware.camera2.CameraManager", lpparam.classLoader, "openCamera", String.class, Executor.class, CameraDevice.StateCallback.class, new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (lpparam.packageName.equals("com.android.chrome")) {
                            XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " called camera ");
                        }
                    }
                });
            }
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }

        try {
            XposedHelpers.findAndHookMethod("android.hardware.SensorManager", lpparam.classLoader, "getDefaultSensor", "int", new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (lpparam.packageName.equals("com.android.chrome")) {
                        Sensor s = (Sensor) param.getResult();
                        XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " called getDefaultSensor(" + s.getName() + ")");

                        StackTraceElement[] strace = Thread.currentThread().getStackTrace();
                        for (StackTraceElement element : strace) {
                            System.out.println("OsLog: "+"Reaper_sensor - stacktrace: " + element);
                        }
                    }
                }
            });
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }

        try {
            XposedHelpers.findAndHookMethod("android.hardware.SensorManager", lpparam.classLoader, "getDefaultSensor", "int","boolean", new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (lpparam.packageName.equals("com.android.chrome")) {
                        Sensor s = (Sensor) param.getResult();
                        XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " called getDefaultSensor(" + s.getName() + ")");

                        StackTraceElement[] strace = Thread.currentThread().getStackTrace();
                        for (StackTraceElement element : strace) {
                            System.out.println("OsLog: "+"Reaper_sensor - stacktrace: " + element);
                        }
                    }
                }
            });
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }


        try {
            XposedHelpers.findAndHookMethod("android.hardware.SensorManager", lpparam.classLoader, "registerListener", SensorEventListener.class, Sensor.class, "int", new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (lpparam.packageName.equals("com.android.chrome")) {
                        XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " called registerListener (" + param.args[1] + ")");

                        StackTraceElement[] strace = Thread.currentThread().getStackTrace();
                        for (StackTraceElement element : strace) {
                            System.out.println("OsLog: "+"Reaper_sensor - stacktrace: " + element);
                        }
                    }
                }
            });
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }

        try {
            XposedHelpers.findAndHookMethod("android.hardware.SensorManager", lpparam.classLoader, "registerListener", SensorEventListener.class, Sensor.class, "int", "int", new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (lpparam.packageName.equals("com.android.chrome")) {
                        XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " called registerListener (" + param.args[1] + ")");

                        StackTraceElement[] strace = Thread.currentThread().getStackTrace();
                        for (StackTraceElement element : strace) {
                            System.out.println("OsLog: "+"Reaper_sensor - stacktrace: " + element);
                        }
                    }
                }
            });
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }

        try {
            XposedHelpers.findAndHookMethod("android.hardware.SensorManager", lpparam.classLoader, "registerListener", SensorEventListener.class, Sensor.class, "int", Handler.class, new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (lpparam.packageName.equals("com.android.chrome")) {
                        XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " called registerListener (" + param.args[1] + ")");
                        StackTraceElement[] strace = Thread.currentThread().getStackTrace();
                        for (StackTraceElement element : strace) {
                            System.out.println("OsLog: "+"Reaper_sensor - stacktrace: " + element);
                        }
                    }
                }
            });
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }

        try {
            XposedHelpers.findAndHookMethod("android.hardware.SensorManager", lpparam.classLoader, "registerListener", SensorEventListener.class, Sensor.class, "int", "int", Handler.class, new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (lpparam.packageName.equals("com.android.chrome")) {
                        XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " called registerListener (" + param.args[1] + ")");
                        StackTraceElement[] strace = Thread.currentThread().getStackTrace();
                        for (StackTraceElement element : strace) {
                            System.out.println("OsLog: "+"Reaper_sensor - stacktrace: " + element);
                        }
                    }
                }
            });
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }


        try {
            XposedHelpers.findAndHookMethod("android.view.Display", lpparam.classLoader, "getRotation", new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (lpparam.packageName.equals("com.android.chrome")) {
                        XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " called orientationListener");
                        StackTraceElement[] strace = Thread.currentThread().getStackTrace();
                        for (StackTraceElement element : strace) {
                            System.out.println("OsLog: "+"Reaper_sensor - stacktrace: " + element);
                        }
                    }
                }
            });
        } catch (NoSuchMethodError e) {
            System.out.println("OsLog: "+"ERROR " + e);
        }

        try {
            XposedBridge.hookAllMethods(android.location.LocationManager.class, "getCurrentLocation", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("OsLog: "+"Location retrieved using getLastKnownLocation");
                    XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " called getCurrentLocation GPS");
                    XposedBridge.log("OsLog: "+"Stacktrace of method" + param.method.getName());
                    StackTraceElement[] strace = Thread.currentThread().getStackTrace();
                    for (StackTraceElement element : strace) {
                        System.out.println("OsLog: "+"Reaper_sensor - stacktrace: " + element);
                    }
                }
            });
        }
        catch(NoSuchMethodError e){
            System.out.println("OsLog: "+"ERROR " + e);
        }

        try {
            XposedBridge.hookAllMethods(android.location.LocationManager.class, "getLastKnownLocation", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("OsLog: "+"Location retrieved using getLastKnownLocation");
                    XposedBridge.log("OsLog: "+"Reaper_sensor: " + lpparam.packageName + " called getLastKnownLocation GPS");
                    XposedBridge.log("OsLog: "+"Stacktrace of method" + param.method.getName());
                    StackTraceElement[] strace = Thread.currentThread().getStackTrace();
                    for (StackTraceElement element : strace) {
                        System.out.println("OsLog: "+"Reaper_sensor - stacktrace: " + element);
                    }
                }
            });
        }
        catch(NoSuchMethodError e){
            System.out.println("OsLog: "+"ERROR " + e);
        }

        try{
            final Class<?> sensorEQ = findClass("android.hardware.SystemSensorManager$SensorEventQueue",lpparam.classLoader);
            XposedHelpers.findAndHookMethod("android.hardware.SystemSensorManager$SensorEventQueue",
                    lpparam.classLoader, "dispatchSensorEvent", int.class, float[].class, int.class, long.class, new XC_MethodHook() {
                        @SuppressWarnings("unchecked")
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("OsLog: installed sensorevent patch in: " + lpparam.packageName);
                            //Field field = param.thisObject.getClass().getEnclosingClass().getDeclaredField("sHandleToSensor");
                            //field.setAccessible(true);
                            Random r = new Random();
                            int handle = (Integer) param.args[0];
                            //Sensor ss = ((SparseArray<Sensor>) field.get(0)).get(handle);
                            //if (ss.getType() == Sensor.TYPE_GYROSCOPE || ss.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED) {
                                for (int f = 0; f < ((float[]) param.args[1]).length; f++) {
                                    float random = 0.001f + r.nextFloat() * (0.005f - 0.001f);
                                    float old = ((float[])param.args[1])[f];
                                    ((float[])param.args[1])[f] = ((float[])param.args[1])[f] + random;
                                    XposedBridge.log("OsLog: "+"Change gyroscope from " + old + " to " + ((float[])param.args[1])[f]);
                                }
                            //}
                        }

                    });
            /*XposedBridge.hookAllMethods(sensorEQ, "dispatchSensorEvent", new XC_MethodHook() {
                @SuppressWarnings("unchecked")
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("OsLog: installed sensorevent patch in: " + lpparam.packageName);
                    //Field field = param.thisObject.getClass().getEnclosingClass().getDeclaredField("sHandleToSensor");
                    //field.setAccessible(true);
                    Random r = new Random();
                    int handle = (Integer) param.args[0];
                    //Sensor ss = ((SparseArray<Sensor>) field.get(0)).get(handle);
                    //if (ss.getType() == Sensor.TYPE_GYROSCOPE || ss.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED) {
                        for (int f = 0; f < ((float[]) param.args[1]).length; f++) {
                            float random = 0.00001f + r.nextFloat() * (0.00005f - 0.00001f);
                            float old = ((float[])param.args[1])[f];
                            ((float[])param.args[1])[f] = ((float[])param.args[1])[f] + random;
                            XposedBridge.log("OsLog: "+"Change gyroscope from " + old + " to " + ((float[])param.args[1])[f]);
                        }
                    //}
                }
            });*/
            XposedBridge.log("OsLog: installed sensorevent patch in: " + lpparam.packageName);
        }catch(NoSuchMethodError e){
            System.out.println("OsLog: "+"ERROR " + e);
        }

    }
}


