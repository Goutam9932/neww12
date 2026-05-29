// Global state variables

let currentUser = null;

let allApplications = [];

let filteredApplications = [];

let activeCategory = 'All';

let searchQuery = '';

 

// Session Inactivity Timers

let idleTimer = null;

let warningTimer = null;

let warningCountdown = 60;

const SESSION_IDLE_LIMIT_MS = 4 * 60 * 1000; // 4 minutes until warning pops up

const WARNING_LIMIT_SECONDS = 60; // 60 seconds warning countdown

 

// DOM elements

const headerAvatar = document.getElementById("headerAvatar");

const headerUsername = document.getElementById("headerUsername");

const welcomeGreeting = document.getElementById("welcomeGreeting");

const welcomeDate = document.getElementById("welcomeDate");

const tilesContainer = document.getElementById("tilesContainer");

const filterChipsContainer = document.getElementById("filterChipsContainer");

const profileDropdown = document.getElementById("profileDropdown");

const profileMenu = document.getElementById("profileMenu");

const adminAuditSection = document.getElementById("adminAuditSection");

const auditTableBody = document.getElementById("auditTableBody");

const adminUsersSection = document.getElementById("adminUsersSection");

const usersTableBody = document.getElementById("usersTableBody");

 

// Profile Modal fields

const profileModal = document.getElementById("profileModal");

const profileFullName = document.getElementById("profileFullName");

const profileUsername = document.getElementById("profileUsername");

const profileEmail = document.getElementById("profileEmail");

const profileDepartment = document.getElementById("profileDepartment");

const profileRolesTags = document.getElementById("profileRolesTags");

 

// Timeout Modal elements

const timeoutModal = document.getElementById("timeoutModal");

const timeoutCountdown = document.getElementById("timeoutCountdown");

const timeoutProgressBar = document.getElementById("timeoutProgressBar");

 

// SVG Icon Lookup Map

const iconSVGs = {

    'users': `<svg viewBox="0 0 24 24"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>`,

    'dollar-sign': `<svg viewBox="0 0 24 24"><line x1="12" y1="1" x2="12" y2="23"></line><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path></svg>`,

    'shield': `<svg viewBox="0 0 24 24"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path></svg>`,

    'database': `<svg viewBox="0 0 24 24"><ellipse cx="12" cy="5" rx="9" ry="3"></ellipse><path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"></path><path d="M3 12c0 1.66 4 3 9 3s9-1.34 9-3"></path></svg>`,

    'book-open': `<svg viewBox="0 0 24 24"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path></svg>`,

    'default': `<svg viewBox="0 0 24 24"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><line x1="9" y1="9" x2="15" y2="15"></line><line x1="15" y1="9" x2="9" y2="15"></line></svg>`

};

 

// Initialize Application

document.addEventListener("DOMContentLoaded", () => {

    checkAuthentication();

    setupActivityListeners();

    setupDropdownCloser();

});

 

// Dropdown handler helper

function toggleProfileDropdown(e) {

    e.stopPropagation();

    profileDropdown.classList.toggle("active");

}

 

function setupDropdownCloser() {

    document.addEventListener("click", (e) => {

        if (!profileMenu.contains(e.target)) {

            profileDropdown.classList.remove("active");

        }

    });

}

 

// 1. Session check & fetch user info

async function checkAuthentication() {

    try {

        const response = await fetch("/api/auth/me");

        if (response.ok) {

            currentUser = await response.json();

            populateUserInfo();

            resetIdleTimer(); // Start tracking inactivity

            

            // Render Tiles and Admin sections

            await loadTiles();

            if (currentUser.roles.includes("ROLE_ADMIN")) {

                adminAuditSection.classList.add("active");

                adminUsersSection.classList.add("active");

                await loadAuditLogs();

                await loadUsers();

            }

        } else {

            // Unauthorized, send to login

            window.location.href = "/login";

        }

    } catch (err) {

        console.error("Authentication check failed", err);

        showToast("", "danger");

    }

}

 

// Populate user information onto panels

function populateUserInfo() {

    // Header Avatar

    const nameInitial = currentUser.fullName ? currentUser.fullName.charAt(0).toUpperCase() : '?';

    headerAvatar.textContent = nameInitial;

    

    // Header Name

    headerUsername.textContent = currentUser.fullName || currentUser.username;

    

    // Greeting Title based on hours

    const hour = new Date().getHours();

    let salutation = "Good morning";

    if (hour >= 12 && hour < 17) salutation = "Good afternoon";

    else if (hour >= 17) salutation = "Good evening";

    

    welcomeGreeting.textContent = `${salutation}, ${currentUser.fullName || currentUser.username}!`;

    

    // Current date presentation

    const dateOptions = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };

    welcomeDate.textContent = new Date().toLocaleDateString("en-US", dateOptions);

}

 

