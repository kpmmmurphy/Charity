


function getAnalytics(){
    
    $.ajaxSetup({ cache: false });
    $.get("Analytics",function(data) {
        $("#main").html(" ");
        $("#main").html(data);
    });
}

function getEditDetails(){
    
    $.ajaxSetup({ cache: false });
    $.get("EditDetails",function(data) {
        $("#main").html(" ");
        $("#main").html(data).hide().fadeIn(2000);
    });
}

function getEditStyles(){
    
    $.ajaxSetup({ cache: false });
    $.get("EditStyles",function(data) {
        $("#main").html(" ");
        $("#main").html(data);
    });
}

function getRegister(dynamicElement, from_signup, with_header){
    
    $.ajaxSetup({ cache: false });
    var url = "Register";
    
    if(from_signup){
        url.concat("?from_signup=true");
        if(with_header){
            url.concat("&with_header=true");
        }
    }
    
    console.debug(url);
    $.get(url,function(data) {
        $(dynamicElement).html(" ");
        $(dynamicElement).html(data).hide().fadeIn(1000);
    });
}

function getChangePassword(){
    $.get("ChangePassword",function(data) {
        $("#main").html(" ");
        $("#main").html(data).hide().fadeIn(1000);
    });
}

function ajaxRegisterSubmit(from_signup) {
    
    $.ajaxSetup({ cache: false });
    var dynamicElement = "#main";
    if(from_signup){
        dynamicElement = "#wrapper";
    }
    $("form#register_form").on("submit", function(data) {
        event.preventDefault();

        $(dynamicElement).html("<section class='ajax_loading'><img src='images/ajax-loader.gif'/><p>Creating Posting...</p></section>");
        $.post("Register", $(this).serialize(),function(data){
            $(dynamicElement).html("<section class='ajax_success'><p>Successfully Registered your Details!</p></section>").fadeIn(1000).fadeOut(2000);
            
            setTimeout(function() {
                getRegister("#main", true, true);
            }, 2000);
        });
    });
}

function ajaxRegisterUpload(from_signup){
    $.ajaxSetup({ cache: false });
    var dynamicElement = "#main";
    if(from_signup){
        dynamicElement = "#wrapper";
    }
    
    $("form#register_upload").on("submit", function(data) {
        event.preventDefault();

        //grab all form data  
        var formData = new FormData($(this)[0]);
        $(dynamicElement).html(" ");
        $(dynamicElement).html("<section class='ajax_loading'><img src='images/ajax-loader.gif'/><p>Uploading Logo...</p></section>");
        $.ajax({
            url: "Register",
            type: 'POST',
            data: formData,
            async: false,
            cache: false,
            contentType: false,
            processData: false,
            success: function (data) {
              $(dynamicElement).html(" ");
              $(dynamicElement).html("<section class='ajax_success'><p>Successfully Uploaded your Logo!</p></section>").hide().fadeIn(2000).fadeOut(2000);

                setTimeout(function() {
                    getRegister(dynamicElement, false, false);
                }, 4000);
                
            }
          }).fail(function() {
            $(dynamicElement).html(" ");
            $(dynamicElement).html("<section class='ajax_fail'><p>Unable to Upload Logo, Please Try Again!</p></section>").hide().fadeIn(2000).fadeOut(2000);
            
             setTimeout(function() {
                getRegister(dynamicElement, false, false);
             }, 4000);
             
        });
        
        
    });
}

function initDashboard(){
    $.ajaxSetup({ cache: false });
    var dynamicElement = "#main";
   
        $(dynamicElement).html("<section class='ajax_loading'><img src='images/ajax-loader.gif'/><p>Loading Dashboard..</p></section>");
        $.get("ManagePosts", $(this).serialize(),function(data){
            $(dynamicElement).html(data).hide().fadeIn(1000);
        });
        
        
}

function ajaxChangePassword(){
    $.ajaxSetup({ cache: false });
    var dynamicElement = "#main";
   
    $("form#change_password_form").on("submit", function(data) {
        event.preventDefault();

        $(dynamicElement).html("<section class='ajax_loading'><img src='images/ajax-loader.gif'/><p>Changeing Password...</p></section>");
        $.post("ChangePassword", $(this).serialize(),function(data){
            $(dynamicElement).html("<section class='ajax_success'><p>Successfully Changed your Password!</p></section>").fadeIn(1000).fadeOut(2000);
            
            setTimeout(function() {
                
                initDashboard();
            }, 2000);
        });
        
        
    });
}

function getDashboardFaq(){
    var article = document.createElement("article");
    article.className = "faq";
    
    
    var postSection    = document.createElement("section");
    postSection.id = "post_section";
    var otherSection   = document.createElement("section");
    otherSection.id = "other_section";
    
    
    
    var type, question, answers;
    $.getJSON('dashboard_faq.json', function(data){
        var faq = data.faq;
        var bulletList  = document.createElement("ul");
        for(var i = 0; i < faq.length; i++){
            
            type     = faq[i].type;
            
            var questionLi = document.createElement("li");
            var questionH2 = document.createElement("h2");
            $(questionH2).html(faq[i].question);
            $(questionLi).html(questionH2);
            
            var answersLi = document.createElement("li");
            answers   = faq[i].answer;
            var answerList = document.createElement("ol");
            
            for(var j = 0; j < answers.length ; j++ ){
                var answerLi = document.createElement("li");
                $(answerList).append($(answerLi).html(answers[j]))
            }
            
            
            if(type === "post"){
                $(questionLi).append(answerList);
                $(postSection).append(questionLi);
                $(bulletList).append(postSection);
            }else if(type === "other"){
                $(questionLi).append(answerList);
                $(otherSection).append(questionLi);
                $(bulletList).append(otherSection);
            } 
            
        }
        
        
        $(postSection).prepend("<h1>For Posts</h1>");
        $(otherSection).prepend("<h1>For Misc</h1>");
       
        
        $("#main").html(" ");
        $(article).append(bulletList);
        $("#main").html(article);
        
    });
}