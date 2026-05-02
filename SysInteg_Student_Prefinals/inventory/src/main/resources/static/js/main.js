document.addEventListener('DOMContentLoaded', () => {
    // Auto-dismiss success alerts after 3.5s
    document.querySelectorAll('.alert-success').forEach(el => {
        setTimeout(() => {
            try { bootstrap.Alert.getOrCreateInstance(el).close(); } catch(e) {}
        }, 3500);
    });

    const path = window.location.pathname;
    const section = path.startsWith('/categories') ? 'categories'
        : path.startsWith('/suppliers') ? 'suppliers'
        : path.startsWith('/transactions') ? 'transactions'
        : path.startsWith('/products') ? 'products'
        : '';

    document.querySelectorAll('.app-nav-links .nav-link').forEach(link => {
        link.classList.toggle('active', section !== '' && link.dataset.section === section);
    });

    const stockMovementFormIds = ['inForm', 'outForm'];
    stockMovementFormIds.forEach(formId => {
        const form = document.getElementById(formId);
        if (!form) return;

        form.addEventListener('submit', event => {
            const type = formId === 'inForm' ? 'in' : 'out';
            const productSelect = document.getElementById('productSelect');
            const quantityInput = document.getElementById('qtyInput');
            const reasonInput = document.getElementById('reasonInput');
            const productId = productSelect?.value.trim() || '';
            const quantity = Number(quantityInput?.value || 0);

            if (!productId) {
                event.preventDefault();
                alert('Please select a product.');
                productSelect?.focus();
                return;
            }

            if (!Number.isInteger(quantity) || quantity < 1) {
                event.preventDefault();
                alert('Please enter a quantity of at least 1.');
                quantityInput?.focus();
                return;
            }

            document.getElementById(type + 'ProductId').value = productId;
            document.getElementById(type + 'Qty').value = String(quantity);
            document.getElementById(type + 'Reason').value = reasonInput?.value || '';
        });
    });
});
