function getPayPalDonationForm(id){
    
    if(id === null){
        $.colorbox({href:"../../PayPal",  width:"40%", height:"60%"});
    }else{
        $.colorbox({href:"../../PayPal?article_id=" + id,  width:"40%", height:"60%"});
    }
    
     $(document).bind('cbox_complete', function(){
         
        $("#donationSubmit").click(function(){
            event.preventDefault();
            $.post( '../../PayPal', $('#donationForm').serialize(), function(data) {  
                    $.colorbox({html:data, width:"40%", height:"70%"});
            });
            
         });
      });
    
}
