package com.ns.shortsnews.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ns.shortsnews.domain.models.LanguageData
import com.ns.shortsnews.domain.models.LanguageTable
import com.ns.shortsnews.domain.models.VideoCategory

object AppPreference {

    private lateinit var preference: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    //Keys
    private const val IS_USER_LOGGED_IN = "is_user_logged_in"
    private const val USER_NAME = "user_name"
    private const val USER_AGE = "user_age"
    private const val USER_LOCATION = "user_location"
    private const val USER_EMAIL = "user_email"
    private const val USER_TOKEN = "user_token"
    private const val USER_PROFILE_IMAGE = "user_profile_image"
    private const val EMPTY_STRING = ""
    private const val IS_PROFILE_UPDATED = "is_profile_updated"
    private const val LANGUAGES_SELECTED = "selected_languag"
    private const val IS_LANGUAGE_SELECTED = "is_language_selected"
    private const val IS_PROFILE_DELETED = "profile_cleared"
    private const val UPDATE_NEEDED = "update_needed"
    private const val FOLLOWING_UPDATE_NEEDED = "following_update_needed"
    private const val MAIN_ACTIVITY_LAUNCHED = "main_activity_launched"
    private const val NOTIFICATION_TOKEN = "notification_token"
    private const val VIDEO_CATEGORIES = "video_category"
    private const val IS_REFRESS_REQUIRED = "is_refresh_required"
    private const val IS_MODIFIED = "is_modified"
    private const val IS_INTEREST_UPDATE_NEEDED = "is_interest_update_needed"
    private const val USER_SELECTIONS = "user_selection"
    private const val SELECTED_LANGUAGES = "selected_languages_list"
    private const val IS_FRIST_TIME = "first_time"


    fun init(context: Context) {
        preference = PreferenceManager.getDefaultSharedPreferences(context)
        editor = preference.edit()
        if (categoryListStr!!.isNotEmpty()) {
            retrieveCategoriesFromPreference()
        }
    }

    fun initLanguage(context: Context) {
        preference = PreferenceManager.getDefaultSharedPreferences(context)
        editor = preference.edit()
        if (categoryListStr!!.isNotEmpty()) {
            retrieveCategoriesFromPreference()
        }
    }

    // Functions for getter and setter of preference data

    //Function for getting and setting login status
    var isUserLoggedIn: Boolean
        get() = preference.getBoolean(IS_USER_LOGGED_IN, false)
        set(value) = preference.edit {
            this.putBoolean(IS_USER_LOGGED_IN, value)
            this.apply()
        }

    //User Update profile status
    var isProfileUpdated: Boolean
        get() = preference.getBoolean(IS_PROFILE_UPDATED, false)
        set(value) = preference.edit {
            this.putBoolean(IS_PROFILE_UPDATED, value)
            this.apply()
        }

    //Function for getting and setting profile image
    var userProfilePic: String?
        get() = preference.getString(USER_PROFILE_IMAGE, EMPTY_STRING)
        set(value) = preference.edit {
            this.putString(USER_PROFILE_IMAGE, value)
            this.apply()
        }

    //Function for getting and setting user token
    var userToken: String
        get() = preference.getString(USER_TOKEN, EMPTY_STRING)!!
        set(value) = preference.edit {
            this.putString(USER_TOKEN, value)
            this.apply()
        }

    //Function for getting and setting user email
    var userEmail: String?
        get() = preference.getString(USER_EMAIL, EMPTY_STRING)
        set(value) = preference.edit {
            this.putString(USER_EMAIL, value)
            this.apply()
        }

    //Function for getting and setting user name
    var userName: String?
        get() = preference.getString(USER_NAME, EMPTY_STRING)
        set(value) = preference.edit {
            this.putString(USER_NAME, value)
            this.apply()
        }

    //Function for getting and setting user age
    var userAge: String?
        get() = preference.getString(USER_AGE, EMPTY_STRING)
        set(value) = preference.edit {
            this.putString(USER_AGE, value)
            this.apply()
        }

    //Function for getting and setting user location
    var userLocation: String?
        get() = preference.getString(USER_LOCATION, EMPTY_STRING)
        set(value) = preference.edit {
            this.putString(USER_LOCATION, value)
            this.apply()
        }

    //Function for getting and setting user language selected by user
    fun getSelectedLanguages(): MutableList<String> {
        val language = preference.getString(LANGUAGES_SELECTED, EMPTY_STRING)
        if(language!!.isEmpty()){
            return mutableListOf<String>()
        }
        val gson = Gson()
        val objectList = gson.fromJson(language, Array<String>::class.java).asList()

        return objectList.toMutableList()
    }

    fun getSelectedLanguagesAsString(): String {
        val language = preference.getString(LANGUAGES_SELECTED, EMPTY_STRING)
        if(language!!.isEmpty()){
            return ""
        }
//        val languageSet: Set<String> = setOf(*language!!.split(",")!!.toTypedArray())
        val gson = Gson()
        val objectList = gson.fromJson(language, Array<String>::class.java).asList()
        val ll = objectList.toString().replace("[", "").replace("]", "")
        return ll
    }

