package at.huber.me;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

import com.evgenii.jsevaluator.JsEvaluator;
import com.evgenii.jsevaluator.interfaces.JsCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.huber.youtubeExtractor.Format;
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YtFile;

/**
 * Created by Ashwani Kumar Singh on 03,May,2023.
 */
public class YouTubeUri {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.98 Safari/537.36";

    private static final Pattern patYouTubePageLink = Pattern.compile("(http|https)://(www\\.|m.|)youtube\\.com/watch\\?v=(.+?)( |\\z|&)");
    public static final Pattern patYouTubeShortLink = Pattern.compile("(http|https)://(www\\.|)youtu.be/(.+?)( |\\z|&)");

    public static final Pattern patPlayerResponse = Pattern.compile("var ytInitialPlayerResponse\\s*=\\s*(\\{.+?\\})\\s*;");
    private static final Pattern patSigEncUrl = Pattern.compile("url=(.+?)(\\u0026|$)");
    private static final Pattern patSignature = Pattern.compile("s=(.+?)(\\u0026|$)");

    private static final Pattern patVariableFunction = Pattern.compile("([{; =])([a-zA-Z$][a-zA-Z0-9$]{0,2})\\.([a-zA-Z$][a-zA-Z0-9$]{0,2})\\(");
    private static final Pattern patFunction = Pattern.compile("([{; =])([a-zA-Z$_][a-zA-Z0-9$]{0,2})\\(");

    private static final Pattern patDecryptionJsFile = Pattern.compile("\\\\/s\\\\/player\\\\/([^\"]+?)\\.js");
    private static final Pattern patDecryptionJsFileWithoutSlash = Pattern.compile("/s/player/([^\"]+?).js");
    private static final Pattern patSignatureDecFunction = Pattern.compile("(?:\\b|[^a-zA-Z0-9$])([a-zA-Z0-9$]{1,4})\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\"\"\\s*\\)");

    private static final SparseArray<Format> FORMAT_MAP = new SparseArray<>();

    static boolean CACHING = true;
    static boolean LOGGING = false;

    private final static String LOG_TAG = "YouTubeExtractor";
    private final static String CACHE_FILE_NAME = "decipher_js_funct";

//    private String videoID;
    private VideoMeta videoMeta;

    public VideoMeta getVideoMeta() {
        return videoMeta;
    }

    private final String cacheDirPath;

    private volatile String decipheredSignature;

    private static String decipherJsFileName;
    private static String decipherFunctions;
    private static String decipherFunctionName;

    private final Lock lock = new ReentrantLock();
    private final Condition jsExecuting = lock.newCondition();

    private Context context;


    public YouTubeUri(Context con) {
        cacheDirPath = con.getCacheDir().getAbsolutePath();
        this.context = con;
    }

