/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

document.addEventListener("DOMContentLoaded", () => {
    const objects = document.getElementsByTagName("object");
    for (const object of objects) {
        if (object.type == "text/html") {
            object.addEventListener("load", () => {
                const innerDocument = object.contentDocument;
                if (innerDocument != null) {
                    const observer = new ResizeObserver(entries => {
                        for (const entry of entries) {
                            if (entry.target.tagName.toLowerCase() == "html") {
                                const size = entry.borderBoxSize.reduce((prev, current) => Math.max(prev, current.blockSize), 0);
                                object.height = size + "px";
                                observer.disconnect();
                                break;
                            }
                        }
                    });
                    for (const element of innerDocument.getElementsByTagName("html")) {
                        observer.observe(element);
                    }
                }
            });
        }
    }
});
