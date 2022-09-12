/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import {marked} from "marked";
import * as DOMPurify from "dompurify";

function sanitize(input: string): string {
    return DOMPurify.sanitize(input);
}

function updatePreview() {
    const preview = document.getElementById("preview-area");
    const editor = document.getElementById("editor") as HTMLTextAreaElement;
    const markdown = sanitize(editor.value);
    preview.innerHTML = marked.parse(markdown);
}

document.addEventListener("DOMContentLoaded", () => {
    const previewHolder = document.getElementById("preview-holder");
    if (previewHolder != null) {
        previewHolder.style.visibility = "visible";
        updatePreview();
        const editor = document.getElementById("editor") as HTMLTextAreaElement;
        editor.oninput = () => updatePreview();
    }
});
