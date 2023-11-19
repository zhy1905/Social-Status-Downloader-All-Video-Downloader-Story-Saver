package com.techhive.statussaver;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.techhive.statussaver.adapter.HomeAppsAdapter;
import com.techhive.statussaver.databinding.ActivityHomeBinding;
import com.techhive.statussaver.model.Apps;
import com.techhive.statussaver.utils.AdManager;
import com.techhive.statussaver.utils.OnRecItemClickListener;
import com.techhive.statussaver.utils.SharedPrefs;
import com.techhive.statussaver.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.rossyn.iab.BillingConnector;
import com.rossyn.iab.BillingEventListener;
import com.rossyn.iab.enums.ProductType;
import com.rossyn.iab.models.BillingResponse;
import com.rossyn.iab.models.ProductInfo;
import com.rossyn.iab.models.PurchaseInfo;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks, OnRecItemClickListener {

    private static final int STORAGE_PERMISSION_CODE = 21;

    public static String[] permissionsList = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storge_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };

    Activity mActivity;
    Context mContext;


    private static final String PRODUCT_ID = "remove_ads_pro";

    public boolean isPro = false;

    private int purchase_price = 3;
    private BillingConnector billingConnector;

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
                Log.e("PPPPPP ", "Granted");
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                Log.e("PPPPPP ", "Post ");
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                // FCM SDK (and your app) can post notifications.
            });


    private AppUpdateManager appUpdateManager;
    private static final int FLEXIBLE_APP_UPDATE_REQ_CODE = 123;
    private InstallStateUpdatedListener installStateUpdatedListener;

    private ArrayList<Apps> listOfApps = new ArrayList<>();

    private HomeAppsAdapter homeAppsAdapter;
    Locale locale;

    ActivityHomeBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mContext= mActivity = this;


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
//                Log.e("AAA ", sharedText);
//                handleSendText(intent); // Handle text being sent
//                return;
            }
        }


        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());

        installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                removeInstallStateUpdateListener();
            } else {
                Toast.makeText(getApplicationContext(), "InstallStateUpdatedListener: state: " + state.installStatus(), Toast.LENGTH_LONG).show();
            }
        };


        init();


    }


    void init() {

        binding.galBtn.setOnClickListener(this);
        binding.settings.setOnClickListener(this);
        askNotificationPermission();

        initializeBillingClient();

        binding.imgRemoveAds.setOnClickListener(v -> {
            initPurchaseDialog();

        });

        isPro = SharedPrefs.getIsPro(mContext);
        Log.d("checkTest", "purchase " + isPro);


        Log.d("checkTest", "admob ads Adshow All  " + !SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow));
        Log.d("checkTest", "admob ads Adshow Inter " + !SharedPrefs.getBooleanValue(mContext, SharedPrefs.showInterstitial));

        if (!SharedPrefs.getIsPro(mContext) && SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow)) {
            binding.imgRemoveAds.setVisibility(View.VISIBLE);
            //admob
            AdManager.loadNativeAds((Activity) mContext, binding.flAdplaceholder, SharedPrefs.getStringValue(mContext, SharedPrefs.nativeAdId));
           Log.d("checkTest", "admob ads load banner");
            if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.showInterstitial)) {
                AdManager.loadInterAd(HomeActivity.this, SharedPrefs.getStringValue(mContext, SharedPrefs.interstitialAdId));
                Log.d("checkTest", "admob ads load inter");
            }
        } else {
            binding.imgRemoveAds.setVisibility(View.GONE);
        }
    }


    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            String[] perms = getPermissionsListForVersion();
            if (EasyPermissions.hasPermissions(this, perms)) {
                Utils.applyLanguage(mContext);
                // Already have permission, do the thing
                // ...

                if (sharedText.contains("josh")) {
                    Intent joshIntent = new Intent(mContext, JoshActivity.class);
                    joshIntent.putExtra("sharedText", sharedText);
                    startActivity(intent);
                } else if (sharedText.contains("instagram")) {
                    Intent instaIntent = new Intent(mContext, InstaActivity.class);
                    instaIntent.putExtra("sharedText", sharedText);
                    startActivity(intent);
                } else if (sharedText.contains("vimeo")) {
                    Intent vimeoIntent = new Intent(mContext, VimeoActivity.class);
                    vimeoIntent.putExtra("sharedText", sharedText);
                    startActivity(intent);
                } else if (sharedText.contains("triller")) {
                    Intent trillerIntent = new Intent(mContext, TrillerActivity.class);
                    trillerIntent.putExtra("sharedText", sharedText);
                    startActivity(intent);
                } else if (sharedText.contains("chingari")) {
                    Intent chingariIntent = new Intent(mContext, ChingariActivity.class);
                    chingariIntent.putExtra("sharedText", sharedText);
                    startActivity(intent);
                } else if (sharedText.contains("dailymotion")) {
                    Intent dailyMotionIntent = new Intent(mContext, DailyMotionActivity.class);
                    dailyMotionIntent.putExtra("sharedText", sharedText);
                    startActivity(intent);
                } else if (sharedText.contains("youtu")) {
                    Intent youyubeIntent = new Intent(mContext, YoutubeActivity.class);
                    youyubeIntent.putExtra("sharedText", sharedText);
                    startActivity(intent);
                } else if (sharedText.contains("Pin")) {
                    Intent pinIntent = new Intent(mContext, PinterestActivity.class);
                    pinIntent.putExtra("sharedText", sharedText);
                    startActivity(intent);
                }
            } else {
                // Do not have permissions, request them now
                EasyPermissions.requestPermissions(
                        this,
                        getString(R.string.rationale_storage),
                        STORAGE_PERMISSION_CODE,
                        perms);
            }
