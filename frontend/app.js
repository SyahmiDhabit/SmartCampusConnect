// API URLs
const STUDENT_API = 'http://localhost:8081/api/students';
const ENROLMENT_API = 'http://localhost:8082/api/enrolments';
const NOTIFICATION_API = 'http://localhost:8083/api/notifications';
const LOAN_API = 'http://localhost:8084/api/loans';
const BOOKING_API = 'http://localhost:8084/api/bookings';
const REPORTING_API = 'http://localhost:8085/api/reports';

// State Management
let currentTab = 'students';
let studentsList = [];
let editMode = false;
let editingStudentId = null;

// Initialize App
document.addEventListener('DOMContentLoaded', () => {
  setupNavigation();
  initDashboardPolling();
  loadAllData();
  
  // Set minimum date for booking-date to today
  const bookingDateInput = document.getElementById('booking-date');
  if (bookingDateInput) {
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const dd = String(today.getDate()).padStart(2, '0');
    bookingDateInput.min = `${yyyy}-${mm}-${dd}`;
    bookingDateInput.addEventListener('change', updateAvailableTimeSlots);
  }
  
  // Form Submit Listeners
  document.getElementById('student-form').addEventListener('submit', handleStudentSubmit);
  document.getElementById('enrolment-form').addEventListener('submit', handleEnrolmentSubmit);
  document.getElementById('loan-form').addEventListener('submit', handleLoanSubmit);
  document.getElementById('booking-form').addEventListener('submit', handleBookingSubmit);
});

function updateAvailableTimeSlots() {
  const dateInput = document.getElementById('booking-date');
  const timeSelect = document.getElementById('booking-time-select');
  if (!dateInput || !timeSelect) return;

  const selectedDate = dateInput.value;
  const today = new Date();
  const yyyy = today.getFullYear();
  const mm = String(today.getMonth() + 1).padStart(2, '0');
  const dd = String(today.getDate()).padStart(2, '0');
  const todayStr = `${yyyy}-${mm}-${dd}`;

  const currentHours = today.getHours();
  const currentMinutes = today.getMinutes();

  Array.from(timeSelect.options).forEach(option => {
    if (!option.value) return; // skip default select option
    
    const slotEndStr = option.value.split(' - ')[1];
    const [slotEndHours, slotEndMinutes] = slotEndStr.split(':').map(Number);
    
    if (selectedDate === todayStr) {
      if (currentHours > slotEndHours || (currentHours === slotEndHours && currentMinutes >= slotEndMinutes)) {
        option.disabled = true;
        if (timeSelect.value === option.value) {
          timeSelect.value = ''; // Reset selected option if it's now disabled
        }
      } else {
        option.disabled = false;
      }
    } else {
      option.disabled = false;
    }
  });
}

