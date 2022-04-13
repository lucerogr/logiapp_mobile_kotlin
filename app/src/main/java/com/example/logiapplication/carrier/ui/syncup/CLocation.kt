package com.example.logiapplication.carrier.ui.syncup

import android.location.Location


class CLocation (
    location: Location?,
    bUseMetricUnits: Boolean = true
) :
    Location(location) {
    var useMetricUnits = false
        private set

    fun setUseMetricunits(bUseMetricUntis: Boolean) {
        useMetricUnits = bUseMetricUntis
    }

    override fun distanceTo(dest: Location): Float {
        // TODO Auto-generated method stub
        var nDistance = super.distanceTo(dest)
        if (!useMetricUnits) {
            //Convert meters to feet
            nDistance = nDistance * 3.28083989501312f
        }
        return nDistance
    }

    override fun getAccuracy(): Float {
        // TODO Auto-generated method stub
        var nAccuracy = super.getAccuracy()
        if (!useMetricUnits) {
            //Convert meters to feet
            nAccuracy = nAccuracy * 3.28083989501312f
        }
        return nAccuracy
    }

    override fun getAltitude(): Double {
        // TODO Auto-generated method stub
        var nAltitude = super.getAltitude()
        if (!useMetricUnits) {
            //Convert meters to feet
            nAltitude = nAltitude * 3.28083989501312
        }
        return nAltitude
    }

    override fun getSpeed(): Float {
        // TODO Auto-generated method stub
        var nSpeed = super.getSpeed() * 3.6f
        if (!useMetricUnits) {
            //Convert meters/second to miles/hour
            nSpeed = nSpeed * 2.2369362920544f / 3.6f
        }
        return nSpeed
    }

    init {
        // TODO Auto-generated constructor stub
        useMetricUnits = bUseMetricUnits
    }
}