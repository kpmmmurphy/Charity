
function getCreatePost() {
    $.get("CreatePost", function(data) {
        $("#main").html(" ");
        $("#main").html(data);
    });
}

function getApprovePost() {
    $.get("ApprovePost", function(data) {
        $("#main").html(" ");
        $("#main").html(data);
    });
}

function getListPosts() {
    $.get("ListPosts", function(data) {
        $("#main").html(" ");
        $("#main").html(data);
    });
}

function getEditPost(id) {
    $.get("EditPost",{"id": id},function(data) {
        $("#main").html(" ");
        $("#main").html(data);
    });
}

function getDeletePost(id) {
    $.post("DeletePost",{"id": id},function(data) {
        $("#main").html(" ");
        $("#main").html(data);
    });
}

function createTwitterOAuthWindow(article_title, charity_name, article_id){
    alert(1);
    window.open("http://cs1.ucc.ie/~kpm2/TwitterOAuth/inittwitteroauth.php?title=" + article_title + "&charity_name=" + charity_name + "&article_id=" + article_id + "","Post Article To Twitter", "width=400,height=400");
    
}

function createFacebookOAuthWindow(article_title, charity_name, article_id){
    alert(1);
    window.open("http://cs1.ucc.ie/~kpm2/FacebookOAuth/facebookoauth.php?title=" + article_title + "&charity_name=" + charity_name + "&article_id=" + article_id + "","Post Article To Facebook", "width=400,height=400");
    
}