// Toast Alerts
function showToast(message, type = 'success') {
  const container = document.getElementById('toast-container');
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  
  const icon = type === 'success' ? 'fa-circle-check' : 'fa-circle-exclamation';
  const color = type === 'success' ? '#10b981' : '#f43f5e';
  
  toast.innerHTML = `
    <i class="fa-solid ${icon}" style="color: ${color}"></i>
    <div>${message}</div>
  `;
  
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

// Navigation Handling
function setupNavigation() {
  const navItems = document.querySelectorAll('.nav-item');
  navItems.forEach(item => {
    const targetTab = item.getAttribute('data-tab');
    if (targetTab) {
      item.addEventListener('click', (e) => {
        e.preventDefault();
        switchTab(targetTab);
      });
    }
  });
}

function switchTab(tabId) {
  currentTab = tabId;
  
  // Update sidebar active state
  document.querySelectorAll('.nav-item').forEach(item => {
    if (item.getAttribute('data-tab') === tabId) {
      item.classList.add('active');
    } else {
      item.classList.remove('active');
    }
  });
  
  // Update main content visibility
  document.querySelectorAll('.tab-content').forEach(content => {
    if (content.id === `${tabId}-tab`) {
      content.classList.add('active');
    } else {
      content.classList.remove('active');
    }
  });

  // Reload data for specific tab
  if (tabId === 'students') loadStudents();
  if (tabId === 'enrolments') loadEnrolments();
  if (tabId === 'library') {
    loadLoans();
    loadBookings();
  }
  if (tabId === 'notifications') loadNotifications();
  if (tabId === 'dashboard') loadDashboardStats();
}

// Load data initially
function loadAllData() {
  loadDashboardStats();
  loadStudents(); // Fetch list to use for dropdowns etc.
}

// Polling for health and notifications count
function initDashboardPolling() {
  // Poll reporting service stats and notification counter
  setInterval(() => {
    pollSystemStatus();
    if (currentTab === 'notifications') {
      loadNotifications();
    }
  }, 5000);
  pollSystemStatus();
}

// ----------------------------------------------------
// DASHBOARD & HEALTH
// ----------------------------------------------------
async function pollSystemStatus() {
  // 1. Check Reporting API & indirect statuses
  let reportingOnline = false;
  try {
    const res = await fetch(`${REPORTING_API}/enrolment-summary`);
    if (res.ok) {
      reportingOnline = true;
      const data = await res.json();
      if (data && data.liveServiceStatuses) {
        updateServiceStatus('student-service-status', data.liveServiceStatuses['student-profile-service']);
        updateServiceStatus('enrolment-service-status', data.liveServiceStatuses['course-enrolment-service']);
        updateServiceStatus('library-service-status', data.liveServiceStatuses['library-service']);
      }
    } else {
      throw new Error();
    }
  } catch (error) {
    updateServiceStatus('student-service-status', 'OFFLINE');
    updateServiceStatus('enrolment-service-status', 'OFFLINE');
    updateServiceStatus('library-service-status', 'OFFLINE');
  }
  updateServiceStatus('reporting-service-status', reportingOnline ? 'ONLINE' : 'OFFLINE');

  // 2. Check Notification API directly
  let notificationOnline = false;
  try {
    const res = await fetch(`${NOTIFICATION_API}/count`);
    if (res.ok) {
      notificationOnline = true;
    }
  } catch (error) {
    // offline
  }
  updateServiceStatus('notification-service-status', notificationOnline ? 'ONLINE' : 'OFFLINE');
}

function updateServiceStatus(elementId, status) {
  const element = document.getElementById(elementId);
  if (!element) return;
  
  if (status === 'ONLINE') {
    element.className = 'status-badge status-online';
    element.textContent = 'Online';
  } else {
    element.className = 'status-badge status-offline';
    element.textContent = 'Offline';
  }
}

async function loadDashboardStats() {
  try {
    const res = await fetch(`${REPORTING_API}/enrolment-summary`);
    if (!res.ok) throw new Error();
    const data = await res.json();
    
    // Update stats metrics (if elements exist)
    const enrolmentsElem = document.getElementById('metric-total-enrolments');
    if (enrolmentsElem) enrolmentsElem.textContent = data.metrics['total_enrolments'] || 0;
    
    const loansElem = document.getElementById('metric-total-loans');
    if (loansElem) loansElem.textContent = data.metrics['total_library_loans'] || 0;
    
    const bookingsElem = document.getElementById('metric-total-bookings');
    if (bookingsElem) bookingsElem.textContent = data.metrics['total_room_bookings'] || 0;
    
    // Render chart
    renderProgrammeChart(data.enrolmentsPerProgramme || {});
    
    // Update status distribution
    const statuses = data.enrolmentStatuses || {};
    document.getElementById('status-confirmed').textContent = statuses['CONFIRMED'] || 0;
    const provisionalElem = document.getElementById('status-provisional');
    if (provisionalElem) provisionalElem.textContent = statuses['PROVISIONAL'] || 0;
    
  } catch (error) {
    console.error('Error fetching dashboard stats', error);
  }
}

function renderProgrammeChart(enrolments) {
  const container = document.getElementById('programme-chart-container');
  container.innerHTML = '';
  
  const entries = Object.entries(enrolments);
  if (entries.length === 0) {
    container.innerHTML = '<div style="color: var(--text-muted); text-align: center; padding: 1rem;">No enrolment data available</div>';
    return;
  }
  
  const maxVal = Math.max(...entries.map(([_, v]) => v)) || 1;
  
  entries.forEach(([prog, val]) => {
    const pct = (val / maxVal) * 100;
    const bar = document.createElement('div');
    bar.className = 'chart-bar-item';
    bar.innerHTML = `
      <span class="chart-bar-label">${prog}</span>
      <div class="chart-bar-fill-bg">
        <div class="chart-bar-fill" style="width: ${pct}%"></div>
      </div>
      <span class="chart-bar-val">${val}</span>
    `;
    container.appendChild(bar);
  });
}

// ----------------------------------------------------
// STUDENTS MANAGEMENT
// ----------------------------------------------------
async function loadStudents() {
  try {
    const res = await fetch(STUDENT_API);
    if (!res.ok) throw new Error('Failed to load students');
    studentsList = await res.json();
    
    renderStudentsTable(studentsList);
    updateStudentDropdowns();
  } catch (error) {
    showToast(error.message, 'error');
  }
}

function renderStudentsTable(students) {
  const tbody = document.getElementById('students-table-body');
  tbody.innerHTML = '';
  
  if (students.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: var(--text-muted);">No student profiles registered</td></tr>`;
    return;
  }
  
  students.forEach(student => {
    const tr = document.createElement('tr');
    
    let gpaClass = 'gpa-low';
    if (student.gpa >= 3.5) gpaClass = 'gpa-high';
    else if (student.gpa >= 2.5) gpaClass = 'gpa-mid';
    
    tr.innerHTML = `
      <td><strong>${escapeHtml(student.id)}</strong></td>
      <td>${escapeHtml(student.name)}</td>
      <td>${escapeHtml(student.email)}</td>
      <td>${escapeHtml(student.programme)}</td>
      <td><span class="gpa-pill ${gpaClass}">${student.gpa.toFixed(2)}</span></td>
      <td>
        <div style="display: flex; gap: 0.5rem;">
          <button class="btn btn-secondary" style="padding: 0.35rem 0.6rem; font-size: 0.8rem; width: auto;" onclick="startEditStudent('${student.id}')"><i class="fa-solid fa-pen"></i></button>
          <button class="btn btn-danger" style="width: auto; padding: 0.35rem 0.6rem;" onclick="deleteStudent('${student.id}')"><i class="fa-solid fa-trash"></i></button>
        </div>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

function updateStudentDropdowns() {
  const dropdowns = ['enrol-student-select', 'loan-student-select', 'booking-student-select'];
  dropdowns.forEach(id => {
    const select = document.getElementById(id);
    if (!select) return;
    
    select.innerHTML = '<option value="">-- Select Student --</option>';
    studentsList.forEach(student => {
      const opt = document.createElement('option');
      opt.value = student.id;
      opt.textContent = `${student.id} - ${student.name}`;
      select.appendChild(opt);
    });
  });
}

async function handleStudentSubmit(e) {
  e.preventDefault();
  const idInput = document.getElementById('student-id');
  const name = document.getElementById('student-name').value;
  const email = document.getElementById('student-email').value;
  const programme = document.getElementById('student-programme').value;
  const gpa = parseFloat(document.getElementById('student-gpa').value);
  
  const payload = {
    id: idInput.value.trim(),
    name: name.trim(),
    email: email.trim(),
    programme: programme,
    gpa: gpa
  };
  
  // Validate email uniqueness (except when editing the same student)
  const emailExists = studentsList.some(s => 
    s.email.trim().toLowerCase() === email.trim().toLowerCase() && 
    (!editMode || s.id !== editingStudentId)
  );
  
  if (emailExists) {
    showToast('A student with this email address is already registered!', 'error');
    return;
  }

  try {
    let res;
    if (editMode) {
      res = await fetch(`${STUDENT_API}/${editingStudentId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
    } else {
      res = await fetch(STUDENT_API, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
    }
    
    if (!res.ok) {
      const err = await res.json();
      throw new Error(err.message || 'Operation failed');
    }
    
    showToast(editMode ? 'Student profile updated successfully!' : 'Student profile created successfully!');
    cancelEditStudent();
    loadStudents();
    pollSystemStatus(); // Refresh dashboard indicators
  } catch (error) {
    showToast(error.message, 'error');
  }
}

function startEditStudent(id) {
  const student = studentsList.find(s => s.id === id);
  if (!student) return;
  
  editMode = true;
  editingStudentId = student.id;
  
  const idInput = document.getElementById('student-id');
  idInput.value = student.id;
  idInput.disabled = true; // Cannot edit PK ID
  
  document.getElementById('student-name').value = student.name;
  document.getElementById('student-email').value = student.email;
  document.getElementById('student-programme').value = student.programme;
  document.getElementById('student-gpa').value = student.gpa;
  
  document.getElementById('student-submit-btn').innerHTML = '<i class="fa-solid fa-floppy-disk"></i> Update Profile';
  document.getElementById('student-cancel-btn').style.display = 'inline-flex';
  document.getElementById('student-form-title').innerHTML = '<i class="fa-solid fa-user-pen"></i> Edit Student Profile';
}

function cancelEditStudent() {
  editMode = false;
  editingStudentId = null;
  
  const idInput = document.getElementById('student-id');
  idInput.value = '';
  idInput.disabled = false;
  
  document.getElementById('student-form').reset();
  
  document.getElementById('student-submit-btn').innerHTML = '<i class="fa-solid fa-user-plus"></i> Register Student';
  document.getElementById('student-cancel-btn').style.display = 'none';
  document.getElementById('student-form-title').innerHTML = '<i class="fa-solid fa-user-plus"></i> Register New Student';
}

async function deleteStudent(id) {
  if (!confirm(`Are you sure you want to delete student profile ${id}?`)) return;
  
  try {
    const res = await fetch(`${STUDENT_API}/${id}`, {
      method: 'DELETE'
    });
    if (!res.ok) throw new Error('Failed to delete student');
    
    showToast('Student profile deleted.');
    loadStudents();
    pollSystemStatus();
  } catch (error) {
    showToast(error.message, 'error');
  }
}

// ----------------------------------------------------
// COURSE ENROLMENTS
// ----------------------------------------------------
async function loadEnrolments() {
  try {
    const res = await fetch(ENROLMENT_API);
    if (!res.ok) throw new Error('Failed to load enrolments');
    const data = await res.json();
    renderEnrolmentsTable(data);
  } catch (error) {
    showToast(error.message, 'error');
  }
}

function renderEnrolmentsTable(enrolments) {
  const tbody = document.getElementById('enrolments-table-body');
  tbody.innerHTML = '';
  
  if (enrolments.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: var(--text-muted);">No student enrolments found</td></tr>`;
    return;
  }
  
  enrolments.forEach(enrol => {
    tbody.innerHTML += `
      <tr>
        <td><strong>${enrol.id}</strong></td>
        <td>${escapeHtml(enrol.studentId)}</td>
        <td>${escapeHtml(enrol.courseCode)}</td>
        <td>${escapeHtml(enrol.semester)}</td>
        <td>
          <span class="status-badge ${enrol.status === 'CONFIRMED' ? 'status-online' : 'status-offline'}">
            ${enrol.status}
          </span>
        </td>
      </tr>
    `;
  });
}

async function handleEnrolmentSubmit(e) {
  e.preventDefault();
  const studentId = document.getElementById('enrol-student-select').value;
  const courseCode = document.getElementById('enrol-course').value;
  const semester = document.getElementById('enrol-semester').value;
  
  if (!studentId) {
    showToast('Please select a student', 'error');
    return;
  }
  
  const payload = { studentId, courseCode, semester };
  
  try {
    const res = await fetch(ENROLMENT_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    
    if (!res.ok) {
      const err = await res.json();
      throw new Error(err.message || 'Enrolment failed');
    }
    
    showToast('Student course enrolment request submitted!');
    document.getElementById('enrolment-form').reset();
    loadEnrolments();
    loadDashboardStats();
  } catch (error) {
    showToast(error.message, 'error');
  }
}

// ----------------------------------------------------
// LIBRARY & ROOM BOOKINGS
// ----------------------------------------------------
async function loadLoans() {
  try {
    const res = await fetch(LOAN_API);
    if (!res.ok) throw new Error('Failed to load book loans');
    const loans = await res.json();
    renderLoansTable(loans);
  } catch (error) {
    showToast(error.message, 'error');
  }
}

function renderLoansTable(loans) {
  const tbody = document.getElementById('loans-table-body');
  tbody.innerHTML = '';
  
  if (loans.length === 0) {
    tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--text-muted);">No book loan records found</td></tr>`;
    return;
  }
  
  loans.forEach(loan => {
    const isReturned = loan.status === 'RETURNED';
    const actionBtn = isReturned 
      ? `<span style="color: var(--text-muted); font-size: 0.85rem;"><i class="fa-solid fa-check-double"></i> Completed</span>`
      : `<button class="btn btn-secondary" style="padding: 0.25rem 0.5rem; font-size: 0.8rem; width: auto;" onclick="returnBook(${loan.id})"><i class="fa-solid fa-arrow-rotate-left"></i> Return</button>`;
      
    tbody.innerHTML += `
      <tr>
        <td><strong>${loan.id}</strong></td>
        <td>${escapeHtml(loan.studentId)}</td>
        <td>${escapeHtml(loan.bookId)}</td>
        <td>${escapeHtml(loan.bookTitle)}</td>
        <td>
          <span class="status-badge ${isReturned ? 'status-online' : 'status-offline'}">
            ${loan.status}
          </span>
        </td>
        <td>${actionBtn}</td>
      </tr>
    `;
  });
}

async function handleLoanSubmit(e) {
  e.preventDefault();
  const studentId = document.getElementById('loan-student-select').value;
  const bookId = document.getElementById('loan-book-select').value;
  
  if (!studentId || !bookId) {
    showToast('Please select a student and a book', 'error');
    return;
  }
  
  try {
    const res = await fetch(LOAN_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ studentId, bookId })
    });
    
    if (!res.ok) {
      const err = await res.json();
      throw new Error(err.message || 'Loan creation failed');
    }
    
    showToast('Book loan transaction registered successfully!');
    document.getElementById('loan-form').reset();
    loadLoans();
    loadDashboardStats();
  } catch (error) {
    showToast(error.message, 'error');
  }
}

