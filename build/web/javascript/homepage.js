/*
 * This file provides the main client side functionality.
 * It handles the rendering and filtering of articles, generation of the donation forms,
 * generation of FAQs, initilising the charity specific index.html page, and posting of
 * user submited comments and posts via AJAX requests. 
 * 
 * @author Kevin Murphy
 * @version 1.1
 * @date 5/3/14
 *
 */

var showContent = false;
var toggleComments = false;

/**
 * Generated the HTML img element of the article
 * 
 * @param {type} charityName
 * @param {type} img
 * @returns {processImg.imgElement|Element}Ren
 */
function processImg(charityName, img) {
    var div = document.createElement("div");
    div.className = "post_img_div";
    var imgElement = document.createElement("img");
    imgElement.className = "article_img";
    //Must be changed, only for testing. Relitive Path instead
    imgElement.src = "../../charities/" + charityName + "/uploads/" + img;
    $(div).html(imgElement);
    return imgElement;
}

/**
 * Generated the HTML commenting form and attaches an AJAX listener to perform the submission
 * 
 * @param {type} charityName
 * @param {type} id
 * @returns {createCommentingForm.section|Element}
 */
function createCommentingForm(charityName, id) {

    var section = document.createElement("section");
    section.id = "submit_comment_" + id;

    var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("name", "comment_form_" + id);
    form.setAttribute("id", "comment_form_" + id);

    var fieldset = document.createElement("fieldset");
    var legend = document.createElement("legend");
    $(legend).html("Post a Comment");

    $(fieldset).append(legend);

    var input = document.createElement("input");
    input.setAttribute("type", "text");
    input.setAttribute("name", "commenter_name");
    input.setAttribute("class", "commenter_name");
    input.setAttribute("placeholder", "Your Name...");

    var hidden_id = document.createElement("input");
    hidden_id.setAttribute("type", "hidden");
    hidden_id.setAttribute("name", "post_id");
    hidden_id.setAttribute("value", id);

    var hidden_charity_name = document.createElement("input");
    hidden_charity_name.setAttribute("type", "hidden");
    hidden_charity_name.setAttribute("name", "charity_name");
    hidden_charity_name.setAttribute("value", charityName);

    var textarea = document.createElement("textarea");
    $(textarea).html("Type Your Comment Here...");
    textarea.name = "comment_textbox";
    textarea.className = "comment_textbox";

    var submit = document.createElement("input");
    submit.setAttribute("type", "submit");
    submit.setAttribute("value", "Submit");

    ajaxSubmit(form, submit, section, id);

    $(fieldset).append(input);
    $(fieldset).append(hidden_id);
    $(fieldset).append(hidden_charity_name);
    $(fieldset).append(textarea);
    $(fieldset).append(submit);
    $(form).append(fieldset);
    $(section).append(form);


    return section;
}

/**
 * Facilitated AJAX form submission
 * 
 * @param {type} form
 * @param {type} submit
 * @param {type} section
 * @param {type} id
 * @returns {undefined}
 */
function ajaxSubmit(form, submit, section, id) {
    $.ajaxSetup({ cache: false });
    $(submit).click(function() {
        event.preventDefault();

        var commenter_name = $(".commenter_name", form).val();
        var comment = $(".comment_textbox", form).val();
        if (commenter_name === null || commenter_name === "") {
            alert("Please enter a Name! ");
            return 0;
        } else if (comment === null || comment === "") {
            alert("Please enter a Comment! ");
            return 0;
        } else {
            $(section).html(" ");
            $(section).html("<section class='ajax_loading'><img src='../../images/loading.gif'/><p>Posting Comment...</p></section>");

            $.post('../../PostComment', $(form).serialize(), function(data) {
                $(section).html(" ");
                $(section).html("<section class='ajax_success'><p>Comment Submitted Successfully!</p></section>").hide().fadeIn(3000).fadeOut(2000);

                createComment(commenter_name, comment, ".comments_" + id, "#comment_section_" + id);

                setTimeout(function() {
                    $(section).html(form).slideToggle("slow");
                }, 5000);
                ajaxSubmit(form, submit, section, id);
            }).fail(function() {
                $(section).html(" ");
                $(section).html("<section class='ajax_fail'><p>Unable to Post Comment, Please Try Again!</p></section>").hide().fadeIn(3000).fadeOut(2000);
                setTimeout(function() {
                    $(section).html(form).slideToggle("slow");
                }, 5000);
            });
        }


    });
}


