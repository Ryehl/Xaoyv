package com.xaoyv.mylibrary.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.security.auth.x500.X500Principal;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;

/**
 * APP工具类
 * APP相关信息工具类。获取版本信息
 *
 * @author jingle1267@163.com
 */
public final class AppUtil {

    private static final boolean DEBUG = true;
    private static final String TAG = "AppUtils";
    private static int VERCODE = -1;
    private static String DEVICEID;
    private static String PACKGENAME;
    private static String ANDROIDID;
    private static String OPERATOR;
    private static String MACADDRESS;
    private static String VERNAME;
    private static String PACKAGE_QQ = "com.tencent.mobileqq";
    private static String PACKAGE_WX = "com.tencent.mm";

    /**
     * Don't let anyone instantiate this class.
     */
    private AppUtil() {
        throw new Error("Do not need instantiate!");
    }

    /**
     * 得到软件版本号
     *
     * @param context 上下文
     * @return 当前版本Code
     */
    public static String getVerCode(Context context) {
        if (VERCODE == -1) {
            try {
                String packageName = context.getPackageName();
                VERCODE = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return VERCODE + "";
    }

    /**
     * 得到DeviceId
     *
     * @param context 上下文
     * @return DeviceId
     */
    @SuppressLint("MissingPermission")
    public static String getDeviceId(Context context) {
        if (DEVICEID == null) {
            DEVICEID = ((TelephonyManager) context.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        }

        if(TextUtils.isEmpty(DEVICEID)){
            DEVICEID = Settings.System.getString(
                    context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

//        if (!TextUtils.isEmpty(DEVICEID)) {
//            return DEVICEID;
//        }
//        if (!TextUtils.isEmpty(getAndroidId(context))) {
//            return getAndroidId(context);
//        }
//        if (!TextUtils.isEmpty(ZYApplication.oaid)) {
//            return ZYApplication.oaid;
//        }
//        if (!TextUtils.isEmpty(getUUID(context))) {
//            return getUUID(context);
//        }
        return DEVICEID;
    }




    /**
     * MId
     *
     * @param context 上下文
     * @return DeviceId
     */
    @SuppressLint("MissingPermission")
    public static String getMId(Context context) {
        if (DEVICEID == null) {
            DEVICEID = ((TelephonyManager) context.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        }
        if (!TextUtils.isEmpty(DEVICEID)) {
            return DEVICEID;
        }
        if (!TextUtils.isEmpty(getAndroidId(context))) {
            return getAndroidId(context);
        }
//        if (!TextUtils.isEmpty(ZYApplication.oaid)) {
//            return ZYApplication.oaid;
//        }
//        if (!TextUtils.isEmpty(getUUID(context))) {
//            return getUUID(context);
//        }
        return DEVICEID;
    }
    //获得独一无二的Psuedo ID
    public static String getUniquePsuedoID() {
        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位

        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    public static String getMac(Context context) {
        String mac = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (context == null) {
                return mac;
            }
            WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = null;
            try {
                info = wifi.getConnectionInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (info == null) {
                return null;
            }
            mac = info.getMacAddress();
            if (!TextUtils.isEmpty(mac)) {
                mac = mac.toUpperCase(Locale.ENGLISH);
            }
            return mac;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String strMacAddr = null;
            try {
                // 获得IpD地址
                InetAddress ip = getLocalInetAddress();
                byte[] b = NetworkInterface.getByInetAddress(ip)
                        .getHardwareAddress();
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < b.length; i++) {
                    if (i != 0) {
                        buffer.append(':');
                    }
                    String str = Integer.toHexString(b[i] & 0xFF);
                    buffer.append(str.length() == 1 ? 0 + str : str);
                }
                strMacAddr = buffer.toString().toUpperCase();
            } catch (Exception e) {
            }
            return strMacAddr;

        }
        return mac;
    }

    public static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            // 列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface
                    .getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {// 是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface
                        .nextElement();// 得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();// 得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return ip;
    }

//    public static String getUUID(Context context) {
//        String uuid = Preferences.getString("otherid_empty_uuid");
//        if (TextUtils.isEmpty(uuid)) {
//            uuid = UUID.randomUUID().toString();
//            Preferences.saveString("otherid_empty_uuid", uuid);
//        }
//        return uuid;
//    }
    /**
     * 得到PackgeName
     * 得到软件包名
     *
     * @param context 上下文
     * @return PackgeName
     */
    public static String getPackgeName(Context context) {
        if (PACKGENAME == null) {
            try {
                PACKGENAME = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), 0).packageName;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return PACKGENAME;
    }

    /**
     * 得到AndroidId
     *
     * @param context 上下文
     * @return AndroidId
     */
    public static String getAndroidId(Context context) {
        if (ANDROIDID == null) {
            ANDROIDID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return ANDROIDID;
    }

    /**
     * 获取SIM卡运营商
     *
     * @param context
     * @return
     */
    public static String getOperators(Context context) {
        if (OPERATOR == null) {
            TelephonyManager tm =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            OPERATOR = tm.getSimOperatorName();
        }
//        if (OPERATOR == null) {
//            TelephonyManager tm = (TelephonyManager) context
//                    .getSystemService(Context.TELEPHONY_SERVICE);
//            String operator = null;
//            @SuppressLint("MissingPermission") String IMSI = tm.getSubscriberId();
//            if (IMSI == null || IMSI.equals("")) {
//                return operator;
//            }
//            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
//                OPERATOR = "中国移动";
//            } else if (IMSI.startsWith("46001")) {
//                OPERATOR = "中国联通";
//            } else if (IMSI.startsWith("46003")) {
//                OPERATOR = "中国电信";
//            }
//        }
        return OPERATOR;
    }

    /**
     * 得到MAC地址
     *
     * @param context 上下文
     * @return MAC地址
     */
    public static String getMacAddressFromIp(Context context) {
        if (TextUtils.isEmpty(MACADDRESS)) {
            MACADDRESS = getMac(context);
        }
//        StringBuilder buf = new StringBuilder();
//        try {
//            byte[] mac;
//            NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(getIpAddress(context)));
//            mac = ne.getHardwareAddress();
//            for (byte b : mac) {
//                buf.append(String.format("%02X:", b));
//            }
//            if (buf.length() > 0) {
//                buf.deleteCharAt(buf.length() - 1);
//            }
//            MACADDRESS = buf.toString();
//        } catch (Exception e) {
////                e.printStackTrace();
//        }
        return MACADDRESS;
    }

    /**
     * 获取IP地址
     *
     * @param context 上下文
     * @return IP地址
     */
    public static String getIpAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            // 3/4g网络
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                //  wifi网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
                return ipAddress;
            } else if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                // 有限网络
                return getLocalIp();
            }
        }
        return null;
    }

    /**
     * 数字IP转字符串
     *
     * @param ip 数字IP
     * @return 字符串IP
     */
    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


    /**
     * 获取本地IP
     *
     * @return 本地IP
     */
    private static String getLocalIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {

        }
        return "0.0.0.0";

    }

    /**
     * 得到软件显示版本信息
     *
     * @param context 上下文
     * @return 当前版本信息
     */
    public static String getVerName(Context context) {
        if (VERNAME == null)
            try {
                String packageName = context.getPackageName();
                VERNAME = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        return VERNAME;
    }

    /**
     * 安装apk
     *
     * @param context 上下文
     * @param file    APK文件
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri data;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(context, "com.zhangyusport.king.fileprovider", file);//TODO
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(file);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 安装apk
     *
     * @param context 上下文
     * @param file    APK文件uri
     */
    public static void installApk(Context context, Uri file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(file, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 卸载apk
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void uninstallApk(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        Uri packageURI = Uri.parse("package:" + packageName);
        intent.setData(packageURI);
        context.startActivity(intent);
    }

    /**
     * 检测服务是否运行
     *
     * @param context   上下文
     * @param className 类名
     * @return 是否运行的状态
     */
    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(ACTIVITY_SERVICE);
        List<RunningServiceInfo> servicesList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo si : servicesList) {
            if (className.equals(si.service.getClassName())) {
                isRunning = true;
            }
        }
        return isRunning;
    }

    /**
     * 停止运行服务
     *
     * @param context   上下文
     * @param className 类名
     * @return 是否执行成功
     */
    public static boolean stopRunningService(Context context, String className) {
        Intent intent_service = null;
        boolean ret = false;
        try {
            intent_service = new Intent(context, Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (intent_service != null) {
            ret = context.stopService(intent_service);
        }
        return ret;
    }

    /**
     * 得到CPU核心数
     *
     * @return CPU核心数
     */
    public static int getNumCores() {
        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                        return true;
                    }
                    return false;
                }
            });
            return files.length;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * whether this process is named with processName
     *
     * @param context     上下文
     * @param processName 进程名
     * @return <ul>
     * return whether this process is named with processName
     * <li>if context is null, return false</li>
     * <li>if {@link ActivityManager#getRunningAppProcesses()} is null,
     * return false</li>
     * <li>if one process of
     * {@link ActivityManager#getRunningAppProcesses()} is equal to
     * processName, return true, otherwise return false</li>
     * </ul>
     */
    public static boolean isNamedProcess(Context context, String processName) {
        if (context == null || TextUtils.isEmpty(processName)) {
            return false;
        }

        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context
                .getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> processInfoList = manager
                .getRunningAppProcesses();
        if (processInfoList == null) {
            return true;
        }

        for (RunningAppProcessInfo processInfo : manager
                .getRunningAppProcesses()) {
            if (processInfo.pid == pid
                    && processName.equalsIgnoreCase(processInfo.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * whether application is in background
     * <ul>
     * <li>need use permission android.permission.GET_TASKS in Manifest.xml</li>
     * </ul>
     *
     * @param context 上下文
     * @return if application is in background return true, otherwise return
     * false
     */
    public static boolean isApplicationInBackground(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(ACTIVITY_SERVICE);
        List<RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty()) {
            ComponentName topActivity = taskList.get(0).topActivity;
            if (topActivity != null
                    && !topActivity.getPackageName().equals(
                    context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context Context
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context, Class<?> clazz) {
        String className = clazz.getName();
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName()))
                return true;
        }
        return false;
    }

    /**
     * 获取应用签名
     *
     * @param context 上下文
     * @param pkgName 包名
     */
    public static String getSign(Context context, String pkgName) {
        try {
            PackageInfo pis = context.getPackageManager().getPackageInfo(
                    pkgName, PackageManager.GET_SIGNATURES);
            return hexdigest(pis.signatures[0].toByteArray());
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将签名字符串转换成需要的32位签名
     *
     * @param paramArrayOfByte 签名byte数组
     * @return 32位签名字符串
     */
    private static String hexdigest(byte[] paramArrayOfByte) {
        final char[] hexDigits = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97,
                98, 99, 100, 101, 102};
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            char[] arrayOfChar = new char[32];
            for (int i = 0, j = 0; ; i++, j++) {
                if (i >= 16) {
                    return new String(arrayOfChar);
                }
                int k = arrayOfByte[i];
                arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
                arrayOfChar[++j] = hexDigits[(k & 0xF)];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 清理后台进程与服务
     *
     * @param context 应用上下文对象context
     * @return 被清理的数量
     */
    public static int gc(Context context) {
        long i = getDeviceUsableMemory(context);
        int count = 0; // 清理掉的进程数
        ActivityManager am = (ActivityManager) context
                .getSystemService(ACTIVITY_SERVICE);
        // 获取正在运行的service列表
        List<RunningServiceInfo> serviceList = am.getRunningServices(100);
        if (serviceList != null)
            for (RunningServiceInfo service : serviceList) {
                if (service.pid == android.os.Process.myPid())
                    continue;
                try {
                    android.os.Process.killProcess(service.pid);
                    count++;
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }

        // 获取正在运行的进程列表
        List<RunningAppProcessInfo> processList = am.getRunningAppProcesses();
        if (processList != null)
            for (RunningAppProcessInfo process : processList) {
                // 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
                // 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
                if (process.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    // pkgList 得到该进程下运行的包名
                    String[] pkgList = process.pkgList;
                    for (String pkgName : pkgList) {
                        if (DEBUG) {
//                            LogUtils.d(TAG, "======正在杀死包名：" + pkgName);
                        }
//                        try {
//                            am.killBackgroundProcesses(pkgName);
//                            count++;
//                        } catch (Exception e) { // 防止意外发生
//                            e.getStackTrace();
//                        }
                    }
                }
            }
        if (DEBUG) {
//            LogUtils.d(TAG, "清理了" + (getDeviceUsableMemory(context) - i) + "M内存");
        }
        return count;
    }

    /**
     * 获取设备的可用内存大小
     *
     * @param context 应用上下文对象context
     * @return 当前内存大小
     */
    public static int getDeviceUsableMemory(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        // 返回当前系统的可用内存
        return (int) (mi.availMem / (1024 * 1024));
    }

    /**
     * 获取系统中所有的应用
     *
     * @param context 上下文
     * @return 应用信息List
     */
    public static List<PackageInfo> getAllApps(Context context) {

        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = paklist.get(i);
            if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                apps.add(pak);
            }
        }
        return apps;
    }

    /**
     * 判断应用是否安装
     *
     * @param context     上下文
     * @param packageNmae 应用包名getLaunchIntentForPackage
     * @return
     */
    public static boolean isAppInstall(Context context, String packageNmae) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(packageNmae)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 唤起QQ连天
     *
     * @param context
     * @param qqNum
     */
    public static void chatWhitQQ(Context context, String qqNum) {
//        if (AppUtils.isQQAppInstall(context)) {
//            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=" + qqNum)));
//        } else {
//            ToastUtil.create().showToast("请先安装QQ");
//        }
    }

    public static void weakWeChat(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
            Toast.makeText(context, "检查到您手机没有安装微信，请安装后使用该功能", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 唤起第三方应用
     *
     * @param context
     * @param schame
     */
    public static void weakApk(Context context, String schame) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(schame)));
        } catch (ActivityNotFoundException e) {
//            ToastUtil.create().showToast("参数异常");
        }
    }

    /**
     * 判断QQ是否安装
     *
     * @param context 上下文
     * @return
     */
    public static boolean isQQAppInstall(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(PACKAGE_QQ)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断微信是否安装
     *
     * @param context 上下文
     * @return
     */
    public static boolean isWXAppInstall(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(PACKAGE_WX)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 检测是否安装支付宝
     *
     * @param context
     * @return
     */
    public static boolean isAliPayInstall(Context context) {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }

    /**
     * 获取手机系统SDK版本
     *
     * @return 如API 17 则返回 17
     */
    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }


    private final static X500Principal DEBUG_DN = new X500Principal(
            "CN=Android Debug,O=Android,C=US");

    /**
     * 检测当前应用是否是Debug版本
     *
     * @param ctx
     * @return
     */
    public static boolean isDebuggable(Context ctx) {
        boolean debuggable = false;
        try {
            PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature signatures[] = pinfo.signatures;
            for (int i = 0; i < signatures.length; i++) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                ByteArrayInputStream stream = new ByteArrayInputStream(signatures[i].toByteArray());
                X509Certificate cert = (X509Certificate) cf
                        .generateCertificate(stream);
                debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
                if (debuggable)
                    break;
            }

        } catch (NameNotFoundException e) {
        } catch (CertificateException e) {
        }
        return debuggable;
    }

    /**
     * 跳转到应用商店评分
     *
     * @param context
     */
    public static void goToMarket(Context context, String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回app运行状态
     *
     * @param context     一个context
     * @param packageName 要判断应用的包名
     * @return int 1:前台 2:后台 0:不存在
     */
    public static int isAppAlive(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(ACTIVITY_SERVICE);
        List<RunningTaskInfo> listInfos = activityManager
                .getRunningTasks(20);
        // 判断程序是否在栈顶
        if (listInfos.get(0).topActivity.getPackageName().equals(packageName)) {
            return 1;
        } else {
            // 判断程序是否在栈里
            for (RunningTaskInfo info : listInfos) {
                if (info.topActivity.getPackageName().equals(packageName)) {
                    return 2;
                }
            }
            return 0;// 栈里找不到，返回3
        }
    }

    /**
     * 判断app是否处于前台
     *
     * @return
     */
//    public static boolean isAppOnForeground() {
//
//        ActivityManager activityManager = (ActivityManager) ZYApplication.getContext()
//                .getSystemService(Context.ACTIVITY_SERVICE);
//        String packageName = ZYApplication.getContext().getPackageName();
//        /**
//         * 获取Android设备中所有正在运行的App
//         */
//        List<RunningAppProcessInfo> appProcesses = activityManager
//                .getRunningAppProcesses();
//        if (appProcesses == null)
//            return false;
//
//        for (RunningAppProcessInfo appProcess : appProcesses) {
//            // The name of the process that this object is associated with.
//            if (appProcess.processName.equals(packageName)
//                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                return true;
//            }
//        }
//
//        return false;
//    }
    //跳转微信
    public static void jumpWeChat(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "检查到您手机没有安装微信，请安装后使用该功能", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 将本应用置顶到最前端
     * 当本应用位于后台时，则将它切换到最前端
     *
     * @param context
     */
    public static void setTopApp(Context context) {
        int appState = isAppAlive(context, context.getPackageName());
        if (1 == appState) {
            return;
        } else if (0 == appState) {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            context.startActivity(intent);
        } else if (2 == appState) {
            /**获取ActivityManager*/
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            /**获得当前运行的task(任务)*/
            List<RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
            for (RunningTaskInfo taskInfo : taskInfoList) {
                /**找到本应用的 task，并将它切换到前台*/
                if (taskInfo.topActivity.getPackageName().equals(context.getPackageName())) {
//                    activityManager.moveTaskToFront(taskInfo.id, 0);
                    break;
                }
            }
        }
    }

    /**
     * 获取腾讯打包工具生成的渠道信息，直接运行或无渠道信息默认"a_zyd"
     *
     * @param context
     * @return
     */
//    public static String getChannel(Context context) {
//        String channelName = SubChannelUtil.getChannel(context);
//        if (!TextUtils.isEmpty(channelName)) {
//            return channelName;
//        }
//        channelName = ChannelReaderUtil.getChannel(context);
//        if (TextUtils.isEmpty(channelName)) {
//            channelName = "a_zyd";
//        }
//        return channelName;
//    }

    /**
     * 获取子渠道
     *
     * @param context
     * @return
     */
//    public static String getSubChannel(Context context) {
//        String subChannelName = SubChannelUtil.getSubChannel(context);
//        if (!TextUtils.isEmpty(subChannelName)) {
//            return subChannelName;
//        }
//        subChannelName = getChannel(context) + AppConfig.subchannel_suffix;
//        return subChannelName;
//    }
}
