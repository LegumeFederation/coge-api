package org.coge.api;

import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.Content;
import us.monoid.web.Resty;
import us.monoid.web.JSONResource;

/**
 * The workhorse core class to make REST calls againt a CoGe web service and instantiate the various objects.
 *
 * @author Sam Hokin
 */
public class CoGe {

    String baseUrl;
    String username;
    String token;
    Resty resty;

    /**
     * Construct given just a base URL, like https://genomevolution.org/coge/api/v1/
     * Used only for calls that don't require authentication.
     */
    public CoGe(String baseUrl) {
        this.baseUrl = baseUrl;
        this.resty = new Resty();
        this.resty.identifyAsResty(); // why not?
    }

    /**
     * Construct given a base URL, username and token, used like https://genomevolution.org/coge/api/v1/stuff/here/and/here?username=x&token=y
     * Used for calls that require authentication.
     */
    public CoGe(String baseUrl, String username, String token) {
        this.baseUrl = baseUrl;
        this.username = username;
        this.token = token;
        this.resty = new Resty();
    }

    ////////// Organism //////////

    /**
     * Organism search
     * GET [base_url/organisms/search/term]
     *
     * @param searchTerm a text string to search on
     */
    public List<Organism> searchOrganism(String searchTerm) throws IOException, JSONException {
        List<Organism> organisms = new ArrayList<Organism>();
        for (CoGeObject object : search("organisms", searchTerm)) {
            organisms.add(new Organism(object));
        }
        return organisms;
    }

    /**
     * Organism fetch - used to populate the genomes
     * GET [base_url/organisms/id]
     * 
     * @param id the organism id
     */
    public Organism fetchOrganism(int id) throws IOException, JSONException {
        JSONObject json = fetch("organisms", id);
        return new Organism(json);
    }

    /**
     * Add a new organism. The response will contain the organism id if successful. The promised success flag is currently not present.
     */
    public CoGeResponse addOrganism(String name, String description) throws CoGeException, IOException, JSONException {
        if (username==null || token==null) throw CoGeException.missingAuthException();
        String url = baseUrl+"/organisms?username="+username+"&token="+token;
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("description", description);
        JSONResource resource = resty.json(url, Resty.put(Resty.content(json)));
        JSONObject response = resource.object();
        if (isError(response)) throw new CoGeException(response);
        return new CoGeResponse(response);
    }

    ////////// Genome //////////

    /**
     * Genome search
     * GET [base_url/genomes/search/term]
     *
     * @param searchTerm a text string to search on
     */
    public List<Genome> searchGenome(String searchTerm) throws IOException, JSONException {
        List<Genome> genomes = new ArrayList<Genome>();
        for (CoGeObject object : search("genomes", searchTerm)) {
            Genome g = new Genome(object);
            if (g.name!=null || g.description!=null) genomes.add(g);
        }
        return genomes;
    }

    /**
     * Genome fetch - used to populate the genomes
     * GET [base_url/genomes/id]
     * 
     * @param id the genome id
     */
    public Genome fetchGenome(int id) throws IOException, JSONException {
        JSONObject json = fetch("genomes", id);
        return new Genome(json);
    }

    /**
     * Genome fetch sequence - grab the full sequence response.
     * GET [​base_url/genomes/​id/sequence]
     *
     * @param id the genome id
     */
    public String fetchGenomeSequence(int id) throws IOException, JSONException {
        String url = baseUrl+"/genomes/"+id+"/sequence";
        JSONResource jr = resty.json(url);
        InputStream stream = jr.stream();
        StringBuffer buffer = new StringBuffer();
        int i = 0;
        while ((i=stream.read())!=-1) buffer.append((char)i);
        return buffer.toString();
    }

    /**
     * Genome fetch sequence - grab a subsequence.
     * GET [​base_url/genomes/​id/sequence/​chr?start=x&stop=y]
     *
     * @param id the genome id
     * @param chr the chromosome name
     * @param start the start index
     * @param stop the stop index
     */
    public String fetchGenomeSequence(int id, String chr, int start, int stop) throws IOException, JSONException {
        String url = baseUrl+"/genomes/"+id+"/sequence/"+chr+"?start="+start+"&stop="+stop;
        JSONResource jr = resty.json(url);
        InputStream stream = jr.stream();
        StringBuffer buffer = new StringBuffer();
        int i = 0;
        while ((i=stream.read())!=-1) buffer.append((char)i);
        return buffer.toString();
    }

