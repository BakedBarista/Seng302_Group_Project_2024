const gardenId = getMeta('_gardenId');
const initialTags = getMeta('_tags').split(',');
if (initialTags.length === 1 && initialTags[0] === '') {
    initialTags.pop();
}

const tagAutocompleteInstance = tagAutocomplete({
    initialTags,
    setTags: (tags) => {
        fetch(`/api/gardens/${gardenId}/tags`, {
            method: 'PUT',
            headers: {
                'content-type': 'application/json',
                [csrfHeader]: csrf,
            },
            body: JSON.stringify(tags),
        })
    },
});
