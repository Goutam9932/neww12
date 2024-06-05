
			// Function to generate dynamic options based on user data
			function toggleUserMenu() {
				document.getElementById('userMenu').classList.toggle('show');
			}


			function toggleUserMenu() {
				var userMenu = document.getElementById('userMenu');
				userMenu.classList.toggle('show');
			}
		
//
		function toggleDropdown() {
			var dropdown = document.getElementById("dropdown");
			dropdown.style.display = (dropdown.style.display === "none" || dropdown.style.display === "") ? "block" : "none";
		}
		//
		function toggleDropdown() {
			var dropdownContent = document.getElementById("dropdownContent");
			dropdownContent.style.display = (dropdownContent.style.display === "none" || dropdownContent.style.display === "") ? "block" : "none";
		}
		//
		function EventDropdown() {
			var dropdownContent1 = document.getElementById("dropdownContent1");
			dropdownContent1.style.display = (dropdownContent1.style.display === "none" || dropdownContent1.style.display === "") ? "block" : "none";
		}
		//
		document.getElementById('toggle-btn').addEventListener('click', function () {
		var sidebar = document.getElementById('sidebar');
		var mainContent = document.querySelector('.main-content');
		sidebar.classList.toggle('collapsed');
		
	});