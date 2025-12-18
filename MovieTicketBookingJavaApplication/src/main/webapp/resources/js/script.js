/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */

let slides = document.querySelectorAll(".slide");
let dots = document.querySelectorAll(".dot");
let current = 0;

function showSlide(index) {
    slides.forEach((slide, i) => {
        slide.classList.remove("active");
        dots[i].classList.remove("active");
    });
    slides[index].classList.add("active");
    dots[index].classList.add("active");
}

dots.forEach((dot, i) => {
    dot.addEventListener("click", () => {
        current = i;
        showSlide(current);
    });
});

setInterval(() => {
    current = (current + 1) % slides.length;
    showSlide(current);
}, 5000);

function toggleLogoutMenu() {
    const box = document.getElementById("logoutBox");
    box.style.display = (box.style.display === "block") ? "none" : "block";
}

document.addEventListener("click", function (e) {
    const box = document.getElementById("logoutBox");
    const icon = document.querySelector(".user-icon");

    if (!icon.contains(e.target)) {
        box.style.display = "none";
    }
});

// In script.js
// In script.js
function confirmLogout(event) {
    event.preventDefault();

    Swal.fire({
        title: "Are you sure?",
        text: "Do you want to logout?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Yes, Logout"
    }).then((result) => {
        if (result.isConfirmed) {
            // *** MODIFIED: Use the correct JSF client ID format ***
            document.getElementById("logoutForm:realLogout").click();
        }
    });
}

function checkStrength(password) {
    const bar = document.getElementById("strength-bar");
    const text = document.getElementById("strength-text");

    let strength = 0;

    if (password.length >= 6)
        strength++;        // length rule
    if (/[A-Z]/.test(password))
        strength++;      // uppercase
    if (/[a-z]/.test(password))
        strength++;      // lowercase
    if (/[0-9]/.test(password))
        strength++;      // numbers
    if (/[@$!%*?&#]/.test(password))
        strength++; // special char

    if (strength <= 1) {
        bar.style.width = "33%";
        bar.style.background = "red";
        text.innerHTML = "Weak";
        text.style.color = "red";

    } else if (strength <= 3) {
        bar.style.width = "66%";
        bar.style.background = "orange";
        text.innerHTML = "Medium";
        text.style.color = "orange";

    } else {
        bar.style.width = "100%";
        bar.style.background = "green";
        text.innerHTML = "Strong";
        text.style.color = "green";
    }
}