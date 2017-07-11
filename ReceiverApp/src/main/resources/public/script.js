$(document).ready(function () {
    //init
    $('#calling-modal').modal({show:false});

    //add custom animation
    $('#calling-modal').on('show.bs.modal', function (e) {
        var open = $(this).attr('data-easein');
        console.log(open);
        $('.modal-dialog').velocity('transition.' + open);
    });

    showModal();
});


function showModal() {
    setTimeout(function () {
        $('#calling-modal').modal('show');
        hideModal();
    }, 4000)
}


function hideModal() {
    setTimeout(function () {
        $('#calling-modal').modal('hide');

        showModal();
    }, 5000)
}



