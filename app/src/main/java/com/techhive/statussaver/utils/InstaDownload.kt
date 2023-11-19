package com.techhive.statussaver.utils

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.annotation.Keep
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.techhive.statussaver.model.response.ModelEdNode
import com.techhive.statussaver.model.response.ModelGetEdgetoNode
import com.techhive.statussaver.model.response.ModelResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.net.URI


object InstaDownload {


    private var Urlwi: String? = ""

    @Keep
    fun startInstaDownload(
        Url: String,
        activity: Activity,
        cookie: String
    ) {

        Utils.displayLoader(activity)


        try {

            val uri = URI(Url)
            Urlwi = URI(
                uri.scheme,
                uri.authority,
                uri.path,
                null,
                uri.fragment
            ).toString()


        } catch (ex: java.lang.Exception) {
            try {
                Utils.dismissLoader()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return
        }

        var urlwithoutlettersqp: String? = Urlwi

        if (urlwithoutlettersqp!!.contains("/reel/")) {
            urlwithoutlettersqp = urlwithoutlettersqp.replace("/reel/", "/p/")
        }

        if (urlwithoutlettersqp.contains("/tv/")) {
            urlwithoutlettersqp = urlwithoutlettersqp.replace("/tv/", "/p/")
        }

        val indexOf: Int = urlwithoutlettersqp.indexOf("/p/") + 3
        val substring: String =
            urlwithoutlettersqp.substring(indexOf, urlwithoutlettersqp.indexOf("/", indexOf))
        Log.e("link ", substring)
//        val urlwithoutlettersqp_noa: String = urlwithoutlettersqp

        if (!(activity).isFinishing) {

//            Log.e("UUUUUU ", urlwithoutlettersqp_noa);
//            Log.e("UUUUUU ", generateUserAgent());
            try {

                val varObj = JSONObject()
                varObj.put("shortcode", substring)
                varObj.put("precomposed_overlay", false)
                varObj.put("has_threaded_comments", true)


                downloadInstagramImageOrVideoResponseOkhttp(
                    varObj, activity, cookie
                )


            } catch (e: java.lang.Exception) {
                try {

                    Utils.dismissLoader()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                e.printStackTrace()
            }

        }

    }

    var myPhotoUrlIs: String? = null
    var myVideoUrlIs: String? = null

    @Keep
    fun downloadInstagramImageOrVideoResponseOkhttp(
        variable: JSONObject,
        activity: Activity,
        cookie: String
    ) {

//TODO check
//        Unirest.config()
//            .socketTimeout(500)
//            .connectTimeout(1000)
//            .concurrency(10, 5)
//            .proxy(Proxy("https://proxy"))
//            .setDefaultHeader("Accept", "application/json")
//            .followRedirects(false)
//            .enableCookieManagement(false)
//            .addInterceptor(MyCustomInterceptor())

        object : Thread() {
            override fun run() {


                try {

                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    // init OkHttpClient
                    val client: OkHttpClient = OkHttpClient.Builder()
//                        .cookieJar(cookieJar)
                        .addInterceptor(logging)
                        .build()

                    val request: Request = Request.Builder()
                        .url("https://www.instagram.com/graphql/query/?query_hash=8c1ccd0d1cab582bafc9df9f5983e80d&variables=$variable")
                        .method("GET", null)
                        .addHeader("Cookie", cookie)
//                        .addHeader("User-Agent", "Instagram 7.16.0 Android")
//                        .addHeader("User-Agent", "Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+")
                        .build()
                    val response = client.newCall(request).execute()

                    val ressd = response.body!!.string()
//                    Log.e("rrrrrrrrrrrr ",ressd);
                    var code = response.code
                    if (!ressd.contains("shortcode_media")) {
                        code = 400
                    }
                    if (code == 200) {


                        try {


                            val listType =
                                object : TypeToken<ModelResponse?>() {}.type
                            val modelInstagramResponse: ModelResponse? =
                                GsonBuilder().create()
                                    .fromJson<ModelResponse>(
                                        ressd,
                                        listType
                                    )


                            if (modelInstagramResponse != null) {


                                if (modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children != null) {
                                    val modelGetEdgetoNode: ModelGetEdgetoNode =
                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children

                                    val modelEdNodeArrayList: List<ModelEdNode> =
                                        modelGetEdgetoNode.modelEdNodes
                                    for (i in modelEdNodeArrayList.indices) {
                                        if (modelEdNodeArrayList[i].modelNode.isIs_video) {
                                            myVideoUrlIs =
                                                modelEdNodeArrayList[i].modelNode.video_url

                                            val timeStamp =
                                                System.currentTimeMillis().toString()
                                            val file = "Insta_$timeStamp"
                                            val ext = "mp4"
                                            val fileName = "$file.$ext"

                                            Utils.downloader(
                                                activity,
                                                myVideoUrlIs?.replace("\"", ""),
                                                Utils.instaDirPath,
                                                fileName
                                            )

                                            try {

                                                Utils.dismissLoader()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }


                                            myVideoUrlIs = ""
                                        } else {
                                            myPhotoUrlIs =
                                                modelEdNodeArrayList[i].modelNode.display_resources[modelEdNodeArrayList[i].modelNode.display_resources.size - 1].src


                                            val timeStamp =
                                                System.currentTimeMillis().toString()
                                            val file = "Insta_$timeStamp"
                                            val ext = "png"
                                            val fileName = "$file.$ext"

                                            Utils.downloader(
                                                activity,
                                                myPhotoUrlIs?.replace("\"", ""),
                                                Utils.instaDirPath,
                                                fileName
                                            )
                                            myPhotoUrlIs = ""
                                            try {

                                                Utils.dismissLoader()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }

                                        }
                                    }
                                } else {
                                    val isVideo =
                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.isIs_video
                                    if (isVideo) {
                                        myVideoUrlIs =
                                            modelInstagramResponse.modelGraphshortcode.shortcode_media.video_url


                                        val timeStamp = System.currentTimeMillis().toString()
                                        val file = "Insta_$timeStamp"
                                        val ext = "mp4"
                                        val fileName = "$file.$ext"

                                        Utils.downloader(
                                            activity,
                                            myVideoUrlIs?.replace("\"", ""),
                                            Utils.instaDirPath,
                                            fileName
                                        )

                                        try {

                                            Utils.dismissLoader()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        myVideoUrlIs = ""
                                    } else {
                                        myPhotoUrlIs =
                                            modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources[modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources.size - 1].src

                                        val timeStamp = System.currentTimeMillis().toString()
                                        val file = "Insta_$timeStamp"
                                        val ext = "png"
                                        val fileName = "$file.$ext"

                                        Utils.downloader(
                                            activity,
                                            myPhotoUrlIs?.replace("\"", ""),
                                            Utils.instaDirPath,
                                            fileName
                                        )
                                        try {
                                            Utils.dismissLoader()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        myPhotoUrlIs = ""
                                    }
                                }


                            } else {
                                Toast.makeText(
                                    activity,
                                    "error",
                                    Toast.LENGTH_SHORT
                                ).show()

                                try {
                                    Utils.dismissLoader()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }


                        } catch (e: Exception) {
//
                            Log.e("ddddddddddd ", e.message.toString())
                            Utils.dismissLoader()
                            activity.runOnUiThread {
                                Toast.makeText(
                                    activity,
                                    "Media not available for download",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
//
                        }

                    } else {
                        Utils.dismissLoader()
                        Log.e("ddddddddddd ", "else 200")
                        activity.runOnUiThread {
                            Toast.makeText(
                                activity,
                                "Media not available for download",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                } catch (e: Throwable) {
                    e.printStackTrace()
                    println("The request has failed " + e.message)
                    Utils.dismissLoader()
                    activity.runOnUiThread {
                        Toast.makeText(
                            activity,
                            "Media not available for download",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }.start()
    }

}