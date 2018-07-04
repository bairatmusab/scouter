

package org.atos.scouter.models;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * BoundingBox class represents an area by coordinates
 *
 * @version 1.0
 */
public class BoundingBox {
    /**
     * Array of {@link LatLong} to represent a {@link BoundingBox}
     *
     * @see LatLong#getLongitude()
     * @see LatLong#getLatitude()
     */
    private final LatLong[] latLongs;

    /**
     * The constructor of BoundingBox class
     *
     * @param points an array of LatLong
     * @throws NullPointerException     if points is null
     * @throws IllegalArgumentException if points has invalid size
     * @see BoundingBox#latLongs
     */
    public BoundingBox(LatLong[] points) {
        Objects.requireNonNull(points);
        Arrays.stream(points).forEach(Objects::requireNonNull);
        if (points.length < 1) {
            throw new IllegalArgumentException("We need at least 1 point in bounding box ");
        }
        this.latLongs = points;
    }

    /**
     * Get {@link BoundingBox} as array of {@link LatLong}
     *
     * @return Array of {@link LatLong}
     * @see BoundingBox#latLongs
     */
    public LatLong[] getLatLongs() {
        return latLongs;
    }

    /**
     * Represent that object in string
     *
     * @return String that represents this {@link BoundingBox}
     * @see BoundingBox#latLongs
     */
    @Override
    public String toString() {
        return '[' + Arrays.stream(latLongs).map(LatLong::toString).collect(Collectors.joining(",", "{", "}")) + "]";
    }
}
