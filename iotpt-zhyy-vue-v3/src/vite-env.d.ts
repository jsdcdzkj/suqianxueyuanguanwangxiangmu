/// <reference types="vite/client" />

import type { App } from "vue";
import type { ProjectManger } from "./core";

interface ImportMetaEnv {
	readonly VITE_APP_TITLE: string;
	readonly VITE_BASE_API: string;
	readonly VITE_TOKEN: string;
}

interface ImportMeta {
	readonly env: ImportMetaEnv;
}

declare global {
	interface Window {
		appInstance: ProjectManger;
		appIsMounted: boolean;
		vueInstance: App | null;
	}
}
