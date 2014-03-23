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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import utilities.DirectoryManager;

/**
 * Handles the creation of articles and the manipulation of the articles.json file. 
 * Provides functionality for reading in and writing to the articles.json file, deletion of
 * articles by id, updating articles by id, inputting user submitted comments and handling tags.
 * 
 * @author  Kevin Murphy
 * @version 1.2
 * @date    21/2/14
 */
public class Article extends CustomJSONObject {

    /* Debug mechinism for testing */
    private static final boolean DEBUG_ON = true;
    
    //Constants associated with articles.json directory locations
    public static final String CHARITY_NAME_FROM_SESSION = "charityName";
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
    private String title;
    /* ID of Article taken from the Database */
    private int id;
    /* Short description of article */
    private String description;
    /* Content of the Article */
    private String content;
    /* Date the article was created. Auto Generated */
    private String date;
    /* Image name associated with the article*/
    private String img;
    /* Type of post - General, Lost and Found, Sponsorship etc*/
    private String type;
    /* JSONArray for Articles tags */
    private JSONArray tags;
    /* Approved status, automatically approved if create by admin */
    private boolean approved = false;
    /* JSONArray for Articles Comments, initializes an empty array. Inputting of comments to be handled with javascript */
    private JSONArray comments;

    /* The path of the JSON file  */
    private static String jsonPath;

    /**
     * Constructor for an Article Object, gives default empty string values for null parameters.
     * 
     * initializes all attributes. If no image was upload, automatically associates the charity's logo image with the article. 
     * 
     * @param request   The HttpServletRequest used to identify the Charity, and write/read the file accordingly
     * @param articleMap A LinkedHashMap containing all the fields associated with an Article
     */
    public Article(HttpServletRequest request, LinkedHashMap articleMap) {
        super();
        initializeDetials(request);

        this.id          = generateArticleId(request);
        this.title       = (articleMap.get("title").toString()       != null) ? articleMap.get("title").toString()       : "";
        this.description = (articleMap.get("description").toString() != null) ? articleMap.get("description").toString() : "";
        this.content     = (articleMap.get("content").toString()     != null) ? articleMap.get("content").toString()     : "";
        this.img         = (!"".equals(articleMap.get("img").toString()))     ? articleMap.get("img").toString()         : Charity.parseJSONtoCharityObj(request).getLogo();
        this.type        = (articleMap.get("type").toString()        != null) ? articleMap.get("type").toString()        : "general";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
        this.date            = sdf.format(new Date());
        this.tags            = createJSONTagsArray(articleMap.get("tags").toString());
        this.comments        = new JSONArray();

        //Checks if the user is authorised from the Session, if so approves post 
        if (authorised) {
            approved = true;
        }
    }
    
     /**
     * Takes the HttpServletRequest and extracts the session attributes from it, instantiating all
     * the class attributes required to perform the creating and manipulation of Article objects
     * @param request 
     */
    private void initializeDetials(HttpServletRequest request) {
        //Get the Session 
        session = request.getSession(true);
        //Get the charity name, must initilize when user is viewing each charity 
        charityName = (String) session.getAttribute("charityName");
        System.out.println(charityName);
        //Only admins will be authorised, see Signup or Login session instantiation
        if (session.getAttribute("authorised") != null) {
            setAuthorised(true);
        }
        //The trimmed and lower case charity name, with spaces removed
        trimmedCharityName = DirectoryManager.toLowerCaseAndTrim(charityName);

        /* Gets the path of the articles.json file */
        jsonPath = getArticlesJSONPath(request);
    }

    /**
     * Handles the writing-out to file of an Article. 
     * If no json file exists, it creates one. If one does, it
     * reads it in and appends the new article on to the end
     * of the JSON array housing the already created articles. 
     * 
     * @param request  The HttpServletRequest used to identify the Charity, and write/read the file accordingly
     */
    public void writeOutArticle(HttpServletRequest request) {

        JSONObject articlesObj = new JSONObject();
        JSONArray articlesArray = new JSONArray();

        JSONObject articleMap = createArticleJSONObj();
        String jsonPath = getArticlesJSONPath(request);

        /* Checks if JSON file exists, if not creates it */
        File jsonArticleFile = new File(jsonPath);
        if (!jsonArticleFile.exists() && !jsonArticleFile.isDirectory()) {
            articlesArray.add(articleMap);
        } else {
            articlesArray = getArticlesArrayFromFile(request);
            articlesArray.add(articleMap);

        }
        articlesObj.put("articles", articlesArray);

        writeJsonToFile(articlesObj, jsonPath);
    }

