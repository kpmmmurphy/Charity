/**
 * Functions asscoiated with PayPal donation handling
 * 
 * @author Kevin Murphy
 * @version 1.1
 * @date 5/3/14
 */

/**
 * Generates the PayPal donation form and displays it using ColorBox, a JQuery pop-out
 * Source: http://www.jacklmoore.com/colorbox/
 * 
 * @param {type} id
 * @returns {undefined}
 */
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