/**
 * Creates the commenting section of each individual charity article/post
 * 
 * @param {type} charityName
 * @param {type} comments
 * @param {type} id
 * @returns {processComments.section|Element}
 */
function processComments(charityName, comments, id) {


    var section = document.createElement("section");
    section.id = "comments_section_" + id;

    var div = document.createElement("div");
    div.className = "comments_" + id;
    $(div).hide();

    var h3 = document.createElement("h3");
    h3.id = "toggle_comments_" + id;
    $(h3).html("<p>Comments...</p><img src='../../images/down_arrow.png' class='toggle_arrow'/>");

    h3.addEventListener("click", function(event) {
        showComments(id);
    });

    $(section).append(h3);

    if (comments.length > 0) {
        for (var k = 0; k < comments.length; k++) {

            createComment(comments[k].name, comments[k].comment, div, section);

        }
    }else{
        $(section).append(div);
    }

    var commentingForm = createCommentingForm(charityName, id);
    $(commentingForm).hide();
    $(section).append(commentingForm);

    return section;
}

/**
 * Creates a comment and displays it onto the existing comment section, showing the user
 * their newly posted comment
 * 
 * @param {type} name
 * @param {type} comment
 * @param {type} container
 * @param {type} section
 * @returns {undefined}
 */
function createComment(name, comment, container, section) {
    var currentComment = document.createElement("article");
    currentComment.className = "comment";

    var nameParagraph = document.createElement("p");
    var commentParagraph = document.createElement("p");

    //Get the commenter's name and append it 
    nameParagraph.className = "comment_name";
    $(nameParagraph).html(name);
    $(currentComment).append(nameParagraph);

    //Get their comment and append it
    commentParagraph.className = "comment_text";
    $(commentParagraph).html(comment);
    $(currentComment).append(commentParagraph);

    $(container).append(currentComment);

    $(section).append(container);


}

/**
 * Toggles the comments section, i.e. shows or hides it.
 * 
 * @param {type} id
 * @returns {undefined}
 */
function showComments(id) {
    $(".comments_" + id).slideToggle();
    $("#submit_comment_" + id).slideToggle();

    var toggleWithDownImg = "<p>Comments...</p><img src='../../images/down_arrow.png' class='toggle_arrow'/>",
            toggleWithUpImg = "<p>Hide Comments...</p><img src='../../images/up_arrow.png' class='toggle_arrow'/>";

    if (toggleComments) {
        $("#toggle_comments_" + id).html(toggleWithDownImg);
    } else {
        $("#toggle_comments_" + id).html(toggleWithUpImg);
    }
    toggleComments = !toggleComments;
}


var showContent = true;
/**
 * Generates the HTML section element to house the contents section of an article
 * 
 * @param {type} content
 * @param {type} id
 * @returns {processContent.section|Element}
 */
function processContent(content, id) {
    var section = document.createElement("section");
    section.className = "content_section";



    var div = document.createElement("div");
    $(div).className = "content_" + id;
    $(div).html(content);
    $(section).append(div);

    return section;
}

/**
 * Shows or hides the contents section of an article/post
 * 
 * @param {type} id
 * @returns {undefined}
 */
function toggleContentSection(id) {
    $("#content_section_" + id).slideToggle();


    var toggleWithDownImg = "<p>Click Here to Read More...</p><img src='../../images/down_arrow.png' class='toggle_arrow'/>",
            toggleWithUpImg = "<p>Less...</p><img src='../../images/up_arrow.png' class='toggle_arrow'/>";

    if (showContent) {
        $("#toggle_" + id).html(toggleWithUpImg);
    } else {
        $("#toggle_" + id).html(toggleWithDownImg);
    }
    showContent = !showContent;
}

