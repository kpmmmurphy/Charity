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

function getApprovePost() {
    
    $.ajaxSetup({ cache: false });
    $.get("ApprovePost", function(data) {
        $("#main").html(" ");
        $("#main").html(data).fadeIn(1000);
    });
}

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

function getListPosts() {
    
    $.ajaxSetup({ cache: false });
    $.get("ListPosts", function(data) {
        $("#main").html(" ");
        $("#main").html(data).hide().fadeIn(500);
    });
}

function getEditPost(id) {
    
    $.ajaxSetup({ cache: false });
    $.get("EditPost", {"id": id}, function(data) {
        $("#main").html(" ");
        $("#main").html(data).hide().fadeIn(1000);
    });
}

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

function ajaxLoadingPrompt(loadingSection, text){
    $(loadingSection).html("<section class='ajax_loading'><img src='./images/ajax-loader.gif'/><p>" + text + "</p></section>");
}

function ajaxSuccessPrompt(loadingSection, text, fadeInTime, fadeOutTime){
    $(loadingSection).html("<section class='ajax_success'><p>" + text + "<p></section>").fadeIn(fadeInTime).fadeOut(fadeOutTime);
}

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

function createTwitterOAuthWindow(article_title, charity_name, article_id) {
    window.open("http://cs1.ucc.ie/~kpm2/TwitterOAuth/inittwitteroauth.php?title=" + article_title + "&charity_name=" + charity_name + "&article_id=" + article_id + "", "Post Article To Twitter", "width=400,height=400");

}

function createFacebookOAuthWindow(article_title, charity_name, article_id) {
    window.open("http://cs1.ucc.ie/~kpm2/FacebookOAuth/facebookoauth.php?title=" + article_title + "&charity_name=" + charity_name + "&article_id=" + article_id + "", "Post Article To Facebook", "width=400,height=400");

}

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