    /**
     * Add a new genome. The response will contain the genome id if successful.
     * PUT [base_url/genomes]
     */
    public CoGeResponse addGenome(Organism organism, String name, String description, String version, String sourceName, String type, boolean restricted, String irodsPath)
        throws CoGeException, IOException, JSONException {
        if (username==null || token==null) throw CoGeException.missingAuthException();
        String url = baseUrl+"/genomes?username="+username+"&token="+token;
        JSONObject json = new JSONObject();
        json.put("organism_id", organism.getId());
        JSONObject metadata = new JSONObject();
        metadata.put("name", name);
        metadata.put("description", description);
        metadata.put("version", version);
        metadata.put("source_name", sourceName);
        metadata.put("type", type);
        metadata.put("restricted", restricted);
        json.put("metadata", metadata);
        JSONArray sourceData = new JSONArray();
        JSONObject source = new JSONObject();
        source.put("type", "irods");
        source.put("path", irodsPath);
        sourceData.put(source);
        json.put("source_data", sourceData);
        System.out.println(json);
        JSONResource resource = resty.json(url, Resty.put(Resty.content(json)));
        JSONObject response = resource.object();
        if (isError(response)) throw new CoGeException(response);
        return new CoGeResponse(response);
    }

    ////////// Feature //////////

    /**
     * Feature search
     * GET [base_url/features/search/term]
     *
     * @param searchTerm a text string to search on
     */
    public List<Feature> searchFeature(String searchTerm) throws IOException, JSONException {
        List<Feature> features = new ArrayList<Feature>();
        String url = baseUrl+"/features/search/"+searchTerm.replaceAll(" ","%20"); // should use a special-purpose method for this
        JSONResource jr  = resty.json(url);
        JSONObject jo = jr.object();
        Iterator<String> joit = jo.keys();
        while (joit.hasNext()) {
            String jkey = joit.next();
            if (jkey.equals("features")) {
                JSONArray ja = jo.getJSONArray(jkey);
                for (int i=0; i<ja.length(); i++) {
                    JSONObject json = ja.getJSONObject(i);
                    Feature f = new Feature(json, this);
                    features.add(f);
                }
            }
        }
        return features;
    }

    /**
     * Feature Fetch
     * GET [base_url/features/id]
     */
    public Feature fetchFeature(int id)  throws IOException, JSONException {
        String url = baseUrl+"/features/"+id;
        JSONResource jr = resty.json(url);
        JSONObject json = jr.object();
        return new Feature(json, this);
    }

    /**
     * Feature fetch sequence - grab the feature's sequence.
     * GET [​base_url/features/​id/sequence]
     *
     * @param id the feature id
     */
    public String fetchFeatureSequence(int id) throws IOException, JSONException {
        String url = baseUrl+"/features/"+id+"/sequence";
        JSONResource jr = resty.json(url);
        InputStream stream = jr.stream();
        StringBuffer buffer = new StringBuffer();
        int i = 0;
        while ((i=stream.read())!=-1) buffer.append((char)i);
        String seq = buffer.toString();
        if (seq.contains("error")) {
            return CoGeException.getErrorMessage(new JSONObject(seq));
        } else {
            return seq;
        }
    }

    ////////// Experiment //////////

    /**
     * Experiment search
     * GET [base_url/experiments/search/term]
     *
     * @param searchTerm a text string to search on
     */
    public List<Experiment> searchExperiment(String searchTerm) throws IOException, JSONException {
        List<Experiment> experiments = new ArrayList<Experiment>();
        for (CoGeObject object : search("experiments", searchTerm)) {
            experiments.add(new Experiment(object));
        }
        return experiments;
    }

    /**
     * Experiment fetch - used to populate the genomes
     * GET [base_url/experiments/id]
     * 
     * @param id the experiment id
     */
    public Experiment fetchExperiment(int id) throws IOException, JSONException {
        JSONObject json = fetch("experiments", id);
        return new Experiment(json);
    }

    ////////// Notebook //////////

