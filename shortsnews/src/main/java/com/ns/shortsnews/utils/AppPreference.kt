package com.ns.shortsnews.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.Preference
import androidx.preference.PreferenceManager

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

    fun init(context: Context) {
        preference = PreferenceManager.getDefaultSharedPreferences(context)
        editor = preference.edit()

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
    var isProfileUpdated:Boolean
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


    // Clear preference on logout
    fun clear(){
        editor.clear().apply()
    }

}