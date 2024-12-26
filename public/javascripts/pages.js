// Signup - related

$(document).ready(function() {
    // Signup - Interests and Categories

    var selectedInterestsCount = 0;
    $(document.body).on("click", "#interests-page .option-pill", function(e) {
        e.preventDefault();
        if ($(this).hasClass("selected")) {
            $(this).removeClass("selected");
            selectedInterestsCount++;
        } else {
            $(this).addClass("selected");
            selectedInterestsCount--;
        }

        if(selectedInterestsCount == 0) {
            $("#interests-page .signup--continue-btn").addClass("disabled");
        } else {
            $("#interests-page .signup--continue-btn").removeClass("disabled");
        }
    });

    var selectedCategoriesCount = 0;
    $(document.body).on("click", "#categories-page .option-pill", function(e) {
        e.preventDefault();
        if ($(this).hasClass("selected")) {
            $(this).removeClass("selected");
            selectedCategoriesCount++;
        } else {
            $(this).addClass("selected");
            selectedCategoriesCount--;
        }

        if(selectedCategoriesCount == 0) {
            $("#categories-page .signup--continue-btn").addClass("disabled");
        } else {
            $("#categories-page .signup--continue-btn").removeClass("disabled");
        }
    });

    $(document.body).on("click", "#interests-page .signup--continue-btn", function(e) {
        e.preventDefault();
        $("#interests-page").hide();
        $("#categories-page").show();
        $(".option-pills .option-pill").each(function() {
            let interestCategoriesDivId = $(this).data("interest-categories-div-id");
            if ($(this).hasClass("selected")) {
                $("#" + interestCategoriesDivId).show();
            } else {
                $("#" + interestCategoriesDivId).hide();
                $("#" + interestCategoriesDivId + " .option-pill").removeClass("selected");
            }
        });
    });

    $(document.body).on("click", "#categories-page .signup--continue-btn", function(e) {
        e.preventDefault();
        $("#categories-page").hide();
        $("#email-password-page").show();
        $("#id-signup-firstname").focus();
    });

    $(document.body).on("click", "#categories-page .signup--back-btn", function(e) {
        e.preventDefault();
        $("#categories-page").hide();
        $("#interests-page").show();
    });

    // Signup - Email password

    var firstNameValidated = false;
    var emailValidated = false;
    var passwordValidated = false;
    $('#id-signup-firstname').blur(function () {
        let firstName = $(this).val();
        if (firstName === "") {
            firstNameValidated = false;
            displayFieldErrorMessage($("#id-signup-firstname-error"), "First Name is required");
            $("#id-signup-email-password-continue-btn").addClass("disabled");
        } else {
            firstNameValidated = true;
            hideFieldErrorMessage($("#id-signup-firstname-error"));
            if(emailValidated && passwordValidated) {
                $("#id-signup-email-password-continue-btn").removeClass("disabled");
            }
        }
    });

    $('#id-signup-email').blur(function () {
        let email = $(this).val();
        if (email === "") {
            emailValidated = false;
            displayFieldErrorMessage($("#id-signup-email-error"), "Email is required");
            $("#id-signup-email-password-continue-btn").addClass("disabled");
        } else if (!isValidEmail(email)) {
            emailValidated = false;
            displayFieldErrorMessage($("#id-signup-email-error"), "Please enter a valid email address");
            $("#id-signup-email-password-continue-btn").addClass("disabled");
        } else {
            emailValidated = true;
            hideFieldErrorMessage($("#id-signup-email-error"));
         	if(passwordValidated && firstNameValidated) {
                $("#id-signup-email-password-continue-btn").removeClass("disabled");
            }
         }
    });

    $('#id-signup-password').blur(function () {
        let password = $(this).val();
        if (password === "") {
            passwordValidated = false;
            displayFieldErrorMessage($("#id-signup-password-error"), "Password is required");
            $("#id-signup-email-password-continue-btn").addClass("disabled");
        } else if (password.trim().length < 8) {
            passwordValidated = false;
            displayFieldErrorMessage($("#id-signup-password-error"), "Password must be at least 8 characters long");
            $("#id-signup-email-password-continue-btn").addClass("disabled");
        } else {
            passwordValidated = true;
            hideFieldErrorMessage($("#id-signup-password-error"));
            if(firstNameValidated && emailValidated) {
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
        let categoryIds = [];
        $('#categories-page .option-pill.selected').each(function() {
            let categoryId = $(this).data('category-id');
            categoryIds.push(categoryId);
        });
        signupLoginPost(categoryIds);
    });

    $(document.body).on("click", "#email-password-page .signup--back-btn", function(e) {
        e.preventDefault();
        $("#email-password-page").hide();
        $("#categories-page").show();
    });

    // Signup - Welcome

    $(document.body).on("click", "#welcome-page .signup--continue-btn", function(e) {
        e.preventDefault();
        homeGet();
    });
});

// Login related

$(document).ready(function() {
    $(document.body).on("click", "#id-login-btn", function(e) {
        e.preventDefault();
        let email = $("#id-login-email").val().trim();
        let password = $("#id-login-password").val().trim();;

        if (email === "") {
            displayErrorMessage("Please enter your email address.");
            return;
        } else if(password === "") {
            displayErrorMessage("Please enter your password.");
            return;
        } else if(!isValidEmail(email)) {
            displayErrorMessage("Please enter a valid email address.");
            return;
        }

        loginPost();
    });

    $(document.body).on("click", "#id-forgot-password-submit-btn", function(e) {
        e.preventDefault();
        let email = $("#id-forgot-password-email").val().trim();
        if (email === "") {
            displayErrorMessage("Please enter your email address.");
            return;
        } else if(!isValidEmail(email)) {
            displayErrorMessage("Please enter a valid email address.");
            return;
        }

        forgotPasswordPost(email);
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
});

// Discover - Search related

$(document).ready(function () {
    $(document.body).on("focus", "#id-discover-search-input", function(e) {
        e.preventDefault();
        $('#id-discover-search-modal').modal('show');
        $("#id-discover-search-modal-input").focus();
    });

    $(document.body).on("input", "#id-discover-search-modal-input", function(e) {
        const searchTerm = $(this).val().trim();
        if (searchTerm.length > 2) {
          bookSearchPost(searchTerm, "id-book-search-results-target");
        }
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