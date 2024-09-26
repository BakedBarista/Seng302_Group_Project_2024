(() => {


    // Saves the theme to local storage
    const getStoredTheme = () => localStorage.getItem('theme')
    const setStoredTheme = theme => {
        localStorage.setItem('theme', theme)
    }
    const checkboxes = document.getElementsByName("checkbox")


    const navLogo = document.getElementById("navbarLogo");
    const locationIcon = document.getElementById("locationIcon");
    const setTheme = theme => {
        document.documentElement.setAttribute('data-bs-theme', theme)
        setStoredTheme(theme);
        if(navLogo != null) {
            navLogo.src = theme === 'dark' ? `${baseUrl}img/yellow-logo.svg` : `${baseUrl}img/logo.svg`;
            locationIcon
        }
        document.cookie = `theme=${theme}; path=/`;
    }



    const loadTheme = () => {
        const storedTheme = getStoredTheme()

        if (!storedTheme) {
            setTheme('light');
            for (let checkbox of checkboxes) {
                checkbox.checked = false;
            }
        } else if (storedTheme === 'dark') {
            for (let checkbox of checkboxes) {
                checkbox.checked = true;
            }
            setTheme('dark');
        } else {
            for (let checkbox of checkboxes) {
                checkbox.checked = false;
            }
            setTheme('light');
        }
    }

    loadTheme();
    for (let checkbox of checkboxes) {
        checkbox.addEventListener('change', () => {
            if (checkbox.checked) {
                for (let checkbox_ of checkboxes) {
                    checkbox_.checked = true;
                }
                setTheme('dark');
            } else {
                for (let checkbox_ of checkboxes) {
                    checkbox_.checked = false;
                }
                setTheme('light');
            }
        });
    }

})()
