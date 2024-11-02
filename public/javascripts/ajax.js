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
        success: function(response) {
            replaceHtml("id-page", response);
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
    	success: function(response) {
    	    $(".spinner").css("display", "none");
    		replaceHtml("id-page", response);
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
        success: function(response) {
            replaceHtml("id-page", response);
        },
        error: function(response, error) {
            if (response.status === HTTP_STATUS.BAD_REQUEST) {
                const errorMessage = response.responseText;
                //$('#error-container').text(errorMessage);
            }
        }
    });
}

function card2ShufflePost(userBookId) {
    $.ajax({
        url: '/shuffle/' + userBookId,
        type : 'POST',
        success: function(response) {
            replaceHtml("id-page", response);
        },
        error: function(response, error) {

        }
    });
}

function favoritePost(targetElementId, userBookId) {
    $.ajax({
        url: '/favorite/' + userBookId,
        type : 'POST',
        success: function(response) {
            replaceHtml(targetElementId, response);
        },
        error: function(response, error) {

        }
    });
}

function replaceHtml(targetElementId, response) {
    $("#" + targetElementId).html("");
    let targetElementHtml = $(response).find("#" + targetElementId).html();
    $("#" + targetElementId).html(targetElementHtml);
}
