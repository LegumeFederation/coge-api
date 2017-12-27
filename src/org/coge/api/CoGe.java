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
     * Organism Search
     * GET [base_url/organisms/search/term]
     *
     * @param searchTerm a text string to search on
     */
    public List<Organism> searchOrganism(String searchTerm) throws IOException, JSONException {
        List<Organism> organisms = new ArrayList<Organism>();
        for (JSONObject json : search("organisms", searchTerm)) {
            organisms.add(new Organism(json));
        }
        return organisms;
    }

    /**
     * Organism Fetch - used to populate the genomes
     * GET [base_url/organisms/id]
     * 
     * @param id the organism id
     */
    public Organism fetchOrganism(int id) throws IOException, JSONException {
        JSONObject json = fetch("organisms", id);
        return new Organism(json);
    }

    /**
     * Organism Add
     * PUT [base_url/organisms]
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
     * Genome Search
     * GET [base_url/genomes/search/term]
     *
     * @param searchTerm a text string to search on
     * @param includeDeleted set true if you'd like to include deleted genomes in the results
     * @return a list of populated Genome instances
     */
    public List<Genome> searchGenome(String searchTerm, boolean includeDeleted) throws IOException, JSONException {
        List<Genome> genomes = new ArrayList<Genome>();
        for (JSONObject json : search("genomes", searchTerm)) {
            Genome g = new Genome(json);
            if ((g.isDeleted() && includeDeleted) || !g.isDeleted()) genomes.add(g);
        }
        return genomes;
    }

    /**
     * Genome Fetch - populate a Genome
     * GET [base_url/genomes/id]
     * 
     * @param id the genome id
     */
    public Genome fetchGenome(int id) throws IOException, JSONException {
        JSONObject json = fetch("genomes", id);
        return new Genome(json);
    }

    /**
     * Genome Fetch Sequence - grab the full sequence response (a FASTA)
     * GET [​base_url]/genomes/​id/sequence
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
     * Genome fetch sequence - grab a subsequence (FASTA)
     * GET [​base_url]/genomes/​id/sequence/​chr?start=x&stop=y
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
     * Genome fetch sequence - an entire chromosome (FASTA)
     * GET [​base_url]/genomes/​id/sequence/​chr
     *
     * @param id the genome id
     * @param chr the chromosome name
     */
    public String fetchChromosomeSequence(int id, String chr) throws IOException, JSONException {
        String url = baseUrl+"/genomes/"+id+"/sequence/"+chr;
        JSONResource jr = resty.json(url);
        InputStream stream = jr.stream();
        StringBuffer buffer = new StringBuffer();
        int i = 0;
        while ((i=stream.read())!=-1) buffer.append((char)i);
        return buffer.toString();
    }

    /**
     * Genome fetch features - grab the features of the given type
     * GET [base_url]/genomes/id/features/type
     *
     * @param id the genome id
     * @param type the feature type (e.g. gene)
     */
    public List<Feature> fetchGenomeFeatures(int id, String type) throws IOException, JSONException {
        String url = baseUrl+"/genomes/"+id+"/features/"+type;
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        JSONResource jr  = resty.json(url);
        JSONObject jo = jr.object();
        if (id==jo.getInt("id")) {
            JSONArray ja = jo.getJSONArray("features");
            ArrayList<Feature> features = new ArrayList<Feature>();
            for (int i=0; i<ja.length(); i++) {
                features.add(new Feature(ja.getJSONObject(i)));
            }
            return features;
        } else {
            return null;
        }
    }


    /**
     * Genome Add
     * PUT [base_url/genomes]
     *
     * @param genome the genome to add to CoGe, with as many fields populated as you can (but lacking id, of course).
     * @param irodsPath the path to the FASTA file on the iRODS DataStore (it can be gzipped).
     */
    public CoGeResponse addGenome(Genome genome, String irodsPath) throws CoGeException, IOException, JSONException {
        if (username==null || token==null) throw CoGeException.missingAuthException();
        String url = baseUrl+"/genomes?username="+username+"&token="+token;
        JSONObject json = new JSONObject();
        json.put("organism_id", genome.getOrganism().getId());
        JSONObject metadata = new JSONObject();
        metadata.put("name", genome.getName());
        metadata.put("description", genome.getDescription());
        metadata.put("version", genome.getVersion());
        metadata.put("source_name", genome.getSourceName());
        metadata.put("sequence_type", genome.getSequenceType().getName());
        metadata.put("restricted", genome.isRestricted());
        json.put("metadata", metadata);
        JSONArray sourceData = new JSONArray();
        JSONObject source = new JSONObject();
        source.put("type", "irods");
        source.put("path", irodsPath);
        sourceData.put(source);
        json.put("source_data", sourceData);
        JSONResource resource = resty.json(url, Resty.put(Resty.content(json)));
        JSONObject response = resource.object();
        if (isError(response)) throw new CoGeException(response);
        return new CoGeResponse(response);
    }

    /**
     * Genome Update -- THIS IS NOT YET IMPLEMENTED AS OF 2/17
     * POST [base_url/genomes/id]
     *
     * @param genome the genome instance which is used to update its CoGe version; id is essential, other fields are updated if populated.
     */
    public boolean updateGenome(Genome genome) throws CoGeException, IOException, JSONException {
        if (username==null || token==null) throw CoGeException.missingAuthException();
        String url = baseUrl+"/genomes/"+genome.getId()+"?username="+username+"&token="+token;
        JSONObject json = new JSONObject();
        if (genome.getOrganism()!=null) json.put("organism_id", genome.getOrganism().getId());
        JSONObject metadata = new JSONObject();
        if (genome.getName()!=null) metadata.put("name", genome.getName());
        if (genome.getDescription()!=null) metadata.put("description", genome.getDescription());
        if (genome.getVersion()!=null) metadata.put("version", genome.getVersion());
        if (genome.getSourceName()!=null) metadata.put("source_name", genome.getSourceName());
        if (genome.getSequenceType()!=null) metadata.put("sequence_type", genome.getSequenceType());
        metadata.put("restricted", genome.isRestricted()); // always set, may be default
        json.put("metadata", metadata);
        JSONResource resource = resty.json(url, Resty.content(json));
        JSONObject response = resource.object();
        if (isError(response)) throw new CoGeException(response);
        CoGeResponse cogeResponse = new CoGeResponse(response);
        return cogeResponse.getSuccess();
    }

    /**
     * Genome Delete -- THIS IS NOT YET IMPLEMENTED AS OF 2/17
     * DELETE [base_url/genomes/id]
     *
     * @param genome the genome to delete from CoGe. Only the id is used.
     */
    public boolean deleteGenome(Genome genome) throws CoGeException, IOException, JSONException {
        if (username==null || token==null) throw CoGeException.missingAuthException();
        String url = baseUrl+"/genomes/"+genome.getId()+"?username="+username+"&token="+token;
        System.out.println("DELETE URL:"+url);
        JSONResource resource = resty.json(url, Resty.delete());
        JSONObject response = resource.object();
        if (isError(response)) throw new CoGeException(response);
        CoGeResponse cogeResponse = new CoGeResponse(response);
        return cogeResponse.getSuccess();
    }

    
    // https://geco.iplantcollaborative.org/coge-qa/coge/api/v1/genomes/16911/features/
    // https://geco.iplantcollaborative.org/coge-qa/coge/api/v1/genomes/16911/features/gene
    // https://geco.iplantcollaborative.org/coge-qa/coge/api/v1/genomes/16911/features/CDS
    // {
    //     "id": 16911,
    //     "features": [
    //         {
    //             "start": 352637,
    //             "chromosome": "1",
    //             "strand": -1,
    //             "type": "gene",
    //             "id": 306206750,
    //             "stop": 354969
    //         },
    //             ...
    //     ]
    // }



    ////////// Feature //////////

    /**
     * Feature Search
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
     * Feature Fetch Sequence - grab the feature's sequence.
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

    /**
     * Feature Add - load a new feature set onto a genome.
     * PUT [base_url/features]
     *
     * @param genome the genome to add the features to
     * @param name the name associated with this feature set
     * @param description the description of this feature set
     * @param version the version of this feature set
     * @param sourceName the name of the source of this feature set
     * @param irodsPath the path to the GFF file containing the features (it can be gzipped)
     */
    public CoGeResponse addFeatures(Genome genome, String name, String description, String version, String sourceName, String irodsPath) throws CoGeException, IOException, JSONException {
        if (username==null || token==null) throw CoGeException.missingAuthException();
        String url = baseUrl+"/features?username="+username+"&token="+token;
        JSONObject json = new JSONObject();
        json.put("genome_id", genome.getId());
        JSONObject metadata = new JSONObject();
        metadata.put("name", name);
        metadata.put("description", description);
        metadata.put("version", version);
        metadata.put("source_name", sourceName);
        json.put("metadata", metadata);
        JSONArray sourceData = new JSONArray();
        JSONObject source = new JSONObject();
        source.put("type", "irods");
        source.put("path", irodsPath);
        sourceData.put(source);
        json.put("source_data", sourceData);
        JSONResource resource = resty.json(url, Resty.put(Resty.content(json)));
        JSONObject response = resource.object();
        if (isError(response)) throw new CoGeException(response);
        return new CoGeResponse(response);
    }

    ////////// Experiment //////////

    /**
     * Experiment Search
     * GET [base_url/experiments/search/term]
     *
     * @param searchTerm a text string to search on
     */
    public List<Experiment> searchExperiment(String searchTerm) throws IOException, JSONException {
        List<Experiment> experiments = new ArrayList<Experiment>();
        for (JSONObject json : search("experiments", searchTerm)) {
            experiments.add(new Experiment(json));
        }
        return experiments;
    }

    /**
     * Experiment Fetch - used to populate the genomes
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
     * Notebook Search
     * GET [base_url/notebooks/search/term]
     *
     * @param searchTerm a text string to search on
     */
    public List<Notebook> searchNotebook(String searchTerm) throws IOException, JSONException {
        List<Notebook> notebooks = new ArrayList<Notebook>();
        for (JSONObject json : search("notebooks", searchTerm)) {
            notebooks.add(new Notebook(json));
        }
        return notebooks;
    }

    /**
     * Notebook Fetch - used to populate the genomes
     * GET [base_url/notebooks/id]
     * 
     * @param id the notebook id
     */
    public Notebook fetchNotebook(int id) throws IOException, JSONException {
        JSONObject json = fetch("notebooks", id);
        return new Notebook(json);
    }

    /**
     * Notebook Add Items
     * POST [base_url/notebooks/id/items/add]
     *
     * @param notebook the notebook to add the items to
     * @param items a map containing the Integer item id and String "genome" or "experiment"
     * @return true if the items add was successful
     */
    public boolean addItemsToNotebook(Notebook notebook, Map<Integer,String> items) throws CoGeException, IOException, JSONException {
        if (username==null || token==null) throw CoGeException.missingAuthException();
        String url = baseUrl+"/notebooks/"+notebook.getId()+"/items/add?username="+username+"&token="+token;
        JSONArray ja = new JSONArray();
        for (Integer id : items.keySet()) {
            String type = items.get(id);
            JSONObject jo = new JSONObject();
            jo.put("id", (int)id);
            jo.put("type", type);
            ja.put(jo);
        }
        JSONObject json = new JSONObject();
        json.put("items", ja);
        JSONResource resource = resty.json(url, Resty.content(json));
        JSONObject response = resource.object();
        if (isError(response)) throw new CoGeException(response);
        CoGeResponse cogeResponse = new CoGeResponse(response);
        return cogeResponse.getSuccess();
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
     *
     * @param objKey the key for the type of object to be searched for, e.g. genomes
     * @param searchTerm the term on which to search
     * @return a list of JSONObject instances which describe the found objects
     */
    protected List<JSONObject> search(String objKey, String searchTerm) throws IOException, JSONException {
        String url = baseUrl+"/"+objKey+"/search/"+searchTerm.replaceAll(" ","%20"); // should use a special-purpose method for this
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        JSONResource jr  = resty.json(url);
        JSONObject jo = jr.object();
        Iterator<String> joit = jo.keys();
        while (joit.hasNext()) {
            String jkey = joit.next();
            if (jkey.equals(objKey)) {
                JSONArray ja = jo.getJSONArray(jkey);
                for (int i=0; i<ja.length(); i++) {
                    jsons.add(ja.getJSONObject(i));
                }
            }
        }
        return jsons;
    }

    /**
     * Generic fetch method
     * @param objKey the key for the type of object to be fetched, e.g. genomes
     * @param id the object's id
     * @return a JSONObject describing the fetched object
     */
    protected JSONObject fetch(String objKey, int id) throws IOException, JSONException {
        String url = baseUrl+"/"+objKey+"/"+id;
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
