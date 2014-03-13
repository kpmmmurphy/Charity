function getPayPalDonationForm(){
    $.colorbox({href:"../../PayPal",  width:"40%", height:"60%"});
     $(document).bind('cbox_complete', function(){
        $("#donationSubmit").click(function(){
            event.preventDefault();
            $.post( '../../PayPal', $('#donationForm').serialize(), function(data) {  
                    $.colorbox({html:data, width:"40%", height:"70%"});
            });
            
         });
      });
    
}
