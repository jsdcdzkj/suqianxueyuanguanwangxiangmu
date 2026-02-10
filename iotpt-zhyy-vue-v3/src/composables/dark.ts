import { useDark, useToggle } from "@vueuse/core";

export const isDark = useDark();
export const toggleDark = useToggle(isDark);

export const useTheme = () => {
	function n() {
		return (
			"startViewTransition" in document &&
			window.matchMedia("(prefers-reduced-motion: no-preference)").matches
		);
	}
	const handeToggleDark = async ({
		clientX: o,
		clientY: s
	}: PointerEvent) => {
		// 保存点击位置
		if (!n()) {
			toggleDark();
			return;
		}

		const i = [
			`circle(0px at ${o}px ${s}px)`,
			`circle(${Math.hypot(Math.max(o, innerWidth - o), Math.max(s, innerHeight - s))}px at ${o}px ${s}px)`
		];

		await document.startViewTransition(async () => {
			toggleDark();
		}).ready;
		document.documentElement.animate(
			{
				clipPath: isDark.value ? i.reverse() : i
			},
			{
				duration: 300,
				easing: "ease-in",
				pseudoElement: `::view-transition-${isDark.value ? "old" : "new"}(root)`
			}
		);
	};

	return { handeToggleDark };
};