    /**
     * Takes all the fields of an article and formats the Article in a JSONObject and returns it 
     *
     * @return JSONObject containing comment attributes
     */
    private JSONObject createArticleJSONObj() {

        JSONObject articleMap = new JSONObject();

        /* Formatting of JSON Article Keys and Values*/
        articleMap.put("id", this.getId());
        articleMap.put("title", this.getTitle());
        articleMap.put("type", this.getType());
        articleMap.put("description", this.getDescription());
        articleMap.put("content", this.getContent());
        articleMap.put("date", this.getDate());
        articleMap.put("img", this.getImg());
        articleMap.put("tags", this.getTags());
        articleMap.put("comments", this.getComments());
        articleMap.put("approved", this.isApproved());

        if (DEBUG_ON) {
            String articleJSONString = JSONValue.toJSONString(articleMap);
            System.out.println("Newest Article : " + articleMap);
        }

        return articleMap;
    }

    /**
     * Generates an ID for an Article by creating a new entry in the Database table Articles, and then reads the 
     * newly create row's id value, which is automatically maintained by the auto_increment functionality provided
     * by our mySQL database
     * 
     * @param request The HttpServletRequest used to identify the Charity, and write/read the file accordingly
     * @return id  
     */
    private int generateArticleId(HttpServletRequest request) {
        int generatedID = -1;
        //Get the Session 
        session = request.getSession(true);
        //Get the charity name, must initilize when user is viewing each charity 
        int charityID = 0;
        if (session.getAttribute("authorised") == null) {

            String charityName = (String) session.getAttribute("charityName");
            String selectCharityID = "SELECT id "
                    + "FROM charities "
                    + "WHERE name = ?";

            //Connect to Database
            DBConnect dbConnect = new DBConnect();
            Connection connection = dbConnect.getConnection();

            try (PreparedStatement selectCharityIDStatement = connection.prepareStatement(selectCharityID)) {

                selectCharityIDStatement.setString(1, charityName);
                ResultSet selectIDResultSet = selectCharityIDStatement.executeQuery();
                if (selectIDResultSet.first()) {
                    charityID = selectIDResultSet.getInt(1);
                }
            } catch (SQLException ex) {
                System.err.println(this.getClass().getName() + ": Article ID cannot be generated, Charity Name from Session does not match one in DB ");
            }

        } else {
            charityID = Integer.valueOf((String) session.getAttribute("charity_id"));
        }

        if (charityID > 0) {
            //Connect to Database
            DBConnect dbConnect = new DBConnect();
            Connection connection = dbConnect.getConnection();

            String insertNewArticle = "INSERT INTO articles (charity_id)"
                    + "VALUES(?)";
            try (PreparedStatement insertNewArticleStatement = connection.prepareStatement(insertNewArticle)) {

                insertNewArticleStatement.setInt(1, charityID);
                insertNewArticleStatement.executeUpdate();
            } catch (SQLException ex) {
                System.err.println(this.getClass().getName() + ": Article ID cannot be generated, Charity ID from Session does not match one in DB ");
            }

            String selectLatestArticleID = "SELECT id "
                    + "From articles "
                    + "WHERE charity_id = ? "
                    + "ORDER BY date_and_time DESC";

            try (PreparedStatement selectNewArticleIDStatement = connection.prepareStatement(selectLatestArticleID);) {
                selectNewArticleIDStatement.setInt(1, charityID);
                ResultSet idResultSet = selectNewArticleIDStatement.executeQuery();

                if (idResultSet.next()) {
                    setId(idResultSet.getInt(1));

                }
                if (DEBUG_ON) {
                    System.out.println("Generated Article ID: " + getId());
                }
            } catch (SQLException ex) {
                System.err.println(this.getClass().getName() + ": Article ID cannot be retrieved, Charity ID from Session does not match one in DB ");
                ex.printStackTrace();
            }
        }

        return getId();

    }
    
    /**
     *  Takes a String of tags separated by a space character and uses a RegExp to extract
     *  each and put them into a JSONarray
     * 
     * @param submittedTags
     * @return 
     */
    private static JSONArray createJSONTagsArray(String submittedTags) {

        JSONArray jsonTagsArray = new JSONArray();
        if (submittedTags != null || !"".equals(submittedTags)) {
            String[] singularTags = submittedTags.split("\\s+");
            for (int i = 0; i < singularTags.length; i++) {
                jsonTagsArray.add(singularTags[i]);
            }
        }
        return jsonTagsArray;
    }

