
package json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Kevin Murphy
 */
public abstract class CustomJSONObject extends JSONObject{
    
    /*
     * Takes a LinkedHashMap representing a JSON file(Key, Value) and
     * performs the static writeJSONString which handles all formatting,
     * and writes it straight to the specified file
     * 
     * @param jsonMap  A LinkedHashMap to be written to file
     * @param path     The path to write the JSON file to
     * 
     * @exception UnsupportedEncodingException if a File's encoding is not supported.
     * @exception IOException if stream to a File cannot be written to or closed.
     */
     protected static void writeJsonToFile(Map jsonMap, String path){
        try( Writer writer = new BufferedWriter( 
                             new OutputStreamWriter(
                             new FileOutputStream(path), "UTF-8"))){
            JSONObject.writeJSONString(jsonMap, writer );
        } catch (UnsupportedEncodingException ex) {  
             Logger.getLogger(CustomJSONObject.class.getName()).log(Level.SEVERE, null, ex);
         } catch (IOException ex) {
             Logger.getLogger(CustomJSONObject.class.getName()).log(Level.SEVERE, null, ex);
         }  
    }
     
     /*
     * Reads a .json file from a directory and parses it, creating and 
     * returning a JSONObject
     * 
     * @param jsonPath     The path to read the json file from
     * 
     * @exception IOException if stream to a File cannot be written to or closed.
     * @exception ParseException if a File cannot be read from
     * 
     * @return JSONObject 
     */
    protected static JSONObject readJsonFile(String jsonPath){
        BufferedReader reader;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        String jsonString;
        Object jsonObj = null;
       
        JSONParser parser = new JSONParser();
        
        try{
            reader = new BufferedReader(new FileReader(jsonPath));
            while((line = reader.readLine()) != null){
                stringBuilder.append(line);
            }
            jsonString = stringBuilder.toString();
            jsonObj = parser.parse(jsonString);
            
        }catch(IOException e){
            System.out.println("IOException with JSON File");
        }catch(ParseException e){
            System.out.println("ParseException with JSON File");
        }
        return (JSONObject)jsonObj;  
    }
}
