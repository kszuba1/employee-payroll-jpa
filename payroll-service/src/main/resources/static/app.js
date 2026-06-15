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
const filterBar = document.getElementById("filterBar");
const resultCount = document.getElementById("resultCount");

let currentResource = "users";
let currentData = [];
const filterState = { users: {}, departments: {}, salaries: {} };

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
    resultCount.textContent = "";
    try {
        const response = await fetch(`/api/${currentResource}`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        currentData = await response.json();
        renderFilters();
        applyFiltersAndRender();
    } catch (err) {
        browseContent.innerHTML = `<p class="status" style="color:var(--danger)">Error: ${escapeHtml(err.message)}</p>`;
        filterBar.innerHTML = "";
    }
}

// --- Filter configs (per resource) ---------------------------------------

const filterConfigs = {
    users: {
        fields: () => [
            { name: "name", label: "Name", type: "text", placeholder: "first or last…" },
            {
                name: "department",
                label: "Department",
                type: "select",
                options: uniqueValues(currentData.flatMap((u) => u.departmentNames)),
            },
        ],
        match: (item, f) => {
            if (f.name && !`${item.firstName} ${item.lastName}`.toLowerCase().includes(f.name.toLowerCase())) return false;
            if (f.department && !item.departmentNames.includes(f.department)) return false;
            return true;
        },
    },

    departments: {
        fields: () => [
            { name: "name", label: "Name", type: "text", placeholder: "department name…" },
        ],
        match: (item, f) => {
            if (f.name && !item.departmentName.toLowerCase().includes(f.name.toLowerCase())) return false;
            return true;
        },
    },

    salaries: {
        fields: () => [
            { name: "year", label: "Year", type: "number", placeholder: "2025" },
            { name: "month", label: "Month", type: "number", placeholder: "1-12", min: 1, max: 12 },
            { name: "user", label: "User", type: "text", placeholder: "first or last…" },
            {
                name: "department",
                label: "Department",
                type: "select",
                options: uniqueValues(currentData.flatMap((s) => s.departmentNames)),
            },
        ],
        match: (item, f) => {
            const date = new Date(item.dateOfSalary);
            if (f.year && date.getUTCFullYear() !== Number(f.year)) return false;
            if (f.month && (date.getUTCMonth() + 1) !== Number(f.month)) return false;
            if (f.user && !`${item.user.firstName} ${item.user.lastName}`.toLowerCase().includes(f.user.toLowerCase())) return false;
            if (f.department && !item.departmentNames.includes(f.department)) return false;
            return true;
        },
    },
};

function renderFilters() {
    const config = filterConfigs[currentResource];
    const fields = config.fields();
    const state = filterState[currentResource];

    filterBar.innerHTML = fields.map((f) => {
        const value = state[f.name] ?? "";
        if (f.type === "select") {
            return `
                <label>
                    ${escapeHtml(f.label)}
                    <select data-filter="${f.name}">
                        <option value="">All</option>
                        ${f.options.map((o) => `<option value="${escapeHtml(o)}" ${o === value ? "selected" : ""}>${escapeHtml(o)}</option>`).join("")}
                    </select>
                </label>
            `;
        }
        const extra = [
            f.placeholder ? `placeholder="${escapeHtml(f.placeholder)}"` : "",
            f.min != null ? `min="${f.min}"` : "",
            f.max != null ? `max="${f.max}"` : "",
        ].filter(Boolean).join(" ");
        return `
            <label>
                ${escapeHtml(f.label)}
                <input type="${f.type}" data-filter="${f.name}" value="${escapeHtml(value)}" ${extra}>
            </label>
        `;
    }).join("") + `<button type="button" class="clear-btn" id="clearFiltersBtn">Clear</button>`;

    filterBar.querySelectorAll("[data-filter]").forEach((el) => {
        el.addEventListener("input", (e) => {
            state[e.target.dataset.filter] = e.target.value;
            applyFiltersAndRender();
        });
        el.addEventListener("change", (e) => {
            state[e.target.dataset.filter] = e.target.value;
            applyFiltersAndRender();
        });
    });

    document.getElementById("clearFiltersBtn").addEventListener("click", () => {
        filterState[currentResource] = {};
        renderFilters();
        applyFiltersAndRender();
    });
}

function applyFiltersAndRender() {
    const config = filterConfigs[currentResource];
    const state = filterState[currentResource];
    const filtered = currentData.filter((item) => config.match(item, state));

    if (filtered.length === 0) {
        browseContent.innerHTML = currentData.length === 0
            ? '<p class="status">No records.</p>'
            : '<p class="status">No matches for current filters.</p>';
    } else {
        browseContent.innerHTML = renderers[currentResource](filtered);
    }

    if (currentData.length === 0) {
        resultCount.textContent = "";
    } else if (filtered.length === currentData.length) {
        resultCount.textContent = `${filtered.length} record${filtered.length === 1 ? "" : "s"}`;
    } else {
        resultCount.textContent = `${filtered.length} of ${currentData.length} records`;
    }
}

// --- Renderers (per resource) --------------------------------------------

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
                                ${u.departmentNames.map((d) => `<span class="badge accent">${escapeHtml(d)}</span>`).join("") || '<span class="muted">—</span>'}
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
                    <th>Departments</th>
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
                        <td>
                            <div class="badges">
                                ${s.departmentNames.map((d) => `<span class="badge accent">${escapeHtml(d)}</span>`).join("") || '<span class="muted">—</span>'}
                            </div>
                        </td>
                        <td>${s.bonus != null ? `<span class="badge accent">${formatMoney(s.bonus)}</span>` : '<span class="muted">—</span>'}</td>
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

function uniqueValues(arr) {
    return [...new Set(arr)].sort();
}

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
