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
        loginPost();
    });
});