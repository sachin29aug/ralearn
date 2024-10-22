$(document).ready(function() {
    $(document.body).on("click", ".category-pill", function(e) {
        if ($(this).hasClass("selected")) {
            $(this).removeClass("selected");
        } else {
            $(this).addClass("selected");
        }
    });
});