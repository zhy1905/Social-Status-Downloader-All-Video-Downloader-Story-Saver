package com.techhive.statussaver.workers;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.techhive.statussaver.R;
import com.techhive.statussaver.HomeActivity;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;

public class AllVideoDownloadWorker extends Worker {
    public static final String DIRECTORY = "DIRECTORY";
    public static final String DOWNLOAD_TYPE = "FORMAT";
    public static final String VIDEO_EXT = "VIDEO_EXT";
    public static final String VIDEO_URL = "VIDEO_URL";
    PendingIntent activityPendingIntent;
    NotificationManagerCompat mNotificationManager;
    NotificationCompat.Builder notifBuilder;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Context mContext;

    public AllVideoDownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    public ListenableWorker.Result doWork() {
        String downloadType = getInputData().getString(DOWNLOAD_TYPE);
        String directory = getInputData().getString(DIRECTORY);
        String videoUrl = getInputData().getString("VIDEO_URL");
        String videoExt = getInputData().getString(VIDEO_EXT);

        Log.e("FFFFF ", videoUrl);
        File downloadDirectory = null;
        if (directory != null) {
            downloadDirectory = new File(directory);
        }

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("CHANNEL",
                    "Download status",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }

//        makeNotificationChannel("CHANNEL", "Download status", 3);
        mNotificationManager = NotificationManagerCompat.from(getApplicationContext());
        notifBuilder = new NotificationCompat.Builder(getApplicationContext(), "CHANNEL");
        Intent activityIntent = new Intent(getApplicationContext(), HomeActivity.class);
        activityIntent.putExtra("fragment", "fragmentFBVideoDownloader");
        activityIntent.addCategory("android.intent.category.LAUNCHER");
        activityIntent.setClass(getApplicationContext(), HomeActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        TaskStackBuilder.create(getApplicationContext()).addNextIntentWithParentStack(activityIntent);
        activityPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, 201326592);
        if (downloadDirectory == null || downloadDirectory.exists() || downloadDirectory.mkdirs()) {

            YoutubeDLRequest request = new YoutubeDLRequest(videoUrl != null ? videoUrl : "");

            notifyDownloading(this.mNotificationManager, this.notifBuilder, "Downloading", this.activityPendingIntent);


          /*  String cookie =  InstaPref.getInstance(mContext).getString(InstaPref.FBCOOKIES);
            Log.e("cookie ",cookie);*/

            request.addOption("--no-mtime");
//            request.addOption("--cookies", cookie);
            request.addOption("--downloader", "libaria2c.so");
            request.addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"");
            request.addOption("-f", "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best");
//            request.addOption("-f", "bestvideo[ext=mp4]/mp4");
//            request.addOption("-f", "b");
            request.addOption("-o", directory + "/%(title)s.%(ext)s");

            final String processId = "MyProcessDownloadId";

            Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().execute(request, processId, (progress, aLong, s) -> Unit.INSTANCE))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(youtubeDLResponse -> {
                        MediaScannerConnection.scanFile(mContext,
                                new String[]{directory.toString()}, null,
                                (path, uri) -> {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> uri=" + uri);
                                });
                        sucessNoti();
                        Toast.makeText(getApplicationContext(), mContext.getResources().getString(R.string.download_complete), Toast.LENGTH_LONG).show();
                    }, e -> {

                        Log.e("TAG", "failed to download", e);

                        failNoti();
                        Toast.makeText(getApplicationContext(), mContext.getResources().getString(R.string.download_failed), Toast.LENGTH_LONG).show();
                    });
            compositeDisposable.add(disposable);

            System.out.println("Started downloading");
        } else {
            notifyFail(this.mNotificationManager, this.notifBuilder, this.activityPendingIntent);
        }
        return ListenableWorker.Result.failure();
    }

    private Result sucessNoti() {
        notifySuccess(this.mNotificationManager, this.notifBuilder, this.activityPendingIntent);
        return ListenableWorker.Result.success();
    }

    private Result failNoti() {
        this.mNotificationManager.cancelAll();
        notifyFail(this.mNotificationManager, this.notifBuilder, this.activityPendingIntent);
        return ListenableWorker.Result.failure();
    }

    @Override
    public void onStopped() {
        System.out.println("STOOOOOOP");
        YoutubeDL.getInstance().destroyProcessById("MyProcessDownloadId");
        try {
            TimeUnit.MILLISECONDS.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.mNotificationManager.cancelAll();
        notifyStop(this.mNotificationManager, this.notifBuilder, this.activityPendingIntent);
        super.onStopped();
    }

    public void notifyDownloading(NotificationManagerCompat managerCompat, NotificationCompat.Builder builder, String text, PendingIntent mPendingIntent) {
        builder.setContentIntent(mPendingIntent).setSmallIcon(R.drawable.app_icon)
                .setContentTitle(getApplicationContext().getResources().getString(R.string.app_name))
                .setContentText(text).setProgress(0, 0, true)
                .setOnlyAlertOnce(true).setPriority(PRIORITY_HIGH);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        managerCompat.notify(1, builder.build());
    }


    public void notifyFail(NotificationManagerCompat managerCompat, NotificationCompat.Builder builder, PendingIntent mPendingIntent) {
        builder.setContentIntent(mPendingIntent).setSmallIcon(R.drawable.app_icon)
                .setContentText(getApplicationContext().getResources().getString(R.string.text_notif_download_error)).setProgress(0, 0, false);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        managerCompat.notify(1, builder.build());
    }


    public void notifySuccess(NotificationManagerCompat managerCompat, NotificationCompat.Builder builder, PendingIntent mPendingIntent) {
        builder.setContentIntent(mPendingIntent).setSmallIcon(R.drawable.app_icon)
                .setContentText(getApplicationContext().getResources().getString(R.string.text_notif_download_finished)).setProgress(0, 0, false);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        managerCompat.notify(1, builder.build());
    }


    public void notifyStop(NotificationManagerCompat managerCompat, NotificationCompat.Builder builder, PendingIntent mPendingIntent) {
        builder.setContentIntent(mPendingIntent).setSmallIcon(R.drawable.app_icon)
                .setContentText(getApplicationContext().getResources().getString(R.string.text_notif_stop)).setProgress(0, 0, false);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        managerCompat.notify(1, builder.build());
    }

}
