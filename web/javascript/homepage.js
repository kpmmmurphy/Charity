var showContent = false;
var toggleComments = false;


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

function ajaxSubmit(form, submit, section, id) {
    $(submit).click(function() {


        event.preventDefault();

        var commenter_name = $(".commenter_name", form).val();
        var comment = $(".comment_textbox", form).val();
        console.debug(comment);
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
    }

    var commentingForm = createCommentingForm(charityName, id);
    $(commentingForm).hide();
    $(section).append(commentingForm);

    return section;
}

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
function processContent(content, id) {
    var section = document.createElement("section");
    section.className = "content_section";



    var div = document.createElement("div");
    $(div).className = "content_" + id;
    $(div).html(content);
    $(section).append(div);

    return section;
}

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



function getArticles(charityName, display_type) {

    var jsonPath = "../../charities/" + charityName + "/json/articles.json";
    var articleFields,
            articleClassFields = {"0": "img", "1": "title", "2": "date", "3": "description", "4": "content", "5": "type", "6": "id", "7": "approved", "8": "comments", "9": "tags"},
    articles, article, htmlArticle, htmlParagraph, title, description, content, type, date, img, tags, id, approved, comments, currentArticleField, contentSection;

    $("#main").html(" ");
    $.getJSON(jsonPath, function(data) {
        articles = data.articles;
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
            console.debug(type);
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

                                $(articleBody).append("<img src='../../images/Pay-Pal-Donation.png' class='donate' onclick='getPayPalDonationForm()'/>");
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
                    $("#main").prepend(htmlArticle);
                }
            }




        }

    });
}

function getCharityDetails(){
    //Get Charity's Address, Desc etc....
}

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

function init() {
    var charity_name;
    $.getJSON("./json/charity.json", function(data) {
        charity_name = data.charity.name;
        $.post("../../CharityPage", JSON.stringify({charity_name: charity_name}), function(data) {
            var mainSection = document.createElement("section");
            mainSection.id = "main";
            $("#wrapper").append(data);
            $("#wrapper").append(mainSection);
        });
    });



}

function getSubmitForm() {
    var from;
    $.get("../../CreatePost", function(data) {
        $("#main").html(" ");
        $("#main").html(data).hide().fadeIn(1000);
    });

}