// 2. Fetch and render authorized applications

async function loadTiles() {

    try {

        const response = await fetch("/api/applications");

        if (response.ok) {

            allApplications = await response.json();

            filteredApplications = [...allApplications];

            

            renderTiles();

            renderCategoryChips();

        } else {

            showToast("Failed to fetch application tiles.", "danger");

        }

    } catch (err) {

        console.error(err);

        showToast("Error loading application tiles.", "danger");

    }

}

 

// HTML Renderer for dynamic tiles

function renderTiles() {

    tilesContainer.innerHTML = "";

    

    if (filteredApplications.length === 0) {

        tilesContainer.innerHTML = `

            <div style="grid-column: 1/-1; text-align: center; padding: 50px 20px;" class="empty-state">

                <svg fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24" style="width: 48px; height: 48px; color: var(--text-muted); margin-bottom: 15px;">

                    <circle cx="11" cy="11" r="8"></circle>

                    <line x1="21" y1="21" x2="16.65" y2="16.65"></line>

                </svg>

                <h4 style="margin-bottom: 5px;">No applications found</h4>

                <p style="font-size: 13px; color: var(--text-muted);">We couldn't find matching tiles for "${searchQuery}" or selected category.</p>

            </div>

        `;

        return;

    }

    

    filteredApplications.forEach(app => {

        const tile = document.createElement("div");

        tile.className = "tile-card glass";

        tile.onclick = () => handleTileClick(app);

        

        const svgContent = iconSVGs[app.iconName] || iconSVGs['default'];

        

        tile.innerHTML = `

            <div class="tile-glow" style="background: ${app.themeColor}"></div>

            <div class="tile-header">

                <div class="tile-icon-container" style="color: ${app.themeColor.includes('rgba') ? app.themeColor.split(',')[0].replace('linear-gradient(135deg, ', '').replace('rgba(', 'rgb(') : 'var(--primary)'}">

                    ${svgContent}

                </div>

                <span class="tile-category-tag">${app.category}</span>

            </div>

            <h4 class="tile-title">${app.name}</h4>

            <p class="tile-desc">${app.description}</p>

            <div class="tile-footer">

                <span class="tile-action-link">

                    <span>Open Application</span>

                    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">

                        <line x1="5" y1="12" x2="19" y2="12"></line>

                        <polyline points="12 5 19 12 12 19"></polyline>

                    </svg>

                </span>

            </div>

        `;

        

        tilesContainer.appendChild(tile);

    });

}

 

// Generate category chips dynamically

function renderCategoryChips() {

    // Keep 'All' chip

    filterChipsContainer.innerHTML = `<button class="filter-chip ${activeCategory === 'All' ? 'active' : ''}" onclick="filterCategory('All', this)">All Portals</button>`;

    

    // Extract unique categories

    const categories = [...new Set(allApplications.map(app => app.category))];

    

    categories.forEach(cat => {

        const chip = document.createElement("button");

        chip.className = `filter-chip ${activeCategory === cat ? 'active' : ''}`;

        chip.textContent = cat;

        chip.onclick = () => filterCategory(cat, chip);

        filterChipsContainer.appendChild(chip);

    });

}

 

// Search utility

function handleSearch(val) {

    searchQuery = val.trim().toLowerCase();

    applyFilters();

}

 

// Category filter utility

function filterCategory(cat, element) {

    activeCategory = cat;

    

    // Update active class on chips

    document.querySelectorAll(".filter-chip").forEach(c => c.classList.remove("active"));

    element.classList.add("active");

    

    applyFilters();

}

 

// Apply both search query and category filters

function applyFilters() {

    filteredApplications = allApplications.filter(app => {

        const matchesCategory = (activeCategory === 'All' || app.category === activeCategory);

        const matchesSearch = app.name.toLowerCase().includes(searchQuery) ||

                              app.description.toLowerCase().includes(searchQuery) ||

                              app.category.toLowerCase().includes(searchQuery);

        return matchesCategory && matchesSearch;

    });

    renderTiles();

}

 

// 3. Tile click logging & redirect

