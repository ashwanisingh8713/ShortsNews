package com.ns.pref;

import android.content.Context;
import android.content.SharedPreferences;


public class DefaultPref {


    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private static DefaultPref mUser;
    private static long SYNC_UP_DURATION = 0l;

    private DefaultPref(Context context) {
        try {
            mPreferences = context.getSharedPreferences("DefaultPref.xml", Context.MODE_PRIVATE);
            mEditor = mPreferences.edit();
        } catch (NumberFormatException e) {

        }

    }

    public static final DefaultPref getInstance(Context context) {
        if(mUser == null) {
            try {
                mUser = new DefaultPref(context);
            } catch (Exception e) {

            }
        }

        return mUser;
    }


    public void setIsFullScreenAdLoaded(boolean isFullScreenAdLoaded) {
        mEditor.putBoolean("isFullScreenAdLoaded", isFullScreenAdLoaded);
        mEditor.apply();
    }

    public boolean isFullScreenAdLoaded() {
        return mPreferences.getBoolean("isFullScreenAdLoaded", false);
    }

    public void setInterstetial_Ads_Shown(boolean Interstetial_Ads_Shown) {
        mEditor.putBoolean("Interstetial_Ads_Shown", Interstetial_Ads_Shown);
        mEditor.commit();
    }

    public boolean getInterstetial_Ads_Shown() {
        return mPreferences.getBoolean("Interstetial_Ads_Shown", false);
    }


    public void setNotificationEnable(boolean isNotificationEnable) {
        mEditor.putBoolean("isNotificationEnable", isNotificationEnable);
        mEditor.apply();
    }

    public boolean isNotificationEnable() {
        return mPreferences.getBoolean("isNotificationEnable", true);
    }

    public void setHomeArticleOptionScreenShown(boolean isHomeArticleOptionScreenShown) {
        mEditor.putBoolean("isHomeArticleOptionScreenShown", isHomeArticleOptionScreenShown);
        mEditor.apply();
    }

    public boolean isHomeArticleOptionScreenShown() {
        return mPreferences.getBoolean("isHomeArticleOptionScreenShown", false);
    }

    public void setDescriptionSize(int size) {
        mEditor.putInt("current_size", size);
        mEditor.commit();
    }

    public int getDescriptionSize() {
        return mPreferences.getInt("current_size", 2);
    }


    public boolean isUserSelectedDfpConsent() {
        return mPreferences.getBoolean("isUserSelectedDfpConsent", false);
    }

    public boolean isDfpConsentExecuted() {
        return mPreferences.getBoolean("isDfpConsentExecuted", false);
    }

    public boolean isUserFromEurope() {
        return false;
//        return mPreferences.getBoolean("isUserFromEurope", false);
    }


    public void setUserSelectedDfpConsent(boolean isUserSelectedDfpConsent) {
        mEditor.putBoolean("isUserSelectedDfpConsent", isUserSelectedDfpConsent);
        mEditor.apply();
    }

    public void setDfpConsentExecuted(boolean isDfpConsentExecuted) {
        mEditor.putBoolean("isDfpConsentExecuted", isDfpConsentExecuted);
        mEditor.apply();
    }

    public void setUserFromEurope(boolean isUserFromEurope) {
        mEditor.putBoolean("isUserFromEurope", isUserFromEurope);
        mEditor.apply();
    }

    public void enableDeviceType() {
        mEditor.putBoolean("deviceType", true);
        mEditor.apply();
    }

    public void disableDeviceType() {
        mEditor.putBoolean("deviceType", false);
        mEditor.apply();
    }

    public boolean isDeviceTypeEnabled() {
        return mPreferences.getBoolean("deviceType", false);
    }


    public void setSelectedLocale(String locale) {
        mEditor.putString("locale", locale);
        mEditor.commit();
    }

    public String getSelectedLocale() {
        return mPreferences.getString("locale", "en");
    }