    public SparseArray<YtFile> getStreamUrls(String videoID) throws IOException, InterruptedException, JSONException {

        String pageHtml;
        SparseArray<String> encSignatures = new SparseArray<>();
        SparseArray<YtFile> ytFiles = new SparseArray<>();

        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;
        URL getUrl = new URL("https://youtube.com/watch?v=" + videoID);
        try {
            urlConnection = (HttpURLConnection) getUrl.openConnection();
            urlConnection.setRequestProperty("User-Agent", USER_AGENT);
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sbPageHtml = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sbPageHtml.append(line);
            }
            pageHtml = sbPageHtml.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        Matcher mat = patPlayerResponse.matcher(pageHtml);
        if (mat.find()) {
            JSONObject ytPlayerResponse = new JSONObject(mat.group(1));
            JSONObject streamingData = ytPlayerResponse.getJSONObject("streamingData");

            JSONArray formats = streamingData.getJSONArray("formats");
            for (int i = 0; i < formats.length(); i++) {

                JSONObject format = formats.getJSONObject(i);

                // FORMAT_STREAM_TYPE_OTF(otf=1) requires downloading the init fragment (adding
                // `&sq=0` to the URL) and parsing emsg box to determine the number of fragment that
                // would subsequently requested with (`&sq=N`) (cf. youtube-dl)
                String type = format.optString("type");
                if (type != null && type.equals("FORMAT_STREAM_TYPE_OTF"))
                    continue;

                int itag = format.getInt("itag");

                if (FORMAT_MAP.get(itag) != null) {
                    if (format.has("url")) {
                        String url = format.getString("url").replace("\\u0026", "&");
                        ytFiles.append(itag, new YtFile(FORMAT_MAP.get(itag), url));
                    } else if (format.has("signatureCipher")) {

                        mat = patSigEncUrl.matcher(format.getString("signatureCipher"));
                        Matcher matSig = patSignature.matcher(format.getString("signatureCipher"));
                        if (mat.find() && matSig.find()) {
                            String url = URLDecoder.decode(mat.group(1), "UTF-8");
                            String signature = URLDecoder.decode(matSig.group(1), "UTF-8");
                            ytFiles.append(itag, new YtFile(FORMAT_MAP.get(itag), url));
                            encSignatures.append(itag, signature);
                        }
                    }
                }
            }

            JSONArray adaptiveFormats = streamingData.getJSONArray("adaptiveFormats");
            for (int i = 0; i < adaptiveFormats.length(); i++) {

                JSONObject adaptiveFormat = adaptiveFormats.getJSONObject(i);

                String type = adaptiveFormat.optString("type");
                if (type != null && type.equals("FORMAT_STREAM_TYPE_OTF"))
                    continue;

                int itag = adaptiveFormat.getInt("itag");

                if (FORMAT_MAP.get(itag) != null) {
                    if (adaptiveFormat.has("url")) {
                        String url = adaptiveFormat.getString("url").replace("\\u0026", "&");
                        ytFiles.append(itag, new YtFile(FORMAT_MAP.get(itag), url));
                    } else if (adaptiveFormat.has("signatureCipher")) {

                        mat = patSigEncUrl.matcher(adaptiveFormat.getString("signatureCipher"));
                        Matcher matSig = patSignature.matcher(adaptiveFormat.getString("signatureCipher"));
                        if (mat.find() && matSig.find()) {
                            String url = URLDecoder.decode(mat.group(1), "UTF-8");
                            String signature = URLDecoder.decode(matSig.group(1), "UTF-8");
                            ytFiles.append(itag, new YtFile(FORMAT_MAP.get(itag), url));
                            encSignatures.append(itag, signature);
                        }
                    }
                }
            }

            JSONObject videoDetails = ytPlayerResponse.getJSONObject("videoDetails");
            this.videoMeta = new VideoMeta(videoDetails.getString("videoId"),
                    videoDetails.getString("title"),
                    videoDetails.getString("author"),
                    videoDetails.getString("channelId"),
                    Long.parseLong(videoDetails.getString("lengthSeconds")),
                    Long.parseLong(videoDetails.getString("viewCount")),
                    videoDetails.getBoolean("isLiveContent"),
                    videoDetails.getString("shortDescription"));

        } else {
            Log.d(LOG_TAG, "ytPlayerResponse was not found");
        }

        if (encSignatures.size() > 0) {

            String curJsFileName;

            if (CACHING
                    && (decipherJsFileName == null || decipherFunctions == null || decipherFunctionName == null)) {
                readDecipherFunctFromCache();
            }

            mat = patDecryptionJsFile.matcher(pageHtml);
            if (!mat.find())
                mat = patDecryptionJsFileWithoutSlash.matcher(pageHtml);
            if (mat.find()) {
                curJsFileName = mat.group(0).replace("\\/", "/");
                if (decipherJsFileName == null || !decipherJsFileName.equals(curJsFileName)) {
                    decipherFunctions = null;
                    decipherFunctionName = null;
                }
                decipherJsFileName = curJsFileName;
            }

            if (LOGGING)
                Log.d(LOG_TAG, "Decipher signatures: " + encSignatures.size() + ", videos: " + ytFiles.size());

            String signature;
            decipheredSignature = null;
            if (decipherSignature(encSignatures)) {
                lock.lock();
                try {
                    jsExecuting.await(7, TimeUnit.SECONDS);
                } finally {
                    lock.unlock();
                }
            }

            signature = decipheredSignature;
            if (signature == null) {
                return null;
            } else {
                String[] sigs = signature.split("\n");
                for (int i = 0; i < encSignatures.size() && i < sigs.length; i++) {
                    int key = encSignatures.keyAt(i);
                    String url = ytFiles.get(key).getUrl();
                    url += "&sig=" + sigs[i];
                    YtFile newFile = new YtFile(FORMAT_MAP.get(key), url);
                    ytFiles.put(key, newFile);
                }
            }
        }

        if (ytFiles.size() == 0) {
            if (LOGGING)
                Log.d(LOG_TAG, pageHtml);
            return null;
        }

        return ytFiles;
    }



