var bugs = {
	"bugs":[
		{
			"Date": "4-3-14",
			"File": "Articles.java",
			"Description": "I was orignially generating article ids by simply counting the nunmbers of articles present in the json File, and incrementing it by one. This lead to duplications of ids. Ids are used to edit, delete and track donations of articles, when they are not unique, it causes multiple deletions and errors when editing.",
			"Status": "solved",
			"Fix": "I Fixed the bug by first creating a new entery in the database table 'articels' with the charity id of the creator, and the timestamp of when the article was created. We configured the db to automatically maintain a unique article id which was auto incremented on the entery of a new row in the table. After the values were entered into the db, I simply used a SELECT query to retrieve the latest atricle id using the charity id and timestamp, to avoid creating race conditions if other charities were creating articles at the same time.",
			"User": "Kevin Murphy",
			"Priority": "Immediate",
			"Severity": "High"
		},
		{
			"Date": "6-3-14",
			"File": "Signup.java",
			"Description": "When using the OWASP HTML sanitizer to filter and sanitize User submitted input to defend against cross side scripting attacks, we found that the '@' symbol was being escaped into its ascii value '&#64;'.",
			"Status": "solved",
			"Fix": "But not using the OWASP sanitizer for filter the e-mail but by using the new HTML5 input type 'email' to ensure correct formatting on the client side, and doing manual checking of the email string paramater on the server side.",
			"User": "Kealan Smyth",
			"Priority": "Medium",
			"Severity": "Medium"
		},
		{
			"Date": "11-3-14",
			"File": "homepage.js",
			"Description": "News articles on the client side are originally displayed in a condensed preview, then expanded on a User click to reveal the main content, then giving the User the option of minimizing the content. The UI element used to perform this toggling of the content area sometime would act in an unexpected manner, for instance, when the content was expanded, it would still read 'Click here to read more.. ', instead of the correct text 'Hide/Less..'. Ths functionality was not in error, but the UI element was.   ",
			"Status": "unsolved",
			"Fix": "The Fix requires that each article maintains a boolean whether it's expanded or not, we were unable to fix this bug due to lack of time at the end of the project.",
			"User": "Kevin Murphy",
			"Priority": "Low",
			"Severity": "Low"
		},
		{
			"Date": "12-3-14",
			"File": "Dashboard.java, dashboard.js",
			"Description": "Ajax calls within the Dashboard calling servlets needed to be paramatereised in order to customise the output from servlets. For example, the CreatePost.java servlet outputs a form which is used both at the server side Dashboard for charity admins to create post, and at the client side for User submitting posts. When the admin is creating a post, an option for posting to their social media account through OAuth is availible, but should not be to Users visiting the site and creating post which must be approved before they get displayed on the site.",
			"Status": "solved",
			"Fix": "Paramatereising ajax requests and if statements with in the servlet to handle the different cases",
			"User": "Kevin Murphy",
			"Priority": "Medium",
			"Severity": "Medium"
		},
		{
			"Date": "13-3-14",
			"File": "Dashboard.java, dashboard.js, homepage.js, Register.java, Upload.java",
			"Description": "Submitting multipart forms via Ajax requests not passing paramaters to the server correctly, null pointer exceptions being thrown.",
			"Status": "solved",
			"Fix": "Utilizing the Javascript FormData object to pass all the paramaters and image back to the Upload.java servlet and processing them using the apache commons IO and Fileupload libraries.",
			"User": "Kevin Murphy",
			"Priority": "Immediate",
			"Severity": "High"
		},
		{
			"Date": "15-3-14",
			"File": "Articles.java, EditPost.java",
			"Description": "Article tags were not being correctly converted and parsed from a String Object to a JSONArray of strings, and vice versa. This was leading to the incorrect formatting of the articles.json File.",
			"Status": "solved",
			"Fix": "Developed two methods to deal with both cases; converting from a String of characters seperated by a space character into a JSONArray,  and from a JSONArray into String of characters as to allow the charity administrator to edit them.",
			"User": "Kevin Murphy",
			"Priority": "Medium",
			"Severity": "Miedium"
		},
		{
			"Date": "15-3-14",
			"File": "homepage.js",
			"Description": "Javascript JQuery Ajax request being cached. When content was upDated and re-requested, it was not upDated, as the previous request was cached.",
			"Status": "solved",
			"Fix": "Fixed with one statement, $.ajaxSetup({cahce:false});, this configues ajax so that there is no caching.",
			"User": "Kevin Murphy",
			"Priority": "Medium",
			"Severity": "Miedium"
		},
		{
			"Date": "16-3-14",
			"File": "Articles.js, EditPost.java, Upload.java",
			"Description": "When editing an Article/Post changing the image associated with that post would not upDate correctly, leaving the original image with the post, though the new image was upload succesfully",
			"Status": "solved",
			"Fix": "Altered the Uplaod.java class to ensure it input the new image's name into the articles.json File ",
			"User": "Kevin Murphy",
			"Priority": "Medium",
			"Severity": "Miedium"
		},
		{
			"Date": "17-3-14",
			"File": "homepage.js, dashboard.js, general.js",
			"Description": "The fucntion to read in the FAQ Q&As which were stored in a JSON File was formatting the html to display the FAQs incorrectly, causing an ununified UI.",
			"Status": "solved",
			"Fix": "Alerted the functions in each File to correctly generate a heirarcical structure of nested Q&As lists giving the desired output",
			"User": "Teng Yu",
			"Priority": "low",
			"Severity": "low"
		}

	]
};

function getBugs(){
	//$.getJSON("./bugs.json", function(data){

		var bugsArray = bugs.bugs;
		for(var i = 0; i < bugsArray.length ; i++){

			var table = document.createElement("table");
			
			$.each(bugsArray[i], function(key, value){
				var tr    = document.createElement("tr");
				var th    = document.createElement("th");
				var td    = document.createElement("td");

				if(value === "solved"){
					td.className = "solved";
				}else if(value === "unsolved"){
					td.className = "unsolved";
				}
				$(tr).append($(th).html(key));
				$(tr).append($(td).html(value));
				$(table).append(tr);
			})
			$("#main").append(table);
		}

	//});
}