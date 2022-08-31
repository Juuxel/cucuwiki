import {marked} from "marked";
import * as DOMPurify from "dompurify";
import {escape} from "html-escaper";

function sanitize(input: string): string {
    // TODO: Is DOMPurify really required here?
    return DOMPurify.sanitize(escape(input));
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