/**
 * Processes the individual tags and displays them for each article.
 * 
 * @param {type} tags
 * @returns {processTags.section|Element}
 */
function processTags(tags) {
    var tagsString = "";
    for (var i = 0; i < tags.length; i++) {
        if (tags[i] !== "") {
            tagsString += "<span>" + tags[i] + " </span>";
        }

    }

    var section = document.createElement("section");
    section.className = "tags";

    var h3 = document.createElement("h3");
    $(h3).html("Tags");

    var p = document.createElement("p");
    $(p).html(tagsString);

    $(section).append(h3);
    $(section).append(p);
    return section;
}


/**
 * This is the main function used to handle the generation for post/articles 
 * of charity's on the clide side. It dynamically reads the charity's article.json file
 * via an JQuery AJAX call and renders them to the browser.
 * 
 * @param {type} charityName
 * @param {type} display_type
 * @returns {undefined}
 */
function getArticles(charityName, display_type) {
    $.ajaxSetup({ cache: false });
    var jsonPath = "../../charities/" + charityName.replace(" ", "").trim().toLowerCase() + "/json/articles.json";
    var articlesContainer, articleFields,
            articleClassFields = {"0": "img", "1": "title", "2": "date", "3": "description", "4": "content", "5": "type", "6": "id", "7": "approved", "8": "comments", "9": "tags"},
    articles, article, htmlArticle, htmlParagraph, title, description, content, type, date, img, tags, id, approved, comments, currentArticleField, contentSection;

    $("#main").html("<p id='no_posts'>No Posts? <a href='#' onclick='getSubmitForm(true)'>Click here to create one!</a></p>");
    $.getJSON(jsonPath, function(data) {
        articles = data.articles;
        articlesContainer = document.createElement("section");
        articlesContainer.id = "articles_container";
        
        for (var i = 0; i < articles.length; i++) {
            article = articles[i];
            title = article.title;
            description = article.description;
            content = article.content;
            type = article.type;
            date = article.date;
            img = article.img;
            tags = article.tags;
            id = article.id;
            approved = article.approved;
            comments = article.comments;
            articleFields = {"0": img, "1": title, "2": date, "3": description, "4": content, "5": type, "6": id, "7": approved, "8": comments, "9": tags};
            
            if (display_type === type || display_type === null) {
                if (approved) {
                    htmlArticle = document.createElement("article");
                    htmlArticle.className = "article";

                    var articleDetails = document.createElement("div");
                    articleDetails.className = "article_details";

                    var articleHeader = document.createElement("section");
                    articleHeader.className = "article_header";

                    contentSection = document.createElement("section");
                    contentSection.id = "content_section_" + id;

                    var articleBody = document.createElement("section");
                    articleBody.id = "content_section_" + id;

                    $(articleBody).hide();
                    for (var j = 0; j < Object.keys(articleFields).length; j++) {


                        currentArticleClassField = articleClassFields[j];
                        currentArticleField = articleFields[j];

                        if (currentArticleClassField === "img") {

                            var imgDiv = processImg(charityName, currentArticleField);
                            $(articleHeader).append(imgDiv);


                        } else if (currentArticleClassField === "comments") {

                            var commentsDiv = processComments(charityName, currentArticleField, id);

                            $(articleBody).append(commentsDiv);


                        } else if (currentArticleClassField === "tags") {

                            var tagsSection = processTags(currentArticleField);
                            $(articleBody).append(tagsSection);

                        } else if (currentArticleClassField === "title" || currentArticleClassField === "description" || currentArticleClassField === "date") {

                            htmlParagraph = document.createElement("p");
                            htmlParagraph.id = currentArticleClassField + "_" + id;
                            $(htmlParagraph).html(currentArticleField);
                            $(articleDetails).append(htmlParagraph);
                            $(articleHeader).append(articleDetails);


                        } else if (currentArticleClassField === "type") {

                            if (currentArticleField === "sponsorship") {

                                $(articleBody).append("<img src='../../images/Pay-Pal-Donation.png' class='donate' onclick=\"getPayPalDonationForm( '" + id + "')\"/>");
                            }

                        } else if (currentArticleClassField === "content") {

                            var contents = processContent(currentArticleField, id);
                            var p = "<div id='toggle_" + id + "' onclick='toggleContentSection(" + id + ")'><p>Click Here to Read More...</p><img src='../../images/down_arrow.png' class='toggle_arrow'/></div>";

                            $(articleDetails).append(p);
                            $(articleBody).append(contents);

                        }
                        $(htmlArticle).append(articleHeader);
                        $(htmlArticle).append(articleBody);
                    }
                    $(articlesContainer).prepend(htmlArticle);
                }
            }




        }
        $("#main").prepend(articlesContainer).hide().fadeIn(1000);
    });
}

