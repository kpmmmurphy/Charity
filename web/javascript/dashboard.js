function getAnalytics(){
    $.post("Analytics",function(data) {
        $("#main").html(" ");
        $("#main").html(data);
    });
}
