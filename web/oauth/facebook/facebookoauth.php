<?php
/*
 * 2014 CS3305 Team9
 * @author Kevin Murphy
 * @version 1.0
 * @date 8/3/14
 * 
 * Copyright 2014 CS3305 Team9
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

/**
 * Code and library "facebook.php" taken from the official Facebook PHP SDK git account https://github.com/facebook/facebook-ios-sdk
 * Modified to allow the updating of the charity's status with the title and link of their newest post.  The Facebook SDK is licenced under 
 * the Apache Licence.
 */

require_once("facebook.php");

  $debug_on = false;

  $config = array(
      'appId' => '600389216721868',
      'secret' => 'fa6b066271bc9fddbf2e7a419bbde4fa',
      'fileUpload' => false, // optional
      'allowSignedRequest' => false, // optional, but should be set to false for non-canvas apps
  );

  $facebook = new Facebook($config);
  $user_id  = $facebook->getUser();

  $article_title = $_GET['title'];
  $article_url   = "http://127.0.0.1/cs3305/DisplayArticle?article_id=" . $_GET['article_id'] . "&charity_name=" . $_GET['charity_name']; 
?>

<html>
  <head></head>
  <body>

  <?php

    if($user_id) {
      // We have a user ID, so probably a logged in user.
      // If not, we'll get an exception, which we handle below.
      try {
        $ret_obj = $facebook->api('/me/feed', 'POST',
                                    array(
                                      'link' => $article_url,
                                      'message' => $article_title
                                 ));
        echo '<p>Sucessfully Posted your Latest Article!</p><br />';
        $facebook->destroySession();

        // Give the user a logout link 
        echo '<br /><p><a href="' . $facebook->getLogoutUrl() . '">logout</a><p>';
        echo '<p><a onclick="window.close()">Close Window</a></p>';
      } catch(FacebookApiException $e) {
        // If the user is logged out, you can have a 
        // user ID even though the access token is invalid.
        // In this case, we'll get an exception, so we'll
        // just ask the user to login again here.
        
        $login_url = $facebook->getLoginUrl( array(
                       'scope' => 'publish_stream'
                       )); 
        echo 'Please <a href="' . $login_url . '">login..</a>';
        echo '<p><a onclick="window.close()">Close Window</a></p>';

        if(debug_on){
        	echo $e->getType();
        	echo $e->getMessage();
        }
      }   
    } else {

      // No user, so print a link for the user to login
      // To post to a user's wall, we need publish_stream permission
      // We'll use the current URL as the redirect_uri, so we don't
      // need to specify it here.
      $login_url = $facebook->getLoginUrl( array( 'scope' => 'publish_stream' ) );
      echo 'Please <a href="' . $login_url . '">login!</a>';
      echo '<p><a onclick="window.close()">Close Window</a></p>';

    } 

  ?>      

  </body> 
</html>  