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

        switch (response.status) {
            case 422:
                const error = await response.text();
                const tagName = tagAutocompleteInstance.removeLastTag();

                tagAutocompleteInstance.setError(error);
                tagAutocompleteInstance.inputElement.value = tagName;
                break;
            case 401:
                location.href = `${baseUrl}users/blocked`;
                break;
        }
    },
    notFoundMessageHtml: 'No matching tag. <u class="text-primary">Create new tag</u>',
    placeholder: 'Start typing tags here',
    acceptButton: true,
    allowNonExistent: true,
});
