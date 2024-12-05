// Signup related

$(document).ready(function() {
    var selectedPillCount = 0;
    $(document.body).on("click", ".category-pill", function(e) {
        e.preventDefault();
        if ($(this).hasClass("selected")) {
            $(this).removeClass("selected");
            selectedPillCount++;
        } else {
            $(this).addClass("selected");
            selectedPillCount--;
        }

        if(selectedPillCount == 0) {
            $(".signup--continue-btn").addClass("disabled");
        } else {
            $(".signup--continue-btn").removeClass("disabled");
        }
    });

    $(document.body).on("click", "#categories-page .signup--continue-btn", function(e) {
        e.preventDefault();
        selectedPillCount == 0;
        $(".signup--continue-btn").addClass("disabled");
        $("#categories-page").hide();
        $("#subcategories-page").show();
        $(".category-pills .category-pill.selected").each(function() {
            let subcategoriesDivId = $(this).data("subcategories-div-id");
            $("#" + subcategoriesDivId).show();
        });
    });

    $(document.body).on("click", "#subcategories-page .signup--continue-btn", function(e) {
        e.preventDefault();
        $(".signup--continue-btn").addClass("disabled");
        $("#subcategories-page").hide();
        $("#email-password-page").show();
    });

    var emailValidated = false;
    var passwordValidated = false;
    $('#id-signup-email').blur(function () {
        let email = $(this).val();
        let emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (email === "") {
            $("#id-signup-email-error").text("Email is required");
            $("#id-signup-email-error").show();
            emailValidated = false;
            $("#id-signup-email-password-continue-btn").addClass("disabled");
        } else if (!emailPattern.test(email)) {
            $("#id-signup-email-error").text("Please enter a valid email address");
            $("#id-signup-email-error").show();
            emailValidated = false;
            $("#id-signup-email-password-continue-btn").addClass("disabled");
        } else {
         	$("#id-signup-email-error").text("");
         	$("#id-signup-email-error").hide();
         	emailValidated = true;
         	if(passwordValidated) {
                $("#id-signup-email-password-continue-btn").removeClass("disabled");
            }
         }
    });

    $('#id-signup-password').blur(function () {
        let password = $(this).val();
        if (password === "") {
            $("#id-signup-password-error").text("Password is required");
            $("#id-signup-password-error").show();
            passwordValidated = false;
            $("#id-signup-email-password-continue-btn").addClass("disabled");
        } else if (password.trim().length < 8) {
            $("#id-signup-password-error").text("Password must be at least 8 characters long");
            $("#id-signup-password-error").show();
            passwordValidated = false;
            $("#id-signup-email-password-continue-btn").addClass("disabled");
        } else {
            $("#id-signup-password-error").text("");
            $("#id-signup-password-error").hide();
            passwordValidated = true;
            if(emailValidated) {
                $("#id-signup-email-password-continue-btn").removeClass("disabled");
            }
        }
    });

    $(document.body).on("click", "#id-signup-show-password", function(e) {
        let passwordTextBox = $("#id-signup-password")
        if (passwordTextBox.attr("type") === "password") {
            passwordTextBox.attr("type", "text");
        } else {
            passwordTextBox .attr("type", "password");
        }
    });

    $(document.body).on("click", "#email-password-page .signup--continue-btn", function(e) {
        e.preventDefault();
        let subcategoryIds = [];
        $('#subcategories-page .category-pill.selected').each(function() {
            let subcategoryId = $(this).data('subcategory-id');
            subcategoryIds.push(subcategoryId);
        });
        signupLoginPost(subcategoryIds);
    });

    $(document.body).on("click", "#welcome-page .signup--continue-btn", function(e) {
        e.preventDefault();
        homeGet();
    });
});

// Login related

$(document).ready(function() {
    $(document.body).on("click", "#id-login-btn", function(e) {
        e.preventDefault();
        loginPost();
    });
});

// Home page related

