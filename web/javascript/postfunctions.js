/**
 * Houses functions for the Dashboard Post/Article management. 
 * Also provides functions for taking the charity admin to our OAuth 
 * scripts on cs1.ucc.ie in a new pop-out window.
 * 
 * AJAX caching is turned off in each individual function.
 * 
 * @author Kevin Murphy
 * @version 1.1
 * @date 23/2/14
 */

/**
 * Make an AJAX GET request to the CreatePost.java servlet and renders it to the browser.
 * 
 * @returns {undefined}
 */
function getCreatePost() {
    $.ajaxSetup({ cache: false });
    ajaxLoadingPrompt("#main", "Loading...");
    
    $.get("CreatePost", function(data) {
        var article = document.createElement("article");
        article.id = "submit_container";
        $(article).html(data);
        $("#main").html(" ");
        $("#main").html(article);
    });
    
}

/**
 * Make an AJAX GET request to the ApprovePost.java servlet and renders it to the browser.
 * 
 * @returns {undefined}
 */
function getApprovePost() {
    
    $.ajaxSetup({ cache: false });
    $.get("ApprovePost", function(data) {
        $("#main").html(" ");
        $("#main").html(data).fadeIn(1000);
    });
}

/**
 * Make an AJAX POST request to the ApprovePost.java servlet with the id of the post/article to be approved, and then renders a success or fail prompt to
 * notify the user. 
 * 
 * @param {type} id
 * @returns {undefined}
 */
function ajaxApprovePost(id){
    
    $.ajaxSetup({ cache: false });
    $("#main").html("<section class='ajax_loading'><img src='../../images/loading.gif'/><p>Posting Comment...</p></section>").fadeIn(1000);
    $.post("ApprovePost", {id:id}, function() {
        $("#main").html("<section class='ajax_success'><p>Post Approved!</p></section>").hide().fadeIn(1000).fadeOut(2000);
        setTimeout(function() {
            getApprovePost();
        }, 3000);
    });
}

/**
 * Make an AJAX GET request to the ListPost.java servlet and render the output to the browser
 * 
 * @returns {undefined}
 */
function getListPosts() {
    
    $.ajaxSetup({ cache: false });
    $.get("ListPosts", function(data) {
        $("#main").html(" ");
        $("#main").html(data).hide().fadeIn(500);
    });
}

/**
 *  Make an AJAX GET request to the EditPost.java servlet and render the output to the browser
 * 
 * @param {type} id
 * @returns {undefined}
 */
function getEditPost(id) {
    
    $.ajaxSetup({ cache: false });
    $.get("EditPost", {"id": id}, function(data) {
        $("#main").html(" ");
        $("#main").html(data).hide().fadeIn(1000);
    });
}

/**
 * Make an AJAX POST request to the EditPost.java servlet with the fields to be changed of the post/article, and then renders a success or fail prompt to
 * notify the user. 
 * 
 * @returns {undefined}
 */
function ajaxSubmitEditedPost(){
    
    $.ajaxSetup({ cache: false });
    $("form#edit_post").on("submit", function(data) {
        event.preventDefault();

        //grab all form data  
        var formData = new FormData($(this)[0]);
        $("#main").html(" ");
        ajaxLoadingPrompt("#main", "Updating Post");
        $.ajax({
            url: "EditPost",
            type: 'POST',
            data: formData,
            async: false,
            cache: false,
            contentType: false,
            processData: false,
            success: function (data) {
              $("#main").html(" ");
              ajaxSuccessPrompt("#main", "Post Updated Successfully!", 2000, 2000);
              
                setTimeout(function() {
                    getListPosts();
                }, 1000);
                
            }
          }).fail(function() {
            $("#main").html(" ");
            $("#main").html("<section class='ajax_fail'><p>Unable to Create Post, Please Try Again!</p></section>").hide().fadeIn(1000).fadeOut(1000);
            
             setTimeout(function() {
                getListPosts();
             }, 1000);
             
        });
        
        
    });
}

/**
 * Display a loading mesage to the user on the specified html section.
 * 
 * @param {type} loadingSection
 * @param {type} text
 * @returns {undefined}
 */
function ajaxLoadingPrompt(loadingSection, text){
    $(loadingSection).html("<section class='ajax_loading'><img src='./images/ajax-loader.gif'/><p>" + text + "</p></section>");
}

