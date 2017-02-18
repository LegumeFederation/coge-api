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
    Organism organism;
    SequenceType sequenceType;
    boolean restricted;
    int chromosomeCount;
    List<Metadata> additionalMetadata;
    List<Integer> experiments;

    /**
     * Construct given id, name, description but not genomes.
     */
    protected Genome(int id, String name, String description) {
        super(id, name, description);
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
        if (id!=0) {
            if (json.has("link")) link = json.getString("link");
            if (json.has("version")) version = json.getString("version");
            if (json.has("organism")) {
                JSONObject org = json.getJSONObject("organism");
                organism = new Organism(org);
            }
            if (json.has("sequence_type")) {
                JSONObject sto = json.getJSONObject("sequence_type");
                sequenceType = new SequenceType(sto.getString("name"), sto.getString("description"));
            }
            if (json.has("restricted")) restricted = json.getBoolean("restricted");
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
    }

    void setLink(String link) {
        this.link = link;
    }

    void setVersion(String version) {
        this.version = version;
    }

    void setOrganism(Organism organism) {
        this.organism = organism;
    }

    void setSequenceType(SequenceType sequenceType) {
        this.sequenceType = sequenceType;
    }

    void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    void setChromosomeCount(int chromosomeCount) {
        this.chromosomeCount = chromosomeCount;
    }

    void setAdditionalMetadata(List<Metadata> additionalMetadata) {
        this.additionalMetadata = additionalMetadata;
    }

    void setExperiments(List<Integer> experiments) {
        this.experiments = experiments;
    }

    /**
     * The venerable toString() method.
     */
    public String toString() {
        return "id="+id+"; name="+name+"; description="+description +
            "; version="+version+"; organism="+organism.name+"; sequenceType="+sequenceType.name +
            "; restricted="+restricted+"; chromosomeCount="+chromosomeCount;
    }

}
        
        

