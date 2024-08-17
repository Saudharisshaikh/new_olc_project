package com.app.mdlive_cp.util;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mdlive_cp.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



/**
 * Created by Engr G M on 10/26/2017.
 */

public class MapDialog {

    AppCompatActivity activity;

    final double MI_LAT = 43.016193, MI_LONG = -83.705521;


    public MapDialog(AppCompatActivity activity) {
        this.activity = activity;
    }


    static Dialog dialog;
    public void showMapDialog(double latitude, double longitude , String markerTittle,String snippet,String sniptImgUrl){
        if(dialog != null){
            if(dialog.isShowing()){
                return;
            }
        }
        dialog = new Dialog(activity,R.style.TransparentThemeBlack);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_map);

        CheckBox cbHighlightGeo = (CheckBox) dialog.findViewById(R.id.cbHighlightGeo);
        cbHighlightGeo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //hoghlightGeofences(isChecked);
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                FragmentManager fm = activity.getFragmentManager();
                Fragment fragment = (fm.findFragmentById(R.id.map));
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment);
                ft.commit();
            }
        });
        dialog.findViewById(R.id.ivCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

        if(latitude == 0 && longitude == 0){
            latitude = MI_LAT;
            longitude = MI_LONG;
        }
        initilizeMap(latitude,longitude,markerTittle,snippet,sniptImgUrl);
    }

    View mapView;
    private void initilizeMap(final double latitude, final double longitude , final String markerTittle, final String snippet, final String sniptImgUrl) {
        OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                googleMap.setTrafficEnabled(true);
                if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
						googleMap.setMyLocationEnabled(true);
					}
                googleMap.setBuildingsEnabled(true);

                if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
                    // Get the button view
                    View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                    // and next place it, on bottom right (as Google Maps app)
                    /*RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                    // position on right bottom
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    layoutParams.setMargins(0, 0, 30, 30);*/

                    //ImageView ivLoc = (ImageView) locationButton;
                    //ivLoc.setImageResource(R.mipmap.ic_mylocation);
                    //ivLoc.setVisibility(View.GONE);
                }


                //boolean success = googleMap.setMapStyle(new MapStyleOptions(MapStyleJSON.MAP_STYLE_JSON));
                //getResources().getString(R.string.style_json)

               /* if (!success) {
                    Log.e("", "Style parsing failed.");
                }else {
                    System.out.println("-- Style applied on map !");
                }*/

                    googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            //return null;
                            View myContentView = activity.getLayoutInflater().inflate(R.layout.custom_marker_window, null);
                            TextView tvTitle = ((TextView) myContentView.findViewById(R.id.title));
                            tvTitle.setText(marker.getTitle());
                            TextView tvSnippet1 = ((TextView) myContentView.findViewById(R.id.snippet1));
                            TextView tvSnippet2 = ((TextView) myContentView.findViewById(R.id.snippet2));
                            //tvSnippet.setText();
                            String snip = marker.getSnippet();
                            if(snip != null){
                                if(snip.isEmpty()){
                                    tvSnippet1.setVisibility(View.GONE);
                                    tvSnippet2.setVisibility(View.GONE);
                                }else{
                                    tvSnippet1.setVisibility(View.VISIBLE);
                                    tvSnippet2.setVisibility(View.VISIBLE);
                                    try {
                                        if(snip.contains("-")){
                                            String arr[] = snip.split("-");
                                            tvSnippet2.setVisibility(View.VISIBLE);

                                            tvSnippet1.setText(arr[0]);
                                            tvSnippet2.setText(arr[1]);
                                        }else {
                                            tvSnippet2.setVisibility(View.GONE);
                                            tvSnippet1.setText(snip);
                                        }

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }else{
                                tvSnippet1.setVisibility(View.GONE);
                                tvSnippet2.setVisibility(View.GONE);
                            }

                            ImageView ivMarker = (ImageView) myContentView.findViewById(R.id.ivMarker);
                            DATA.loadImageFromURL(sniptImgUrl, R.drawable.icon_call_screen, ivMarker);

                            return myContentView;
                        }
                        @Override
                        public View getInfoContents(Marker marker) {
                            return null;//a padding was shown on marker info window
                        }
                    });
                // create marker   Online Urgent Care
                //  MarkerOptions marker = new MarkerOptions().position(new LatLng(lati, longi)).title(address);
                MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(markerTittle)
                        //.snippet("Event: "+snippet)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.doctor_icon_small));

                if(!snippet.isEmpty()){
                    marker.snippet(snippet);
                }
                // adding marker
                googleMap.addMarker(marker).showInfoWindow();

                //Bus stop info window onClick event
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                    }
                });



                LatLng cpos = new LatLng(latitude, longitude);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 11f);
                googleMap.animateCamera(update);
            }
        };
        MapFragment mapFragment = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.map));
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(onMapReadyCallback);
    }


    GoogleMap mMap;
    /*static ArrayList<Polygon> polygons = new ArrayList<>();
    public void hoghlightGeofences(boolean highlight){
        try {

            if(!highlight){
                for (int j = 0; j < polygons.size(); j++) {
                    polygons.get(j).remove();
                }
                polygons.clear();
            }else {
                polygons.clear();
                SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
                ArrayList<Geofence> geofences = sharedPrefsHelper.getAllGeofence();
                if(geofences != null){
                    for (int i = 0; i < geofences.size(); i++) {
                        ArrayList<GeofencePoint> geofencePoints = geofences.get(i).geofencePoints;

                        if(! geofencePoints.isEmpty()){
                            LatLng[] geoLatlngs = new LatLng[geofencePoints.size()];

                            for (int j = 0; j < geofencePoints.size(); j++) {
                                geoLatlngs[j] = new LatLng(geofencePoints.get(j).lat,geofencePoints.get(j).lon);

                            }

                            String colorcode = getRandomColor();
                            PolygonOptions rectOptions = new PolygonOptions()
                                    .add(geoLatlngs)
                                    .strokeColor(Color.parseColor(colorcode.replace("80","")))
                                    .fillColor(Color.parseColor(colorcode));
                            //.strokeColor(Color.parseColor("#81D4FA"))
                            //.fillColor(Color.parseColor("#B3E5FC"));

                            Polygon polygon = mMap.addPolygon(rectOptions);
                            polygons.add(polygon);

                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/
}
