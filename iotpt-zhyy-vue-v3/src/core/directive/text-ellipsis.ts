import type { Directive, VNode } from "vue";
import { h, render } from "vue";
import { ElTooltip } from "element-plus";
let elVnode: VNode | null = null;
let index = -1;

// 导出一个名为 textEllipsis 的指令对象
export const textEllipsis: Directive = {
	// 当指令绑定到元素上时调用
	mounted: function (el: HTMLElement) {
		// 增加全局索引值
		index += 1;
		// 为元素添加鼠标悬停事件监听器
		el.onmouseover = () => {
			// 如果元素的实际滚动宽度大于其可见宽度，说明内容溢出
			if (el.scrollWidth > el.clientWidth) {
				// 如果已经存在虚拟节点
				if (elVnode) {
					// 如果虚拟节点有组件
					if (elVnode.component) {
						// 设置组件的可见属性为 true
						elVnode.component.props.visible = true;
						// 设置组件的内容为当前元素的内部 HTML
						elVnode.component.props.content = el.innerHTML;
						// 设置组件的虚拟引用为当前元素
						elVnode.component.props.virtualRef = el;
						// 更新组件
						elVnode.component.update();
					}
				} else {
					// 如果不存在虚拟节点，创建一个新的 ElTooltip 组件
					elVnode = h(ElTooltip, {
						virtualRef: el, // 设置虚拟引用为当前元素
						visible: true, // 设置可见属性为 true
						placement: "top", // 设置提示位置为顶部
						persistent: false,
						virtualTriggering: true, // 设置为虚拟触发
						content: el.innerHTML // 设置提示内容为当前元素的内部 HTML
					});
					// 如果当前元素有父元素，将虚拟节点渲染到文档的 body 中
					if (el.parentElement) render(elVnode, document.body);
				}
			} else {
				// 如果内容未溢出，且存在虚拟节点及其组件
				if (elVnode && elVnode.component) {
					// 设置组件的可见属性为 false
					elVnode.component.props.visible = false;
					// 更新组件
					elVnode.component.update();
				}
			}
		};

		el.onmouseleave = () => {
			// 如果内容未溢出，且存在虚拟节点及其组件
			if (elVnode && elVnode.component) {
				// 设置组件的可见属性为 false
				elVnode.component.props.visible = false;
				// 更新组件
				elVnode.component.update();
			}
		};
	},
	// 当指令从元素上解绑时调用
	unmounted: function () {
		// 减少全局索引值
		index -= 1;
		// 如果索引值为 0，重置虚拟节点
		if (index == -1) {
			elVnode = null;
		}
	}
};
