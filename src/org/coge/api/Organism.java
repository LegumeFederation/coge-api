package org.coge.api;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import us.monoid.json.JSONException;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

/**
 * Encapsulate an Organism record.
 *
 * @author Sam Hokin
 */
public class Organism extends CoGeObject {

    List<Integer> genomes;

    /**
     * Construct given id, name, description and genomes.
     */
    protected Organism(int id, String name, String description) {
        super(id, name, description);
    }

    /**
     * Construct from an instantiated CoGeObject.
     */
    protected Organism(CoGeObject object) {
        super(object);
    }

    /**
     * Construct an Organism from a JSONObject.
     */
    protected Organism(JSONObject json) throws IOException, JSONException {
        super(json);
        if (id!=0) {
            if (json.has("genomes")) {
                genomes = new ArrayList<Integer>();
                JSONArray genomeStrings = json.getJSONArray("genomes");
                for (int i=0; i<genomeStrings.length(); i++) {
                    genomes.add(Integer.parseInt(genomeStrings.get(i).toString()));
                }
            }
        }
    }

    /**
     * Set the genomes list.
     */
    protected void setGenomes(List<Integer> genomes) {
        this.genomes = genomes;
    }

    public List<Integer> getGenomes() {
        return genomes;
    }

}
        
        

