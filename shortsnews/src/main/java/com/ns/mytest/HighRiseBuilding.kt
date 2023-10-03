package com.ns.mytest

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Created by Ashwani Kumar Singh on 25,September,2023.
 */
class HighRiseBuilding(private val name:String, val floors: Int, private val coroutineScope: CoroutineScope) {

    suspend fun buildFoundation() = coroutineScope.launch {
            delay(300)
            println("Foundation built for $name")
        }

    suspend fun buildFloor(floorNumber: Int) = coroutineScope.launch {
        delay(200)
            println("Floor $floorNumber built for $name")
        }

    suspend fun installWindows(floorNumber: Int) = coroutineScope.launch {
            delay(100)
            println("Windows installed on floor $floorNumber for $name")
        }

    suspend fun installDoors(floorNumber: Int) = coroutineScope.launch {
            delay(100)
            println("Doors installed on floor $floorNumber for $name")
        }

    suspend fun fitElectricity(floorNumber: Int) = coroutineScope.launch {
            delay(100)
            println("Electricity fitted on floor $floorNumber for $name")
        }

    suspend fun installPlumbing(floorNumber: Int) = coroutineScope.launch {
            delay(100)
            println("Plumbing installed on floor $floorNumber for $name")
        }

    suspend fun fitOut(floorNumber: Int) = coroutineScope.launch {
            delay(200)
            println("Fit out on floor $floorNumber for $name")
        }
    suspend fun buildRoof() = coroutineScope.launch {
        delay(500)
        println("Roof built for $name")
    }

}

class BuildingYard() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runBlocking {
                startProject()
            }
        }

        private suspend fun startProject() {

            val building = withContext(Dispatchers.Default) {
                val highRiseBuilding = HighRiseBuilding("BuildingYard", 10, this)
                // First build the foundation
                highRiseBuilding.buildFoundation().join()
                println()
                // Then build all the floors
                (1..highRiseBuilding.floors).forEach {
                    // A floor should be raised before we can decorate it
                    highRiseBuilding.buildFloor(it).join()
                    // These decorations could be made at the same time
                    highRiseBuilding.installWindows(it)
                    highRiseBuilding.installDoors(it)
                    highRiseBuilding.fitElectricity(it)
                    highRiseBuilding.installPlumbing(it)
                    highRiseBuilding.fitOut(it)
                    println()
                }
                println()
                highRiseBuilding.buildRoof().join()

                highRiseBuilding
            }

        }
    }




}