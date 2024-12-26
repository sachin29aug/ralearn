// Get calls

function homeGet() {
    $(".spinner").css("display", "flex");
    let url = "/home";
    $.ajax({
    	url: url,
    	type : 'GET',
    	success: function(response) {
    	    $(".spinner").css("display", "none");
    		replaceHtml("id-page", response, url, null);
    	},
    	error: function(request, error) {

    	}
    });
}

function bookDetailsGet(bookId) {
    let url = "/book/" + bookId;
    $.ajax({
    	url: url,
    	type : 'GET',
    	success: function(response) {
    	    hideBSBackDrop();
    		replaceHtml("id-page", response, url, true);
    	},
    	error: function(request, error) {

    	}
    });
}

function favoriteListsGet() {
    let url = "/list/favorites";
    $.ajax({
    	url: url,
    	type : 'GET',
    	success: function(response) {
    		replaceHtml("id-page", response, url, true);
    	},
    	error: function(request, error) {

    	}
    });
}

function recentListsGet() {
    let url = "/list/recent";
    $.ajax({
    	url: url,
    	type : 'GET',
    	success: function(response) {
    		replaceHtml("id-page", response, url, true);
    	},
    	error: function(request, error) {

    	}
    });
}

function categoryListsGet(categoryId) {
    let url = "/list/category/" + categoryId;
    $.ajax({
    	url: url,
    	type : 'GET',
    	success: function(response) {
    		replaceHtml("id-page", response, url, true);
    	},
    	error: function(request, error) {

    	}
    });
}

function feedbackGet() {
    let url = "/feedback";
    $.ajax({
    	url: url,
    	type : 'GET',
    	success: function(response) {
    		replaceHtml("id-page", response, url, true);
    	},
    	error: function(request, error) {

    	}
    });
}

function discoverGet() {
    let url = "/discover";
    $(".spinner").css("display", "flex");
    $.ajax({
    	url: url,
    	type : 'GET',
    	success: function(response) {
    		replaceHtml("id-page", response, url, true);
    		$(".spinner").css("display", "none");
    	},
    	error: function(request, error) {

    	}
    });
}

function profileGet() {
    let url = "/profile";
    $.ajax({
    	url: url,
    	type : 'GET',
    	success: function(response) {
    		replaceHtml("id-page", response, url, true);
    	},
    	error: function(request, error) {

    	}
    });
}

// Post calls

function signupLoginPost(categoryIds) {
    let requestParams = {
        firstname: $('#id-signup-firstname').val(),
        lastname: $('#id-signup-lastname').val(),
        email: $('#id-signup-email').val(),
        password: $('#id-signup-password').val(),
        categoryIds: categoryIds.join(','),
        _csrfToken: $('[name=_csrfToken]').val()
    };

    $.ajax({
        url: '/signup/login',
        type : 'POST',
        data: requestParams,
        success: function(response) {
            replaceHtml("id-page", response, null, null);
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
        _csrfToken: $('[name=_csrfToken]').val(),
        timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
    };

    $(".spinner").css("display", "flex");
    $.ajax({
        url: '/login',
        type : 'POST',
        data: requestParams,
        success: function(response) {
            $(".spinner").css("display", "none");
            replaceHtml("id-page", response, null, null);
            history.pushState(null, "", "/home");
        },
        error: function(response, error) {
            if (response.status === HTTP_STATUS.BAD_REQUEST) {
                const errorMessage = response.responseText;
                //$('#error-container').text(errorMessage);
            }
        }
    });
}

function forgotPasswordPost(email) {
    $.ajax({
        url: '/forgot-password',
        type : 'POST',
        data: {"email" : email},
        success: function(response) {
            replaceHtml("id-page", response, null, null);
            displaySuccessMessage("If an account is associated with this email address, you will receive instructions to reset your password shortly.");
        },
        error: function(response, error) {

        }
    });
}

function card2ShufflePost(userBookId) {
    $.ajax({
        url: '/shuffle/' + userBookId,
        type : 'POST',
        success: function(response) {
            replaceHtml("id-page", response, null, null);
        },
        error: function(response, error) {

        }
    });
}

function favoritePost(targetElementId, bookId) {
    $.ajax({
        url: '/favorite/' + bookId,
        type : 'POST',
        success: function(response) {
            replaceHtml(targetElementId, response, null, null);
        },
        error: function(response, error) {

        }
    });
}

function feedbackPost(feedbackText) {
    $.ajax({
        url: '/feedback',
        type : 'POST',
        data: {"feedbackText": feedbackText},
        success: function(response) {
            $("#id-feedback-div").hide();
            $("#id-feedback-success-div").show();
            //replaceHtml(targetElementId, response, null, null);
        },
        error: function(response, error) {

        }
    });
}

function discoverCategoryPost(categoryId) {
    $(".spinner").css("display", "flex");
    $.ajax({
        url: '/discover/' + categoryId,
        type : 'POST',
        success: function(response) {
            replaceHtml("id-page", response, null, null);
            $(".spinner").css("display", "none");
        },
        error: function(response, error) {

        }
    });
}

function userRatingPost(bookId, rating, text) {
    $.ajax({
        url: '/book/user/rating/' +  bookId,
        type : 'POST',
        data: {"rating": rating, "text": text},
        success: function(response) {
            replaceHtml("id-rate-book-target", response, null, null);
        },
        error: function(response, error) {

        }
    });
}

function bookSearchPost(searchTerm, htmlReplacementTargetId) {
    $(".spinner").css("display", "flex");
    $.ajax({
        url: '/book/search',
        method: 'POST',
        data: {"searchTerm": searchTerm},
        success: function (response) {
            $(".spinner").css("display", "none");
            replaceHtml(htmlReplacementTargetId, response, null, null);
        },
        error: function (response, error) {

        },
    });
}

function replaceHtml(targetElementId, response, url, scrollToTop) {
    $("#" + targetElementId).html("");
    let targetElementHtml = $(response).find("#" + targetElementId).html();
    $("#" + targetElementId).html(targetElementHtml);
    if(url) {
        history.pushState(null, "", url);
    }

    if(scrollToTop) {
        $(window).scrollTop(0);
    }
}
