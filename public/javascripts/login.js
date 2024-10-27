$(document).ready(function() {
    $(document.body).on("click", "#id-login-btn", function(e) {
        e.preventDefault();
        loginPost();
    });
});