/**
 * Gets the charity.json file of the current charity and builds a section containing
 * its basic details, which is seen when a user first visits the charity's homepage(index.html)
 * 
 * @returns {undefined}
 */
function getCharityDetails(){
    $.ajaxSetup({ cache: false });
    var description, address, telephone, facebook, twitter, googleplus;
    $.getJSON("./json/charity.json", function(data) {
        
        var charityDetails = document.createElement("article");
        charityDetails.id = "charity_details";
        
        charity_name        = data.charity.name;
        description = data.charity.description;
        address     = data.charity.address;
        telephone   = data.charity.telephone;
        facebook    = data.charity.facebook;
        twitter     = data.charity.twitter;
        googleplus  = data.charity.googleplus;
        logo        = data.charity.logo;
        
        var logoImg = document.createElement("img");
        logoImg.src = "./uploads/" + logo;
        
        var nameH2 = document.createElement("h2"); 
        nameH2.id = "charity_name";
        $(nameH2).html(charity_name);
        

        

        var descArticle = document.createElement("article"); 
        descArticle.id = "description";
        var descP = document.createElement("p");
        $(descP).html(description);
        $(descArticle).html(descP);
        
        var addressArticle = document.createElement("article"); 
        addressArticle.id = "address";
        var addressP = document.createElement("p");
        $(addressP).html(address);
        $(addressArticle).html(addressP);
        
        var telephoneArticle = document.createElement("article"); 
        telephoneArticle.id = "telephone";
        var teleP = document.createElement("p");
        $(teleP).html(telephone);
        $(telephoneArticle).html(teleP);
        
        //Social Media Icons gotten from - 
        //http://dribbble.com/shots/1233464-24-Free-Flat-Social-Icons
        var socialArticle = document.createElement("article"); 
        socialArticle.id  = "social";
        
        var facebookImg   = document.createElement("img");
        var facebookAnchor  = document.createElement("a");
        facebookImg.src = "../../images/social/facebook.png";
        facebookAnchor.setAttribute("href", facebook);
        $(facebookAnchor).html(facebookImg);
        
        var twitterImg  = document.createElement("img");
        var twitterAnchor  = document.createElement("a");
        twitterImg.src = "../../images/social/twitter.png";
        twitterAnchor.setAttribute("href", twitter);
        $(twitterAnchor).html(twitterImg);
        
        var googleplusImg = document.createElement("img");
        var googleplusAnchor  = document.createElement("a");
        googleplusImg.src = "../../images/social/googleplus.png";
        googleplusAnchor.setAttribute("href", googleplus );
        $(googleplusAnchor).html(googleplusImg);
        
        $(socialArticle).append(facebookAnchor);
        $(socialArticle).append(twitterAnchor);
        $(socialArticle).append(googleplusAnchor);
        
        
        $("#main").html(" ");
        $(charityDetails).append(logoImg);
        $(charityDetails).append(nameH2);
        $(charityDetails).append(descArticle);
        $(charityDetails).append(addressArticle);
        $(charityDetails).append(telephoneArticle);
        $(charityDetails).append(socialArticle);
        $("#main").append(charityDetails).hide().fadeIn(2000);
        
        
    });
}

