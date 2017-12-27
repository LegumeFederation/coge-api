package org.coge.api;

import java.io.IOException;
import java.util.List;

import us.monoid.json.JSONException;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

/**
 * Encapsulate a Feature type, an array of which is returned from a Genome fetch, which includes the count of said features.
 *
 * @author Sam Hokin
 */
public class FeatureType {

    int id;       // "type_id": 2,
    String name;  // "type_name": "mRNA",
    int count;    // "count": 39297

    /**
     * Construct from an id, name and count.
     */
    protected FeatureType(int id, String name, int count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    /**
     * Construct from a JSON object
     */
    protected FeatureType(JSONObject json) throws IOException, JSONException {
        if (json.has("type_id")) id = json.getInt("type_id");
        if (json.has("type_name")) name = json.getString("type_name");
        if (json.has("count")) count = json.getInt("count");
    }

    public int getId() {
        return id;
    }
    void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }
    void setCount(int count) {
        this.count = count;
    }

}