    /**
     * Notebook search
     * GET [base_url/notebooks/search/term]
     *
     * @param searchTerm a text string to search on
     */
    public List<Notebook> searchNotebook(String searchTerm) throws IOException, JSONException {
        List<Notebook> notebooks = new ArrayList<Notebook>();
        for (CoGeObject object : search("notebooks", searchTerm)) {
            notebooks.add(new Notebook(object));
        }
        return notebooks;
    }

    /**
     * Notebook fetch - used to populate the genomes
     * GET [base_url/notebooks/id]
     * 
     * @param id the notebook id
     */
    public Notebook fetchNotebook(int id) throws IOException, JSONException {
        JSONObject json = fetch("notebooks", id);
        return new Notebook(json);
    }


    ////////// Group //////////

    /**
     * Group search
     * GET [base_url/groups/search/term]
     *
     * @param searchTerm a text string to search on
     */
    public List<Group> searchGroup(String searchTerm) throws IOException, JSONException {
        List<Group> groups = new ArrayList<Group>();
        String url = baseUrl+"/groups/search/"+searchTerm.replaceAll(" ","%20"); // should use a special-purpose method for this
        List<Group> objects = new ArrayList<Group>();
        JSONResource jr  = resty.json(url);
        JSONObject jo = jr.object();
        Iterator<String> joit = jo.keys();
        while (joit.hasNext()) {
            String jkey = joit.next();
            if (jkey.equals("groups")) {
                JSONArray ja = jo.getJSONArray(jkey);
                for (int i=0; i<ja.length(); i++) {
                    JSONObject jjo = ja.getJSONObject(i);
                    int id = jjo.getInt("id"); // all returned objects should have an id
                    String name = jjo.getString("name");
                    String description = jjo.getString("description");
                    String role = jjo.getString("role");
                    groups.add(new Group(id, name, description, role));
                }
            }
        }
        return groups;
    }

    /**
     * Group fetch - used to populate the genomes
     * GET [base_url/groups/id]
     * 
     * @param id the group id
     */
    public Group fetchGroup(int id) throws IOException, JSONException {
        JSONObject json = fetch("groups", id);
        return new Group(json);
    }

    ////////// DataStoreList //////////

    /**
     * Get a list from the data store corresponding to the given path.
     * GET [base_url/irods/list/path]
     *
     */
    public DataStoreList listDataStore(String path) throws CoGeException, IOException, JSONException {
        if (username==null || token==null) throw CoGeException.missingAuthException();
        String url = baseUrl+"/irods/list/"+path+"?username="+username+"&token="+token;
        JSONResource jr = resty.json(url);
        JSONObject jo = jr.object();
        if (isError(jo)) throw new CoGeException(jo);
        return new DataStoreList(jo);
    }

    ////////// Generic //////////

    /**
     * Generic search method
     */
    protected List<CoGeObject> search(String orgKey, String searchTerm) throws IOException, JSONException {
        String url = baseUrl+"/"+orgKey+"/search/"+searchTerm.replaceAll(" ","%20"); // should use a special-purpose method for this
        List<CoGeObject> objects = new ArrayList<CoGeObject>();
        JSONResource jr  = resty.json(url);
        JSONObject jo = jr.object();
        Iterator<String> joit = jo.keys();
        while (joit.hasNext()) {
            String jkey = joit.next();
            if (jkey.equals(orgKey)) {
                JSONArray ja = jo.getJSONArray(jkey);
                for (int i=0; i<ja.length(); i++) {
                    JSONObject jjo = ja.getJSONObject(i);
                    int id = jjo.getInt("id"); // all returned objects should have an id
                    String name = jjo.getString("name");
                    String description = jjo.getString("description");
                    objects.add(new CoGeObject(id, name, description));
                }
            }
        }
        return objects;
    }

    /**
     * Generic fetch method
     */
    protected JSONObject fetch(String orgKey, int id) throws IOException, JSONException {
        String url = baseUrl+"/"+orgKey+"/"+id;
        JSONResource jr = resty.json(url);
        return jr.object();
    }

    /**
     * Print out an arbitrary JSON response, for testing purposes.
     *
     * @param url the full API URL
     */
    void printResponse(String url) throws IOException, JSONException {
        JSONResource jr  = resty.json(url);
        JSONObject jo = jr.object();
        System.out.println(jo.toString());
    }

    /**
     * Return true if the JSONObject is a CoGe error message.
     */
    boolean isError(JSONObject json) {
        return json.has("error");
    }
    
}
