package com.xunneng.iwop.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Env {

	public static final int ANDROID_1_5 = 3;
	public static final int ANDROID_1_6 = 4;
	public static final int ANDROID_2_0 = 5;
	public static final int ANDROID_2_0_1 = 6;
	public static final int ANDROID_2_1 = 7;
	public static final int ANDROID_2_2 = 8;
	public static final int ANDROID_2_3 = 9;
	public static final int ANDROID_2_3_3 = 10;
	public static final int ANDROID_3_0 = 11;
	public static final int ANDROID_4_0 = 14;
	public static final int ANDROID_4_1 = 16;
	public static final int ANDROID_4_2 = 17;
	public static final int ANDROID_4_3 = 18;
    public static final int ANDROID_4_4 = 19;
    public static final int ANDROID_5_0_1 = 21;
    public static final int ANDROID_5_1_1 = 22;
    public static final int ANDROID_6_0 = 23;
    public static final int ANDROID_7_0 = 24;

	public static final int ROOT_STATUS_UNCHECK = -2;
	public static final int ROOT_STATUS_UNKNOWN = -1;
	public static final int ROOT_STATUS_FALSE = 0;
	public static final int ROOT_STATUS_TRUE = 1;
	
	public static final int MIUI_ROM_DEV = 0;
	public static final int MIUI_ROM_RELEASE = 1;
	
	private static final String TAG = "Env";
	@SuppressWarnings("deprecation")
	private static final int SDK_LEVEL = Integer.parseInt(Build.VERSION.SDK);
    private static int rootStatus = ROOT_STATUS_UNCHECK;

	public static String getVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException nnfe) {
			return "null";
		} catch (Exception e) {
			return "null";
		}
	}

	public static int getVersionCode(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException nnfe) {
			return -1;
		} catch (Exception e) {
			return -1;
		}
	}

	

	public static String getAndroidId(Context context) {
		return Settings.Secure.getString(context.getContentResolver(),
				 Settings.Secure.ANDROID_ID);
	}
	

	public static String getLocalMacAdress(Context context) {
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			return info.getMacAddress();
	}
	
	public static String getSystemVersion() {
		return Build.VERSION.RELEASE;
	}

	public static int getSDKLevel() {
		return SDK_LEVEL;
	}

	@SuppressWarnings("deprecation")
	public static String getSDK() {
		if (Build.VERSION.SDK != null)
			return Build.VERSION.SDK;
		return "null";
	}
	
	public static String getSystemVersionName(){
		String version = "2.2";
		switch (Build.VERSION.SDK_INT) {
		case 3:
			version = "1.5";
			break;
		case 4:
			version = "1.6";
			break;
		case 5:
			version = "2.0";
			break;
		case 6:
			version = "2.0.1";
			break;
		case 7:
			version = "2.1";
			break;
		case 8:
			version = "2.2";
			break;
		case 9:
			version = "2.3";
			break;
		case 10:
			version = "2.3.3";
			break;
		case 11:
			version = "3.0";
			break;
		case 14:
			version = "4.0";
			break;
		case 16:
			version = "4.1";
			break;
		case 17:
			version = "4.2";
			break;
		case 18:
			version = "4.3";
			break;
		case 19:
			version = "4.4";
			break;
		case 20:
			version = "5.0";
			break;
		case 21:
			version = "5.0.1";
			break;
		case 22:
			version = "5.1.1";
			break;
		case 23:
			version = "6.0";
			break;
		case 24:
			version = "7.0";
			break;
		}
		return version;
	}
	
	/**
	 * model截断小于30个字符
	 * 
	 * @return
	 */
	public static String getModels() {
		final int maxLength = 30;
		String model = Build.MODEL;
		if (model == null) {
			model = "null";
		}
		if (model.length() > maxLength) {
			model = model.substring(0, maxLength);
		}
		return model;
	}

	public static int getTimeZone() {
		TimeZone timezone = TimeZone.getDefault();
		return timezone.getOffset(System.currentTimeMillis());
	}

	public static String getCounty(Context context) {
		// String locale =
		// context.getResources().getConfiguration().locale.getCountry();
		// String locale =
		// context.getResources().getConfiguration().locale.getDisplayCountry();
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String countryCode = tm.getSimCountryIso();
		if (countryCode == null)
			countryCode = "null";
		return countryCode;
	}

	/**
	 * 获得小于64个字符的rom信息
	 * 
	 * @return
	 */
	public static String getRom64() {
		StringBuilder sb = new StringBuilder();
		sb.append(getRom());
		sb.append(" ");
		sb.append(getExtraRom());
		if (sb.length() > 64) {
			return sb.substring(0, 63);
		}
		return sb.toString();
	}

	public static String getRom() {
		if (Build.BOARD != null)
			return Build.BOARD;
		return "null";
	}

	private static String getExtraRom() {
		if (Build.DISPLAY != null) {
			return Build.DISPLAY;
		}
		return "null";
	}
	
	public static String getRomBrand() {
		String rom = Build.FINGERPRINT;
		if (rom == null) {
			rom = "null";
		} else {
			String[] items = rom.split("/");
			if(items.length >= 5) {
				rom = items[0];
			}
		}
		
		return rom;
	}
	
	
	public static String getFingerPrint(){
		return Build.FINGERPRINT;
	}
	
	/**
	 * 是否是不响铃的MIUI rom，具体规则详见
	 * http://svn.s.zdworks.com/zdworks/docs/ZDClock/android_zdclock/4.9/%E5%AE%89%E5%8D%93/4.9.534/正点闹钟4.9.534-MIUI弹框-基础版本号列表.xlsx
	 * @return true or false
	 */
	public static boolean isNotAlarmMIUIRom() {
		String rom = Build.FINGERPRINT;
		if (rom == null) {
			return false;
		} else {
			String[] items = rom.split("/");
			if (items.length < 5) return false;
			
			String brand = items[0];
			if (brand != null && brand.length() > 0 && brand.equals("Xiaomi")) {
				String version = items[4];
				if (version == null || version.length() <= 0) return false;
				
				if (version.charAt(0) == 'V') {
					Pattern p = Pattern.compile("V(\\d{1,2}\\.\\d{1,2}\\.\\d{1,2}\\.\\d{1,2})\\.([A-Za-z]+):user");
					Matcher m = p.matcher(version);
					if (m.matches() && m.groupCount() == 2) {
						String numbericPart = m.group(1);
						String alphabeticPart = m.group(2);
						
						String baseNumbericPart = getMIUINumbericPart(alphabeticPart);
						return compareTwoMIUINumbericPart(numbericPart, baseNumbericPart) >= 0;
					} else {
						return false;
					}
				} else if (version.charAt(0) == '5') {
					Pattern p = Pattern.compile("(\\d{1,2}\\.\\d{1,2}\\.\\d{1,2}):user");
					Matcher m = p.matcher(version);
					if (m.matches() && m.groupCount() == 1) {
						String numbericPart = m.group(1);
						return compareTwoMIUINumbericPart(numbericPart, "5.6.11") >= 0;
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
	public static int getMIUIRomType() {
		String rom = Build.FINGERPRINT;
		if (rom == null) {
			return -1;
		} else {
			String[] items = rom.split("/");
			if (items.length < 5) return -1;
			
			String brand = items[0];
			if (brand != null && brand.length() > 0 && brand.equals("Xiaomi")) {
				String version = items[4];
				if (version == null || version.length() <= 0) return -1;
				
				if (version.charAt(0) == 'V') {
					return MIUI_ROM_RELEASE;
				} else {
					return MIUI_ROM_DEV;
				}
			} else {
				return -1;
			}
		}
	}
	
	private static int compareTwoMIUINumbericPart(String numbericPart1, String numbericPart2) {
		if (numbericPart1 == null || numbericPart2 == null || numbericPart1.length() <= 0 || numbericPart2.length() <= 0) return -1;
		
		try {
			String[] numberStrs1 = numbericPart1.split("\\.");
			String[] numberStrs2 = numbericPart2.split("\\.");
			
			int len = Math.min(numberStrs1.length, numberStrs2.length);
			for (int i = 0;i < len; ++i) {
				int number1 = Integer.parseInt(numberStrs1[i]);
				int number2 = Integer.parseInt(numberStrs2[i]);
				
				if (number1 > number2) {
					return 1;
				} else if (number1 < number2) {
					return -1;
				}
			}
			
			if (numberStrs1.length > len) {
				return 1;
			} else if (numberStrs2.length > len) {
				return -1;
			}
			
			return 0;
		} catch (Exception e) {
			return -1;
		}
	}
	
	private static String getMIUINumbericPart(String alphabeticPart) {
		if (alphabeticPart.equals("KXCCNCD")) {
			return "6.5.1.0";
		} else if (alphabeticPart.equals("LXHCNCD")) {
			return "6.5.10.0";
		} else if (alphabeticPart.equals("KHJCNCD")) {
			return "6.5.2.0";
		} else if (alphabeticPart.equals("KHLCNCD")) {
			return "6.5.3.0";
		} else if (alphabeticPart.equals("KXDCNCD")) {
			return "6.5.3.0";
		} else if (alphabeticPart.equals("KXGCNCD")) {
			return "6.5.3.0";
		} else if (alphabeticPart.equals("KHICNCD")) {
			return "6.5.4.0";
		} else if (alphabeticPart.equals("KHIMICD")) {
			return "6.5.4.0";
		} else if (alphabeticPart.equals("KHKCNCD")) {
			return "6.5.4.0";
		} else if (alphabeticPart.equals("KXECNCD")) {
			return "6.5.4.0";
		} else if (alphabeticPart.equals("LXICNCD")) {
			return "6.5.5.0";
		} else if (alphabeticPart.equals("LXIMICD")) {
			return "6.5.6.0";
		} else {
			return "";
		}
	}


    public static boolean hasGooglePlay(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=".concat(context
                        .getPackageName())));
        List<ResolveInfo> infos = context.getPackageManager().queryIntentActivities(intent, 0);
        if(infos != null && !infos.isEmpty()) {
            for(ResolveInfo info : infos) {
                if(info != null) {
                    ActivityInfo ai = info.activityInfo;
                    if(ai != null) {
                        if(ai.packageName != null && ai.packageName.toLowerCase().equals("com.android.vending")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static List<String> getMarketList(Context context) {
        List<String> marketList = new ArrayList<String>(5);
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=".concat(context
                        .getPackageName())));
        List<ResolveInfo> infos = context.getPackageManager().queryIntentActivities(intent, 0);
        if(infos != null && !infos.isEmpty()) {
            for(ResolveInfo info : infos) {
                if(info != null) {
                    ActivityInfo ai = info.activityInfo;
                    if(ai != null && ai.packageName != null) {
                        if(ai.packageName.contains("inputmethod")) continue;
                        marketList.add(ai.packageName);
                    }
                }
            }
        }
        return marketList;
    }

	public static int getRootStatus() {
		if (rootStatus == ROOT_STATUS_UNCHECK) {
			String pathStr = System.getenv("PATH");
			String[] paths;

			if (pathStr == null) {
				paths = new String[] { "/sbin", "/system/sbin", "/system/bin",
						"/system/xbin" };
				rootStatus = ROOT_STATUS_UNKNOWN;
			} else {
				paths = pathStr.split(File.pathSeparator);
				rootStatus = ROOT_STATUS_FALSE;
			}

			for (String path : paths) {
				File suFile = new File(path + File.separatorChar + "su");
				try {
					if (suFile.isFile()) {
						rootStatus = ROOT_STATUS_TRUE;
						break;
					}
				} catch (SecurityException e) {
					Log.w(TAG, "", e);
					rootStatus = ROOT_STATUS_UNKNOWN;
				}
			}
		}
		return rootStatus;
	}

	/**
	 * 应用内切换到指定语言
	 * 
	 * @param context
	 * @param language 如：Locale.ENGLISH 
	 */
	public static void changeToSpecificLanguage(Context context, Locale language) {
		Resources res = context.getResources();
    	Configuration config = res.getConfiguration();
    	config.locale = language;
    	res.updateConfiguration(config, null);
	}
	

	private static Map<String, Locale> getLanguageMap() {
		Map<String, Locale> languageMap = new HashMap<String, Locale>();
		languageMap.put("English", Locale.ENGLISH);
		languageMap.put("de", Locale.GERMAN);
		languageMap.put("es", new Locale("es"));
		languageMap.put("fr", Locale.FRENCH);
		languageMap.put("ja", Locale.JAPANESE);
		languageMap.put("ko", Locale.KOREAN);
		languageMap.put("pl", new Locale("pl"));
		languageMap.put("pt", new Locale("pt"));
		languageMap.put("ru", new Locale("ru"));
		languageMap.put("zh-rCN", Locale.SIMPLIFIED_CHINESE);
		languageMap.put("zh-rHK", Locale.TRADITIONAL_CHINESE);
		languageMap.put("zh-rTW", Locale.TAIWAN);
		
		return languageMap;
	}

	public static String getOsBuidBrand(){
		try{
			String brand = Build.BRAND;
			return brand;
		}catch(Exception e){
			
		}
		return null;
	}
	public static String getOsBuidHost(){
		try{
			String host = Build.HOST;
			return host;
		}catch(Exception e){
			
		}
		return null;
	}
	
	/**
	 * 获取设备生产厂商
	 */
	public static String getOsBuildManufacturer(){
		try{
			String brand = Build.MANUFACTURER;
			return brand;
		}catch(Exception e){
		}
		return null;
	}
	
	public static int getNetworkType(Context context) {
		try {
			TelephonyManager manager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			
			return manager.getNetworkType();
		} catch (Exception e) {
			return 0;
		}
	}
	
}