async function handleTileClick(app) {

    // Send audit log request to backend

    try {

        await fetch("/api/audit/log", {

            method: "POST",

            headers: { "Content-Type": "application/json" },

            body: JSON.stringify({

                applicationName: app.name,

                applicationUrl: app.url

            })

        });

        showToast(`Logged access ledger entry for "${app.name}".`, "success");

        

        // Refresh audit table if admin

        if (currentUser && currentUser.roles.includes("ROLE_ADMIN")) {

            await loadAuditLogs();

        }

    } catch (err) {

        console.error("Audit logging error", err);

    }

    

    // Perform redirection in new window

    window.open(app.url, "_blank");

}

 

// 4. Activity timers for session inactivity

function setupActivityListeners() {

    const activityEvents = ["mousemove", "keydown", "click", "scroll"];

    

    activityEvents.forEach(evt => {

        window.addEventListener(evt, () => {

            // Only reset if the warning modal is not active

            if (!timeoutModal.classList.contains("active")) {

                resetIdleTimer();

            }

        });

    });

}

 

function resetIdleTimer() {

    clearTimeout(idleTimer);

    

    idleTimer = setTimeout(() => {

        showTimeoutWarning();

    }, SESSION_IDLE_LIMIT_MS);

}

 

function showTimeoutWarning() {

    // Open modal

    timeoutModal.classList.add("active");

    

    warningCountdown = WARNING_LIMIT_SECONDS;

    timeoutCountdown.textContent = warningCountdown;

    timeoutProgressBar.style.width = "100%";

    

    clearInterval(warningTimer);

    

    // Tick countdown every second

    warningTimer = setInterval(() => {

        warningCountdown--;

        timeoutCountdown.textContent = warningCountdown;

        

        // Shrink visual bar percentage

        const pct = (warningCountdown / WARNING_LIMIT_SECONDS) * 100;

        timeoutProgressBar.style.width = `${pct}%`;

        

        if (warningCountdown <= 0) {

            clearInterval(warningTimer);

            triggerLogout(true); // Terminate session

        }

    }, 1000);

}

 

async function extendSession() {

    // Close modal, reset counters

    timeoutModal.classList.remove("active");

    clearInterval(warningTimer);

    

    // Send standard request to backend to refresh Http session

    try {

        await fetch("/api/auth/me");

        showToast("Your session has been successfully extended.", "success");

        resetIdleTimer();

    } catch (err) {

        console.error(err);

        showToast("Error extending session. Please re-authenticate.", "danger");

    }

}

 

// 5. Logout flow

/*async function triggerLogout(wasTimeout = false) {

    clearTimeout(idleTimer);

    clearInterval(warningTimer);

    

    try {

        const response = await fetch("/api/auth/logout", { method: "POST" });

        if (response.ok) {

            window.location.href = wasTimeout === true ? "/logout?reason=timeout" : "/logout";

        } else {

            window.location.href = "/logout";

        }

    } catch (err) {

        window.location.href = "/logout";

    }

}*/

 

 

async function triggerLogout(event) {

    if (event) {
        event.preventDefault();
    }

    clearTimeout(idleTimer);
    clearInterval(warningTimer);

    const form = document.createElement("form");
    form.method = "POST";
    form.action = "/api/auth/logout";

    document.body.appendChild(form);
    form.submit();
}
 

// 6. Admin Panel logs fetch

async function loadAuditLogs() {

    try {

        const response = await fetch("/api/audit/logs");

        if (response.ok) {

            const logs = await response.json();

            renderAuditTable(logs);

        }

    } catch (err) {

        console.error("Failed to load audit logs", err);

    }

}

 

function renderAuditTable(logs) {

    auditTableBody.innerHTML = "";

    

    if (logs.length === 0) {

        auditTableBody.innerHTML = `

            <tr>

                <td colspan="6" class="empty-state">

                    <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24" style="width: 32px; height: 32px; margin-bottom: 10px; color: var(--text-muted);">

                        <circle cx="12" cy="12" r="10"></circle>

                        <line x1="12" y1="16" x2="12" y2="12"></line>

                        <line x1="12" y1="8" x2="12.01" y2="8"></line>

                    </svg>

                    <div>No access logs recorded in database.</div>

                </td>

            </tr>

        `;

        return;

    }

    

    logs.forEach(log => {

        const row = document.createElement("tr");

        row.innerHTML = `

            <td style="font-weight: 700; color: var(--primary);">#${log.id}</td>

            <td style="font-weight: 600; color: var(--text-primary);">${log.username}</td>

            <td>

                <span class="role-tag" style="background: rgba(255, 255, 255, 0.05); color: var(--text-primary); font-weight: 600;">

                    ${log.applicationName}

                </span>

            </td>

            <td style="font-family: monospace; font-size: 12px; color: var(--text-secondary);">${log.applicationUrl}</td>

            <td style="font-family: monospace; font-size: 12px; color: var(--text-muted);">${log.ipAddress}</td>

            <td style="color: var(--text-secondary); font-size: 13px;">${log.accessedAt}</td>

        `;

        auditTableBody.appendChild(row);

    });

}

 

