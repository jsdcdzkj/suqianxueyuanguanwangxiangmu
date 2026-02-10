import globals from "globals";
import pluginJs from "@eslint/js";
import tseslint from "typescript-eslint";
import pluginVue from "eslint-plugin-vue";
import { FlatCompat } from "@eslint/eslintrc";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const compat = new FlatCompat({
	baseDirectory: __dirname
});

export default [
	{ files: ["**/*.{js,mjs,cjs,ts,vue,tsx}"] },
	{ languageOptions: { globals: globals.browser } },
	pluginJs.configs.recommended,
	...compat.extends("plugin:prettier/recommended"),
	...tseslint.configs.recommended,
	...pluginVue.configs["flat/essential"],
	{
		files: ["**/*.vue"],
		languageOptions: { parserOptions: { parser: tseslint.parser } }
	},
	{
		ignores: [
			"config/*",
			".husky",
			".local",
			"public/*",
			".vscode",
			"node_modules",
			"**/dist/**"
		]
	},
	{
		rules: {
			"@typescript-eslint/no-explicit-any": "off"
		}
	}
];
