package com.techhive.statussaver.utils;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import com.techhive.statussaver.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {
    public static void applyLanguage(Context mContext) {
        Locale locale = null;
        /*-------------------change language--------------------
         *This declaration is check language set in preference then convert to that lang.
         * if not then default set English........................*/


        String lang = SharedPrefs.getLanguage(mContext);
        Log.e("lang", lang);
        if (lang.equals("")) {
            SharedPrefs.setLanguage(mContext, SharedPrefs.ENGLISH_LOCALE);
            lang = SharedPrefs.getLanguage(mContext);
        }
        switch (lang) {
            case SharedPrefs.ENGLISH_LOCALE:
                locale = new Locale(SharedPrefs.ENGLISH_LOCALE);
                break;
            case SharedPrefs.GUJARATI_LOCALE:
                locale = new Locale(SharedPrefs.GUJARATI_LOCALE);
                break;
            case SharedPrefs.HINDI_LOCALE:
                locale = new Locale(SharedPrefs.HINDI_LOCALE);
                break;
        }
        Resources resources = mContext.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);

        mContext.getResources().updateConfiguration(configuration,
                mContext.getResources().getDisplayMetrics());
//        onConfigurationChanged(configuration);
        /*-----------------------------------------------------*/
    }

    public static void changeLanguage(final Context mainContext, Locale locale) {
        Resources resources = mainContext.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        Log.e("LLLLLLLLLLLLL", String.valueOf(configuration.getLocales()));
        mainContext.getResources().updateConfiguration(configuration,
                mainContext.getResources().getDisplayMetrics());
        ((AppCompatActivity) mainContext).onConfigurationChanged(configuration);
//        ((AppCompatActivity) mainContext).recreate();

    }
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };

    public static boolean checkPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static File downloadWhatsAppDir = new File(Environment.getExternalStorageDirectory() + "/Download/Social Status Saver/Whatsapp");
    public static File downloadWABusiDir = new File(Environment.getExternalStorageDirectory() + "/Download/Social Status Saver/WABusiness");

    public static String instaDirPath = "Social Status Saver/Instagram/";
    public static File downloadInstaDir = new File(Environment.getExternalStorageDirectory() + "/Download/Social Status Saver/Instagram");

    public static File downloadVimeoDir = new File(Environment.getExternalStorageDirectory() + "/Download/Social Status Saver/Vimeo");

    public static File downloadFBDir = new File(Environment.getExternalStorageDirectory() + "/Download/Social Status Saver/Facebook");

    public static File downloadYTubeDir = new File(Environment.getExternalStorageDirectory() + "/Download/Social Status Saver/Youtube");
    public static File downloadPinterestDir = new File(Environment.getExternalStorageDirectory() + "/Download/Social Status Saver/Pinterest");
    public static File downloadDailymotionDir = new File(Environment.getExternalStorageDirectory() + "/Download/Social Status Saver/Dailymotion");
    public static File downloadTrillerDir = new File(Environment.getExternalStorageDirectory() + "/Download/Social Status Saver/Triller");
    public static String joshDirPath = "Social Status Saver/Josh/";
    public static File downloadJoshDir = new File(Environment.getExternalStorageDirectory() + "/Download/Social Status Saver/Josh");

    public static String chinagriDirPath = "Social Status Saver/Chinagri/";
    public static File downloadChinagriDir = new File(Environment.getExternalStorageDirectory() + "/Download/Social Status Saver/Chinagri");

    public static String getBack(String paramString1, String paramString2) {
        Matcher localMatcher = Pattern.compile(paramString2).matcher(paramString1);
        if (localMatcher.find()) {
            return localMatcher.group(1);
        }
        return "";
    }


    public static void mShare(String filepath, Activity activity, boolean isVideo) {
        File fileToShare = new File(filepath);
        if (isVideo) {
            Uri videoURI = FileProvider.getUriForFile(activity.getApplicationContext(), activity.getApplicationContext()
                    .getPackageName() + ".provider", fileToShare);
            Intent videoshare = new Intent(Intent.ACTION_SEND);
            videoshare.setType("*/*");
            videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            videoshare.putExtra(Intent.EXTRA_STREAM, videoURI);

            activity.startActivity(videoshare);
        } else {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.setType("image/*");
            Uri photoURI = FileProvider.getUriForFile(
                    activity.getApplicationContext(), activity.getApplicationContext()
                            .getPackageName() + ".provider", fileToShare);
            share.putExtra(Intent.EXTRA_STREAM,
                    photoURI);
            activity.startActivity(Intent.createChooser(share, "Share via"));
        }

    }

    public static void shareFile(Context context, boolean isVideo, String path) {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        if (isVideo)
            share.setType("Video/*");
        else
            share.setType("image/*");

        Uri uri;
        if (path.startsWith("content")) {
            uri = Uri.parse(path);
        } else {
            uri = FileProvider.getUriForFile(context,
                    context.getApplicationContext().getPackageName() + ".provider", new File(path));
        }

        share.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(share);
    }


    public static Boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw == null) return false;
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
        return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
    }

    public static void mediaScanner(Context context, String filePath, String fileName) {
        try {
            MediaScannerConnection.scanFile(context, new String[]{new File(DIRECTORY_DOWNLOADS + "/" + filePath + fileName).getAbsolutePath()},
                    null, (path, uri) -> {
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static AlertDialog alertDialog = null;

    public static void displayLoader(Context context) {
        if (alertDialog == null) {

            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
            View view = layoutInflaterAndroid.inflate(R.layout.dialog_loading, null);
            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setView(view);
            alertDialog = alert.create();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        }

    }

    public static void dismissLoader() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    public static void downloader(Context context, String downloadURL, String path, String fileName) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(context, "" + context.getString(R.string.dl_started), Toast.LENGTH_SHORT).show());

        String desc = context.getString(R.string.downloading);
        Uri Download_Uri = Uri.parse(downloadURL);


        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(true);
        request.setTitle(context.getString(R.string.app_name));
        request.setVisibleInDownloadsUi(true);
        request.setDescription(desc);
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, path + fileName);
        dm.enqueue(request);

        Utils.mediaScanner(context, path, fileName);
    }

    public static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public static boolean isVideoFile(Context context, String path) {
        if (path.startsWith("content")) {
            DocumentFile fromTreeUri = DocumentFile.fromSingleUri(context, Uri.parse(path));
            assert fromTreeUri != null;
            String mimeType = fromTreeUri.getType();
            return mimeType != null && mimeType.startsWith("video");
        } else {
            String mimeType = URLConnection.guessContentTypeFromName(path);
            return mimeType != null && mimeType.startsWith("video");
        }
    }

    public static boolean copyFileInSavedDir(Context context, String sourceFile, boolean isWApp) {

        String finalPath = getDir(isWApp).getAbsolutePath();

        String pathWithName = finalPath + File.separator + new File(sourceFile).getName();
        Uri destUri = Uri.fromFile(new File(pathWithName));

        InputStream is;
        OutputStream os;
        try {
            Uri uri = Uri.parse(sourceFile);
            is = context.getContentResolver().openInputStream(uri);
            os = context.getContentResolver().openOutputStream(destUri, "w");

            byte[] buffer = new byte[1024];

            int length;
            while ((length = is.read(buffer)) > 0)
                os.write(buffer, 0, length);

            is.close();
            os.flush();
            os.close();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(destUri);
            context.sendBroadcast(intent);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }


    static File getDir(boolean isWApp) {

        File rootFile = downloadWhatsAppDir;
        if (!isWApp) {
            rootFile = downloadWABusiDir;
        }
        boolean isMake = rootFile.mkdirs();
        if (isMake) Log.e("isMake ", "true");
        return rootFile;

    }

    public static boolean download(Context context, String sourceFile, boolean isWApp) {
        return copyFileInSavedDir(context, sourceFile, isWApp);
    }

    public static boolean isNullOrEmpty(String s) {
        return (s == null) || (s.length() == 0) || (s.equalsIgnoreCase("null")) || (s.equalsIgnoreCase("0"));
    }


   public static String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return jsonString;
    }
}
