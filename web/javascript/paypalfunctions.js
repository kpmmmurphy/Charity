function getPayPalDonationForm(){
   
    $.colorbox({href:"PayPal"});
    
    
      
     $(document).bind('cbox_complete', function(){
        $("#donationSubmit").click(function(){
            //event.preventDefault();
            alert($('#donationForm').serialize());
            $.post( 'PayPal', $('#donationForm').serialize(), function(data) {  
                    alert(3);
                
                        $.colorbox(data);

            });
            
      });
      });
    
}

function setUpAjaxSubmit(){
    
      
        
   
    
}




function getPayPalDonation(){
    
    $.get("PayPal", function(data) {
        var dialogDiv = document.createElement("div");
        dialogDiv.innerHTML = data;
        dialogDiv.className = "paypal_dialog";
        dialogDiv.title = "Donation";
        $("body").prepend(dialogDiv);
        $(".paypal_dialog").colorbox();
        $.colorbox({href:"PayPal"});
    });
    $.colorbox({href:"thankyou.html"});
    
}