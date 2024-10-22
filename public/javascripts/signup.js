$(document).ready(function() {
    var selectedPillCount = 0;
    $(document.body).on("click", ".category-pill", function(e) {
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
});