    private void readDecipherFunctFromCache() {
        File cacheFile = new File(cacheDirPath + "/" + CACHE_FILE_NAME);
        // The cached functions are valid for 2 weeks
        if (cacheFile.exists() && (System.currentTimeMillis() - cacheFile.lastModified()) < 1209600000) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(cacheFile), "UTF-8"));
                decipherJsFileName = reader.readLine();
                decipherFunctionName = reader.readLine();
                decipherFunctions = reader.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



    private boolean decipherSignature(final SparseArray<String> encSignatures) throws IOException {
        // Assume the functions don't change that much
        if (decipherFunctionName == null || decipherFunctions == null) {
            String decipherFunctUrl = "https://youtube.com" + decipherJsFileName;

            BufferedReader reader = null;
            String javascriptFile;
            URL url = new URL(decipherFunctUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", USER_AGENT);
            try {
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append(" ");
                }
                javascriptFile = sb.toString();
            } finally {
                if (reader != null)
                    reader.close();
                urlConnection.disconnect();
            }

            if (LOGGING)
                Log.d(LOG_TAG, "Decipher FunctURL: " + decipherFunctUrl);
            Matcher mat = patSignatureDecFunction.matcher(javascriptFile);
            if (mat.find()) {
                decipherFunctionName = mat.group(1);
                if (LOGGING)
                    Log.d(LOG_TAG, "Decipher Functname: " + decipherFunctionName);

                Pattern patMainVariable = Pattern.compile("(var |\\s|,|;)" + decipherFunctionName.replace("$", "\\$") +
                        "(=function\\((.{1,3})\\)\\{)");

                String mainDecipherFunct;

                mat = patMainVariable.matcher(javascriptFile);
                if (mat.find()) {
                    mainDecipherFunct = "var " + decipherFunctionName + mat.group(2);
                } else {
                    Pattern patMainFunction = Pattern.compile("function " + decipherFunctionName.replace("$", "\\$") +
                            "(\\((.{1,3})\\)\\{)");
                    mat = patMainFunction.matcher(javascriptFile);
                    if (!mat.find())
                        return false;
                    mainDecipherFunct = "function " + decipherFunctionName + mat.group(2);
                }

                int startIndex = mat.end();

                for (int braces = 1, i = startIndex; i < javascriptFile.length(); i++) {
                    if (braces == 0 && startIndex + 5 < i) {
                        mainDecipherFunct += javascriptFile.substring(startIndex, i) + ";";
                        break;
                    }
                    if (javascriptFile.charAt(i) == '{')
                        braces++;
                    else if (javascriptFile.charAt(i) == '}')
                        braces--;
                }
                decipherFunctions = mainDecipherFunct;
                // Search the main function for extra functions and variables
                // needed for deciphering
                // Search for variables
                mat = patVariableFunction.matcher(mainDecipherFunct);
                while (mat.find()) {
                    String variableDef = "var " + mat.group(2) + "={";
                    if (decipherFunctions.contains(variableDef)) {
                        continue;
                    }
                    startIndex = javascriptFile.indexOf(variableDef) + variableDef.length();
                    for (int braces = 1, i = startIndex; i < javascriptFile.length(); i++) {
                        if (braces == 0) {
                            decipherFunctions += variableDef + javascriptFile.substring(startIndex, i) + ";";
                            break;
                        }
                        if (javascriptFile.charAt(i) == '{')
                            braces++;
                        else if (javascriptFile.charAt(i) == '}')
                            braces--;
                    }
                }
                // Search for functions
                mat = patFunction.matcher(mainDecipherFunct);
                while (mat.find()) {
                    String functionDef = "function " + mat.group(2) + "(";
                    if (decipherFunctions.contains(functionDef)) {
                        continue;
                    }
                    startIndex = javascriptFile.indexOf(functionDef) + functionDef.length();
                    for (int braces = 0, i = startIndex; i < javascriptFile.length(); i++) {
                        if (braces == 0 && startIndex + 5 < i) {
                            decipherFunctions += functionDef + javascriptFile.substring(startIndex, i) + ";";
                            break;
                        }
                        if (javascriptFile.charAt(i) == '{')
                            braces++;
                        else if (javascriptFile.charAt(i) == '}')
                            braces--;
                    }
                }

                if (LOGGING)
                    Log.d(LOG_TAG, "Decipher Function: " + decipherFunctions);
                decipherViaWebView(encSignatures);
                if (CACHING) {
                    writeDeciperFunctToChache();
                }
            } else {
                return false;
            }
        } else {
            decipherViaWebView(encSignatures);
        }
        return true;
    }


    private void writeDeciperFunctToChache() {
        File cacheFile = new File(cacheDirPath + "/" + CACHE_FILE_NAME);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cacheFile), "UTF-8"));
            writer.write(decipherJsFileName + "\n");
            writer.write(decipherFunctionName + "\n");
            writer.write(decipherFunctions);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void decipherViaWebView(final SparseArray<String> encSignatures) {
        final Context context = this.context;
        if (context == null) {
            return;
        }

        final StringBuilder stb = new StringBuilder(decipherFunctions + " function decipher(");
        stb.append("){return ");
        for (int i = 0; i < encSignatures.size(); i++) {
            int key = encSignatures.keyAt(i);
            if (i < encSignatures.size() - 1)
                stb.append(decipherFunctionName).append("('").append(encSignatures.get(key)).
                        append("')+\"\\n\"+");
            else
                stb.append(decipherFunctionName).append("('").append(encSignatures.get(key)).
                        append("')");
        }
        stb.append("};decipher();");

        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                new JsEvaluator(context).evaluate(stb.toString(), new JsCallback() {
                    @Override
                    public void onResult(String result) {
                        lock.lock();
                        try {
                            decipheredSignature = result;
                            jsExecuting.signal();
                        } finally {
                            lock.unlock();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        lock.lock();
                        try {
                            if (LOGGING)
                                Log.e(LOG_TAG, errorMessage);
                            jsExecuting.signal();
                        } finally {
                            lock.unlock();
                        }
                    }
                });
            }
        });
    }



}
