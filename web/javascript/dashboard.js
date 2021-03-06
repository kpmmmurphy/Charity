/**
 * This file handles AJAX call functionality for the charity's Dashboard.
 * It houses funtions for getting the individually designed elements which create
 * the functionality of our system. 
 * 
 * All AJAX caching is turned off to ensure the latest content is requested 
 * on each call.
 * 
 * @author Kevin Murphy
 * @version 1.1
 * @date 20/2/14
 */


/**
 * Makes an AJAX GET request to the Analytics.java servlet, which creates two tables
 * which are populated by data from our database
 * 
 * @returns {undefined}
 */
function getAnalytics(){
    
    $.ajaxSetup({ cache: false });
    $.get("Analytics",function(data) {
        $("#main").html(" ");
        $("#main").html(data);
    });
}

/**
 * Makes an AJAX Get request to the EditDetails.java servlet, which renders to the 
 * Dashboard a new set of options for editing the charity's details, such as updating the
 * charity's description,address, changing their password and deleting their account.  
 * 
 * @returns {undefined}
 */
function getEditDetails(){
    
    $.ajaxSetup({ cache: false });
    $.get("EditDetails",function(data) {
        $("#main").html(" ");
        $("#main").html(data).hide().fadeIn(2000);
    });
}

/**
 * Makes an AJAX Get request to the EditStyles.java servlet, which passes back
 * the html form which is rendered to the browser.
 * 
 * @returns {undefined}
 */
function getEditStyles(){
    
    $.ajaxSetup({ cache: false });
    $.get("EditStyles",function(data) {
        $("#main").html(" ");
        $("#main").html(data);
    });
}

/**
 * Makes an AJAX Get request to the Register.java servlet, and renders its output to the 
 * browser.
 * 
 * @param {type} dynamicElement
 * @param {type} from_signup
 * @param {type} with_header
 * @returns {undefined}
 */
function getRegister(dynamicElement, from_signup, with_header){
    
    $.ajaxSetup({ cache: false });
    var url = "Register";
   
    $.get(url,{from_signup:from_signup, with_header:with_header}, function(data) {
        $(dynamicElement).html(" ");
        $(dynamicElement).html(data).fadeIn(1000);
    });
}

/**
 * Makes an AJAX Get request to the ChangePassword.java servlet, and renders its output to the 
 * browser.
 * @returns {undefined}
 */
function getChangePassword(){
    $.get("ChangePassword",function(data) {
        $("#main").html(" ");
        $("#main").html(data).hide().fadeIn(1000);
    });
}

/**
 * Submits the form generated by calling the Register.java servlet via an AJAX POST request with 
 * the serialised paramaters.
 * 
 * @param {type} from_signup
 * @returns {undefined}
 */
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
            
            if(from_signup){
                setTimeout(function() {
                    getRegister(dynamicElement, true, true);
                }, 2000);
            }else{
                setTimeout(function() {
                    getRegister(dynamicElement, false, false);
                }, 2000);
            }
            
        });
    });
}

/**
 * Submits the multi-part form generated by calling the Register.java servlet via an AJAX Post request with 
 * th FormData JS Object.
 * 
 * @param {type} from_signup
 * @returns {undefined}
 */
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

/**
 * Initilises the Dashboard. Makes an AJAX GET request to the ManagePost.java servlet which
 * privides the charity admin with all functionality related to managing posts.
 * 
 * @returns {undefined}
 */
function initDashboard(){
    $.ajaxSetup({ cache: false });
    var dynamicElement = "#main";
   
        $(dynamicElement).html("<section class='ajax_loading'><img src='images/ajax-loader.gif'/><p>Loading Dashboard..</p></section>");
        $.get("ManagePosts", $(this).serialize(),function(data){
            $(dynamicElement).html(data).hide().fadeIn(1000);
        });
        
        
}

/**
 * AJAX submitting of changing password via a POST request to the ChangePassword.java servlet. 
 * 
 * @returns {undefined}
 */
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

/**
 * Generates the Dashboard FAQ from the dashboard_faq.json file which is situated on the
 * document root of the project. 
 * 
 * @returns {undefined}
 */
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