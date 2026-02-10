import { computed, h, nextTick, onBeforeMount, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
export type CacheItem = {
	title: string;
	loaded: boolean;
	label: string;
	_component: string;
	path: string;
	name: string;
	icon: string;
	fullPath: string;
	menuName: string;
	pathName: string;
};

export type StoreType = {
	cachedList: CacheItem[];
	curretnIndex: number;
	excludeRouter: Set<string>;
	refreshView: boolean;
	fullContainer: boolean;
	fullScreen: boolean;
	toolbarWidth: number;
	scrollWidth: number;
	leftDisabled: boolean;
	rightDisabeld: boolean;
	showArrow: boolean;
};

class _ResizeObserver extends window.ResizeObserver {
	constructor(callback: ResizeObserverCallback) {
		super((entries) => {
			// 使用requestAnimationFrame来延迟执行回调函数
			requestAnimationFrame(() => {
				callback(entries, this);
			});
		});
	}
}

export const useToolBarComputed = (toolbarId: string, scrollId: string) => {
	const toobar = ref<HTMLDivElement | null>();
	const scrollContainer = ref<HTMLDivElement>();
	const router = useRouter();

	const toolBarConfig = reactive<StoreType>({
		toolbarWidth: 0,
		scrollWidth: 0,
		leftDisabled: true,
		rightDisabeld: true,
		showArrow: false,
		curretnIndex: 0,
		cachedList: [],
		excludeRouter: new Set(),
		refreshView: true,
		fullContainer: false,
		fullScreen: false
	});

	// 滚动容器的宽度发送变化，进行重新计算
	const watchWidthResize = () => {
		// 重新获取toolbar 宽度
		if (toobar.value) {
			toolBarConfig.toolbarWidth = toobar.value.getBoundingClientRect().width;
		}
		// 获取滚动容器宽度
		if (scrollContainer.value) {
			toolBarConfig.scrollWidth = scrollContainer.value.scrollWidth;
		}
		// 左右箭头显示控制
		toolBarConfig.showArrow = toolBarConfig.scrollWidth > toolBarConfig.toolbarWidth;

		computedArrowDiabled();
	};
	// 监听节点宽度变化
	const bser = new _ResizeObserver(function () {
		watchWidthResize();
	});

	const computedArrowDiabled = () => {
		if (scrollContainer.value) {
			toolBarConfig.leftDisabled = scrollContainer.value.scrollLeft == 0;
			toolBarConfig.rightDisabeld =
				scrollContainer.value.scrollLeft + toolBarConfig.toolbarWidth > scrollContainer.value.scrollWidth;
		}
	};

	const onClickScrollRight = () => {
		if (scrollContainer.value) {
			scrollContainer.value.scrollLeft = scrollContainer.value.scrollLeft + toolBarConfig.toolbarWidth;
			computedArrowDiabled();
		}
	};

	// 向右滑动
	const onClickScrollLeft = () => {
		if (scrollContainer.value) {
			const distance = scrollContainer.value.scrollLeft - toolBarConfig.toolbarWidth;
			scrollContainer.value.scrollLeft = distance < 0 ? 0 : distance;
			computedArrowDiabled();
		}
	};

	// 向左滑动
	const scrollEnd = () => {
		if (scrollContainer.value) {
			scrollContainer.value.scrollLeft = scrollContainer.value.scrollWidth;
			computedArrowDiabled();
		}
	};

	// tag item 滚动到中心点
	const scrollCenter = (offsetLeft: number) => {
		if (scrollContainer.value) {
			const fNum = Math.floor(offsetLeft / toolBarConfig.toolbarWidth);
			const centerDistance = toolBarConfig.toolbarWidth * fNum + offsetLeft / 2;
			scrollContainer.value.scrollLeft = centerDistance;
			computedArrowDiabled();
		}
	};

	const toolBarPageMange = {
		// 删除当前item右侧的所有页面
		removeRightPages() {
			toolBarConfig.cachedList = toolBarConfig.cachedList.slice(0, toolBarConfig.curretnIndex + 1);
			watchWidthResize();
		},
		// 删除其他页面
		removeOtherPages() {
			toolBarConfig.cachedList = [toolBarConfig.cachedList[toolBarConfig.curretnIndex]];
			toolBarConfig.curretnIndex = 0;
			watchWidthResize();
		},
		// 删除选中item左侧所有页面
		removeLeftPages() {
			// toolBarConfig.cachedList = toolBarConfig.cachedList.slice(0, toolBarConfig.curretnIndex);
			toolBarConfig.cachedList = toolBarConfig.cachedList.slice(
				toolBarConfig.curretnIndex,
				toolBarConfig.cachedList.length
			);
			toolBarConfig.curretnIndex = 0;
			watchWidthResize();
		},
		// 容器最大/最小控制
		changeFulleContainer() {
			toolBarConfig.fullContainer = !toolBarConfig.fullContainer;
		},
		// 添加路由
		addRoute(item: CacheItem) {
			toolBarConfig.curretnIndex = toolBarConfig.cachedList.findIndex((it) => it.fullPath == item.fullPath);
			if (toolBarConfig.curretnIndex == -1) {
				item.loaded = false;
				toolBarConfig.cachedList.push(item);
				toolBarConfig.curretnIndex = toolBarConfig.cachedList.length - 1;
				nextTick(() => {
					watchWidthResize();
					scrollEnd();
				});
			} else {
				item.loaded = true;

				nextTick(() => {
					if (scrollContainer.value) {
						const itemEl = scrollContainer.value.children[toolBarConfig.curretnIndex] as HTMLDivElement;

						if (itemEl) {
							scrollCenter(itemEl.offsetLeft);
						}
					}
				});
			}
		},
		// 删除路由
		removeRoute(item: CacheItem, index: number = -1) {
			if (index > -1) {
				toolBarConfig.cachedList.splice(index, 1);
			} else {
				toolBarConfig.cachedList.splice(
					toolBarConfig.cachedList.findIndex((it) => it.path == item.path),
					1
				);
			}
			toolBarConfig.curretnIndex = toolBarConfig.cachedList.length - 1;
			const itemCache = toolBarConfig.cachedList[toolBarConfig.curretnIndex];
			watchWidthResize();
			return itemCache;
		},
		// 刷新路由
		async refreshRoute(router: CacheItem) {
			toolBarConfig.excludeRouter.add(router.name);
			toolBarConfig.refreshView = false;
			router.loaded = true;
			await new Promise((resolve) => setTimeout(resolve, 200));
			toolBarConfig.excludeRouter.delete(router.name);
			router.loaded = false;
			toolBarConfig.refreshView = true;
		},
		// 插入路由
		insertRoute(index: number, item: CacheItem) {
			toolBarConfig.cachedList.splice(index, 0, item);
			watchWidthResize();
		},
		// 获取不需要缓存的路由
		getExcludeRouter(): string[] {
			return [...toolBarConfig.excludeRouter];
		},
		// 页面刷新状态
		isRefreshView(): boolean {
			return toolBarConfig.refreshView;
		},
		// 屏幕最大化
		changeFullScreen() {
			if (!document.fullscreenElement) {
				// 当前不在全屏模式
				const documentElement = document.documentElement as any;
				if (documentElement.requestFullscreen) {
					documentElement.requestFullscreen();
				} else if (documentElement.mozRequestFullScreen) {
					/* Firefox */
					documentElement.mozRequestFullScreen();
				} else if (documentElement.webkitRequestFullscreen) {
					/* Chrome, Safari & Opera */
					documentElement.webkitRequestFullscreen();
				} else if (documentElement.msRequestFullscreen) {
					/* IE/Edge */
					documentElement.msRequestFullscreen();
				}
				toolBarConfig.fullScreen = true;
			} else {
				// 当前处于全屏模式
				if (document.exitFullscreen) {
					document.exitFullscreen();
				} else if ((document as any).mozCancelFullScreen) {
					/* Firefox */
					(document as any).mozCancelFullScreen();
				} else if ((document as any).webkitExitFullscreen) {
					/* Chrome, Safari and Opera */
					(document as any).webkitExitFullscreen();
				} else if ((document as any).msExitFullscreen) {
					/* IE/Edge */
					(document as any).msExitFullscreen();
				}
				toolBarConfig.fullScreen = false;
			}
		},
		refreshPage: () => {
			toolBarPageMange.refreshRoute(toolBarConfig.cachedList[toolBarConfig.curretnIndex]);
		},
		removeCurrentPage: () => {
			const currentPage = toolBarPageMange.removeRoute(
				toolBarConfig.cachedList[toolBarConfig.curretnIndex],
				toolBarConfig.curretnIndex
			);
			router.replace({ name: currentPage.name });
		},
		repalcePage: (item: CacheItem) => {
			router.push({ name: item.name });
		}
	};

	onMounted(() => {
		// 将首次加载的路由添加到toolbar进行管理
		toolBarPageMange.addRoute(router.currentRoute.value.meta as CacheItem);
		// 获取 toolbar dom 节点
		toobar.value = document.getElementById(toolbarId) as HTMLDivElement;
		// 获取 滑动 节点
		scrollContainer.value = document.getElementById(scrollId) as HTMLDivElement;

		if (!toobar.value || !scrollContainer.value) {
			throw new Error("节点不存在");
		}
		// 监听节点resize时间
		if (scrollContainer.value) bser.observe(scrollContainer.value);
	});

	onBeforeMount(() => {
		if (scrollContainer.value) bser.unobserve(scrollContainer.value);
	});

	router.beforeEach((to, _, next) => {
		toolBarPageMange.addRoute(to.meta as CacheItem);

		next();
	});

	// 获取已缓存的tag列表
	const cachedList = computed(() => {
		return toolBarConfig.cachedList.map((item) => item.pathName);
	});

	return {
		cachedList,
		toobar,
		scrollContainer,
		toolBarConfig,
		onClickScrollRight,
		onClickScrollLeft,
		toolBarActions: toolBarPageMange
	};
};
