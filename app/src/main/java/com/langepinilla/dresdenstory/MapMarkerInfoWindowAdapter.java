package com.langepinilla.dresdenstory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

//this class is used to define the look of the InfoWindow for the chapter map markers

public class MapMarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private final Context mContext;

    //constructor with reference to layout file
    public MapMarkerInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.map_marker_info_window, null);
    }

    private void renderWindowText(Marker marker, View view) {
        //set title of marker in the info window
        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.marker_title);
        tvTitle.setText(title);

        //set position of marker in the info window, this unpacks the object that was passed as the marker tag
        if (marker.getTag() instanceof MapsActivity.MarkerTag) {
            MapsActivity.MarkerTag mMarkerTag = ((MapsActivity.MarkerTag)marker.getTag());
            String markerLocationText = mMarkerTag.getChapterLocationText();
            TextView tvLatLng = view.findViewById(R.id.marker_lat_lng);
            tvLatLng.setText(String.format(mContext.getString(R.string.chapter_location_format_text_lat_lon), markerLocationText, marker.getPosition().latitude, marker.getPosition().longitude));
        }

        //set snippet of marker in the info window
        String snippet = marker.getSnippet();
        TextView tvSnippet = view.findViewById(R.id.marker_snippet);
        tvSnippet.setText(snippet);
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}
