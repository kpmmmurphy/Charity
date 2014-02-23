package json;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Kevin Murphy
 */
public class Article extends CustomJSONObject {
    
    /* Debug mechinism for testing */
    private boolean DEBUG_ON = true;
    
    /* Article Title*/
    private String   title;
    /* Content of the Article */
    private String   content;
    /* Date the article was created. Auto Generated */
    private String   date;
    /* Linked map to house Article details for JSON conversion */
    private LinkedHashMap articleHashMap;
    /* JSONArray for Articles Comments */
    private JSONArray comments;
    /* The path of the JSON file  */
    private String jsonPath;

    /* Things to include
            Abstract
            photo
            tags
            approved
            submit and clear
    */
    /*
     * Constructor
     * Initialises all attributes
     * 
     * @param title   The title of the article
     * @param content The content of the Article
     */
    public Article(String title, String content){
        super();
        this.title   = title;
        this.content = content;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
	this.date = sdf.format(new Date()); 
        this.comments = new JSONArray();
    }
    
    public static void main(String[] args){
        testArticleClass();
    }
    
    /*
     * Method to test and display the basic functionality of the class
     */
    private static void testArticleClass(){
        /* Gets the current path */
        Path currentRelativePath = Paths.get("");
        /* Gets the absolute current path and adds the JSON file name to it */
        String jsonPath = currentRelativePath.toAbsolutePath().toString() + "\\articles.json";
        System.out.println("Write/Read Path: " + jsonPath);
        
        /* Creating an Article*/
        Article article = new Article("Our First Article", "This is my first Article!");
        //Writes out the Article to file */
        article.createArticleJSONFile(jsonPath);
    }
    
    /*
     * Handels the writing-out to file of an Article. 
     * If no json file exists, it creates one. If one does, it
     * reads it in and appends the new article on to the end
     * of the JSON array housing the already created articles. 
     * 
     * @param path  The path to be written out to 
     */
    private void createArticleJSONFile(String path){
        JSONObject articlesObj;
        JSONArray  articlesArray;
        JSONObject articleObj = createJSONArticleObj();
        
        /* Checks if JSON file exists, if not creates it */
        File jsonArticleFile = new File(path);
        if(!jsonArticleFile.exists() && !jsonArticleFile.isDirectory()) {
            articlesObj = createNewJSONArticleFile();
            articlesArray = new JSONArray();
        }else{
            articlesObj = CustomJSONObject.readJsonFile(path);
            articlesArray = (JSONArray)articlesObj.get("articles");
            System.out.println("yiss" + articlesArray);
        }
        articlesArray.add(articleObj);
        
        articlesObj.put("articles", articlesArray);
        
        writeJsonToFile(articlesObj, path);
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
        articleHashMap.put("title",    this.title);
        articleHashMap.put("date",     this.date);
        articleHashMap.put("content",  this.content);
        articleHashMap.put("comments", this.comments);
        
        articleObj.put("article", articleHashMap);
        
        if(DEBUG_ON){
            String articleJSONString = JSONValue.toJSONString(articleObj);
            System.out.println(articleJSONString);
        }
        
        return articleObj;
    }
    
}
