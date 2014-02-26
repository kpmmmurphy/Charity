package json;

import database.DBConnect;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import utilities.DirectoryManager;

/**
 *
 * @author Kevin Murphy
 */
public class Article extends CustomJSONObject {
    
    /* Debug mechinism for testing */
    private boolean DEBUG_ON = true;
    
    /* Charity name for creating the write path for the JSON file */
    private String charityName;
    private String trimmedCharityName;
    private boolean authorised;
    private String servletContext;
    
    /* Session for tracking who's creating the Article */
    private HttpSession session;
    
    /* Article Title*/
    private String    title;
    /* ID of Article taken from the Database */
    private int       id;
    /* Short description of article */
    private String    description;
    /* Content of the Article */
    private String    content;
    /* Date the article was created. Auto Generated */
    private String    date;
    /* Image name associated with the article*/
    private String    img;
    /* Type of post - General, Lost and Found, Sponsor etc*/
    private String    type;
    /* JSONArray for Articles tags */
    private JSONArray tags;
    /* Approved status, automatically approved if create by admin */
    private boolean   approved = false;
    /* Linked map to house Article details for JSON conversion */
    private LinkedHashMap articleHashMap;
    /* JSONArray for Articles Comments, initializes an empty array. Inputting of comments to be handled with javascript */
    private JSONArray     comments;
     
    /* The path of the JSON file  */
    private String jsonPath;
    
    /*
     * Constructor
     * Initialises all attributes
     * 
     * @param title   The title of the article
     * @param content The content of the Article
     */
    public Article( HttpServletRequest request, LinkedHashMap articleHashMap, String servletContext ){
        super();
        initializeDetials(request, servletContext);
        
        this.id              = generateArticleId();
        this.title           = articleHashMap.get("title").toString();
        this.description     = articleHashMap.get("description").toString();
        this.content         = articleHashMap.get("content").toString();
        this.img             = articleHashMap.get("img").toString();
        this.type            = articleHashMap.get("type").toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
	this.date            = sdf.format(new Date()); 
        this.tags            = new JSONArray();
        this.comments        = new JSONArray();
       
        //Checks if the user is authorised from the Session, if so approves post 
        if(authorised){
            approved = true;
        }
        createArticleJSONFile();
    }
    
    public static void main(String[] args){
        //testArticleClass();
    }
    
    /*
     * Method to test and display the basic functionality of the class
     */
    private static void testArticleClass(){
        LinkedHashMap<String, String> testMap = new LinkedHashMap<>();
        testMap.put("title", "First Article");
        testMap.put("description", "Description here! ");
        testMap.put("content", "Content here! ");
        testMap.put("img", "Image name here! ");
        testMap.put("type", "General! ");
        
        /* Gets the current path */
        Path currentRelativePath = Paths.get("");
        /* Gets the absolute current path and adds the JSON file name to it */
        String jsonPath = currentRelativePath.toAbsolutePath().toString() + "/articles.json";
        System.out.println("Write/Read Path: " + jsonPath);
        
        /* Creating an Article*/
        Article article = new Article(null, testMap);
        //Writes out the Article to file */
        article.createArticleJSONFile();
    }
    
    /*
     * Handels the writing-out to file of an Article. 
     * If no json file exists, it creates one. If one does, it
     * reads it in and appends the new article on to the end
     * of the JSON array housing the already created articles. 
     * 
     * @param path  The path to be written out to 
     */
    private void createArticleJSONFile(){
        JSONObject articlesObj;
        JSONArray  articlesArray;
        JSONObject articleObj = createJSONArticleObj();
        
        
        
        /* Checks if JSON file exists, if not creates it */
        File jsonArticleFile = new File(jsonPath);
        if(!jsonArticleFile.exists() && !jsonArticleFile.isDirectory()) {
            articlesObj = createNewJSONArticleFile();
            articlesArray = new JSONArray();
        }else{
            articlesObj   = readJsonFile(jsonPath);
            articlesArray = (JSONArray)articlesObj.get("articles");
        }
        articlesArray.add(articleObj);
        
        articlesObj.put("articles", articlesArray);
        
        writeJsonToFile(articlesObj, jsonPath);
    }
    
    /*
     * Creates a new JSON article file, containing a JSON array to 
     * hold the individual articles
     * 
     * @return JSONObject 
     */
    private JSONObject createNewJSONArticleFile(){
        JSONObject articlesObj = new JSONObject();
        JSONArray  articlesArray    = new JSONArray();
        
        articlesObj.put("articles", articlesArray);
        
        return articlesObj;
    }
    
    /*
     * Formats the Article's JSON file
     * 
     * @return JSONObject containing comment attributes
     */
    private JSONObject createJSONArticleObj(){
        JSONObject articleObj = new JSONObject();
        articleHashMap = new LinkedHashMap<>();
        
        /* Formatting of JSON Article Keys and Values*/
        articleHashMap.put("id",          this.id);
        articleHashMap.put("title",       this.title);
        articleHashMap.put("type",       this.type);
        articleHashMap.put("description", this.description);
        articleHashMap.put("content",     this.content);
        articleHashMap.put("date",        this.date);
        articleHashMap.put("img",         this.img);
        articleHashMap.put("tags",        this.tags);
        articleHashMap.put("comments",    this.comments);
        articleHashMap.put("approved",    this.approved);
        
        //Adds the article, indexed by it's id
        articleObj.put(this.id, articleHashMap);
        
        if(DEBUG_ON){
            String articleJSONString = JSONValue.toJSONString(articleObj);
            System.out.println(articleJSONString);
        }
        
        return articleObj;
    }
    
    private int generateArticleId(){
        int id = 0;
        System.out.println("Write/Read Path: " + jsonPath);
        
        JSONObject articlesArrayObj = (JSONObject)readJsonFile(jsonPath);
        if(articlesArrayObj != null){
            JSONArray  articlesArray     = (JSONArray)articlesArrayObj.get("articles");
            if(DEBUG_ON){
                System.out.println("Articles array: " + articlesArrayObj);
                System.out.println("Num Articles in array: " + articlesArray.size());
            }
            //The size of the array plus one, to form the new id
            id = articlesArray.size() + 1;
        }
        
        
        return id;
    }
    
    private void initializeDetials(HttpServletRequest request, String servletContext){
        //Get the Session 
        session     = request.getSession(false);
        //Get the charity name, must initilize when user is viewing each charity 
        charityName = (String)session.getAttribute("charityName");
        //Only admins will be authorised, see Signup or Login session instantiation
        authorised  = (Boolean)session.getAttribute("authorised");
        //The trimmed and lower case charity name, with spaces removed
        trimmedCharityName = DirectoryManager.toLowerCaseAndTrim(charityName);
        
        /* Build the path for the uploaded file*/
        //Get the Servlet Context for writing the json file
        String charitiesDir   = "charities/";
        jsonPath = servletContext + charitiesDir + trimmedCharityName + "/json/articles.json";
    }
    
}