$(document).ready(function() {
    $(document.body).on("click", "#id-card2-shuffle-btn", function(e) {
        e.preventDefault();
        card2ShufflePost($(this).data("user-book-id"));
    });

    $(document.body).on("click", ".cls-favorite-btn", function(e) {
        e.preventDefault();
        favoritePost($(this).attr("id"), $(this).data("book-id"));
    });

    // Consider moving this to a generic class later
    $(document.body).on("click", ".cls-more-options-modal-btn", function(e) {
        e.preventDefault();
        let moreOptionsModal = new bootstrap.Modal($(".more-options-modal"));
        $("#id-good-reads-element").attr("href", $(this).data("good-reads-url"));
        $("#id-amazon-element").attr("href", $(this).data("amazon-url"));
        $("#id-youtube-element").attr("href", $(this).data("youtube-url"));

        let googleBooksPreviewUrl = $(this).data("google-books-preview-url");
        if(!googleBooksPreviewUrl) {
            $(".cls-google-books-preview-btn-wrapper").hide();
        } else {
            $(".cls-google-books-preview-btn").attr("href", $(this).data("google-books-preview-url"));
        }

        moreOptionsModal.show();
    });

    $(document.body).on("click", ".cls-book-details-btn", function(e) {
        e.preventDefault();
        bookDetailsGet($(this).data("book-id"));
    });

    $(document.body).on("click", "#id-favorite-list-btn", function(e) {
        e.preventDefault();
        favoriteListsGet();
    });

    $(document.body).on("click", "#id-recent-list-btn", function(e) {
        e.preventDefault();
        recentListsGet();
    });

    $(document.body).on("click", ".cls-category-list-btn", function(e) {
        e.preventDefault();
        categoryListsGet($(this).data("category-id"));
    });
});

// Discover related

$(document).ready(function() {
    $(document.body).on("click", "#id-discover-category", function(e) {
        e.preventDefault();
        discoverCategoryPost($(this).data("category-id"));
    });

    $(document.body).on("click", "#id-discover-preferred-subcategory", function(e) {
        e.preventDefault();
        discoverCategoryPost($(this).data("preferred-subcategory-id"));
    });

    $(document.body).on("click", "#id-discover-results-subcategory", function(e) {
        e.preventDefault();
        discoverCategoryPost($(this).data("subcategory-id"));
    });

    $(document).on("click", "#id-discover-hamburger-btn", function (e) {
        e.preventDefault();
        $("#side-menu").addClass("open");
    });

    $(document).on("click", "#close-menu", function (e) {
        e.preventDefault();
        $("#side-menu").removeClass("open");
    });

    $(document.body).on("click", ".cls-category-btn", function(e) {
        e.preventDefault();
        discoverCategoryPost($(this).data("subcategory-id"));
    });

    $(document.body).on("focus", "#id-discover-search-input", function(e) {
        e.preventDefault();
        $('#id-discover-search-modal').modal('show');
    });
});

// Feedback related

$(document).ready(function() {
    $(document.body).on("input", "#id-feedback-text-area", function(e) {
        if($(this).val().trim() === '') {
            $("#id-submit-feedback-btn").addClass("disabled");
        } else {
            $("#id-submit-feedback-btn").removeClass("disabled");
        }
    });

    $(document.body).on("click", "#id-submit-feedback-btn", function(e) {
        e.preventDefault();
        feedbackPost($('#id-feedback-text-area').val().trim());
    });
});

// Rate book related

$(document).ready(function() {
    $(document.body).on("mouseover", ".rate-book--stars .fa-star", function(e) {
        let rating = $(this).data("value");
        $(".rate-book--stars .fa-star").removeClass("fa-solid selected").addClass("fa-regular");
        $(".rate-book--stars .fa-star").each(function (index) {
            if (index < rating) {
                $(this).removeClass("fa-regular").addClass("fa-solid selected");
            }
        });
    });

    $(document.body).on("click", ".rate-book--stars .fa-star", function(e) {
        let selectedRating = $(this).data("value");
        userRatingPost($(this).data("book-id"), selectedRating, null);
        $(".rate-book--modal .fa-star").removeClass("fa-solid selected").addClass("fa-regular");
        $(".rate-book--modal .fa-star").each(function (index) {
            if (index < selectedRating) {
                $(this).removeClass("fa-regular").addClass("fa-solid selected");
            }
        });

        let rateBookModalElement = document.getElementById("id-rate-book-modal");
        if (!rateBookModalElement.classList.contains("show")) {
            let rateBookModal = new bootstrap.Modal(rateBookModalElement);
            rateBookModal.show();
        }
    });

    $(document.body).on("click", "#id-submit-review-btn", function(e) {
        e.preventDefault();
        userRatingPost($(this).data("book-id"), null, $('#id-rating-text-area').val().trim());
        let rateBookModal = bootstrap.Modal.getInstance(document.getElementById("id-rate-book-modal"));
        rateBookModal.hide();
    });
});