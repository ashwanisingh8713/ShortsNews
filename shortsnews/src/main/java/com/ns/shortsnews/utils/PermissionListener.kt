package com.ns.shortsnews.utils

interface PermissionListener {
    fun   shouldShowRationaleInfo()
    fun   isPermissionGranted(isGranted : Boolean)
}