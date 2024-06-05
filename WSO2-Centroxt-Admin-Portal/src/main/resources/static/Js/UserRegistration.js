document.addEventListener("DOMContentLoaded", function () {
    const nextButtons = document.querySelectorAll(".btn-next");
    const prevButtons = document.querySelectorAll(".btn-previous");
    const formSteps = document.querySelectorAll(".form-step");
    const stepIcons = document.querySelectorAll(".step-icon");
    const loading = document.getElementById("loading");

    let currentStep = 0;

    nextButtons.forEach(button => {
        button.addEventListener("click", () => {
            const currentFormStep = formSteps[currentStep];
            const requiredFields = currentFormStep.querySelectorAll('[required]');

            let allFilled = true;
            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    allFilled = false;
                    field.style.borderColor = 'red'; // Highlight the empty field
                    field.setCustomValidity('Please fill out this field.');
                    field.reportValidity();
                } else {
                    field.style.borderColor = ''; // Remove highlight if filled
                    field.setCustomValidity('');
                }
            });

            if (allFilled) {
                if (currentStep < formSteps.length - 1) {
                    formSteps[currentStep].classList.remove("form-step-active");
                    stepIcons[currentStep].classList.remove("active");
                    currentStep++;
                    showLoading(() => {
                        formSteps[currentStep].classList.add("form-step-active");
                        stepIcons[currentStep].classList.add("active");
                    });
                }
            } else {
                alert('Please fill in all required fields marked with *.');
            }
        });
    });

    prevButtons.forEach(button => {
        button.addEventListener("click", () => {
            if (currentStep > 0) {
                formSteps[currentStep].classList.remove("form-step-active");
                stepIcons[currentStep].classList.remove("active");
                currentStep--;
                showLoading(() => {
                    formSteps[currentStep].classList.add("form-step-active");
                    stepIcons[currentStep].classList.add("active");
                });
            }
        });
    });

    stepIcons.forEach((icon, index) => {
        icon.addEventListener("click", () => {
            if (index !== currentStep) {
                formSteps[currentStep].classList.remove("form-step-active");
                stepIcons[currentStep].classList.remove("active");
                currentStep = index;
                showLoading(() => {
                    formSteps[currentStep].classList.add("form-step-active");
                    stepIcons[currentStep].classList.add("active");
                });
            }
        });
    });

    function showLoading(callback) {
        loading.classList.add("active");
        setTimeout(() => {
            loading.classList.remove("active");
            callback();
        }, 1000); // simulate a loading time of 1 second
    }
});

function generatePassword() {
    fetch('http://localhost:9098/generate')
        .then(response => response.text())
        .then(password => {
            document.getElementById('password').value = password;
        })
        .catch(error => console.error('Error generating password:', error));
}

function togglePasswordVisibility() {
    const passwordField = document.getElementById('password');
    const toggleIcon = document.querySelector('.toggle-password');
    if (passwordField.type === 'password') {
        passwordField.type = 'text';
        toggleIcon.classList.add('fa-eye');
        toggleIcon.classList.remove('fa-eye-slash');
    } else {
        passwordField.type = 'password';
        toggleIcon.classList.add('fa-eye');
        toggleIcon.classList.remove('fa-eye-slash');
    }
}
