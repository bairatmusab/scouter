

package org.atos.scouter.models;

/**
 * Object that represents a GPS position of an {@link Event}
 *
 * @version 1.0
 */
public class LatLong {
    /**
     * Latitude position
     *
     * @see LatLong#getLatitude()
     */
    private double latitude;
    /**
     * Longitude position
     *
     * @see LatLong#getLongitude()
     */
    private double longitude;

    /**
     * Create a LatLong
     *
     * @param latitude  latitude of the event
     * @param longitude longitude of the event
     * @throws IllegalArgumentException if latitude is not between -90 an 90, or longitude is not between -180 and 180
     */
    public LatLong(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90° and +90° inclusive.");
        } else if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180° and +180° inclusive.");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLong(){

    }

    /**
     * Get latitude
     *
     * @return latitude
     * @see LatLong#latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Get longitude
     *
     * @return longitude
     * @see LatLong#longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Generated method to test if {@link LatLong} are same
     *
     * @param o Other {@link LatLong} to compare
     * @return true if there are equals
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LatLong latLong = (LatLong) o;

        if (Double.compare(latLong.latitude, latitude) != 0) return false;
        return Double.compare(latLong.longitude, longitude) == 0;
    }

    /**
     * Generated method
     *
     * @return hashcode of {@link LatLong}
     */
    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * Represent this {@link LatLong} as a String
     *
     * @return this {@link LatLong} as String
     * @see LatLong#longitude
     * @see LatLong#latitude
     */
    @Override
    public String toString() {
        return "{" + latitude + "," + longitude + "}";
    }
}
