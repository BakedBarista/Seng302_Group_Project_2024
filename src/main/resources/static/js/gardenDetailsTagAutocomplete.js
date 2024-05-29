const gardenId = getMeta('_gardenId');
const initialTags = getMeta('_tags').split(',');
if (initialTags.length === 1 && initialTags[0] === '') {
    initialTags.pop();
}

const tagAutocompleteInstance = tagAutocomplete({
    initialTags,
    setTags: async (tags) => {
        const response = await fetch(`${apiBaseUrl}/gardens/${gardenId}/tags`, {
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
});
