$(document).ready(function () {
	 //init
    $('#calling-modal').modal({show:false});
    //add custom animation
    $('#calling-modal').on('show.bs.modal', function (e) {
        var open = $(this).attr('data-easein');
        $('.modal-dialog').velocity('transition.' + open);
    });
	
    var elementServer=document.getElementById("ipserver");
	var server=elementServer.getAttribute("name");
    
	var ws = new WebSocket('ws://'+server+':15674/ws');
	var client = Stomp.over(ws);
	
	var on_connect = function() {
		console.log('connected');
		var element=document.getElementById("queue");
		var queue=element.getAttribute("name");
		console.log(name);
		client.subscribe(queue, function(d) {
			var numberToCall = d.body;
			console.log(numberToCall);
			
			$("#numToCall").html(numberToCall);
			showModal();
		}/*, {"auto-delete": true, "exchange": "spring-boot-exchanger"}*/);
	};
	var on_error =  function() {
		console.log('error');
	};
	
	client.connect('guest', 'guest', on_connect, on_error, '/');
    
});


function showModal() {
	$('#calling-modal').modal('show');
	
    setTimeout(function () {
        hideModal();
    }, 3000)
}



function hideModal() {
	$('#calling-modal').modal('hide');
}


