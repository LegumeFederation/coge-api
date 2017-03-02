package org.coge.api;

import java.io.IOException;

import us.monoid.json.JSONException;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

/**
 * The abstract object which contains data and methods common to all objects.
 *
 * @author Sam Hokin
 */
public class CoGeObject {

    int id;
    String name;
    String description;

    /**
     * Default constructor. Does nothing, but allows child classes to create their own constructors.
     */
    protected CoGeObject() {
    }

    /**
     * Minimal constructor, just id.
     */
    public CoGeObject(int id) {
        this.id = id;
    }

    /**
     * Nearly minimal constructor, just id and name.
     */
    public CoGeObject(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Standard constructor, id, name and description.
     */
    public CoGeObject(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Construct from a JSON object. Don't require id, maybe some objects don't have it populated.
     */
    protected CoGeObject(JSONObject json) throws IOException, JSONException {
        if (json.has("id")) id = json.getInt("id");
        if (json.has("name")) name = json.getString("name");
        if (json.has("description")) description = json.getString("description");
    }

    /**
     * A copy constructor so we can instantiate child classes from a CoGeObject.
     */
    protected CoGeObject(CoGeObject object) {
        this.id = object.id;
        this.name = object.name;
        this.description = object.description;
    }

    /**
     * Set the id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Set the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the description.
     */
    public String getDescription() {
        return description;
    }

}