// Admin Panel users fetch

async function loadUsers() {

    try {

        const response = await fetch("/api/auth/users");

        if (response.ok) {

            const users = await response.json();

            renderUsersTable(users);

        }

    } catch (err) {

        console.error("Failed to load registered users", err);

    }

}

 

function renderUsersTable(users) {

    usersTableBody.innerHTML = "";

    

    if (users.length === 0) {

        usersTableBody.innerHTML = `

            <tr>

                <td colspan="6" class="empty-state">

                    <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24" style="width: 32px; height: 32px; margin-bottom: 10px; color: var(--text-muted);">

                        <circle cx="12" cy="12" r="10"></circle>

                        <line x1="12" y1="16" x2="12" y2="12"></line>

                        <line x1="12" y1="8" x2="12.01" y2="8"></line>

                    </svg>

                    <div>No registered users found.</div>

                </td>

            </tr>

        `;

        return;

    }

    

    users.forEach(user => {

        const row = document.createElement("tr");

        

        // Generate role tags with style class

        const roleTagsHtml = user.roles.map(role => {

            return `<span class="role-tag ${getRoleClass(role)}" style="margin-right: 5px;">${role}</span>`;

        }).join("");

        

        row.innerHTML = `

            <td style="font-weight: 700; color: var(--primary);">#${user.id || 'N/A'}</td>

            <td style="font-weight: 600; color: var(--text-primary);">${user.fullName || 'N/A'}</td>

            <td style="font-weight: 600; color: var(--text-secondary);">${user.username}</td>

            <td style="color: var(--text-secondary);">${user.email || 'N/A'}</td>

            <td>

                <span class="role-tag" style="background: rgba(255, 255, 255, 0.05); color: var(--text-primary); font-weight: 600;">

                    ${user.department || 'N/A'}

                </span>

            </td>

            <td>${roleTagsHtml}</td>

        `;

        usersTableBody.appendChild(row);

    });

}

 

// 7. Profile drawer modal handlers

function openProfileModal(e) {

    if (e) e.preventDefault();

    profileDropdown.classList.remove("active");

    

    // Set field values

    profileFullName.textContent = currentUser.fullName || currentUser.username;

    profileUsername.textContent = currentUser.username;

    profileEmail.textContent = currentUser.email || 'N/A';

    profileDepartment.textContent = currentUser.department || 'N/A';

    

    // Set roles tags

    profileRolesTags.innerHTML = "";

    currentUser.roles.forEach(role => {

        const tag = document.createElement("span");

        tag.className = `role-tag ${getRoleClass(role)}`;

        tag.textContent = role;

        profileRolesTags.appendChild(tag);

    });

    

    profileModal.classList.add("active");

}

 

function getRoleClass(role) {

    switch (role) {

        case 'ROLE_ADMIN': return 'role-admin';

        case 'ROLE_HR': return 'role-hr';

        case 'ROLE_FINANCE': return 'role-finance';

        case 'ROLE_IT': return 'role-it';

        default: return 'role-user';

    }

}

 

function closeProfileModal() {

    profileModal.classList.remove("active");

}

 

    // --------------------------------------------------------------------

    // SSO login – simple redirect to Spring Security’s OIDC endpoint

    // --------------------------------------------------------------------

    function handleSSOLogin() {

        // Spring Security will start the OIDC Authorization Code flow.

        window.location.href = "/oauth2/authorization/wso2";

    }

 

 

// 8. Toast popup system helper

function showToast(message, type = "info") {

    const container = document.getElementById("toastContainer");

    

    const toast = document.createElement("div");

    toast.className = `toast glass ${type}`;

    

    // Choose icon

    let icon = `<svg class="toast-icon" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>`;

    if (type === 'success') {

        icon = `<svg class="toast-icon" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24"><polyline points="20 6 9 17 4 12"></polyline></svg>`;

    } else if (type === 'danger') {

        icon = `<svg class="toast-icon" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>`;

    }

    

    toast.innerHTML = `

        ${icon}

        <span>${message}</span>

    `;

    

    container.appendChild(toast);

    

    // Auto-remove after 4 seconds

    setTimeout(() => {

        toast.style.animation = "toastIn 0.3s reverse forwards";

        setTimeout(() => toast.remove(), 300);

    }, 4000);

}