function signupLoginPost(subcategoryIds) {
    let requestParams = {
        email: $('#id-signup-email').val(),
        password: $('#id-signup-password').val(),
        subcategoryIds: subcategoryIds.join(','),
        _csrfToken: $('[name=_csrfToken]').val()
    };

    $.ajax({
        url: '/signup/login',
        type : 'POST',
        data: requestParams,
        success: function(result) {
            $("#id-page").html("");
            let targetElementHtml = $(result).find("#id-page").html();
            $("#id-page").html(targetElementHtml);
        },
        error: function(request, error) {

        }
    });
}

function homeGet() {
    $(".spinner").css("display", "flex");
    $.ajax({
    	url: '/home',
    	type : 'GET',
    	success: function(result) {
    	    $(".spinner").css("display", "none");
    		$("#id-page").html("");
            let targetElementHtml = $(result).find("#id-page").html();
            $("#id-page").html(targetElementHtml);
    	},
    	error: function(request, error) {

    	}
    });
}

function loginPost() {
    let requestParams = {
        email: $('#id-login-email').val(),
        password: $('#id-login-password').val(),
        returnUrl: $('#id-return-url').val(),
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
        error: function(response, error) {
            if (response.status === HTTP_STATUS.BAD_REQUEST) {
                const errorMessage = response.responseText;
                //$('#error-container').text(errorMessage);
            }
        }
    });
}
