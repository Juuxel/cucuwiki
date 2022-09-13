/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import { marked } from "marked";
import * as DOMPurify from "dompurify";
import { DOMPurifyI } from "dompurify";

function sanitize(input: string): string {
    // Awful hack for Rollup - looks like it can't deal with
    // DOMPurify being a namespace but also a factory for an object.

    /* eslint-disable @typescript-eslint/ban-ts-comment */
    // @ts-ignore
    const purify: DOMPurifyI = DOMPurify.default as DOMPurifyI;
    return purify.sanitize(input);
}

function updatePreview() {
    const preview = document.getElementById("preview-area")!;
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
