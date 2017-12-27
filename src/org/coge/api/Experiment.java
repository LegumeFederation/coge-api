package org.coge.api;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import us.monoid.json.JSONException;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

/**
 * Encapsulate a Experiment record.
 *
 * @author Sam Hokin
 */
public class Experiment extends CoGeObject {

    String link;
    String version;
    int genomeId;
    String source;
    Map<String,String> types;
    boolean restricted;
    List<Metadata> additionalMetadata; // type_group:string, type:string, text:string, link:string

    /**
     * Construct given id, name, description but not experiments.
     */
    protected Experiment(int id, String name, String description) {
        super(id, name, description);
    }

    /**
     * Construct from an instantiated superclass.
     */
    protected Experiment(CoGeObject object) {
        super(object);
    }

    /**
     * Construct from a JSONObject.
     */
    protected Experiment(JSONObject json) throws IOException, JSONException {
        super(json);
        if (id!=0) {
            if (json.has("version")) version = json.getString("version");
            if (json.has("genome_id")) genomeId = json.getInt("genome_id");
            if (json.has("source")) source = json.getString("source");
            if (json.has("types")) {
                types = new LinkedHashMap<String,String>();
                JSONArray typarray = json.getJSONArray("types");
                for (int i=0; i<typarray.length(); i++) {
                    JSONObject type = typarray.getJSONObject(i);
                    if (type.has("name") && type.has("description")) {
                        types.put(type.getString("name"), type.getString("description"));
                    }
                }
            }
            if (json.has("restricted")) restricted = json.getBoolean("restricted");
            if (json.has("additional_metadata")) {
                additionalMetadata = new ArrayList<Metadata>();
                JSONArray metarray = json.getJSONArray("additional_metadata");
                for (int i=0; i<metarray.length(); i++) {
                    JSONObject meta = metarray.getJSONObject(i);
                    additionalMetadata.add(new Metadata(meta.getString("type_group"), meta.getString("type"), meta.getString("text"), meta.getString("link")));
                }
            }
        }
    }


    public String getLink() {
        return link;
    }
    void setLink(String link) {
        this.link = link;
    }

    public String getVersion() {
        return version;
    }
    void setVersion(String version) {
        this.version = version;
    }

    public int getGenomeId() {
        return genomeId;
    }
    void setGenomeId(int genomeId) {
        this.genomeId = genomeId;
    }

    public String getSource() {
        return source;
    }
    void setSource(String source) {
        this.source = source;
    }

    public Map<String,String> getTypes() {
        return types;
    }
    void setTypes(Map<String,String> types) {
        this.types = types;
    }
    
    public boolean isRestricted() {
        return restricted;
    }
    void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public List<Metadata> getAdditionalMetadata() {
        return additionalMetadata;
    }
    void setAdditionalMetadata(List<Metadata> additionalMetadata) {
        this.additionalMetadata = additionalMetadata;
    }

    /**
     * The venerable toString() method.
     */
    public String toString() {
        return "Experiment.toString() not yet written.";
    }

}
        
        