    fun saveSelectedLanguagesToPreference(languageSet: List<String>) {
        val gson = Gson()
        val json = gson.toJson(languageSet)
        var languages = json.toString()
        preference.edit().apply {
            putString(LANGUAGES_SELECTED, languages)
            commit()
        }

    }

    var isRefreshRequired: Boolean
        get() = preference.getBoolean(IS_REFRESS_REQUIRED, false)
        set(value) = preference.edit {
            this.putBoolean(IS_REFRESS_REQUIRED, value)
            this.apply()
        }

    var categoryListStr: String?
        get() = preference.getString(VIDEO_CATEGORIES, EMPTY_STRING)
        set(value) = preference.edit {
            this.putString(VIDEO_CATEGORIES, value)
            this.apply()
        }

    var languageListStr: String?
        get() = preference.getString(SELECTED_LANGUAGES, EMPTY_STRING)
        set(value) = preference.edit {
            this.putString(SELECTED_LANGUAGES, value)
            this.apply()
        }

    var isLanguageSelected: Boolean
        get() = preference.getBoolean(IS_LANGUAGE_SELECTED, false)
        set(value) = preference.edit {
            this.putBoolean(IS_LANGUAGE_SELECTED, value)
            this.apply()
        }

    var isInterestUpdateNeeded: Boolean
        get() = preference.getBoolean(IS_INTEREST_UPDATE_NEEDED, false)
        set(value) = preference.edit {
            this.putBoolean(IS_INTEREST_UPDATE_NEEDED, value)
            this.apply()
        }

    var isProfileDeleted: Boolean
        get() = preference.getBoolean(IS_PROFILE_DELETED, false)
        set(value) = preference.edit {
            this.putBoolean(IS_PROFILE_DELETED, value)
            this.apply()
        }

    var isUpdateNeeded: Boolean
        get() = preference.getBoolean(UPDATE_NEEDED, false)
        set(value) = preference.edit {
            this.putBoolean(UPDATE_NEEDED, value)
            this.apply()
        }

    var isFollowingUpdateNeeded: Boolean
        get() = preference.getBoolean(FOLLOWING_UPDATE_NEEDED, false)
        set(value) = preference.edit {
            this.putBoolean(FOLLOWING_UPDATE_NEEDED, value)
            this.apply()
        }

    var isMainActivityLaunched: Boolean
        get() = preference.getBoolean(MAIN_ACTIVITY_LAUNCHED, false)
        set(value) = preference.edit {
            this.putBoolean(MAIN_ACTIVITY_LAUNCHED, value)
            this.apply()
        }

    var fcmToken: String?
        get() = preference.getString(NOTIFICATION_TOKEN, EMPTY_STRING)
        set(value) = preference.edit {
            this.putString(NOTIFICATION_TOKEN, value)
            this.apply()
        }


    // Clear preference on logout
    fun clear() {
        editor.clear().commit()
    }

    /*var languages: String?
        get() = preference.getString(LANGUAGES_SELECTED, EMPTY_STRING)
        set(value) = preference.edit {
            this.putString(LANGUAGES_SELECTED, value)
            this.apply()
        }*/

    fun saveCategoriesToPreference(categoryList: List<VideoCategory>) {
        val gson = Gson()
        val json = gson.toJson(categoryList)
        categoryListStr = json.toString()
    }

    var categoryList: MutableList<VideoCategory> = mutableListOf<VideoCategory>()
    private fun retrieveCategoriesFromPreference(): List<VideoCategory> {
        if (categoryList.isEmpty()) {
            val gson = Gson()
            val categoryJsonString = categoryListStr
            val myListType = object : TypeToken<List<VideoCategory>>() {}.type
            categoryList = gson.fromJson<List<VideoCategory>>(
                categoryJsonString,
                myListType
            ) as MutableList<VideoCategory>
        } else {
            val gson = Gson()
            val categoryJsonString = categoryListStr
            val myListType = object : TypeToken<List<VideoCategory>>() {}.type
            categoryList = gson.fromJson<List<VideoCategory>>(
                categoryJsonString,
                myListType
            ) as MutableList<VideoCategory>
        }

        return categoryList
    }

//    fun saveSelectedLanguages(categoryList: Set<String>) {
//        val gson = Gson()
//        val json = gson.toJson(categoryList)
//        languageListStr = json.toString()
//    }
//
//    var languageList: MutableList<LanguageData> = mutableListOf<LanguageData>()
//    private fun retrieveLanguagesFromPreference(): List<LanguageData> {
//        if (languageList.isEmpty()) {
//            val gson = Gson()
//            val languageJsonString = languageListStr
//            val myListType = object : TypeToken<List<LanguageData>>() {}.type
//            languageList = gson.fromJson<List<LanguageData>>(
//                languageJsonString,
//                myListType
//            ) as MutableList<LanguageData>
//        } else {
//            val gson = Gson()
//            val languageJsonString = languageListStr
//            val myListType = object : TypeToken<List<LanguageData>>() {}.type
//            languageList = gson.fromJson<List<LanguageData>>(
//                languageJsonString,
//                myListType
//            ) as MutableList<LanguageData>
//        }
//
//        return languageList
//    }

    var isModified: Boolean
        get() = preference.getBoolean(IS_MODIFIED, false)
        set(value) = preference.edit {
            this.putBoolean(IS_MODIFIED, value)
            this.apply()
        }

}