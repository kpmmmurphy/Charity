<?php

/*
 * 2014 CS3305 Team9
 * @author Kevin Murphy
 * @version 1.0
 * @date 8/3/14
 * 
 * 
 * Taken largly from https://github.com/abraham/twitteroauth
 * Using libraries OAuth.php anf twitteroauth.php by Abraham Williams - http://abrah.am - abraham@abrah.am
 *
 * Modified to allow the updating of the charity's status with the title and link of their newest post. 
 *
 */

/* Start session and load library. */
session_start();
require_once('twitteroauth.php');


define('CONSUMER_KEY', 'LzgDGH7PtPqSFYNHjyV5zQ');
define('CONSUMER_SECRET', 'avlWZ5xTbxG6sLixu3bzVMzDRoiI5MjmtKvLEmPUHE');

define('OAUTH_CALLBACK', 'http://cs1.ucc.ie/~kpm2/TwitterOAuth/callback.php');


/* Build TwitterOAuth object with client credentials. */
$connection = new TwitterOAuth(CONSUMER_KEY, CONSUMER_SECRET);
 
/* Get temporary credentials. */
$request_token = $connection->getRequestToken(OAUTH_CALLBACK);

/* Save temporary credentials to session. */
$_SESSION['oauth_token'] = $token = $request_token['oauth_token'];
$_SESSION['oauth_token_secret'] = $request_token['oauth_token_secret'];

$_SESSION['article_title'] = $_GET['title'] . "       Click the Link below to Read More! ";
$_SESSION['article_url'] = "http://127.0.0.1/cs3305/DisplayArticle?article_id=" . $_GET['article_id'] . "&charity_name=" . $_GET['charity_name'];
 
/* If last connection failed don't display authorization link. */
switch ($connection->http_code) {
  case 200:
    /* Build authorize URL and redirect user to Twitter. */
    $url = $connection->getAuthorizeURL($token);
    header('Location: ' . $url); 
    break;
  default:
    /* Show notification if something went wrong. */
    echo 'Could not connect to Twitter. Refresh the page or try again later.';
}

?>