// --- Tab switching -------------------------------------------------------

document.querySelectorAll(".tab-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
        const target = btn.dataset.tab;
        document.querySelectorAll(".tab-btn").forEach((b) => b.classList.toggle("active", b === btn));
        document.querySelectorAll(".tab-panel").forEach((p) => p.classList.toggle("active", p.id === target));
    });
});

// --- Browse tab ----------------------------------------------------------

const browseContent = document.getElementById("browseContent");
let currentResource = "users";

document.querySelectorAll(".resource-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
        document.querySelectorAll(".resource-btn").forEach((b) => b.classList.toggle("active", b === btn));
        currentResource = btn.dataset.resource;
        loadResource();
    });
});

document.getElementById("reloadBtn").addEventListener("click", loadResource);

async function loadResource() {
    browseContent.innerHTML = '<p class="status">Loading…</p>';
    try {
        const response = await fetch(`/api/${currentResource}`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const data = await response.json();
        if (data.length === 0) {
            browseContent.innerHTML = '<p class="status">No records.</p>';
            return;
        }
        browseContent.innerHTML = renderers[currentResource](data);
    } catch (err) {
        browseContent.innerHTML = `<p class="status" style="color:var(--danger)">Error: ${escapeHtml(err.message)}</p>`;
    }
}

const renderers = {
    users: (users) => `
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Username</th>
                    <th>Description</th>
                    <th>Departments</th>
                    <th>Salaries</th>
                </tr>
            </thead>
            <tbody>
                ${users.map((u) => `
                    <tr>
                        <td>${u.id}</td>
                        <td>${escapeHtml(u.firstName)} ${escapeHtml(u.lastName)}</td>
                        <td><code>${escapeHtml(u.userName)}</code></td>
                        <td>${escapeHtml(u.description ?? "")}</td>
                        <td>
                            <div class="badges">
                                ${u.departments.map((d) => `<span class="badge accent">${escapeHtml(d.departmentName)}</span>`).join("") || '<span class="muted">—</span>'}
                            </div>
                        </td>
                        <td><span class="badge">${u.salaries.length}</span></td>
                    </tr>
                `).join("")}
            </tbody>
        </table>
    `,

    departments: (departments) => `
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Address</th>
                    <th>Mail</th>
                    <th>Phone</th>
                    <th>Members</th>
                </tr>
            </thead>
            <tbody>
                ${departments.map((d) => `
                    <tr>
                        <td>${d.id}</td>
                        <td><strong>${escapeHtml(d.departmentName)}</strong></td>
                        <td>${escapeHtml(d.address)}</td>
                        <td><a href="mailto:${escapeHtml(d.mail)}">${escapeHtml(d.mail)}</a></td>
                        <td><code>${escapeHtml(d.phone)}</code></td>
                        <td><span class="badge">${d.userCount}</span></td>
                    </tr>
                `).join("")}
            </tbody>
        </table>
    `,

    salaries: (salaries) => `
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Date</th>
                    <th>User</th>
                    <th>Bonus</th>
                    <th class="amount">Amount</th>
                </tr>
            </thead>
            <tbody>
                ${salaries.map((s) => `
                    <tr>
                        <td>${s.id}</td>
                        <td><code>${s.dateOfSalary}</code></td>
                        <td>${escapeHtml(s.user.firstName)} ${escapeHtml(s.user.lastName)}</td>
                        <td>${s.bonus ? `<span class="badge accent">${escapeHtml(s.bonus)}</span>` : '<span class="muted">—</span>'}</td>
                        <td class="amount">${formatMoney(s.salary)}</td>
                    </tr>
                `).join("")}
            </tbody>
        </table>
    `,
};

// --- Queries tab ---------------------------------------------------------

document.querySelectorAll(".query-form").forEach((form) => {
    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        const result = form.parentElement.querySelector(".query-result");
        const params = new URLSearchParams(new FormData(form));
        const url = `${form.dataset.endpoint}?${params}`;
        result.className = "query-result";
        result.textContent = "Loading…";
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error(`HTTP ${response.status} — ${await response.text()}`);
            const data = await response.json();
            result.classList.add("has-result");
            result.innerHTML = `
                <div class="result-label">Total</div>
                <div class="result-value">${formatMoney(data.total)}</div>
            `;
        } catch (err) {
            result.classList.add("has-error");
            result.textContent = `Error: ${err.message}`;
        }
    });
});

// --- Helpers -------------------------------------------------------------

function formatMoney(value) {
    const n = Number(value);
    if (Number.isNaN(n)) return value;
    return new Intl.NumberFormat("en-US", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
    }).format(n);
}

function escapeHtml(str) {
    if (str == null) return "";
    return String(str)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

// --- Initial load --------------------------------------------------------

loadResource();
