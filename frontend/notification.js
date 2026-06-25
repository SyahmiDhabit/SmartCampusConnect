const NOTIFICATION_API = 'http://localhost:8083/api/notifications';

// Initialize Page
document.addEventListener('DOMContentLoaded', () => {
  loadNotifications();
  // Poll for new notifications every 5 seconds
  setInterval(loadNotifications, 5000);
});

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

// Fetch and render notifications
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
