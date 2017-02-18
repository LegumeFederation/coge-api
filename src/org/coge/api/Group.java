package org.coge.api;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import us.monoid.json.JSONException;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

/**
 * Encapsulate a Group record.
 *
 * @author Sam Hokin
 */
public class Group extends CoGeObject {

    String role;
    List<Integer> users;

    /**
     * Construct given id, name, description but not groups.
     */
    protected Group(int id, String name, String description, String role) {
        super(id, name, description);
        this.role = role;
    }

    /**
     * Construct from a JSONObject.
     */
    Group(JSONObject json) throws IOException, JSONException {
        super(json);
        if (json.has("role")) role = json.getString("role");
        if (json.has("users")) {
            users = new ArrayList<Integer>();
            JSONArray usearray = json.getJSONArray("users");
            for (int i=0; i<usearray.length(); i++) {
                int user = usearray.getInt(i);
                users.add(user);
            }
        }
    }


    void setUsers(List<Integer> users) {
        this.users = users;
    }
}
        
        