async function returnBook(loanId) {
  try {
    const res = await fetch(`${LOAN_API}/${loanId}/return`, {
      method: 'PUT'
    });
    if (!res.ok) throw new Error('Return failed');
    
    showToast('Book returned successfully.');
    loadLoans();
    loadDashboardStats();
  } catch (error) {
    showToast(error.message, 'error');
  }
}

// Room bookings
async function loadBookings() {
  try {
    const res = await fetch(BOOKING_API);
    if (!res.ok) throw new Error('Failed to load room bookings');
    const bookings = await res.json();
    renderBookingsTable(bookings);
  } catch (error) {
    showToast(error.message, 'error');
  }
}

function renderBookingsTable(bookings) {
  const tbody = document.getElementById('bookings-table-body');
  tbody.innerHTML = '';
  
  if (bookings.length === 0) {
    tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--text-muted);">No study room bookings found</td></tr>`;
    return;
  }
  
  bookings.forEach(b => {
    tbody.innerHTML += `
      <tr>
        <td><strong>${b.id}</strong></td>
        <td>${escapeHtml(b.studentId)}</td>
        <td>${escapeHtml(b.roomId)}</td>
        <td>${escapeHtml(b.roomName)}</td>
        <td>${escapeHtml(b.bookingDate)}</td>
        <td><span style="font-family: monospace; background: var(--bg-tertiary); padding: 0.15rem 0.4rem; border-radius: 4px;">${escapeHtml(b.timeSlot)}</span></td>
      </tr>
    `;
  });
}

