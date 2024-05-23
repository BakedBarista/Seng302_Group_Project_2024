/**
 * Sets up tag autocomplete for the tag input field.
 *
 * @param {{ initialTags: string[], setTags: (tags: string[]) => void }} options Options for the tag autocomplete.
 * @returns An object with methods to interact with the tag autocomplete.
 */
function tagAutocomplete(options) {
    const tags = options.initialTags;

    const tagAutocompleteContainer = document.getElementById(
        'tag-autocomplete-container'
    );
    const tagContainer = document.getElementById('tag-container');

    const tagAutocomplete = autocomplete(tagAutocompleteContainer, addTag, {
        apiUrl: `${apiBaseUrl}/tag-autocomplete`,
        notFoundMessageHtml:
            'No matching tag. <u class="text-primary">Create new tag</u>',
        placeholder: 'Start typing tags here',
        acceptButton: true,
    });

    for (const tag of tags) {
        appendTagElement(tag);
    }

    /**
     * Validates tag input before adding the tag element
     *
     * @param {{ formatted: string } | string} tag - The entry in the autocomplete API response
     */
    function validateTag(tag) {
        const tagsError = document.getElementById("gardenTagsError");
        const regex = new RegExp("\^[a-zA-Z0-9\\s\\-_']+$");

        if (tag.length > 25) {
            tagsError.textContent = "A tag cannot exceed 25 characters"
            tagAutocomplete.focus();
        } else if (!regex.test(tag)) {
            tagsError.textContent = "The tag name must only contain alphanumeric characters, spaces, -, _, ', or "
            tagAutocomplete.focus();
        } else {
            tagsError.textContent ="";
            appendTagElement(tag)
        }
    }

    /**
     * Appends a tag element to the tag container.
     */
    function appendTagElement(tag) {
        tagAutocomplete.clear();

        const tagText = document.createTextNode(tag + ' ');

        const tagElement = document.createElement('span');
        tagElement.className = 'badge badge-md text-bg-secondary';
        tagElement.setAttribute('data-tag', tag);

        tagElement.appendChild(tagText);

        const closeButton = document.createElement('button');
        closeButton.className = 'btn-close';
        closeButton.ariaLabel = 'remove tag';
        closeButton.addEventListener('click', removeTag);
        tagElement.appendChild(closeButton);

        tagContainer.appendChild(tagElement);
        tagContainer.appendChild(document.createTextNode(' '));
    }

    /**
     * Adds a tag to the list of selected tags.
     *
     * @param {{ formatted: string } | string} tag - The entry in the autocomplete API response.
     */
    function addTag(tag) {
        tag = tag.formatted ?? tag;
        if (tags.includes(tag)) {
            return;
        }

        validateTag(tag);

        tags.push(tag);
        options.setTags(tags);
    }

    /**
     * Removes a tag from the list of selected tags.
     *
     * @param {MouseEvent} event - The close button click event.
     */
    function removeTag(event) {
        event.preventDefault();

        /**@type {HTMLButtonElement} */
        const closeButton = event.target;
        const tagElement = closeButton.parentElement;
        const tag = tagElement.getAttribute('data-tag');

        tagElement.remove();

        const index = tags.indexOf(tag);
        if (index >= 0) {
            tags.splice(index, 1);
            options.setTags(tags);
        }
    }

    return { addTag };
}
