package json;

import database.DBConnect;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
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
    private static final boolean DEBUG_ON = true;
    private static final String CHARITY_NAME_FROM_SESSION = "charityName";
    
    public static final String ARTICLES_FILE_NAME = "articles.json";
    public static final String CHARITIES_DIR = "charities/";
    public static final String JSON_DIR = "/json/";
    
    
    /* Charity name for creating the write path for the JSON file */
    private String charityName;
    private static String trimmedCharityName;
    private boolean authorised;
    
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
    /* JSONArray for Articles Comments, initializes an empty array. Inputting of comments to be handled with javascript */
    private JSONArray     comments;
     
    /* The path of the JSON file  */
    private static String jsonPath;
    
    /*
     * Constructor
     * Initialises all attributes
     * 
     * @param title   The title of the article
     * @param content The content of the Article
     */
    public Article( HttpServletRequest request, LinkedHashMap articleObj ){
        super();
        initializeDetials(request);
        
        this.id              = generateArticleId(request);
        this.title           = (articleObj.get("title").toString()       != null) ? articleObj.get("title").toString()       : "" ;
        this.description     = (articleObj.get("description").toString() != null) ? articleObj.get("description").toString() : "" ;
        this.content         = (articleObj.get("content").toString()     != null) ? articleObj.get("content").toString()     : "" ;
        this.img             = (articleObj.get("img").toString()         != null) ? articleObj.get("img").toString()         : "" ;
        this.type            = (articleObj.get("type").toString()        != null) ? articleObj.get("type").toString()        : "general" ;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
	this.date            = sdf.format(new Date()); 
        this.tags            = createJSONTagsArray(articleObj.get("tags").toString());
        this.comments        = new JSONArray();
       
        //Checks if the user is authorised from the Session, if so approves post 
        if(authorised){
            approved = true;
        }
    }
    
    /*
     * Handels the writing-out to file of an Article. 
     * If no json file exists, it creates one. If one does, it
     * reads it in and appends the new article on to the end
     * of the JSON array housing the already created articles. 
     * 
     * @param path  The path to be written out to 
     */
    public void writeOutArticle(HttpServletRequest request){
        
        JSONObject articlesObj   = new JSONObject();
        JSONArray  articlesArray = new JSONArray();
        
        JSONObject articleObj    = createArticleJSONObj();
        String     jsonPath      = getArticlesJSONPath(request);
        
        /* Checks if JSON file exists, if not creates it */
        File jsonArticleFile = new File(jsonPath);
        if(!jsonArticleFile.exists() && !jsonArticleFile.isDirectory()) {
            articlesArray.add(articleObj);
        }else{
            articlesArray = getArticlesArrayFromFile(request);
            articlesArray.add(articleObj);
            
        }
        articlesObj.put("articles", articlesArray);
        
        writeJsonToFile(articlesObj, jsonPath);
    }
    
    
    
    /**
     * Formats the Article's JSON file
     * 
     * @return JSONObject containing comment attributes
     */
    private JSONObject createArticleJSONObj(){
        
        JSONObject articleObj = new JSONObject();
        
        /* Formatting of JSON Article Keys and Values*/
        articleObj.put("id", this.getId());
        articleObj.put("title", this.getTitle());
        articleObj.put("type", this.getType());
        articleObj.put("description", this.getDescription());
        articleObj.put("content", this.getContent());
        articleObj.put("date", this.getDate());
        articleObj.put("img", this.getImg());
        articleObj.put("tags", this.getTags());
        articleObj.put("comments", this.getComments());
        articleObj.put("approved", this.isApproved());
        
        if(DEBUG_ON){
            String articleJSONString = JSONValue.toJSONString(articleObj);
            System.out.println("Newest Article : " + articleObj);
        }
        
        return articleObj;
    }
    
    private int generateArticleId(HttpServletRequest request){
        /*
        int id = 0;
        System.out.println("Write/Read Path: " + jsonPath);
        
        JSONObject articlesArrayObj = (JSONObject)readJsonFile(jsonPath);
        if(articlesArrayObj != null){
            JSONArray  articlesArray     = (JSONArray)articlesArrayObj.get("articles");
            if(DEBUG_ON){
                System.out.println("Articles array: " + articlesArrayObj);
                System.out.println("Num Articles in array: " + articlesArray.size());
            }
            //The size of the array 
            id = articlesArray.size();
        }
        
        
        return id;
        */
        
        int generatedID = -1;
        //Get the Session 
        session     = request.getSession(true);
        //Get the charity name, must initilize when user is viewing each charity 
        int charityID = Integer.valueOf((String)session.getAttribute("charity_id"));
        
        if(charityID > 0){
            //Connect to Database
            DBConnect dbConnect   = new DBConnect();
            Connection connection = dbConnect.getConnection();

            String insertNewArticle = "INSERT INTO articles (charity_id)"
                                    + "VALUES(?)";
            try(PreparedStatement insertNewArticleStatement = connection.prepareStatement(insertNewArticle)) {
                
                insertNewArticleStatement.setInt(1, charityID);
                insertNewArticleStatement.executeUpdate();
            } catch (SQLException ex) {
                System.err.println(this.getClass().getName() + ": Article ID cannot be generated, Charity ID from Session does not match one in DB ");
            }
            
            String selectLatestArticleID = "SELECT id "
                                         + "From articles "
                                         + "WHERE charity_id = ? "
                                         + "ORDER BY date_and_time DESC";
           
            try(PreparedStatement selectNewArticleIDStatement = connection.prepareStatement(selectLatestArticleID);){
                selectNewArticleIDStatement.setInt(1, charityID);
                ResultSet idResultSet = selectNewArticleIDStatement.executeQuery();
                
                if(idResultSet.next()){
                    setId(idResultSet.getInt(1));
                    
                }
                if(DEBUG_ON){
                    System.out.println("Generated Article ID: " + getId());
                }
            } catch (SQLException ex) {
                System.err.println(this.getClass().getName() + ": Article ID cannot be retrieved, Charity ID from Session does not match one in DB ");
                ex.printStackTrace();
            }
        }
        
        return getId();
        
    }
    
    private void initializeDetials(HttpServletRequest request){
        //Get the Session 
        session     = request.getSession(true);
        //Get the charity name, must initilize when user is viewing each charity 
        charityName = (String)session.getAttribute("charityName");
        //Only admins will be authorised, see Signup or Login session instantiation
        setAuthorised((boolean) (Boolean)session.getAttribute("authorised"));
        //The trimmed and lower case charity name, with spaces removed
        trimmedCharityName = DirectoryManager.toLowerCaseAndTrim(charityName);
        
        /* Gets the path of the articles.json file */
        jsonPath = getArticlesJSONPath(request);
    }
    
    private static JSONArray createJSONTagsArray(String submittedTags){
        
        JSONArray jsonTagsArray = new JSONArray();
        if(submittedTags != null || !"".equals(submittedTags)){
            String[] singularTags = submittedTags.split("\\s+");
            for(int i = 0 ; i < singularTags.length ; i++){
                jsonTagsArray.add(singularTags[i]);
            }
        }
        return jsonTagsArray;
    }
    
    public static String getTagsAsString(JSONObject article){
        
        Object obj  = JSONValue.parse(article.get("tags").toString());
        JSONArray tagsArray = (JSONArray)obj;
        
        if(DEBUG_ON){
            System.out.println("Tags:" + article.get("tags").toString());
        }
        
        String tagsString = "";
        for(int i = 0; i < tagsArray.size(); i++){
            tagsString = tagsString.concat(" ").concat(tagsArray.get(i).toString());
        }
        return tagsString;
        
        
    }
    
    public static JSONObject getArticleById(HttpServletRequest request,String id){
        JSONArray  articlesArray = getArticlesArrayFromFile(request);
        JSONObject article = new JSONObject();
        JSONObject selectedArticle = new JSONObject();
        for(int i = 0; i < articlesArray.size(); i++){
          selectedArticle = (JSONObject)articlesArray.get(i);
          if(id.equals(selectedArticle.get("id").toString())){
              article = selectedArticle;
          }
        }
        
        return article;
    }
    
    public static void updateArticleById(HttpServletRequest request, String id, LinkedHashMap fields){
        
        JSONArray  articlesArray = getArticlesArrayFromFile(request);
        JSONArray  updatedArticlesArray = new JSONArray();
        System.out.println(articlesArray);
        JSONObject articles = new JSONObject();
        JSONObject selectedArticle;
        for(int i = 0; i < articlesArray.size(); i++){
          selectedArticle = (JSONObject)articlesArray.get(i);
          if(id.equals(selectedArticle.get("id").toString())){
              //Get a set of all the entries Key - Value pairs contained in the LinkedHashMap 
              Set<Entry<String, Object>> entrySet = fields.entrySet();
              System.out.println("Entry Set: " + entrySet);
              
              for(Entry<String, Object> entry:entrySet){
                  String key = entry.getKey();
                  Object value = entry.getValue();
                  
                  if("tags".equals(key)){
                      JSONArray tags = createJSONTagsArray(value.toString());
                      selectedArticle.put(key, tags);
                  }else{
                      selectedArticle.put(key, value);
                  }
                  
              }
          }
          updatedArticlesArray.add(selectedArticle);
        }
        
        jsonPath = getArticlesJSONPath(request);
        
        articles.put("articles", updatedArticlesArray);
        writeJsonToFile(articles, jsonPath);
    }
    
    public static void deleteArticleById(HttpServletRequest request, String id){
        JSONObject articles = new JSONObject();
        JSONArray articlesArray = getArticlesArrayFromFile(request);
        JSONArray updatedArticleArray = new JSONArray();
        JSONObject selectedArticle = new JSONObject();
        for(int i = 0; i < articlesArray.size(); i++){
          selectedArticle = (JSONObject)articlesArray.get(i);
          if(! id.equals(selectedArticle.get("id").toString())){
              updatedArticleArray.add(selectedArticle);
          }
        }
        articles.put("articles",updatedArticleArray );
        String jsonPath = getArticlesJSONPath(request);
        writeJsonToFile(articles, jsonPath);
    }
    
    public static JSONArray getArticlesArrayFromFile(HttpServletRequest request){
        JSONArray articles  = new JSONArray();
        HttpSession session = request.getSession(true);
        String charityName  = (String)session.getAttribute(CHARITY_NAME_FROM_SESSION);
        if(charityName != null && !"".equals(charityName)){
            String jsonPath = getArticlesJSONPath(request);
            JSONObject articlesObj = readJsonFile(jsonPath);
            
            if(articlesObj == null){
                articles = new JSONArray();
            }else{
                articles = (JSONArray)readJsonFile(jsonPath).get("articles");
            }
        }
        return articles;
    }
    
    public static String getArticlesJSONPath(HttpServletRequest request){
        String jsonPath = "";
        HttpSession session = request.getSession(true);
        String charityName = (String)session.getAttribute(CHARITY_NAME_FROM_SESSION);
        if(charityName != null && !"".equals(charityName)){
            //Trim, set to lower case and remove white spaces
            trimmedCharityName = DirectoryManager.toLowerCaseAndTrim(charityName);
            /* Build the path for reading in the  file*/
            String servletContext = request.getServletContext().getRealPath("/");
            jsonPath = servletContext + CHARITIES_DIR + trimmedCharityName + JSON_DIR + ARTICLES_FILE_NAME ;
        }
        return jsonPath;
    }
    
    public static void approvePost(HttpServletRequest request, String id){
          JSONArray articlesArray = getArticlesArrayFromFile(request);
          JSONObject articles = new JSONObject();
          JSONArray  updatedArticles = new JSONArray();
          JSONObject selectedArticle = new JSONObject();
          String     articlesPath = getArticlesJSONPath(request);
          
          for(int i = 0; i < articlesArray.size(); i++){
            selectedArticle = (JSONObject)articlesArray.get(i);
            if(id.equals(selectedArticle.get("id").toString())){
                
                selectedArticle.put("approved", true);
            }
            updatedArticles.add(selectedArticle);
          }
          articles.put("articles", updatedArticles);
          writeJsonToFile(articles, articlesPath);
     }
    
    public static JSONArray getUnapprovedPosts(HttpServletRequest request){
        JSONArray unapprovedPosts = new JSONArray();
        JSONArray articlesArray = getArticlesArrayFromFile(request);

        //Create a new array to house all unapproved posts
        unapprovedPosts = new JSONArray();

        //A temp JSONObject to house the current article being checked for it's approved status
        JSONObject tmpArticle;

        //Loops through all articles, checks each approved status, adding to the unapprovedPost JSONArray if not approved
        for(int i = 0; i < articlesArray.size(); i++){
            tmpArticle = (JSONObject)articlesArray.get(i);
            System.out.println("tmpArticle: " + tmpArticle);
            if(! (Boolean)tmpArticle.get("approved")){
                unapprovedPosts.add(tmpArticle);
            }
        }
        
        return unapprovedPosts;
     }
    
    public static LinkedHashMap<String, String> getDefaultValueMap(HttpServletRequest request){
        /* Default values to be displayed when fields are not present */
        String id          = "-1";
        String title       = "No Title";
        String description = "No Description";
        String content     = "No Content";
        String img         = Charity.parseJSONtoCharityObj(request).getLogo();
        String type        = "general"; 
        String date        = "No Date";
        String tags        = "";
        
        LinkedHashMap<String, String> fieldsMap = new LinkedHashMap<>();
        fieldsMap.put("id", id);
        fieldsMap.put("title", title);
        fieldsMap.put("description", description); 
        fieldsMap.put("content", content); 
        fieldsMap.put("img", img); 
        fieldsMap.put("type", type); 
        fieldsMap.put("date", date); 
        fieldsMap.put("tags", tags); 
        
        return fieldsMap;
         
     }

    /**
     * @return the authorised
     */
    public boolean isAuthorised() {
        return authorised;
    }

    /**
     * @param authorised the authorised to set
     */
    public void setAuthorised(boolean authorised) {
        this.authorised = authorised;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
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
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the img
     */
    public String getImg() {
        return img;
    }

    /**
     * @param img the img to set
     */
    public void setImg(String img) {
        this.img = img;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the tags
     */
    public JSONArray getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(JSONArray tags) {
        this.tags = tags;
    }

    /**
     * @return the approved
     */
    public boolean isApproved() {
        return approved;
    }

    /**
     * @param approved the approved to set
     */
    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    /**
     * @return the comments
     */
    public JSONArray getComments() {
        return comments;
    }

    /**
     * @param comments the comments to set
     */
    public void setComments(JSONArray comments) {
        this.comments = comments;
    }
}
