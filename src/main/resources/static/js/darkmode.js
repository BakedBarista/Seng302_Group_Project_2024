(() => {
    // Saves the theme to local storage
    const getStoredTheme = () => localStorage.getItem('theme')
    const setStoredTheme = theme => localStorage.setItem('theme', theme)
    const checkbox = document.getElementById('checkbox');

    const setTheme = theme => {
        document.documentElement.setAttribute('data-bs-theme', theme)
        setStoredTheme(theme);
    }

    const loadTheme = () => {
        const storedTheme = getStoredTheme()

        if (!storedTheme) {
            setTheme('light');
            checkbox.checked = false;
        } else if (storedTheme === 'dark') {
            checkbox.checked = true;
            setTheme('dark');
        } else {
            checkbox.checkbox = false;
            setTheme('light');
        }
    }

    window.addEventListener('DOMContentLoaded', () => {
        // Loads the theme on DOM load
        loadTheme();
        checkbox.addEventListener('change', () => {
            if (checkbox.checked) {
                setTheme('dark');
            } else {
                setTheme('light');
            }
        });
    })
})()
