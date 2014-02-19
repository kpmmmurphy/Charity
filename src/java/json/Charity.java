package json;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.*;


/**
 * Implementation of Charity object, housing public information to be
 * displayed on the charity's page.  
 * 
 * Provides methods for converting Charity Objects to and 
 * from JSON files
 * 
 * Extends the abstract class CustomJSONObject, which provides
 * write and read methods for JSON files.
 * @author kpm2
 * @version 1
 * @date 2/2/14
 */
public class Charity extends CustomJSONObject{
    
    /* Debug mechinism for testing */
    private static boolean DEBUG_ON = true;
    
    /* The path of the JSON file to be read from / write to */
    private static String jsonPath = ".";
   
    
    /*LinkedHashMap to ensure order of JSON file*/
    private Map<String, Object> charityHashMap;
    /* House the (key,value) pairs of the Charity's class attributes  */
    private JSONObject    charityJSONObj;
    
    /* Charity's class attributes */
    private final String name;
    private String description = "";
    private String address;
    private String telephone = "";
    private String facebook = "";
    private String twitter = "";
    private String googleplus = "";
    
    
    /*
     * Constructor
     */
    public Charity( String name, String description, String address, String telephone, String facebook, String twitter, String googleplus) {
        super();
        this.name = name;
        this.description = description;
        this.address = address;
        this.telephone = telephone;
        this.facebook = facebook;
        this.twitter = twitter;
        this.googleplus = googleplus;
    }
    
    public static void main(String[] args) throws FileNotFoundException{
       testCharityClass();
    }
    
    /*
     * A method for testing the class's functionality
     */
    private static void testCharityClass() throws FileNotFoundException{
        Path currentRelativePath = Paths.get("");
        jsonPath = currentRelativePath.toAbsolutePath().toString() + "/$charity.json";
        System.out.println("Write/Read Path: " + jsonPath);
        
        //Creating a Charity Object
        Charity charity = new Charity("Concern", "Concern worldwide","Co.Cork, Ireland", "001121","facebookurl","twitterurl","googleplusurl");
        //Writes it to a file
        charity.createCharityJSONFile(jsonPath);
        
        //Setting Charity Description 
        charity.setDescription("This is the new desc.");
        //Write to file
        charity.createCharityJSONFile(jsonPath);
        
        //Reading JSON charity file from disk and Convert to Charity Obj
        Charity newCharity = parseJSONtoCharityObj("Concern", jsonPath);
        //Set Description
        newCharity.setDescription("new Charity desc");
        //Writes out to specified path
        newCharity.createCharityJSONFile(jsonPath);
    }
    
    /*
     * Handles the creation of the a JSONObject which holds 
     * the Charity's class attributes and writes them out to a 
     * specified file
     * 
     * @param path  The write path
     */
    public void createCharityJSONFile(String servletContext) throws FileNotFoundException{
        
        //Converts name to lower case, trims and replaces whitespaces
        String lowerCaseName = this.name.toLowerCase().trim().replaceAll("\\s+","");
         
        //The path where the JSON file will be output to
        String jsonFilePath = servletContext + "/charities/" + lowerCaseName + "/json/charity.json";
        
        JSONObject charityObj = new JSONObject();
        charityHashMap = new LinkedHashMap<>();
        charityHashMap.put("name", this.name);
        charityHashMap.put("description", this.description);
        charityHashMap.put("address", this.address);
        charityHashMap.put("telephone", this.telephone);
        charityHashMap.put("facebook", this.facebook);
        charityHashMap.put("twitter", this.twitter);
        charityHashMap.put("googleplus", this.googleplus);
        charityObj.put("charity", charityHashMap);
        if(DEBUG_ON){
            String charityJSONString = JSONValue.toJSONString(charityObj);
            System.out.println(charityJSONString);
        }
        super.writeJsonToFile(charityObj, jsonFilePath);
    }
    
    
    /*
     * Reads in JSON file from disk and converts it to a Charity
     * Object
     * 
     * @param path  The path of the JSON file to be parsed and converted
     * 
     * @return  Charity object
     */
    public static Charity parseJSONtoCharityObj(String nameOfCharity, String servletContext){
        
        //Converts name to lower case, trims and replaces whitespaces
        String lowerCaseName = nameOfCharity.toLowerCase().trim().replaceAll("\\s+","");
        
        //The path where the JSON file will be output to
        String jsonFilePath = servletContext + "/charities/" + lowerCaseName + "/json/charity.json";
        
        JSONObject jsonCharity = readJsonFile(jsonFilePath);
        
        if(DEBUG_ON){
            System.out.println( "Read in CharityObj \n" + jsonCharity);
        }
        
        jsonCharity = (JSONObject)jsonCharity.get("charity");
        
        String charityName = jsonCharity.get("name").toString();
        String desc = jsonCharity.get("description").toString();
        String address = jsonCharity.get("address").toString();
        String tele = jsonCharity.get("telephone").toString();
        String face = jsonCharity.get("facebook").toString();
        String twit = jsonCharity.get("twitter").toString();
        String google = jsonCharity.get("googleplus").toString();
        
        return new Charity(charityName, desc, address, tele, face, twit, google);
    }
    
     public static JSONObject parseJSON(String nameOfCharity, String servletContext){
        
        //Converts name to lower case, trims and replaces whitespaces
        String lowerCaseName = nameOfCharity.toLowerCase().trim().replaceAll("\\s+","");
        
        //The path where the JSON file will be output to
        String jsonFilePath = servletContext + "/charities/" + lowerCaseName + "/json/charity.json";
        
        JSONObject jsonCharity = readJsonFile(jsonFilePath);
        
        
        return jsonCharity;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the telephone
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * @param telephone the telephone to set
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * @return the facebook
     */
    public String getFacebook() {
        return facebook;
    }

    /**
     * @param facebook the facebook to set
     */
    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    /**
     * @return the twitter
     */
    public String getTwitter() {
        return twitter;
    }

    /**
     * @param twitter the twitter to set
     */
    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    /**
     * @return the googleplus
     */
    public String getGoogleplus() {
        return googleplus;
    }

    /**
     * @param googleplus the googleplus to set
     */
    public void setGoogleplus(String googleplus) {
        this.googleplus = googleplus;
    }

    /**
     * @return the charityJSONObject
     */
    public JSONObject getCharityJSONObject() {
        return charityJSONObj;
    }
    
    
    
    
}