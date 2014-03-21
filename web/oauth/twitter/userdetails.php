<?php
/**
 * @file
 * User has successfully authenticated with Twitter. Access tokens saved to session and DB.
 */

/* Load required lib files. */
session_start();
require_once('twitteroauth.php');
define('CONSUMER_KEY', 'UUd6kdNzbeEYHJMUXbPEw');
define('CONSUMER_SECRET', 'T0Xb7rfi2EgXomWu44BJeGLhBbrsysXVYynVpZWY');

/* Get user access tokens out of the session. */
$access_token = $_SESSION['access_token'];

/* Create a TwitterOauth object with consumer/user tokens. */
$connection = new TwitterOAuth(CONSUMER_KEY, CONSUMER_SECRET, $access_token['oauth_token'], $access_token['oauth_token_secret']);

/* If method is set change API call made. Test is called by default. */
$account = $connection->get('account/verify_credentials');

//var_dump($account);

echo "<p> Hello " . $account->name . "! Or should I call you " . $account->screen_name .  "? </p>";
echo "<p> Description : " . $account->description . " </p>";
echo "<p>Your last tweet was : " . $account->status->text . "</p>";


?>