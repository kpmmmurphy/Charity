
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




