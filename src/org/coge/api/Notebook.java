package org.coge.api;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import us.monoid.json.JSONException;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

/**
 * Encapsulate a Notebook record.
 *
 * @author Sam Hokin
 */
public class Notebook extends CoGeObject {

    String type;
    boolean restricted;
    List<Metadata> additionalMetadata; // type_group:string, type:string, text:string, link:string
    List<Item> items;

    /**
     * Construct given id, name, description but not notebooks.
     */
    public Notebook(int id, String name, String description) {
        super(id, name, description);
    }

    /**
     * Construct from an instantiated superclass.
     */
    public Notebook(CoGeObject object) {
        super(object);
    }

    /**
     * Construct from a JSONObject.
     */
    public Notebook(JSONObject json) throws IOException, JSONException {
        super(json);
        if (json.has("type")) type = json.getString("type");
        if (json.has("restricted")) restricted = json.getBoolean("restricted");
        if (json.has("additional_metadata")) {
            additionalMetadata = new ArrayList<Metadata>();
            JSONArray metarray = json.getJSONArray("additional_metadata");
            for (int i=0; i<metarray.length(); i++) {
                JSONObject meta = metarray.getJSONObject(i);
                additionalMetadata.add(new Metadata(meta.getString("type_group"), meta.getString("type"), meta.getString("text"), meta.getString("link")));
            }
        }
        if (json.has("items")) {
            items = new ArrayList<Item>();
            JSONArray itemarray = json.getJSONArray("items");
            for (int i=0; i<itemarray.length(); i++) {
                JSONObject item = itemarray.getJSONObject(i);
                items.add(new Item(item.getInt("id"), item.getString("type")));
            }
        }
    }


    void setType(String type) {
        this.type = type;
    }

    void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    void setAdditionalMetadata(List<Metadata> additionalMetadata) {
        this.additionalMetadata = additionalMetadata;
    }

    void setItems(List<Item> items) {
        this.items = items;
    }

    /**
     * The venerable toString() method.
     */
    public String toString() {
        return "Notebook.toString() not yet written.";
    }

}
        
        

