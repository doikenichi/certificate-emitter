const interval = 1000;  // 1000 = 1 second, 3000 = 3 seconds
$(document).ready(function () {
    $("#cert-btn").click(function (event) {
        // $("#cert-btn").prop("disabled", true);
        getConsole();
        getProgress();
    });

});
function getConsole() {
    $.ajax({
        type: "GET",
        url: "http://localhost:8080/messages",
        cache: false,
        success: function (data) {
            $("#logsaqui").text(data);
            // console.log(data);
        },
        complete: function (data) {
            // Schedule the next
            setTimeout(getConsole, interval);
        }
    });
}
function getProgress() {
    $.ajax({
        type: "GET",
        url: "http://localhost:8080/current-progress",
        cache: false,
        success: function (data) {
            $("#progressbar").width(data + "%");
            $("#progressbar").attr("aria-valuenow", data);
            $("#progressbar").text(data + "%");
            // console.log(data);
        },
        complete: function (data) {
            // Schedule the next
            setTimeout(getProgress, interval);
        }
    });
}