/* ═══════════════════════════════════════════════════════════
   PLAY26 — JavaScript Utilities
   ═══════════════════════════════════════════════════════════ */

document.addEventListener('DOMContentLoaded', function () {

    // 1. Toast Notifications — auto-show
    document.querySelectorAll('.toast').forEach(function (el) {
        var toast = new bootstrap.Toast(el, { delay: 5000 });
        toast.show();
    });

    // 2. Auto-dismiss alerts after 5 seconds
    document.querySelectorAll('.alert-dismissible').forEach(function (el) {
        setTimeout(function () {
            var bsAlert = bootstrap.Alert.getOrCreateInstance(el);
            if (bsAlert) bsAlert.close();
        }, 5000);
    });

    // 3. Delete confirmation
    document.querySelectorAll('.btn-delete, [data-confirm]').forEach(function (btn) {
        btn.addEventListener('click', function (e) {
            var msg = btn.getAttribute('data-confirm') || 'Are you sure you want to delete this?';
            if (!confirm(msg)) {
                e.preventDefault();
                e.stopPropagation();
            }
        });
    });

    // 4. Form submit — show loading
    document.querySelectorAll('form[data-loading]').forEach(function (form) {
        form.addEventListener('submit', function () {
            showLoading();
        });
    });

    // 5. Image/Banner URL preview
    document.querySelectorAll('[data-preview]').forEach(function (input) {
        var targetId = input.getAttribute('data-preview');
        var target = document.getElementById(targetId);
        if (target) {
            input.addEventListener('input', function () {
                if (input.value) {
                    target.src = input.value;
                    target.style.display = 'block';
                } else {
                    target.style.display = 'none';
                }
            });
        }
    });

    // 6. Search with debounce
    var searchInput = document.getElementById('searchInput');
    var searchForm = document.getElementById('searchForm');
    if (searchInput && searchForm) {
        var debounceTimer;
        searchInput.addEventListener('input', function () {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(function () {
                searchForm.submit();
            }, 600);
        });
    }

    // 7. Sidebar toggle for mobile
    var sidebarToggle = document.getElementById('sidebarToggle');
    var sidebar = document.getElementById('sidebar');
    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', function () {
            sidebar.classList.toggle('show');
        });
    }

    // 8. Strike Rate calculator (batting form)
    var runsInput = document.getElementById('runs');
    var ballsInput = document.getElementById('balls');
    var srDisplay = document.getElementById('strikeRateDisplay');
    if (runsInput && ballsInput && srDisplay) {
        function calcSR() {
            var r = parseInt(runsInput.value) || 0;
            var b = parseInt(ballsInput.value) || 0;
            srDisplay.textContent = b > 0 ? ((r * 100.0) / b).toFixed(2) : '0.00';
        }
        runsInput.addEventListener('input', calcSR);
        ballsInput.addEventListener('input', calcSR);
    }

    // 9. Economy calculator (bowling form)
    var bwRunsInput = document.getElementById('bwRuns');
    var oversInput = document.getElementById('bwOvers');
    var econDisplay = document.getElementById('economyDisplay');
    if (bwRunsInput && oversInput && econDisplay) {
        function calcEcon() {
            var r = parseInt(bwRunsInput.value) || 0;
            var o = parseFloat(oversInput.value) || 0;
            econDisplay.textContent = o > 0 ? (r / o).toFixed(2) : '0.00';
        }
        bwRunsInput.addEventListener('input', calcEcon);
        oversInput.addEventListener('input', calcEcon);
    }

    // 10. Extras total calculator
    var extrasFields = ['wides', 'noBalls', 'byes', 'legByes', 'penalty'];
    var totalDisplay = document.getElementById('extrasTotal');
    if (totalDisplay) {
        function calcExtras() {
            var sum = 0;
            extrasFields.forEach(function (f) {
                var el = document.getElementById(f);
                if (el) sum += parseInt(el.value) || 0;
            });
            totalDisplay.textContent = sum;
        }
        extrasFields.forEach(function (f) {
            var el = document.getElementById(f);
            if (el) el.addEventListener('input', calcExtras);
        });
    }

    // 11. Dismissal type toggle
    var isOutCheck = document.getElementById('isOut');
    var dismissalSection = document.getElementById('dismissalSection');
    if (isOutCheck && dismissalSection) {
        function toggleDismissal() {
            dismissalSection.style.display = isOutCheck.checked ? 'block' : 'none';
        }
        isOutCheck.addEventListener('change', toggleDismissal);
        toggleDismissal();
    }
});

// Loading overlay functions
function showLoading() {
    var overlay = document.getElementById('loadingOverlay');
    if (overlay) overlay.classList.add('active');
}
function hideLoading() {
    var overlay = document.getElementById('loadingOverlay');
    if (overlay) overlay.classList.remove('active');
}
