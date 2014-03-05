/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package json;

import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kpmmmurphy
 */
public class ArticleTest {
    
    public ArticleTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class Article.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        Article.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of writeOutArticle method, of class Article.
     */
    @Test
    public void testWriteOutArticle() {
        System.out.println("writeOutArticle");
        HttpServletRequest request = null;
        Article instance = null;
        instance.writeOutArticle(request);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTagsAsString method, of class Article.
     */
    @Test
    public void testGetTagsAsString() {
        System.out.println("getTagsAsString");
        JSONObject article = null;
        String expResult = "";
        String result = Article.getTagsAsString(article);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getArticleById method, of class Article.
     */
    @Test
    public void testGetArticleById() {
        System.out.println("getArticleById");
        HttpServletRequest request = null;
        String id = "";
        JSONObject expResult = null;
        JSONObject result = Article.getArticleById(request, id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateArticleById method, of class Article.
     */
    @Test
    public void testUpdateArticleById() {
        System.out.println("updateArticleById");
        HttpServletRequest request = null;
        String id = "";
        LinkedHashMap fields = null;
        Article.updateArticleById(request, id, fields);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteArticleById method, of class Article.
     */
    @Test
    public void testDeleteArticleById() {
        System.out.println("deleteArticleById");
        HttpServletRequest request = null;
        String id = "";
        Article instance = null;
        instance.deleteArticleById(request, id);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getArticlesArrayFromFile method, of class Article.
     */
    @Test
    public void testGetArticlesArrayFromFile() {
        System.out.println("getArticlesArrayFromFile");
        HttpServletRequest request = null;
        JSONArray expResult = null;
        JSONArray result = Article.getArticlesArrayFromFile(request);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getArticlesJSONPath method, of class Article.
     */
    @Test
    public void testGetArticlesJSONPath() {
        System.out.println("getArticlesJSONPath");
        HttpServletRequest request = null;
        String expResult = "";
        String result = Article.getArticlesJSONPath(request);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of approvePost method, of class Article.
     */
    @Test
    public void testApprovePost() {
        System.out.println("approvePost");
        HttpServletRequest request = null;
        String id = "";
        Article.approvePost(request, id);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUnapprovedPosts method, of class Article.
     */
    @Test
    public void testGetUnapprovedPosts() {
        System.out.println("getUnapprovedPosts");
        HttpServletRequest request = null;
        JSONArray expResult = null;
        JSONArray result = Article.getUnapprovedPosts(request);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