    public void setLanguageSupportTTS(int isLanguageSupportTTS) {
        mEditor.putInt("isLanguageSupportTTS", isLanguageSupportTTS);
        mEditor.commit();
    }

    public int isLanguageSupportTTS() {
        return mPreferences.getInt("isLanguageSupportTTS", -1);
    }

    public void setUserTheme(boolean isDayTheme) {
        mEditor.putBoolean("isDayTheme", isDayTheme);
        mEditor.commit();
    }

    public boolean isUserThemeDay() {
        return mPreferences.getBoolean("isDayTheme", true);
    }


     /*
    save permission dialog cancel
     */
    public void savePermissionDialogPreference(boolean value) {
        mEditor.putBoolean("userDialogPreference", value);
        mEditor.commit();
    }

    public boolean getPermissionDialogPreference() {
        return mPreferences.getBoolean("userDialogPreference",false );
    }

//    MP Preferences start
    public void setMeteredPaywallEnabled(boolean isMpFeatureEnabled) {
        mEditor.putBoolean("isMeteredPaywallEnabled", isMpFeatureEnabled);
        mEditor.commit();
    }

    public boolean isMeteredPaywallEnabled() {
        return mPreferences.getBoolean("isMeteredPaywallEnabled",false );
    }

    /*Get Start time in Millis*/
    public long getMPStartTimeInMillis() {
        return mPreferences.getLong("mpStartTimeInMillis", 0);
    }


    public void setConfigurationOnceLoaded(boolean isConfigurationOnceLoaded) {
        mEditor.putBoolean("isConfigurationOnceLoaded", isConfigurationOnceLoaded);
        mEditor.apply();
    }

    public boolean isConfigurationOnceLoaded() {
        return mPreferences.getBoolean("isConfigurationOnceLoaded", false);
    }

//    Set and get any preferences start
    public void putBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.apply();
    }

    public boolean getBoolean(String key) {
        return mPreferences.getBoolean(key, false);
    }
