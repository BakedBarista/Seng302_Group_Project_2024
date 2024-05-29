const initialTags = getMeta('_tags').split(',');
if (initialTags.length === 1 && initialTags[0] === '') {
    initialTags.pop();
}

const tagAutocompleteInstance = tagAutocomplete({
    initialTags,
    setTags: async (tags) => {
        const tagsInput = document.getElementById("tagsInput");
        tagsInput.value = tags.join(",");
    },
    appendUserInput: true,
    notFoundMessageHtml: `No tag matching `,
    placeholder: 'Search tags here',
    acceptButton: false,
    allowNonExistent: false
});
