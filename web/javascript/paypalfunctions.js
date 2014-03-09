function getPayPalDonationForm(){
   
    $.colorbox({href:"PayPal"});
      
     $(document).bind('cbox_complete', function(){
        $("#donationSubmit").click(function(){
            event.preventDefault();
            //alert($('#donationForm').serialize());
            $.post( 'PayPal', $('#donationForm').serialize(), function(data) {  
                    $.colorbox({html:data});

            });
            
         });
      });
    
}