    /**
     * Takes a JSONObject which has a JSONArray called tags and extracts each value and
     * create a String containing all the values.
     * 
     * @param article
     * @return String of tags
     */
    public static String getTagsAsString(JSONObject article) {

        Object obj = JSONValue.parse(article.get("tags").toString());
        JSONArray tagsArray = (JSONArray) obj;

        if (DEBUG_ON) {
            System.out.println("Tags:" + article.get("tags").toString());
        }

        String tagsString = "";
        for (int i = 0; i < tagsArray.size(); i++) {
            tagsString = tagsString.concat(" ").concat(tagsArray.get(i).toString());
        }
        return tagsString;

    }

    /**
     * Retrieves an Article from the charity specific articles.json using it's unique id
     * 
     * @param request
     * @param id
     * @return JSONObject
     */
    public static JSONObject getArticleById(HttpServletRequest request, String id) {
        JSONArray articlesArray = getArticlesArrayFromFile(request);
        JSONObject article = new JSONObject();
        JSONObject selectedArticle = new JSONObject();

        for (int i = 0; i < articlesArray.size(); i++) {
            selectedArticle = (JSONObject) articlesArray.get(i);
            if (id.equals(selectedArticle.get("id").toString())) {
                article = selectedArticle;
            }
        }

        return article;
    }

