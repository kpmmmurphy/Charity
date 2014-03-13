<?php
/*
 * 2014 CS3305 Team9
 * @author Kevin Murphy
 * @version 1.0
 * @date 8/3/14
 * 
 * Taken largly from https://github.com/abraham/twitteroauth
 * Using libraries OAuth.php anf twitteroauth.php by Abraham Williams - http://abrah.am - abraham@abrah.am
 * 
 * 
 * Modified to allow the updating of the charity's status with the title and link of their newest post. 
 * 
 * @file
 * Take the user when they return from Twitter. Get access tokens.
 * Verify credentials and redirect to based on response from Twitter.
 */

/* Start session and load lib */
session_start();
require_once('twitteroauth.php');
define('CONSUMER_KEY', 'LzgDGH7PtPqSFYNHjyV5zQ');
define('CONSUMER_SECRET', 'avlWZ5xTbxG6sLixu3bzVMzDRoiI5MjmtKvLEmPUHE');


/* Create TwitteroAuth object with app key/secret and token key/secret from default phase */
$connection = new TwitterOAuth(CONSUMER_KEY, CONSUMER_SECRET, $_SESSION['oauth_token'], $_SESSION['oauth_token_secret']);

/* Request access tokens from twitter */
$access_token = $connection->getAccessToken($_REQUEST['oauth_verifier']);

/* Save the access tokens. Normally these would be saved in a database for future use. */
$_SESSION['access_token'] = $access_token;



$oauth_token        = $_SESSION['oauth_token'];
$oauth_token_secret = $_SESSION['oauth_token_secret'];

$article_title  = $_SESSION['article_title'];
$article_url    = $_SESSION['article_url'];

/* Remove no longer needed request tokens */
unset($_SESSION['oauth_token']);
unset($_SESSION['oauth_token_secret']);


/* If HTTP response is 200 continue otherwise send to connect page to retry */
if (200 == $connection->http_code) {
  /* The user has been verified and the access tokens can be saved for future use */
  $_SESSION['status'] = 'verified';
  $newStatus = $article_title . $article_url;
  $status = $connection->post('statuses/update', array('status' => $newStatus));
  echo '<p>Sucessfully Posted your Latest Article!</p><br />';
  echo '<p><a onclick="window.close()">Close Window</a></p>';
  die();
} else {
  echo "Unable to Connect Error: " . $connection->http_code;
  die();
}

?>