/**
 * Generates the donation section of the homepage, rendered when a visitor
 * clicks the 'Donate' nav link. Calls the function, getPayPalForm() from the 
 * file paypalfunctions.js, which uses an AJAX call to get the PayPal html form 
 * from the PayPal.java servlet. 
 * 
 * @returns {undefined}
 */
function getDonate() {

    var article = document.createElement("article");
    article.className = "donate_section";

    var p1 = document.createElement("p");
    var p2 = document.createElement("p");
    var img = document.createElement("img");
    img.src = "../../images/Pay-Pal-Donation.png";
    img.className = "donate_button";
    $(img).click(function() {
        getPayPalDonationForm();
    })
            ;

    $(p1).html("All donations will go directly to our Charity. Any amount will help us help others.");
    $(p2).html("Thank you.");

    $(article).append(p1);
    $(article).append(p2);
    $(article).append(img);

    $("#main").hide().html(article).fadeIn(1000);

}

/**
 * This function is invoked when the body of the index.html page specific to the 
 * charity is loaded. It retrieves the relitivce charity.json file, and and passed the charity name
 * to the CharityPage.java servlet, which then passes back the dynamically generated html nav element with
 * links to each client side function of the system. The CharityPage.java servlet manages session tracking.
 * 
 * @returns {undefined}
 */
function init() {
    $.ajaxSetup({ cache: false });
    var charity_name;
    $.getJSON("./json/charity.json", function(data) {
        charity_name = data.charity.name;
        $.post("../../CharityPage", JSON.stringify({charity_name: charity_name}), function(data) {
            var mainSection = document.createElement("section");
            mainSection.id = "main";
            $("#wrapper").append(data);
            $("#wrapper").append(mainSection);
            getCharityDetails();
        });
    });



}

/**
 * Gets the Create Post html form via an ajax call to the CreatePost.java servlet, and 
 * renders it to the browser.
 * 
 * @returns {undefined}
 */
function getSubmitForm() {
    $.get("../../CreatePost", function(data) {
        
        var submitArticle = document.createElement("article");
        submitArticle.id = "submit_container";
        
        $(submitArticle).html(data);
        $("#main").html(submitArticle).hide().fadeIn(1500);
    });

}

/**
 * Takes the FAQ, which is housed in a JSON file under the document root, parses
 * it and generates a html section of nested lists containing the questions and answers. 
 * 
 * @returns {undefined}
 */
function getFAQ(){
    var article = document.createElement("article");
    article.className = "faq";
    
    
    var newsSection    = document.createElement("section");
    newsSection.id = "news_section";
    
    var lostAndFoundSection    = document.createElement("section");
    lostAndFoundSection.id = "lost_and_found_section";
    
    var sponsorshipSection   = document.createElement("section");
    sponsorshipSection.id = "sponsorship_section";
    
    
    
    var type, question, answers;
    $.getJSON('./../../charity_faq.json', function(data){
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
            
            if(type === "news"){
                $(questionLi).append(answerList);
                $(newsSection).append(questionLi);
                $(bulletList).append(newsSection);
            }else if(type === "lost_and_found"){
                $(questionLi).append(answerList);
                $(lostAndFoundSection).append(questionLi);
                $(bulletList).append(lostAndFoundSection);
            }else if(type === "sponsorship") {
                $(questionLi).append(answerList);
                $(sponsorshipSection).append(questionLi);
                $(bulletList).append(sponsorshipSection);
            }
            
        }
        
        $(newsSection).prepend("<h1>For News</h1>");
        $(lostAndFoundSection).prepend("<h1>For Lost and Found</h1>");
        $(sponsorshipSection).prepend("<h1>For Sponsorship</h1>");
        
        
        $("#main").html(" ");
        $(article).append(bulletList);
        $("#main").html(article).fadeIn(3000);
        
    });
}

