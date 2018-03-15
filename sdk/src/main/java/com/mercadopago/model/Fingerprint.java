package com.mercadopago.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Fingerprint {
    private static final String TAG = "Fingerprint";
    private static final String SHARED_PREFS_FINGERPRINT_LOCATION = "FINGERPRINT_LOCATION";
    public static final String PLATFORM_PROPERTY = "ro.product.cpu.abi";

    private transient Context mContext;
    private transient LocationManager mLocationManager;
    private transient LocationListener mLocationListener;
    public ArrayList<VendorId> vendorIds;
    public String model;
    public String os;
    public String systemVersion;
    public String resolution;
    public Long ram;
    public long diskSpace;
    public long freeDiskSpace;
    public VendorSpecificAttributes vendorSpecificAttributes;
    public Location location;


    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

    public Fingerprint(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new FingerprintLocationListener();
        registerLocationUpdate(context);

        vendorIds = getVendorIds();
        model = getModel();
        os = getOs();
        systemVersion = getSystemVersion();
        resolution = getResolution();
        ram = getRam();
        diskSpace = getDiskSpace();
        freeDiskSpace = getFreeDiskSpace();
        vendorSpecificAttributes = getVendorSpecificAttributes();
        location = getLocation(context);
    }

    @SuppressLint("MissingPermission")
    private void registerLocationUpdate(Context context) {
        if (isLocationPermissionGranted(context)) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    0, 0, mLocationListener, Looper.getMainLooper());
        }
    }

    private boolean isLocationPermissionGranted(Context context) {
        return mLocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER) &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public ArrayList<VendorId> getVendorIds() {
        ArrayList<VendorId> vendorIds = new ArrayList<VendorId>();

        // android_id
        String androidId = getAndroidId(mContext);
        vendorIds.add(new VendorId("android_id", androidId));

        if (!TextUtils.isEmpty(Build.SERIAL) && !"unknown".equals(Build.SERIAL)) {
            vendorIds.add(new VendorId("serial", Build.SERIAL));
        }

        // SecureRandom
        String randomId = SecureRandomId.getValue(mContext);
        if (!TextUtils.isEmpty(randomId)) {
            vendorIds.add(new VendorId("fsuuid", randomId));
        }

        return vendorIds;
    }

    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private static class SecureRandomId {
        private static final String FILENAME_FSUUID = "fsuuid";

        private static String mFSUUID = null;

        private static String readFile(File file) throws IOException {
            RandomAccessFile f = new RandomAccessFile(file, "r");
            byte[] bytes = new byte[(int) f.length()];
            f.readFully(bytes);
            f.close();
            return new String(bytes);
        }

        private static void writeFile(File file) throws IOException {
            FileOutputStream out = new FileOutputStream(file);
            SecureRandom random = new SecureRandom();
            String id = new BigInteger(64, random).toString(16);
            out.write(id.getBytes());
            out.close();
        }

        public synchronized static String getValue(Context context) {
            if (mFSUUID == null) {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                File file = new File(path + "/" + context.getPackageName(), FILENAME_FSUUID);
                try {
                    if (!file.exists()) {
                        boolean dirs = file.getParentFile().mkdirs();
                        if (dirs) {
                            writeFile(file);
                        }
                    }
                    mFSUUID = readFile(file);
                } catch (Exception ignored) {
                }
            }

            return mFSUUID;
        }
    }

    public String getModel() {
        return Build.MODEL;
    }

    public String getOs() {
        return "android";
    }

    public String getSystemVersion() {
        return getDeviceSystemVersion();
    }

    public static String getDeviceSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getDeviceResolution(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();

        return display.getWidth() + "x" + display.getHeight();
    }

    public String getResolution() {
        return getDeviceResolution(mContext);
    }

    public Long getRam() {
        Long ram = null;
        RandomAccessFile reader = null;
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            String load = reader.readLine();

            Pattern pattern = Pattern.compile("(\\d+)");
            Matcher matcher = pattern.matcher(load);
            if (matcher.find()) {
                ram = Long.valueOf(matcher.group(0));
            }
        } catch (Exception ignored) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {
                }
            }
        }

        return ram;
    }

    public static float getDeviceScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public long getDiskSpace() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return ((long) statFs.getBlockSize() * (long) statFs.getBlockCount()) / 1048576;
    }

    public long getFreeDiskSpace() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return ((long) statFs.getBlockSize() * statFs.getAvailableBlocks()) / 1048576;
    }

    public VendorSpecificAttributes getVendorSpecificAttributes() {
        return new VendorSpecificAttributes();
    }

    public Location getLocation(Context context) {
        Location diskLocation = Location.fromDisk(context);
        Location resolvedLocation = getLocationFromNetworkOrDefault(context, diskLocation);
        return resolvedLocation.hasLocation() ? resolvedLocation : null;
    }

    @SuppressLint("MissingPermission")
    private Location getLocationFromNetworkOrDefault(Context context, Location defLocation) {
        if (isLocationPermissionGranted(context)) {
            android.location.Location cached = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (cached != null) {
                Location location = new Location(cached.getLatitude(), cached.getLongitude());
                location.save(context);
                return location;
            }
        }
        return defLocation;
    }

    private class VendorId {
        private String name;
        private String value;

        public VendorId(String mname, String mvalue) {
            name = mname;
            value = mvalue;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    private class VendorSpecificAttributes {

        boolean featureCamera;
        boolean featureFlash;
        boolean featureFrontCamera;
        String product;
        String device;
        String platform;
        String brand;
        boolean featureAccelerometer;
        boolean featureBluetooth;
        boolean featureCompass;
        boolean featureGps;
        boolean featureGyroscope;
        boolean featureMicrophone;
        boolean featureNfc;
        boolean featureTelephony;
        boolean featureTouchScreen;
        String manufacturer;
        float screenDensity;

        private VendorSpecificAttributes() {
            this.featureCamera = getFeatureCamera();
            this.featureFlash = getFeatureFlash();
            this.featureFrontCamera = getFeatureFrontCamera();
            this.product = getProduct();
            this.device = getDevice();
            this.platform = getPlatform();
            this.brand = getBrand();
            this.featureAccelerometer = getFeatureAccelerometer();
            this.featureBluetooth = getFeatureBluetooth();
            this.featureCompass = getFeatureCompass();
            this.featureGps = getFeatureGps();
            this.featureGyroscope = getFeatureGyroscope();
            this.featureMicrophone = getFeatureMicrophone();
            this.featureNfc = getFeatureNfc();
            this.featureTelephony = getFeatureTelephony();
            this.featureTouchScreen = getFeatureTouchScreen();
            this.manufacturer = getManufacturer();
            this.screenDensity = getScreenDensity();
        }

        public boolean getFeatureCamera() {
            return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        }

        public boolean getFeatureFlash() {
            return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        }

        public boolean getFeatureFrontCamera() {
            return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
        }

        public String getProduct() {
            return Build.PRODUCT;
        }

        public String getDevice() {
            return Build.DEVICE;
        }

        public String getPlatform() {
            return getSystemProperty(PLATFORM_PROPERTY);
        }

        public String getBrand() {
            return Build.BRAND;
        }

        public boolean getFeatureAccelerometer() {
            return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
        }

        public boolean getFeatureBluetooth() {
            return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        }

        public boolean getFeatureCompass() {
            return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
        }

        public boolean getFeatureGps() {
            return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        }

        public boolean getFeatureGyroscope() {
            return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
        }

        public boolean getFeatureMicrophone() {
            return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
        }

        public boolean getFeatureNfc() {
            return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC);
        }

        public boolean getFeatureTelephony() {
            return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        }

        public boolean getFeatureTouchScreen() {
            return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN);
        }

        public String getManufacturer() {
            return Build.MANUFACTURER;
        }

        public float getScreenDensity() {
            return getDeviceScreenDensity(mContext);
        }
    }

    private static class Location {
        private static final String LOCATION_TIMESTAMP = "timestamp";
        private static final String LOCATION_LATITUDE = "latitude";
        private static final String LOCATION_LONGITUDE = "longitude";

        private JSONObject location;

        private Location(@NonNull JSONObject location) {
            this.location = location;
        }

        Location(double latitude, double longitude) {
            location = new JSONObject();
            try {
                location.put(LOCATION_TIMESTAMP, System.currentTimeMillis() / 1000L);
                location.put(LOCATION_LATITUDE, latitude);
                location.put(LOCATION_LONGITUDE, longitude);
            } catch (JSONException ignored) {
                // Do nothing
            }

        }

        @Override
        public String toString() {
            Gson gson = new Gson();
            try {
                return gson.toJson(this);
            } catch (Exception ignored) {
                // Do nothing
            }
            return null;
        }

        static Location fromDisk(Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String cache = prefs.getString(SHARED_PREFS_FINGERPRINT_LOCATION, null);
            return cache == null ? Location.invalidLocation() : Location.fromString(cache);
        }

        private static Location fromString(String cache) {
            try {
                JSONObject jsonObject = new JSONObject(cache);
                return new Location(jsonObject);
            } catch (JSONException exception) {
                return Location.invalidLocation();
            }
        }

        private static Location invalidLocation() {
            return new Location(new JSONObject());
        }

        boolean hasLocation() {
            return location.has(LOCATION_LATITUDE)
                    && location.has(LOCATION_LATITUDE);
        }

        public void save(Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(SHARED_PREFS_FINGERPRINT_LOCATION, location.toString());
            editor.apply();
        }
    }

    private class FingerprintLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(android.location.Location location) {
            Location newLocation = new Location(location.getLatitude(), location.getLongitude());
            newLocation.save(mContext);

            try {
                mLocationManager.removeUpdates(mLocationListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            // do nothing
        }

        @Override
        public void onProviderEnabled(String s) {
            // do nothing
        }

        @Override
        public void onProviderDisabled(String s) {
            // do nothing
        }
    }
}
