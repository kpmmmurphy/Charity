
function getCreatePost() {
    $.get("CreatePost", function(data) {
        $("#main").html(" ");
        $("#main").html(data);
    });
}