    /**
     * Updates all values of an article identified by it's unique id with the given Map of fields
     * 
     * @param request The HttpServletrequest
     * @param id
     * @param fields A Map of fields for the Article
     */
    public static void updateArticleById(HttpServletRequest request, String id, Map fields) {

        JSONArray articlesArray = getArticlesArrayFromFile(request);
        JSONArray updatedArticlesArray = new JSONArray();
        JSONObject articles = new JSONObject();
        JSONObject selectedArticle;
        for (int i = 0; i < articlesArray.size(); i++) {
            selectedArticle = (JSONObject) articlesArray.get(i);
            if (id.equals(selectedArticle.get("id").toString())) {
                //Get a set of all the entries Key - Value pairs contained in the Map 
                Set<Entry<String, Object>> entrySet = fields.entrySet();

                for (Entry<String, Object> entry : entrySet) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    
                    if ("tags".equals(key)) {
                        JSONArray tags = new JSONArray();
                        if (value instanceof String) {
                            tags = createJSONTagsArray(value.toString());
                        } else {
                            tags = (JSONArray) value;
                            String tagsString = "";
                            for (int j = 0; j < tags.size(); j++) {
                                System.out.println(tags.size());
                                tagsString += " " + tags.get(j);
                            }
                            System.out.println(tagsString);
                            tags = createJSONTagsArray(tagsString);
                        }

                        selectedArticle.put(key, tags);
                    } else {
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

    /**
     * Deletes an article using it's unique id by looping through each article object
     * present in the charity specific articles.json file and and creating a new JSONObject 
     * but excluding the article to be delete, then writes out this newly created JSONObject to file.
     * 
     * @param request The HttpServletrequest
     * @param id 
     */
    public static void deleteArticleById(HttpServletRequest request, String id) {
        JSONObject articles = new JSONObject();
        JSONArray articlesArray = getArticlesArrayFromFile(request);
        JSONArray updatedArticleArray = new JSONArray();
        JSONObject selectedArticle = new JSONObject();
        for (int i = 0; i < articlesArray.size(); i++) {
            selectedArticle = (JSONObject) articlesArray.get(i);
            if (!id.equals(selectedArticle.get("id").toString())) {
                updatedArticleArray.add(selectedArticle);
            }
        }
        articles.put("articles", updatedArticleArray);
        String jsonPath = getArticlesJSONPath(request);
        writeJsonToFile(articles, jsonPath);
    }
    
    /**
     * Retrieves and returns the JSONArray of Articles from the chairy specific articles.json file
     * 
     * @param request The HttpServletrequest
     * @return JSONArray
     */
    public static JSONArray getArticlesArrayFromFile(HttpServletRequest request) {
        JSONArray articles = new JSONArray();
        HttpSession session = request.getSession(true);
        String charityName = (String) session.getAttribute(CHARITY_NAME_FROM_SESSION);
        if (charityName != null && !"".equals(charityName)) {
            String jsonPath = getArticlesJSONPath(request);
            JSONObject articlesObj = readJsonFile(jsonPath);

            if (articlesObj == null) {
                articles = new JSONArray();
            } else {
                articles = (JSONArray) readJsonFile(jsonPath).get("articles");
            }
        }
        return articles;
    }

    /**
     * Returns the charity specific path as a string of the articles.json file
     * 
     * @param request The HttpServletrequest
     * @return The String path of the articles.json file
     */
    public static String getArticlesJSONPath(HttpServletRequest request) {
        String jsonPath = "";
        HttpSession session = request.getSession(true);
        String charityName = (String) session.getAttribute(CHARITY_NAME_FROM_SESSION);
        if (charityName != null && !"".equals(charityName)) {
            //Trim, set to lower case and remove white spaces
            trimmedCharityName = DirectoryManager.toLowerCaseAndTrim(charityName);
            /* Build the path for reading in the  file*/
            String servletContext = request.getServletContext().getRealPath("/");
            jsonPath = servletContext + CHARITIES_DIR + trimmedCharityName + JSON_DIR + ARTICLES_FILE_NAME;
        }
        return jsonPath;
    }

    /**
     * Approves an article by id, so that it will be shown on the charity page.
     * 
     * @param request The HttpServletrequest
     * @param id id of article to be approved
     */
    public static void approvePost(HttpServletRequest request, String id) {
        JSONArray articlesArray = getArticlesArrayFromFile(request);
        JSONObject articles = new JSONObject();
        JSONArray updatedArticles = new JSONArray();
        JSONObject selectedArticle = new JSONObject();
        String articlesPath = getArticlesJSONPath(request);

        for (int i = 0; i < articlesArray.size(); i++) {
            selectedArticle = (JSONObject) articlesArray.get(i);
            if (id.equals(selectedArticle.get("id").toString())) {

                selectedArticle.put("approved", true);
            }
            updatedArticles.add(selectedArticle);
        }
        articles.put("articles", updatedArticles);
        writeJsonToFile(articles, articlesPath);
    }

    /**
     * Retrieves all unapproved posts and returns them as a JSONArray
     * @param request The HttpServletrequest
     * @return JSONArray of unapproved posts
     */
    public static JSONArray getUnapprovedPosts(HttpServletRequest request) {
        JSONArray unapprovedPosts = new JSONArray();
        JSONArray articlesArray = getArticlesArrayFromFile(request);

        //Create a new array to house all unapproved posts
        unapprovedPosts = new JSONArray();

        //A temp JSONObject to house the current article being checked for it's approved status
        JSONObject tmpArticle;

        //Loops through all articles, checks each approved status, adding to the unapprovedPost JSONArray if not approved
        for (int i = 0; i < articlesArray.size(); i++) {
            tmpArticle = (JSONObject) articlesArray.get(i);
            if (!(Boolean) tmpArticle.get("approved")) {
                unapprovedPosts.add(tmpArticle);
            }
        }

        return unapprovedPosts;
    }

    /**
     * Takes the HttpServletRequest and extracts the parameters 'charity_name', 'post_id', 'commenter_name' and 'comment_textbox' 
     * and inputs them into the articles.json file in the comment object of the articles object where the article id equals the post_id
     * 
     * @param request The HttpServletrequest 
     */
    public static void inputComment(HttpServletRequest request) {
        String charityName = (request.getParameter("charity_name") == null) ? "" : request.getParameter("charity_name");
        String postID = (request.getParameter("post_id") == null) ? "" : request.getParameter("post_id");
        String commenterName = (request.getParameter("commenter_name") == null || "".equals(request.getParameter("commenter_name"))) ? "Anon" : request.getParameter("commenter_name");
        String comment = (request.getParameter("comment_textbox") == null || "".equals(request.getParameter("comment_textbox"))) ? "No Comment." : request.getParameter("comment_textbox");

        if (!"".equals(charityName) || !"".equals(postID)) {

            request.getSession(true).setAttribute("charityName", charityName);
            JSONObject article = getArticleById(request, postID);

            System.out.println(article);
            JSONObject latestComment = new JSONObject();
            latestComment.put("name", commenterName);
            latestComment.put("comment", comment);

            JSONArray commentsArray = (JSONArray) article.get("comments");

            System.out.println(commentsArray);
            commentsArray.add(latestComment);
            article.put("comments", commentsArray);

            updateArticleById(request, postID, article);
        }

    }

    /**
     * Returns a LinkedHashMap containing all the fields of an article instantiated with a default value
     * 
     * @param request LinkedHashMap
     * @return LinkedHashMap<String, String> with default values for each article field
     */
    public static LinkedHashMap<String, String> getDefaultValueMap(HttpServletRequest request) {
        /* Default values to be displayed when fields are not present */
        String id = "-1";
        String title = "No Title";
        String description = "No Description";
        String content = "No Content";
        String img = Charity.parseJSONtoCharityObj(request).getLogo();
        String type = "general";
        String date = "No Date";
        String tags = "";

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
