function loginPost() {
    let requestParams = {
        email: $('#id-signup-email').val(),
        password: $('#id-signup-password').val(),
        _csrfToken: $('[name=_csrfToken]').val()
    };

    $.ajax({
        url: '/login',
        type : 'POST',
        data: requestParams,
        success: function(result) {
            $("#id-page").html("");
            let targetElementHtml = $(result).find("#id-page").html();
            $("#id-page").html(targetElementHtml);
        },
        error: function(request, error) {

        }
    })
}

function homeGet() {
    $.ajax({
    	url: '/home',
    	type : 'GET',
    	success: function(result) {
    		$("#id-page").html("");
            let targetElementHtml = $(result).find("#id-page").html();
            $("#id-page").html(targetElementHtml);
    	},
    	error: function(request, error) {

    	}
    });
}