//    Set and get any preferences end


    public void setDefaultContentBaseUrl(String defaultContentBaseUrl) {
        mEditor.putString("defaultContentBaseUrl", defaultContentBaseUrl);
        mEditor.commit();
    }

    public String getDefaultContentBaseUrl() {
        return mPreferences.getString("defaultContentBaseUrl", "");
    }

    public void setNewsDigestUrl(String newsDigestUrl) {
        mEditor.putString("newsDigestUrl", newsDigestUrl);
        mEditor.commit();
    }

    public String getNewsDigestUrl() {
        return mPreferences.getString("newsDigestUrl", "");
    }

    public void setOldBookmarkLoaded(boolean isOldBookmarkLoaded) {
        mEditor.putBoolean("isOldBookmarkLoaded", isOldBookmarkLoaded);
        mEditor.apply();
    }

    public boolean isOldBookmarkLoaded() {
        return mPreferences.getBoolean("isOldBookmarkLoaded", false);
    }

    /*public void setUserJourneyLoaded(boolean isUserJourneyLoaded) {
        mEditor.putBoolean("isUserJourneyLoaded", isUserJourneyLoaded);
        mEditor.apply();
    }*/

    public boolean isUserJourneyLoaded() {
        return mPreferences.getBoolean("isUserJourneyLoaded", true);
    }

    public void setLocationEnabled(boolean isEnabled) {
        mEditor.putBoolean("LOCATION_ENABLE", isEnabled);
        mEditor.apply();
    }

    public boolean isLocationEnabled() {
        return mPreferences.getBoolean("LOCATION_ENABLE", false);
    }

    public void saveSectionSyncTimePref(String sectionId) {
        mEditor.putLong(sectionId, System.currentTimeMillis());
        mEditor.apply();
    }

    public long getLastUpdateTime(String sectionId) {
        return mPreferences.getLong(sectionId, 1484567729999l);
    }

    public void clearLastUpdateTime(String sectionId) {
        mEditor.putLong(sectionId, 1484567729999l);
        mEditor.commit();
    }


    public void saveLanguageName(String languageName) {
        mEditor.putString("languageName", languageName);
        mEditor.apply();
    }

    public String getLanguageName() {
        return mPreferences.getString("languageName", "");
    }

    public void saveLanguageId(String languageId) {
        mEditor.putString("languageId", languageId);
        mEditor.apply();
    }

    public String getLanguageId() {
        return mPreferences.getString("languageId", "");
    }
    public void saveLanguageDomain(String languageDomain) {
        mEditor.putString("languageDomain", languageDomain);
        mEditor.apply();
    }

    public String getLanguageDomain() {
        return mPreferences.getString("languageDomain", "");
    }

    public void setConfigLUT(String configLut) {
        mEditor.putString("configLut", configLut);
        mEditor.commit();
    }

    public String getConfigLUT() {
        return mPreferences.getString("configLut", "");
    }

    public void setSectionListLUT(String sectionListLut) {
        mEditor.putString("sectionListLut", sectionListLut);
        mEditor.commit();
    }

    public String getSectionListLUT() {
        return mPreferences.getString("sectionListLut", "");
    }

    public void setConfigurationId(String configId) {
        mEditor.putString("configId", configId);
        mEditor.commit();
    }



    public void setUserToken(String json) {
        mEditor.putString("user_token", json);
        mEditor.commit();
    }

    public String getUserToken() {
        return mPreferences.getString("user_token", null);
    }

    public void removeUserToken() {
        mPreferences.edit().remove("user_token").apply();
    }


    public boolean isForceUpdateCheckExecuted() {
        return mPreferences.getBoolean("isForceUpdateCheckExecuted", false);
    }

    public void setForceUpdateCheckExecuted(boolean isForceUpdateCheckExecuted) {
        mEditor.putBoolean("isForceUpdateCheckExecuted", isForceUpdateCheckExecuted);
        mEditor.commit();
    }


    public void setAppUpdateType(String updateType) {
        mEditor.putString("updateType", updateType);
        mEditor.apply();
    }

    public void setIsEUUserAdsFree(boolean isUserAdsFree) {
        mEditor.putBoolean("isUserAdsFree", isUserAdsFree);
        mEditor.commit();
    }

    public boolean isEUUserAdsFree() {
        return mPreferences.getBoolean("isUserAdsFree", false);
    }

    public String getAppUpdateType() {
        return mPreferences.getString("updateType", "FLEXIBLE");
    }

    /*
     * From v6.2 or demerged build, if user is not logged in set True - consider user as demerged user.
     * If token is available or user logged in found from v6.1 or below, then call Piano.refreshToken() and set True.
     */
    public void setIsUserDemerged(boolean isUserDemerged) {
        mEditor.putBoolean("isUserDemerged", isUserDemerged);
        mEditor.apply();
    }

    public boolean isUserDemerged() {
        return mPreferences.getBoolean("isUserDemerged", false);
    }

    public String getPurchaseData() {
        return mPreferences.getString("purchaseData", null);
    }

    //This to store user's last purchase history until Piano Payment Receipt API is success
    public void setPurchaseData(String purchaseData) {
        mEditor.putString("purchaseData", purchaseData);
        mEditor.apply();
    }

    public String getTermId() {
        return mPreferences.getString("termId", null);
    }

    public void setTermId(String termId) {
        mEditor.putString("termId", termId);
        mEditor.apply();
    }

    public String getLastPurchaseUserId() {
        return mPreferences.getString("pUserId", null);
    }

    public void setLastPurchaseUserId(String termId) {
        mEditor.putString("pUserId", termId);
        mEditor.apply();
    }

    public void clearLastPaymentData() {
        mEditor.putString("purchaseData", null);
        mEditor.putString("termId", null);
        mEditor.putString("pUserId", null);
        mEditor.apply();
    }

}
