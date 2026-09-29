// ===== HRGenius frontend: plain JavaScript, no frameworks =====
let auth = null;      // { header: 'Basic ...', user: {...} }
let current = null;   // the view (function) currently on screen

const $ = s => document.querySelector(s);
const esc = v => String(v ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
const money = v => '₹' + Number(v ?? 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
const empName = (emps, id) => { const e = emps.find(x => x.id === id); return e ? e.firstName + ' ' + e.lastName : '#' + id; };
const options = (list, valueFn, labelFn) => list.map(x => `<option value="${valueFn(x)}">${esc(labelFn(x))}</option>`).join('');

// ---------- talking to the backend ----------
async function api(path, method = 'GET', body = null) {
  const opts = { method, headers: { 'Authorization': auth.header } };
  if (body) { opts.headers['Content-Type'] = 'application/json'; opts.body = JSON.stringify(body); }
  const res = await fetch(path, opts);
  if (res.status === 401) { logout(); throw new Error('Session expired, please log in again'); }
  const text = await res.text();
  let data = null;
  try { data = text ? JSON.parse(text) : null; } catch (e) { /* not JSON */ }
  if (!res.ok) throw new Error((data && data.message) || 'Something went wrong (' + res.status + ')');
  return data;
}

// ---------- login / logout ----------
async function login() {
  const u = $('#username').value.trim();
  const p = $('#password').value;
  const header = 'Basic ' + btoa(u + ':' + p);
  try {
    const res = await fetch('/api/auth/me', { headers: { 'Authorization': header } });
    if (!res.ok) { $('#loginError').textContent = 'Wrong username or password'; return; }
    auth = { header, user: await res.json() };
    sessionStorage.setItem('auth', JSON.stringify(auth));
    startApp();
  } catch (e) {
    $('#loginError').textContent = 'Cannot reach the server';
  }
}

function logout() {
  sessionStorage.removeItem('auth');
  auth = null;
  $('#app').classList.add('hidden');
  $('#loginBox').classList.remove('hidden');
  $('#password').value = '';
}

// ---------- menus (depends on role) ----------
const STAFF_MENU = [
  ['Dashboard', dashboard], ['Employees', employees], ['Departments', departments],
  ['Recruitment', recruitment], ['Attendance', attendanceAll], ['Leaves', leavesAll],
  ['Payroll', payrollAll], ['Performance', reviewsAll]
];
const MY_MENU = [
  ['My Profile', myProfile], ['My Attendance', myAttendance], ['My Leaves', myLeaves],
  ['My Payslips', myPayslips], ['My Reviews', myReviews]
];

let MENU = [];
function startApp() {
  $('#loginBox').classList.add('hidden');
  $('#app').classList.remove('hidden');
  const u = auth.user;
  $('#who').textContent = u.name + ' (' + u.role + ')';
  MENU = (u.role === 'EMPLOYEE') ? MY_MENU : [...STAFF_MENU, ...MY_MENU];
  $('#nav').innerHTML = MENU.map((m, i) => `<button id="nav${i}" onclick="go(${i})">${m[0]}</button>`).join('');
  go(0);
}

function go(i) {
  document.querySelectorAll('#nav button').forEach(b => b.classList.remove('active'));
  $('#nav' + i).classList.add('active');
  current = MENU[i][1];
  run(current);
}

async function run(fn) {
  try { await fn(); } catch (e) { show('<p class="error">' + esc(e.message) + '</p>'); }
}

// do something, show a message if it fails, then refresh the current screen
async function act(fn, okMessage) {
  try { await fn(); if (okMessage) alert(okMessage); } catch (e) { alert(e.message); }
  run(current);
}

function show(html) { $('#content').innerHTML = html; }

function table(cols, rows) {
  if (!rows.length) return '<p class="muted">Nothing here yet.</p>';
  return '<table><thead><tr>' + cols.map(c => `<th>${c[0]}</th>`).join('') + '</tr></thead><tbody>' +
    rows.map(r => '<tr>' + cols.map(c => `<td>${c[1](r)}</td>`).join('') + '</tr>').join('') +
    '</tbody></table>';
}

// ================= DASHBOARD =================
function bars(map) {
  const entries = Object.entries(map || {});
  if (!entries.length) return '<p class="muted">No data yet.</p>';
  const max = Math.max(1, ...entries.map(e => e[1]));
  return entries.map(([k, v]) => `<div class="bar"><span>${esc(k)}</span><div style="width:${v / max * 300}px">${v}</div></div>`).join('');
}
const card = (label, value) => `<div class="card"><div class="n">${value}</div>${label}</div>`;

async function dashboard() {
  const a = await api('/api/analytics');
  show('<h2>Dashboard</h2><div class="cards">' +
    card('Active employees', a.totalEmployees) + card('Open jobs', a.openJobs) +
    card('Pending leaves', a.pendingLeaves) + card('Candidates', a.totalCandidates) +
    card('Latest payroll total', money(a.lastPayrollTotal)) + '</div>' +
    '<h3>Headcount by department</h3>' + bars(a.headcountByDepartment) +
    '<h3>Candidates by stage</h3>' + bars(a.candidatesByStage));
}

// ================= EMPLOYEES =================
async function employees() {
  const [emps, deps] = await Promise.all([api('/api/employees'), api('/api/departments')]);
  const depName = id => (deps.find(d => d.id === id) || {}).name || '-';
  show(`<h2>Employees</h2>
    <div class="form">
      <input id="e_first" placeholder="First name"><input id="e_last" placeholder="Last name">
      <input id="e_email" placeholder="Email"><input id="e_phone" placeholder="Phone">
      <input id="e_title" placeholder="Job title">
      <select id="e_dept">${options(deps, d => d.id, d => d.name)}</select>
      <input id="e_salary" type="number" placeholder="Monthly basic salary">
      <input id="e_join" type="date">
      <button onclick="addEmployee()">Add employee</button>
    </div>
    <p class="muted small">A login is created automatically: username = the part of the email before @, password = welcome123</p>` +
    table([
      ['ID', e => e.id], ['Name', e => esc(e.firstName + ' ' + e.lastName)], ['Email', e => esc(e.email)],
      ['Title', e => esc(e.jobTitle)], ['Department', e => esc(depName(e.departmentId))],
      ['Salary', e => money(e.salary)], ['Joined', e => esc(e.joinDate)],
      ['Status', e => `<span class="tag">${esc(e.status)}</span>`],
      ['', e => e.status === 'ACTIVE' ? `<button class="small-btn danger" onclick="deactivate(${e.id})">Deactivate</button>` : '']
    ], emps));
}
function addEmployee() {
  const body = {
    firstName: $('#e_first').value, lastName: $('#e_last').value, email: $('#e_email').value,
    phone: $('#e_phone').value, jobTitle: $('#e_title').value,
    departmentId: Number($('#e_dept').value) || null,
    salary: Number($('#e_salary').value) || null, joinDate: $('#e_join').value || null
  };
  act(() => api('/api/employees', 'POST', body), 'Employee added');
}
function deactivate(id) {
  if (confirm('Mark this employee as inactive?')) act(() => api('/api/employees/' + id, 'DELETE'));
}

// ================= DEPARTMENTS =================
async function departments() {
  const deps = await api('/api/departments');
  show(`<h2>Departments</h2>
    <div class="form"><input id="d_name" placeholder="Department name"><button onclick="addDept()">Add</button></div>` +
    table([['ID', d => d.id], ['Name', d => esc(d.name)]], deps));
}
function addDept() {
  act(() => api('/api/departments', 'POST', { name: $('#d_name').value }));
}

// ================= RECRUITMENT =================
async function recruitment() {
  const [jobs, cands, deps] = await Promise.all([api('/api/jobs'), api('/api/candidates'), api('/api/departments')]);
  const jobTitle = id => (jobs.find(j => j.id === id) || {}).title || '#' + id;
  const depName = id => (deps.find(d => d.id === id) || {}).name || '-';
  const stages = ['APPLIED', 'INTERVIEW', 'OFFERED', 'REJECTED'];
  show(`<h2>Recruitment</h2><h3>Job openings</h3>
    <div class="form">
      <input id="j_title" placeholder="Job title">
      <select id="j_dept">${options(deps, d => d.id, d => d.name)}</select>
      <input id="j_desc" placeholder="Description" size="40">
      <button onclick="addJob()">Post job</button>
    </div>` +
    table([
      ['ID', j => j.id], ['Title', j => esc(j.title)], ['Department', j => esc(depName(j.departmentId))],
      ['Status', j => `<span class="tag">${esc(j.status)}</span>`],
      ['', j => `<button class="small-btn" onclick="toggleJob(${j.id}, '${j.status === 'OPEN' ? 'CLOSED' : 'OPEN'}')">${j.status === 'OPEN' ? 'Close' : 'Reopen'}</button>`]
    ], jobs) +
    `<h3>Candidates</h3>
    <div class="form">
      <select id="c_job">${options(jobs.filter(j => j.status === 'OPEN'), j => j.id, j => j.title)}</select>
      <input id="c_name" placeholder="Candidate name"><input id="c_email" placeholder="Candidate email">
      <button onclick="addCandidate()">Add candidate</button>
    </div>` +
    table([
      ['Name', c => esc(c.name)], ['Email', c => esc(c.email)], ['Applied for', c => esc(jobTitle(c.jobId))],
      ['Stage', c => c.stage === 'HIRED' ? '<span class="tag">HIRED</span>' :
        `<select onchange="setStage(${c.id}, this.value)">${stages.map(s => `<option ${s === c.stage ? 'selected' : ''}>${s}</option>`).join('')}</select>`],
      ['', c => c.stage === 'HIRED' ? 'Onboarded as employee #' + c.employeeId :
        (c.stage === 'REJECTED' ? '' : `<button class="small-btn ok" onclick="hire(${c.id})">Hire</button>`)]
    ], cands));
}
function addJob() {
  act(() => api('/api/jobs', 'POST', { title: $('#j_title').value, departmentId: Number($('#j_dept').value) || null, description: $('#j_desc').value }));
}
function toggleJob(id, status) { act(() => api('/api/jobs/' + id + '/status?value=' + status, 'PUT')); }
function addCandidate() {
  act(() => api('/api/candidates', 'POST', { jobId: Number($('#c_job').value) || null, name: $('#c_name').value, email: $('#c_email').value }));
}
function setStage(id, value) { act(() => api('/api/candidates/' + id + '/stage?value=' + value, 'PUT')); }
function hire(id) {
  const salary = prompt('Monthly basic salary for this new employee?', '30000');
  if (salary === null) return;
  act(() => api('/api/candidates/' + id + '/hire?salary=' + encodeURIComponent(salary), 'POST'),
    'Hired! An employee record and login were created (password: welcome123).');
}

// ================= ATTENDANCE (HR view) =================
async function attendanceAll() {
  const [recs, emps] = await Promise.all([api('/api/attendance'), api('/api/employees')]);
  show('<h2>Attendance (everyone)</h2>' + table([
    ['Date', r => esc(r.workDate)], ['Employee', r => esc(empName(emps, r.employeeId))],
    ['Check in', r => esc(r.checkIn)], ['Check out', r => esc(r.checkOut || '-')]
  ], recs));
}

// ================= LEAVES (HR view) =================
async function leavesAll() {
  const [ls, emps] = await Promise.all([api('/api/leaves'), api('/api/employees')]);
  show('<h2>Leave requests</h2>' + table([
    ['Employee', l => esc(empName(emps, l.employeeId))], ['Type', l => esc(l.leaveType)],
    ['From', l => esc(l.startDate)], ['To', l => esc(l.endDate)], ['Reason', l => esc(l.reason)],
    ['Status', l => `<span class="tag">${esc(l.status)}</span>`],
    ['', l => l.status === 'PENDING'
      ? `<button class="small-btn ok" onclick="decideLeave(${l.id},'APPROVED')">Approve</button><button class="small-btn danger" onclick="decideLeave(${l.id},'REJECTED')">Reject</button>` : '']
  ], ls));
}
function decideLeave(id, value) { act(() => api('/api/leaves/' + id + '/status?value=' + value, 'PUT')); }

// ================= PAYROLL (HR view) =================
async function payrollAll() {
  const [ps, emps] = await Promise.all([api('/api/payroll'), api('/api/employees')]);
  const now = new Date();
  const month = now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0');
  show(`<h2>Payroll</h2>
    <div class="form"><input id="p_month" value="${month}" placeholder="YYYY-MM">
    <button onclick="generatePayroll()">Generate payslips for this month</button></div>` + payslipTable(ps, emps));
}
function generatePayroll() {
  const m = $('#p_month').value;
  act(async () => { const r = await api('/api/payroll/generate?month=' + encodeURIComponent(m), 'POST'); alert(r.message); });
}
function payslipTable(ps, emps) {
  const cols = [];
  if (emps) cols.push(['Employee', p => esc(empName(emps, p.employeeId))]);
  cols.push(['Month', p => esc(p.payMonth)], ['Basic', p => money(p.basic)], ['HRA', p => money(p.hra)],
    ['Gross', p => money(p.gross)], ['PF', p => money(p.pf)], ['Tax', p => money(p.tax)],
    ['<b>Net pay</b>', p => '<b>' + money(p.netPay) + '</b>']);
  return table(cols, ps);
}

// ================= PERFORMANCE (HR view) =================
async function reviewsAll() {
  const [rs, emps] = await Promise.all([api('/api/reviews'), api('/api/employees')]);
  show(`<h2>Performance reviews</h2>
    <div class="form">
      <select id="r_emp">${options(emps, e => e.id, e => e.firstName + ' ' + e.lastName)}</select>
      <input id="r_period" placeholder="Period (e.g. Q3 2026)">
      <select id="r_rating"><option>5</option><option>4</option><option selected>3</option><option>2</option><option>1</option></select>
      <input id="r_comments" placeholder="Comments" size="40">
      <button onclick="addReview()">Add review</button>
    </div>` + table([
      ['Employee', r => esc(empName(emps, r.employeeId))], ['Period', r => esc(r.period)],
      ['Rating', r => '★'.repeat(r.rating) + '☆'.repeat(5 - r.rating)], ['Reviewer', r => esc(r.reviewer)],
      ['Comments', r => esc(r.comments)]
    ], rs));
}
function addReview() {
  act(() => api('/api/reviews', 'POST', {
    employeeId: Number($('#r_emp').value) || null, period: $('#r_period').value,
    rating: Number($('#r_rating').value), comments: $('#r_comments').value
  }));
}

// ================= MY SPACE (every user) =================
async function myProfile() {
  const e = await api('/api/employees/me');
  show(`<h2>My profile</h2><div class="card">
    <p><b>${esc(e.firstName + ' ' + e.lastName)}</b></p>
    <p>${esc(e.jobTitle)}</p><p>Email: ${esc(e.email)}</p><p>Phone: ${esc(e.phone || '-')}</p>
    <p>Joined: ${esc(e.joinDate)}</p><p>Monthly basic salary: ${money(e.salary)}</p></div>`);
}

async function myAttendance() {
  const recs = await api('/api/attendance/my');
  show(`<h2>My attendance</h2>
    <div class="form"><button onclick="act(()=>api('/api/attendance/check-in','POST'),'Checked in')">Check in</button>
    <button onclick="act(()=>api('/api/attendance/check-out','POST'),'Checked out')">Check out</button></div>` +
    table([['Date', r => esc(r.workDate)], ['Check in', r => esc(r.checkIn)], ['Check out', r => esc(r.checkOut || '-')]], recs));
}

async function myLeaves() {
  const ls = await api('/api/leaves/my');
  show(`<h2>My leaves</h2>
    <div class="form">
      <select id="l_type"><option>Casual</option><option>Sick</option><option>Annual</option></select>
      <input id="l_start" type="date"><input id="l_end" type="date">
      <input id="l_reason" placeholder="Reason" size="30">
      <button onclick="applyLeave()">Apply</button>
    </div>` + table([
      ['Type', l => esc(l.leaveType)], ['From', l => esc(l.startDate)], ['To', l => esc(l.endDate)],
      ['Reason', l => esc(l.reason)], ['Status', l => `<span class="tag">${esc(l.status)}</span>`]
    ], ls));
}
function applyLeave() {
  act(() => api('/api/leaves/apply', 'POST', {
    leaveType: $('#l_type').value, startDate: $('#l_start').value || null,
    endDate: $('#l_end').value || null, reason: $('#l_reason').value
  }), 'Leave request sent');
}

async function myPayslips() {
  const ps = await api('/api/payroll/my');
  show('<h2>My payslips</h2>' + payslipTable(ps, null));
}

async function myReviews() {
  const rs = await api('/api/reviews/my');
  show('<h2>My performance reviews</h2>' + table([
    ['Period', r => esc(r.period)], ['Rating', r => '★'.repeat(r.rating) + '☆'.repeat(5 - r.rating)],
    ['Reviewer', r => esc(r.reviewer)], ['Comments', r => esc(r.comments)]
  ], rs));
}

// ---------- restore the session after a page refresh ----------
(function restore() {
  const saved = sessionStorage.getItem('auth');
  if (saved) { auth = JSON.parse(saved); startApp(); }
})();
