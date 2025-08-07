package com.maydaymemory.mae.control.montage;

/**
 * Animation notification state marker, used for marking the start and end of animation notification states.
 * 
 * @param <T> Context type
 * 
 * @author MaydayMemory
 * @since 1.0.4
 */
public class AnimationNotifyStateMarker<T> {
    /** Notification state instance */
    private final IAnimationNotifyState<T> notify;
    
    /** Marker type (start or end) */
    private final MarkerType markerType;

    /**
     * Construct a new animation notification state marker.
     * 
     * @param notify Notification state instance
     * @param markerType Marker type
     */
    public AnimationNotifyStateMarker(IAnimationNotifyState<T> notify, MarkerType markerType) {
        this.notify = notify;
        this.markerType = markerType;
    }

    /**
     * Get the marker type.
     * 
     * @return Marker type
     */
    public MarkerType getMarkerType() {
        return markerType;
    }

    /**
     * Get the notification state instance.
     * 
     * @return Notification state instance
     */
    public IAnimationNotifyState<T> getNotify() {
        return notify;
    }

    /**
     * Marker type enum, defining the types of notification state markers.
     */
    public enum MarkerType {
        /** Start marker, indicating the start of notification state */
        START, 
        /** End marker, indicating the end of notification state */
        END
    }
}
