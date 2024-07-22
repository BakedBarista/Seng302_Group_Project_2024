/**
 * Sets up tag autocomplete for the tag input field.
 *
 * @param {{ initialTags: string[], setTags: (tags: string[]) => void, appendUserInput: boolean, notFoundMessageHtml: string, placeholder: string, acceptButton: boolean, allowNonExistent?: boolean }} options Options for the tag autocomplete.
 * @returns An object with methods to interact with the tag autocomplete.
 */
function tagAutocomplete(options) {
    const tags = options.initialTags;

    const tagAutocompleteContainer = document.getElementById(
        'tag-autocomplete-container'
    );
    const tagContainer = document.getElementById('tag-container');

    if(!tagAutocompleteContainer) {
        return;
    }

    const tagAutocomplete = autocomplete(tagAutocompleteContainer, addTag, {
        apiUrl: `${apiBaseUrl}/tag-autocomplete`,
        notFoundMessageHtml: options.notFoundMessageHtml,
        appendUserInput: options.appendUserInput,
        placeholder: options.placeholder,
        acceptButton: options.acceptButton,
        allowNonExistent: options.allowNonExistent,
        minAutocompleteLength: 1,
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
        const regex = /^[\p{L}0-9\s\-_'"]*$/u;

        if (tag.length > 25) {
            setError("A tag cannot exceed 25 characters");
        } else if (!regex.test(tag)) {
            setError(`The tag name must only contain alphanumeric characters, spaces, -, _, ', or ".`);
        } else {
            setError(null);
            appendTagElement(tag);
        }
    }

    /**
     * Set the error that is shown
     *
     * @param {string | null} error The error to show, or null if no error should be shown
     */
    function setError(error) {
        const tagsError = document.getElementById("gardenTagsError");
        if (error) {
            tagsError.textContent = error;
            tagAutocomplete.inputElement.classList.add('is-invalid');
            tagAutocomplete.focus();
        } else {
            tagsError.textContent = '';
            tagAutocomplete.inputElement.classList.remove('is-invalid');
        }
    }

    /**
     * Removes the most recent tag
     *
     * @return {string | null} - The tag that was removed.
     */
    function removeLastTag() {
        const removedTags = tags.splice(-1, 1);
        tagContainer.lastElementChild.remove();
        return removedTags[0] ?? null;
    }

    /**
     * Appends a tag element to the tag container.
     */
    function appendTagElement(tag) {
        tagAutocomplete.clear();

        const tagText = document.createTextNode(tag + ' ');

        const tagElement = document.createElement('span');
        tagElement.className = 'badge badge-md text-bg-secondary mb-1';
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
        // dont allow new tags to be added if allowNonExistent is false
        if (typeof tag === 'string' && options.allowNonExistent === false) {
            return;
        }

        tag = (tag.formatted ?? tag).trim();
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

    return { addTag, removeLastTag, setError, ...tagAutocomplete };
}