async function handleBookingSubmit(e) {
  e.preventDefault();
  const studentId = document.getElementById('booking-student-select').value;
  const roomId = document.getElementById('booking-room-select').value;
  const bookingDate = document.getElementById('booking-date').value;
  const timeSlot = document.getElementById('booking-time-select').value;
  
  if (!studentId || !roomId || !bookingDate || !timeSlot) {
    showToast('Please fill out all booking fields', 'error');
    return;
  }
  
  const today = new Date();
  const yyyy = today.getFullYear();
  const mm = String(today.getMonth() + 1).padStart(2, '0');
  const dd = String(today.getDate()).padStart(2, '0');
  const todayStr = `${yyyy}-${mm}-${dd}`;
  
  if (bookingDate < todayStr) {
    showToast('Reservation date cannot be in the past', 'error');
    return;
  }
  
  if (bookingDate === todayStr) {
    const currentHours = today.getHours();
    const currentMinutes = today.getMinutes();
    const slotEndStr = timeSlot.split(' - ')[1];
    const [slotEndHours, slotEndMinutes] = slotEndStr.split(':').map(Number);
    
    if (currentHours > slotEndHours || (currentHours === slotEndHours && currentMinutes >= slotEndMinutes)) {
      showToast('Selected time slot has already passed for today', 'error');
      return;
    }
  }
  
  const payload = { studentId, roomId, bookingDate, timeSlot };
  
  try {
    const res = await fetch(BOOKING_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    
    if (!res.ok) {
      const err = await res.json();
      throw new Error(err.message || 'Room booking failed');
    }
    
    showToast('Study room reservation confirmed!');
    document.getElementById('booking-form').reset();
    loadBookings();
    loadDashboardStats();
  } catch (error) {
    showToast(error.message, 'error');
  }
}

// ----------------------------------------------------
// SYSTEM NOTIFICATIONS
// ----------------------------------------------------
async function loadNotifications() {
  try {
    const res = await fetch(NOTIFICATION_API);
    if (!res.ok) throw new Error('Failed to fetch notifications');
    const data = await res.json();
    renderNotifications(data);
  } catch (error) {
    console.error(error);
  }
}

function renderNotifications(events) {
  const container = document.getElementById('events-log-container');
  container.innerHTML = '';
  
  if (events.length === 0) {
    container.innerHTML = '<div style="color: var(--text-muted); text-align: center; padding: 2rem;">No system notifications recorded. Trigger operations to view event flows.</div>';
    return;
  }
  
  // Sort events newest first
  const sortedEvents = [...events].sort((a, b) => b.id - a.id);
  
  sortedEvents.forEach(e => {
    let typeClass = 'et-created';
    if (e.eventType.includes('UPDATED')) typeClass = 'et-updated';
    else if (e.eventType.includes('RETURNED') || e.eventType.includes('DELIVERED')) typeClass = 'et-returned';
    
    const formattedDate = new Date(e.receivedAt).toLocaleTimeString();
    
    const card = document.createElement('div');
    card.className = 'event-card';
    card.innerHTML = `
      <div class="event-header">
        <span class="event-type ${typeClass}">${escapeHtml(e.eventType.replace('EVENT_', ''))}</span>
        <span class="event-time">${formattedDate}</span>
      </div>
      <div class="event-body">${escapeHtml(e.payload)}</div>
      <div class="event-footer">
        <span>Publisher: <strong>${escapeHtml(e.source)}</strong></span>
        <span>Key: <em>${escapeHtml(e.routingKey)}</em></span>
      </div>
    `;
    container.appendChild(card);
  });
}

async function clearNotificationLogs() {
  if (!confirm('Are you sure you want to clear all notification records?')) return;
  try {
    const res = await fetch(NOTIFICATION_API, {
      method: 'DELETE'
    });
    if (!res.ok) throw new Error('Failed to clear notifications');
    showToast('Notification logs cleared.');
    loadNotifications();
    loadDashboardStats();
  } catch (error) {
    showToast(error.message, 'error');
  }
}

// Utility HTML escape helper
function escapeHtml(str) {
  if (!str) return '';
  return str.toString()
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}