/**
 * Display a success mesage to the user on the specified html section.
 * 
 * @param {type} loadingSection
 * @param {type} text
 * @param {type} fadeInTime
 * @param {type} fadeOutTime
 * @returns {undefined}
 */
function ajaxSuccessPrompt(loadingSection, text, fadeInTime, fadeOutTime){
    $(loadingSection).html("<section class='ajax_success'><p>" + text + "<p></section>").fadeIn(fadeInTime).fadeOut(fadeOutTime);
}

/**
 * Make an AJAX POST request to the DeletePost.java servlet with the id to be changed of the post/article to be deleted, and then renders a success or fail prompt to
 * notify the user.
 * 
 * @param {type} id
 * @returns {undefined}
 */
function getDeletePost(id) {
    
    $.ajaxSetup({ cache: false });
    ajaxLoadingPrompt("#main", "Deleting Post..");
    
    $.post("DeletePost", {"id": id}, function(data) {
        ajaxSuccessPrompt("#main", "Post Deleted", 500, 1000);
    });
    
    setTimeout(function(){
        $("#main").slideToggle(1600);
        getListPosts();
    }, 1400);
    
    
}


/**
 * Creates a new pop-out window and requests the url of my Twitter OAuth Script which 
 * sits on cs1.ucc.ie. It passes all the nessasary parameters along with the request
 * in order to create the status/tweet
 * 
 * @param {type} article_title
 * @param {type} charity_name
 * @param {type} article_id
 * @returns {undefined}
 */
function createTwitterOAuthWindow(article_title, charity_name, article_id) {
    window.open("http://cs1.ucc.ie/~kpm2/TwitterOAuth/inittwitteroauth.php?title=" + article_title + "&charity_name=" + charity_name + "&article_id=" + article_id + "", "Post Article To Twitter", "width=400,height=400");

}

/**
 * Creates a new pop-out window and requests the url of my Facebook OAuth Script which 
 * sits on cs1.ucc.ie. It passes all the nessasary parameters along with the request
 * in order to create the status/tweet
 * 
 * @param {type} article_title
 * @param {type} charity_name
 * @param {type} article_id
 * @returns {undefined}
 */
function createFacebookOAuthWindow(article_title, charity_name, article_id) {
    window.open("http://cs1.ucc.ie/~kpm2/FacebookOAuth/facebookoauth.php?title=" + article_title + "&charity_name=" + charity_name + "&article_id=" + article_id + "", "Post Article To Facebook", "width=400,height=400");

}

/**
 * Handles AJAX submitting of new posts/articles.
 * Uses the FormData object to pass multi-part form data to the CreatePost.java servlet.
 * 
 * @param {type} homepage
 * @returns {undefined}
 */
function ajaxPostSubmit(homepage) {

    $.ajaxSetup({ cache: false });
    $("form#create_post").on("submit", function(data) {
        event.preventDefault();

        var url = "CreatePost";
        if(homepage){
            url = "../../CreatePost";
        }
        //grab all form data  
        var formData = new FormData($(this)[0]);
        $("#main").html(" ");
        $("#main").html("<section class='ajax_loading'><img src='images/loading.gif'/><p>Creating Posting...</p></section>");
        $.ajax({
            url: url,
            type: 'POST',
            data: formData,
            async: false,
            cache: false,
            contentType: false,
            processData: false,
            success: function (data) {
              $("#main").html(" ");
              $("#main").html("<section class='ajax_success'><p>Post Submitted Successfully!</p></section>").hide().fadeIn(2000).fadeOut(2000);

              if(homepage){
                  setTimeout(function() {
                    $("#main").html("<p id='to_be_approved'>Thank you. Your Post must be approved before it will be visible on the site, check back soon.<p>").fadeIn(1000);
                }, 4000);
              }else{
                  setTimeout(function() {
                      
                    if(data.length === 2 ){
                        $.get("ManagePosts", $(this).serialize(),function(data){
                            $("#main").html(data).hide().fadeIn(1000);
                        });
                    }else{
                        $("#main").html(" ");
                        $("#main").html(data).slideToggle("slow");
                    }
                }, 3000);
              }
                
                
            }
          }).fail(function() {
            $("#main").html(" ");
            $("#main").html("<section class='ajax_fail'><p>Unable to Create Post, Please Try Again!</p></section>").hide().fadeIn(3000).fadeOut(2000);
            
             setTimeout(function() {
                getCreatePost();
             }, 5000);
             
        });
        
        
    });
}






