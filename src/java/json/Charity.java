package json;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import static json.Article.ARTICLES_FILE_NAME;
import static json.Article.CHARITIES_DIR;
import static json.Article.JSON_DIR;
import org.json.simple.*;
import utilities.DirectoryManager;
import utilities.Upload;


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
    
     private static final String CHARITY_NAME_FROM_SESSION = "charityName";
    
    public static final String CHARITY_FILE_NAME = "charity.json";
    public static final String CHARITIES_DIR = "charities/";
    public static final String JSON_DIR = "/json/";
    
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
    private String logo = "";
    
    
    /*
     * Constructor
     */
    public Charity( String name, String description, String address, String telephone, String facebook, String twitter, String googleplus, String logo) {
        super();
        this.name = name;
        this.description = description;
        this.address = address;
        this.telephone = telephone;
        this.facebook = facebook;
        this.twitter = twitter;
        this.googleplus = googleplus;
        this.logo = logo;
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
        charityHashMap.put("logo", this.logo);
        charityObj.put("charity", charityHashMap);
        if(DEBUG_ON){
            String charityJSONString = JSONValue.toJSONString(charityObj);
            System.out.println(charityJSONString);
        }
        writeJsonToFile(charityObj, jsonFilePath);
    }
    
    
    /*
     * Reads in JSON file from disk and converts it to a Charity
     * Object
     * 
     * @param path  The path of the JSON file to be parsed and converted
     * 
     * @return  Charity object
     */
    public static Charity parseJSONtoCharityObj(HttpServletRequest request){
        
        //The path where the JSON file will be output to
        String jsonFilePath = getCharityJSONPath(request);
        
        JSONObject jsonCharity = readJsonFile(jsonFilePath);
        
        if(DEBUG_ON){
            System.out.println( "Read in CharityObj: " + jsonCharity);
            System.out.println( "Json file path : " + jsonFilePath);
        }
        
        jsonCharity = (JSONObject)jsonCharity.get("charity");
        
        String charityName = jsonCharity.get("name").toString();
        String desc = jsonCharity.get("description").toString();
        String address = jsonCharity.get("address").toString();
        String tele = jsonCharity.get("telephone").toString();
        String face = jsonCharity.get("facebook").toString();
        String twit = jsonCharity.get("twitter").toString();
        String google = jsonCharity.get("googleplus").toString();
        String logo = jsonCharity.get("logo").toString();
        
        return new Charity(charityName, desc, address, tele, face, twit, google, logo);
    }
    
    /*
     * Overloaded - Reads in JSON file from disk and converts it to a Charity
     * Object
     * 
     * @param path  The path of the JSON file to be parsed and converted
     * 
     * @return  Charity object
     */
    public static Charity parseJSONtoCharityObj(String jsonFilePath){
        
        JSONObject jsonCharity = readJsonFile(jsonFilePath);
        
        if(DEBUG_ON){
            System.out.println( "Read in CharityObj: " + jsonCharity);
            System.out.println( "Json file path : " + jsonFilePath);
        }
        
        jsonCharity = (JSONObject)jsonCharity.get("charity");
        
        String charityName = jsonCharity.get("name").toString();
        String desc = jsonCharity.get("description").toString();
        String address = jsonCharity.get("address").toString();
        String tele = jsonCharity.get("telephone").toString();
        String face = jsonCharity.get("facebook").toString();
        String twit = jsonCharity.get("twitter").toString();
        String google = jsonCharity.get("googleplus").toString();
        String logo = jsonCharity.get("logo").toString();
        
        return new Charity(charityName, desc, address, tele, face, twit, google, logo);
    }
    
     public static JSONObject parseJSON(HttpServletRequest request ){
         
        //The path where the JSON file will be output to
        String jsonFilePath = getCharityJSONPath(request);
        JSONObject jsonCharity = readJsonFile(jsonFilePath);
        
        return jsonCharity;
    }
     
     public static String getCharityJSONPath(HttpServletRequest request){
        String jsonPath = "";
        HttpSession session = request.getSession(true);
        String charityName = (String)session.getAttribute(CHARITY_NAME_FROM_SESSION);
        if(charityName != null && !"".equals(charityName)){
            /* Build the path for reading in the  file*/
            String servletContext = request.getServletContext().getRealPath("/");
            jsonPath = servletContext + CHARITIES_DIR + DirectoryManager.toLowerCaseAndTrim(charityName) + JSON_DIR + CHARITY_FILE_NAME ;
        }
        return jsonPath;
    }
     
     public static String getCharityUploadsPath(HttpServletRequest request){
        String jsonPath = "";
        HttpSession session = request.getSession(true);
        String charityName = (String)session.getAttribute(CHARITY_NAME_FROM_SESSION);
        if(charityName != null && !"".equals(charityName)){
            /* Build the path for reading in the  file*/
            String servletContext = request.getServletContext().getRealPath("/");
            jsonPath =  CHARITIES_DIR + DirectoryManager.toLowerCaseAndTrim(charityName) + Upload.UPLOADS_DIR;
        }
        return jsonPath;
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
     * @return the logo
     */
    public String getLogo() {
        return logo;
    }
    
    /**
     * @param filename filename of the logo
     */
    public void setLogo(String filename) {
        this.logo = filename;
    }
    

    /**
     * @return the charityJSONObject
     */
    public JSONObject getCharityJSONObject() {
        return charityJSONObj;
    }
    
    
    
    
}