//            Log.e("AAA ", sharedText);

            // Update UI to reflect text being shared
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
//        Log.e("AAA ", "MMMMMMMMM");
        Utils.applyLanguage(newBase);
    }

    private void getJson() {
        String lang = SharedPrefs.getLanguage(mContext);
        if (lang.equals("")) {
            SharedPrefs.setLanguage(mContext, SharedPrefs.ENGLISH_LOCALE);
            lang = SharedPrefs.getLanguage(mContext);
        }

        listOfApps.clear();
        String jsonObjectString = Utils.getJsonFromAssets(mContext, "apps.json");
        if (jsonObjectString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonObjectString);
                JSONArray jsonArray = jsonObject.getJSONArray("apps");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject appObj = jsonArray.getJSONObject(i);
                    boolean isVisible = appObj.getBoolean("visible");
                    if (isVisible) {
                        Apps apps = new Apps();
                        apps.setId(appObj.getInt("id"));

                        String app_name = appObj.getString("app_name");
                        apps.setApp_name(app_name);

                        String name = appObj.getString("name");
                        String gujarati_name = appObj.getString("gujarati_name");
                        String hindi_name = appObj.getString("hindi_name");
                        switch (lang) {
                            case SharedPrefs.ENGLISH_LOCALE:
                                apps.setName(name);
                                break;
                            case SharedPrefs.GUJARATI_LOCALE:
                                apps.setName(gujarati_name);
                                break;
                            case SharedPrefs.HINDI_LOCALE:
                                apps.setName(hindi_name);
                                break;
                        }

                        apps.setBackgroundColor(appObj.getString("card_bg_color"));
                        String icon = appObj.getString("icon");
                        final int resourceId = mContext.getResources().getIdentifier(icon, "drawable", mContext.getPackageName());
                        apps.setIcon(resourceId);
                        listOfApps.add(apps);
                    }
                }
                binding.recycler.setLayoutManager(new GridLayoutManager(mContext, 2));
                homeAppsAdapter = new HomeAppsAdapter(mContext, listOfApps);
                binding.recycler.setAdapter(homeAppsAdapter);
                homeAppsAdapter.setClickListener(this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View v) {
        String[] perms = getPermissionsListForVersion();
        int id = v.getId();
        if (id == R.id.galBtn) {
            if (EasyPermissions.hasPermissions(this, perms)) {
                Utils.applyLanguage(mContext);
                startActivityes(new Intent(HomeActivity.this, MyGalleryActivity.class));
            } else {
                // Do not have permissions, request them now
                EasyPermissions.requestPermissions(
                        this,
                        getString(R.string.rationale_storage),
                        STORAGE_PERMISSION_CODE,
                        perms);
            }
        } else if (id == R.id.settings) {
            Utils.applyLanguage(mContext);
            startActivityes(new Intent(HomeActivity.this, SettingsActivity.class));
        }

    }

    void startActivityes(Intent intent) {
        if (!SharedPrefs.getIsPro(mContext)) {
            if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow) && SharedPrefs.getBooleanValue(mContext, SharedPrefs.showInterstitial)) {
                AdManager.adCounter++;
                AdManager.showInterAd(HomeActivity.this, SharedPrefs.getStringValue(mContext, SharedPrefs.interstitialAdId), intent, 0);
            } else {
                startActivity(intent);
            }
        } else {
            startActivity(intent);
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (this.doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000L);
    }


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

    public static String[] getPermissionsListForVersion() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storge_permissions_33;
        } else {
            p = permissionsList;
        }
        return p;
    }

    //    @AfterPermissionGranted(STORAGE_PERMISSION_CODE)
    private void reqPermission(String name) {
        String[] perms = getPermissionsListForVersion();
        if (EasyPermissions.hasPermissions(this, perms)) {
            Utils.applyLanguage(mContext);
            // Already have permission, do the thing
            // ...
            if (name.equalsIgnoreCase("Whatsapp")) {
                startActivityes(new Intent(HomeActivity.this, WAppActivity.class));
            } else if (name.equalsIgnoreCase("WA Business")) {
                startActivityes(new Intent(HomeActivity.this, WABusiActivity.class));
            } else if (name.equalsIgnoreCase("Josh")) {
                startActivityes(new Intent(HomeActivity.this, JoshActivity.class));
            } else if (name.equalsIgnoreCase("Instagram")) {
                startActivityes(new Intent(HomeActivity.this, InstaActivity.class));
            } else if (name.equalsIgnoreCase("")) {
                startActivityes(new Intent(HomeActivity.this, FacebookActivity.class));
            } else if (name.equalsIgnoreCase("Vimeo")) {
                startActivityes(new Intent(HomeActivity.this, VimeoActivity.class));
            } else if (name.equalsIgnoreCase("Triller")) {
                startActivityes(new Intent(HomeActivity.this, TrillerActivity.class));
            } else if (name.equalsIgnoreCase("Chingari")) {
                startActivityes(new Intent(HomeActivity.this, ChingariActivity.class));
            } else if (name.equalsIgnoreCase("Daily Motion")) {
                startActivityes(new Intent(HomeActivity.this, DailyMotionActivity.class));
            } else if (name.equalsIgnoreCase("youtube")) {
                startActivityes(new Intent(HomeActivity.this, YoutubeActivity.class));
            } else if (name.equalsIgnoreCase("Pinterest")) {
                startActivityes(new Intent(HomeActivity.this, PinterestActivity.class));
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_storage),
                    STORAGE_PERMISSION_CODE,
                    perms);
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d("TAG", "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d("TAG", "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }

    @Override
    public void onItemClick(View view, int position) {
        int id = view.getId();
        if (id == R.id.cardMain) {
            if (listOfApps.size() > 0) reqPermission(listOfApps.get(position).getApp_name());
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        binding.txtGallery.setText(getString(R.string.my_gallery));
        binding.txtAllApp.setText(getString(R.string.allApplication));
        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getJson();

        Locale locale = new Locale(SharedPrefs.getLanguage(mContext));
        Utils.changeLanguage(mContext, locale);


    }


    public void initPurchaseDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.app_purchase_dialog);
        dialog.getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);
        dialog.show();
        TextView txtPrice = dialog.findViewById(R.id.txtPrice);
        txtPrice.setText("" + purchase_price + "$");
        TextView txtPurchase = dialog.findViewById(R.id.txtPurchase);
        txtPurchase.setOnClickListener(view -> {
            removeAds();
            dialog.dismiss();
        });
        ImageView imageViewClose = dialog.findViewById(R.id.imageViewClose);
        imageViewClose.setOnClickListener(view -> dialog.dismiss());
    }


    private void removeAds() {
        billingConnector.purchase(mActivity, PRODUCT_ID);
    }

    private void initializeBillingClient() {
        List<String> nonConsumableIds = new ArrayList<>();
        nonConsumableIds.add(PRODUCT_ID);

        billingConnector = new BillingConnector(this, BuildConfig.LICENSE_KEY)
                .setNonConsumableIds(nonConsumableIds)
                .autoAcknowledge()
                .enableLogging()
                .connect();

        billingConnector.setBillingEventListener(new BillingEventListener() {
            @Override
            public void onProductsFetched(@NonNull List<ProductInfo> productDetails) {

            }

            //this IS the listener in which we can restore previous purchases
            @Override
            public void onPurchasedProductsFetched(@NonNull ProductType productType, @NonNull List<PurchaseInfo> purchases) {
                String purchasedProduct;
                boolean isAcknowledged;

                for (PurchaseInfo purchaseInfo : purchases) {
                    purchasedProduct = purchaseInfo.getProduct();
                    isAcknowledged = purchaseInfo.isAcknowledged();

                    if (!isPro) {
                        if (purchasedProduct.equalsIgnoreCase(PRODUCT_ID)) {
                            if (isAcknowledged) {
                                isPro = true;
                                //here we are saving the purchase status into our "userPrefersAdFree" variable
                                SharedPrefs.setIsPro(mContext, true);
                                binding.imgRemoveAds.setVisibility(View.GONE);
//                                Toast.makeText(mActivity, "The previous purchase was successfully restored.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }

            //this IS NOT the listener in which we'll give user entitlement for purchases (see ReadMe.md why)
            @Override
            public void onProductsPurchased(@NonNull List<PurchaseInfo> purchases) {

            }

            //this IS the listener in which we'll give user entitlement for purchases (the ReadMe.md explains why)
            @Override
            public void onPurchaseAcknowledged(@NonNull PurchaseInfo purchase) {
                String acknowledgedProduct = purchase.getProduct();

                if (acknowledgedProduct.equalsIgnoreCase(PRODUCT_ID)) {
                    isPro = true;
                    //here we are saving the purchase status into our "userPrefersAdFree" variable
                    SharedPrefs.setIsPro(mContext, true);
                    binding.imgRemoveAds.setVisibility(View.GONE);
                    reloadScreen();
                    Toast.makeText(mActivity, "The purchase was successfully made.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPurchaseConsumed(@NonNull PurchaseInfo purchase) {

            }

            @Override
            public void onBillingError(@NonNull BillingConnector billingConnector, @NonNull BillingResponse response) {
                switch (response.getErrorType()) {
                    case ACKNOWLEDGE_WARNING:
                        //this response will be triggered when the purchase is still PENDING
                        Toast.makeText(mActivity, "The transaction is still pending. Please come back later to receive the purchase!", Toast.LENGTH_SHORT).show();
                        break;
                    case BILLING_UNAVAILABLE:
                    case SERVICE_UNAVAILABLE:
                        Toast.makeText(mActivity, "Billing is unavailable at the moment. Check your internet connection!", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR:
                        Toast.makeText(mActivity, "Something happened, the transaction was canceled!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void reloadScreen() {
        //Reload the screen to activate the removeAd and remove the actual Ad off the screen.
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void checkUpdate() {

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            }
        });
    }

    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
// Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
// an activity result launcher registered via registerForActivityResult
                    updateFlowResultLauncher,
// Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
// flexible updates.
                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build());
//            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, FLEXIBLE_APP_UPDATE_REQ_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final ActivityResultLauncher<IntentSenderRequest> updateFlowResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                        if (result.getResultCode() == FLEXIBLE_APP_UPDATE_REQ_CODE) {
                            int resultCode = result.getResultCode();
                            Toast.makeText(getApplicationContext(), "Update Failed! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
                            checkUpdate();
                        }
                    });


    private void popupSnackBarForCompleteUpdate() {
        Snackbar.make(findViewById(android.R.id.content).getRootView(), "New Update is ready!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Install", view -> {
                    if (appUpdateManager != null) {
                        appUpdateManager.completeUpdate();
                    }
                })
                .setActionTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary))
                .show();
    }

    private void removeInstallStateUpdateListener() {
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }
}
