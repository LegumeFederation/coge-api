package org.coge.api;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import us.monoid.json.JSONException;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

/**
 * Encapsulate a Data Store list of string:string maps.
 *
 * @author Sam Hokin
 */
public class DataStoreList {

    String path;
    List<Map<String,String>> items;

    /**
     * Constructor just initializes items.
     */
    protected DataStoreList() {
        items = new ArrayList<Map<String,String>>();
    }

    /**
     * Construct from a JSON object.
     */
    protected DataStoreList(JSONObject json) throws IOException, JSONException {
        if (json.has("path")) path = json.getString("path");
        if (json.has("items")) {
            items = new ArrayList<Map<String,String>>();
            JSONArray ja = json.getJSONArray("items");
            for (int i=0; i<ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                Map<String,String> item = new LinkedHashMap<String,String>();
                Iterator<String> keys = jo.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    item.put(key, jo.getString(key));
                }
                items.add(item);
            }
        }
    }

    public List<Map<String,String>> getItems() {
        return items;
    }
    void addItem(Map<String,String> item) {
        items.add(item);
    }

    public String getPath() {
        return path;
    }
    void setPath(String path) {
        this.path = path;
    }
    

    // "items":[
    //          {"name":"cicar.CDCFrontier.gnm1/","order":1,"path":"/iplant/home/shared/Legume_Federation/Cicer_arietinum/cicar.CDCFrontier.gnm1","type":"directory"},
    //          {"name":"cicar.CDCFrontier.gnm1.ann1/","order":1,"path":"/iplant/home/shared/Legume_Federation/Cicer_arietinum/cicar.CDCFrontier.gnm1.ann1","type":"directory"},
    //          {"name":"cicar.CDCFrontier.gnm1.synt1/","order":1,"path":"/iplant/home/shared/Legume_Federation/Cicer_arietinum/cicar.CDCFrontier.gnm1.synt1","type":"directory"}
    //          ],
    // "path":"/iplant/home/shared/Legume_Federation/Cicer_arietinum"

}
        
        

