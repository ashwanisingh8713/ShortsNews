package com.mytest

/**
 * Created by Ashwani Kumar Singh on 26,September,2023.
 */
class ReverseString {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

        }

        // Reverse String
        fun reverseString(str: String): String{
            val chArray = str.toCharArray()
            var start: Int = 0
            var end: Int = chArray.size

            while(start < end) {
                swapChar(chArray, start, end)
                start++
                end--
            }
            return String(chArray)
        }

        // Swapping Characters in String
        fun swapChar(chArray: CharArray, i: Int, j: Int) {
            val temp = chArray[i]
            chArray[i] = chArray[j]
            chArray[j] = temp
        }
    }

}