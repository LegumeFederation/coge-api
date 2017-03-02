package org.coge.api;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import us.monoid.json.JSONException;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

/**
 * Encapsulate a Genome record.
 *
 * @author Sam Hokin
 */
public class Genome extends CoGeObject {

    String link;
    String version;
    String sourceName;
    Organism organism;
    CoGeObject sequenceType;
    boolean restricted = true; // default to restricted
    boolean deleted = false;   // default to not deleted
    int chromosomeCount;
    List<Metadata> additionalMetadata;
    List<Integer> experiments;

    /**
     * Construct given id, name, description.
     */
    public Genome(int id, String name, String description) {
        super(id, name, description);
    }

    /**
     * Construct given just name, description; used for adding a new genome.
     */
    public Genome(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Construct from an instantiated superclass.
     */
    protected Genome(CoGeObject object) {
        super(object);
    }

    /**
     * Construct from a JSONObject.
     */
    protected Genome(JSONObject json) throws IOException, JSONException {
        super(json);
        if (json.has("link")) link = json.getString("link");
        if (json.has("version")) version = json.getString("version");
        if (json.has("organism")) {
            JSONObject org = json.getJSONObject("organism");
            organism = new Organism(org);
        }
        if (json.has("sequence_type")) sequenceType = new CoGeObject(json.getJSONObject("sequence_type"));
        if (json.has("restricted")) restricted = json.getBoolean("restricted");
        if (json.has("deleted")) deleted = json.getBoolean("deleted");
        if (json.has("chromosome_count")) chromosomeCount = json.getInt("chromosome_count");
        if (json.has("additional_metadata")) {
            additionalMetadata = new ArrayList<Metadata>();
            JSONArray metarray = json.getJSONArray("additional_metadata");
            for (int i=0; i<metarray.length(); i++) {
                JSONObject meta = metarray.getJSONObject(i);
                additionalMetadata.add(new Metadata(meta.getString("type_group"), meta.getString("type"), meta.getString("text"), meta.getString("link")));
            }
        }
        if (json.has("experiments")) {
            experiments = new ArrayList<Integer>();
            JSONArray exparray = json.getJSONArray("experiments");
            for (int i=0; i<exparray.length(); i++) {
                experiments.add(exparray.getInt(i));
            }
        }
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    public void setSequenceType(CoGeObject sequenceType) {
        this.sequenceType = sequenceType;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public void setChromosomeCount(int chromosomeCount) {
        this.chromosomeCount = chromosomeCount;
    }

    public void setAdditionalMetadata(List<Metadata> additionalMetadata) {
        this.additionalMetadata = additionalMetadata;
    }

    public void setExperiments(List<Integer> experiments) {
        this.experiments = experiments;
    }

    public String getLink() {
        return link;
    }

    public String getVersion() {
        return version;
    }

    public String getSourceName() {
        return sourceName;
    }

    public Organism getOrganism() {
        return organism;
    }

    public CoGeObject getSequenceType() {
        return sequenceType;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public int getChromosomeCount() {
        return chromosomeCount;
    }

    public List<Metadata> getAdditionalMetadata() {
        return additionalMetadata;
    }

    public List<Integer> getExperiments() {
        return experiments;
    }

}
        
        

