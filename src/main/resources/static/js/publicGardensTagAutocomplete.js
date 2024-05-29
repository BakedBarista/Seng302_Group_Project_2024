const initialTags = []

const tagAutocompleteInstance = tagAutocomplete({
    initialTags,
    setTags: async (tags) => {
        const response = await fetch(`${apiBaseUrl}/gardens/2/tags`, {
            method: 'PUT',
            headers: {
                'content-type': 'application/json',
                [csrfHeader]: csrf,
            },
            body: JSON.stringify(tags),
        });

        if (response.status === 422) {
            const error = await response.text();
            tagAutocompleteInstance.removeLastTag();

            tagAutocompleteInstance.setError(error);
        }
    },
    appendUserInput: true,
    notFoundMessageHtml: `No tag matching `,
    placeholder: 'Search tags here',
});
