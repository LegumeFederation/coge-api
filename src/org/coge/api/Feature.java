package org.coge.api;

import java.io.IOException;
import java.util.List;

import us.monoid.json.JSONException;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

/**
 * Encapsulate a Feature record. Doesn't extend CoGeObject since it doesn't contain name and description.
 *
 * @author Sam Hokin
 */
public class Feature {

    int id;
    String type;
    String name;
    String chromosome;
    Genome genome;
    int start;
    int stop;
    int strand;
    String sequence;

    // "annotations":[
    //                {"category":"db_xref","type":"CDD","value":"28970"},
    //                {"type":"note","value":"Homeodomain"},
    //                {"type":"note","value":"Homeodomain   DNA binding domains involved in the transcriptional regulation of key eukaryotic developmental processes"},
    //                {"type":"note","value":"Homeodomain   DNA binding domains involved in the transcriptional regulation of key eukaryotic developmental processes  may bind to DNA as monomers or as homo- and\/or heterodimers, in a sequence-specific manner"},
    //                {"type":"note","value":"Homeodomain   DNA binding domains involved in the transcriptional regulation of key eukaryotic developmental processes  may bind to DNA as monomers or as homo- and\/or heterodimers, in a sequence-specific manner  Region: homeodomain"},
    //                {"type":"note","value":"Homeodomain   DNA binding domains involved in the transcriptional regulation of key eukaryotic developmental processes  may bind to DNA as monomers or as homo- and\/or heterodimers, in a sequence-specific manner  Region: homeodomain  cd00086"}
    //                ],
    //  "chromosome":"5",
    //  "genome":{"id":18626,"organism":"Arabidopsis thaliana (thale cress)","version":"1"},
    //  "id":341489236,
    //  "locations":[
    //               {"start":24397806,"stop":24397966,"strand":1},
    //               {"start":24398275,"stop":24398296,"strand":1}
    //               ],
    //  "names":["AT5G60690","REV","REVOLUTA"],
    //  "sequence":"aagtacgttaggtacacagctgagcaagtcgaggctcttgagcgtgtctacgctgagtgtcctaagcctagctctctccgtcgacaacaattgatccgtgaatgttccattttggccaatattgagcctaagcagatcaaagtctggtttcagaaccgcaggtgtcgagataagcagaggaaa",
    //  "start":24397806,
    //  "stop":24398296,
    //  "strand":1,
    //  "type":"misc_feature"

    
    
    /**
     * Construct from an id and type.
     */
    protected Feature(int id, String type) {
        this.id = id;
        this.type = type;
    }

    /**
     * Construct from a JSON object and CoGe instance (because we may need to fetch the genome).
     */
    protected Feature(JSONObject json, CoGe coge) throws IOException, JSONException {
        if (json.has("id")) {
            id = json.getInt("id");
            type = json.getString("type");
            name = json.getString("name");
            if (json.has("chromosome")) chromosome = json.getString("chromosome");
            if (json.has("genome")) {
                JSONObject go = json.getJSONObject("genome");
                int gid = go.getInt("id");
                genome = coge.fetchGenome(gid);
            }
            if (json.has("start")) start = json.getInt("start");
            if (json.has("stop")) stop = json.getInt("stop");
            if (json.has("strand")) strand = json.getInt("strand");
            if (json.has("sequence")) sequence = json.getString("sequence");
        }
    }

    /**
     * Construct from a JSON object without instantiating the genome (so CoGe instance not needed)
     */
    protected Feature(JSONObject json) throws IOException, JSONException {
        if (json.has("id")) {
            id = json.getInt("id");
            type = json.getString("type");
            name = json.getString("name");
            if (json.has("chromosome")) chromosome = json.getString("chromosome");
            if (json.has("start")) start = json.getInt("start");
            if (json.has("stop")) stop = json.getInt("stop");
            if (json.has("strand")) strand = json.getInt("strand");
            if (json.has("sequence")) sequence = json.getString("sequence");
        }
    }

    ////////// getters and setters //////////

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
    
    public String getChromosome() {
        return chromosome;
    }
    void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public Genome getGenome() {
        return genome;
    }
    void setGenome(Genome genome) {
        this.genome = genome;
    }

    public int getStart() {
        return start;
    }
    void setStart(int start) {
        this.start = start;
    }

    public int getStop() {
        return stop;
    }
    void setStop(int stop) {
        this.stop = stop;
    }

    public int getStrand() {
        return strand;
    }
    void setStrand(int strand) {
        this.strand = strand;
    }

    public String getSequence() {
        return sequence;
    }
    void setSequence(String sequence) {
        this.sequence = sequence;
    }

}
        
        

