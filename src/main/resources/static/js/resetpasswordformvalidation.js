/**
 * 
 */

     document.addEventListener("DOMContentLoaded", function () {
        const newPassword = document.getElementById("newpassword");
        const confirmPassword = document.getElementById("confirmpassword");
        const passwordError = document.getElementById("passwordError");
        const passwordMatch = document.getElementById("passwordMatch");
        const form = document.querySelector("form");

        confirmPassword.addEventListener("input", function () {
            if (confirmPassword.value === "") {
                // Hide both messages when confirm password is empty
                passwordError.style.display = "none";
                passwordMatch.style.display = "none";
            } else if (newPassword.value === confirmPassword.value) {
                // Passwords match
                passwordError.style.display = "none";
                passwordMatch.style.display = "block";
            } else {
                // Passwords do not match
                passwordError.style.display = "block";
                passwordMatch.style.display = "none";
            }
        });
        
        form.addEventListener("submit", function (event) {
        if (newPassword.value !== confirmPassword.value) {
            event.preventDefault(); // Prevent form submission
            passwordError.style.display = "block";
            passwordMatch.style.display = "none";
            alert("Passwords do not match. Please correct this before submitting.");
        }
    });
    });
