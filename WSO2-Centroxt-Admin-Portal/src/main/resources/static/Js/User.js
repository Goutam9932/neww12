
	var modal = document.getElementById("addUserModal");
	var btn = document.getElementById("addUserBtn");
	var span = document.getElementsByClassName("close");
	var iframe = document.getElementById("modalIframe");

	btn.onclick = function () {
		iframe.src = "http://localhost:9098/adduser"; // Set the URL of the content to be loaded
		modal.style.display = "block"; // Display the modal
	}

	for (let i = 0; i < span.length; i++) {
		span[i].onclick = function () {
			modal.style.display = "none"; // Close the modal when the close button is clicked
		}
	}

	window.onclick = function (event) {
		if (event.target == modal) {
			modal.style.display = "none"; // Close the modal when clicked outside the modal
		}
	}




 document.addEventListener('DOMContentLoaded', function() {
    const editButtons = document.querySelectorAll('.edit-btn');

    editButtons.forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.getAttribute('data-user-id');
            window.location.href = `/edit/${userId}`;
        });
    });
});


