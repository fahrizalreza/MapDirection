package com.example.mapdirection;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionParser {

    public List<List<HashMap<String, String>>> parse(JSONObject jsonObject) {
        List<List<HashMap<String, String>>> routes = new ArrayList<~>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {
            jRoutes = jsonObject.getJSONArray("routes");

            // loop for all routes
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                // loop for all legs
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    // loop for all steps
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List list = decodePolyline(polyline);

                        // loop for all points
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hashMap = new HashMap<~>();
                            hashMap.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hashMap.put("lon", Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hashMap);
                        }
                    }
                    routes.add(path);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }

        return routes;
    }

    private List decodePolyline(String encoded) {
        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while ( b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat +=  dlat;

            shift = 0;
            result = 0;

            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
                    (int) (((double) lng / 1E5) * 1E6));
            poly.add(p);
        }

        return poly;
    }
}
