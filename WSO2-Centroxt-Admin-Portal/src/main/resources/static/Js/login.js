document.getElementById('togglePassword').addEventListener('click', function() {
    var passwordInput = document.getElementById('password');
    var passwordVisibilityMark = document.getElementById('passwordVisibilityMark');
    
    if (passwordInput.type === 'password') {
        passwordInput.type = 'text'; // Show password
        passwordVisibilityMark.textContent = '✓'; // Display mark
    } else {
        passwordInput.type = 'password'; // Hide password
        passwordVisibilityMark.textContent = ''; // Remove mark
    }
});
