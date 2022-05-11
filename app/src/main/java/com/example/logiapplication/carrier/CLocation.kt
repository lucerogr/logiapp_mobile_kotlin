package com.example.logiapplication.carrier

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
        var nDistance = super.distanceTo(dest)
        if (!useMetricUnits) {
            nDistance = nDistance * 3.28083989501312f
        }
        return nDistance
    }

    override fun getAccuracy(): Float {
        // TODO Auto-generated method stub
        var nAccuracy = super.getAccuracy()
        if (!useMetricUnits) {
            nAccuracy = nAccuracy * 3.28083989501312f
        }
        return nAccuracy
    }

    override fun getAltitude(): Double {
        // TODO Auto-generated method stub
        var nAltitude = super.getAltitude()
        if (!useMetricUnits) {
            nAltitude = nAltitude * 3.28083989501312
        }
        return nAltitude
    }

    override fun getSpeed(): Float {
        var nSpeed = super.getSpeed() * 3.6f
        if (!useMetricUnits) {
            nSpeed = nSpeed * 2.2369362920544f / 3.6f
        }
        return nSpeed
    }

    init {
        useMetricUnits = bUseMetricUnits
    }
}