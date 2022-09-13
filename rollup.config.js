import { defineConfig } from "rollup";
import { nodeResolve } from "@rollup/plugin-node-resolve";
import typescript from "@rollup/plugin-typescript";
import multi from "@rollup/plugin-multi-entry";
import { terser } from "rollup-plugin-terser";

export default defineConfig({
    input: "src/main/typescript/**/*.ts",
    output: {
        dir: "build",
        format: "iife",
        sourcemap: "inline",
    },
    plugins: [
        typescript(),
        multi({
            entryFileName: "script.js",
        }),
        nodeResolve(),
        terser(),
    ],
});
