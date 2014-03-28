/**
 * General functions 
 * 
 * @author Kevin Murphy and Teng Yu
 * @version 1.1
 * @date 10/3/14

 */

/**
 * Makes an AJAX GET request to the file homepage_faq.json, parses it and renders it to
 * the browser. 
 * 
 * @returns {undefined}
 */
function getFAQ(){
    var article = document.createElement("article");
    article.className = "faq";
    
    
    var signupSection    = document.createElement("section");
    signupSection.id = "signup_section";
    var loginSection   = document.createElement("section");
    loginSection.id = "login_section";
    
    
    
    var type, question, answers;
    $.getJSON('homepage_faq.json', function(data){
        var faq = data.faq;
        var orderedList  = document.createElement("ul");
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
            
            if(type === "signup"){
                $(questionLi).append(answerList);
                $(signupSection).append(questionLi);
                $(orderedList).append(signupSection);
            }else if(type === "login"){
                $(questionLi).append(answerList);
                $(loginSection).append(questionLi);
                $(orderedList).append(loginSection);
            } 
            
        }
        
        $(signupSection).prepend("<h1>For Signup</h1>");
        $(loginSection).prepend("<h1>For Login and Forgetting your Password</h1>");
        
        $("#wrapper").html(" ");
        $(article).append(orderedList);
        $("#wrapper").html(article);
        
    });
}