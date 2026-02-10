// import LocalProvider from "@/components/local-provider";

import { h, render } from "vue";
import type { JSX } from "vue/jsx-runtime";
import DialogContainer from "./dialog-container";
import type { ComponentOptions, VNode } from "vue";
import { ElConfigProvider, ElDialog, ElDrawer } from "element-plus";
import zhCn from "element-plus/es/locale/lang/zh-cn";
import DrawerContainer from "./drawer-container";
import ContextConfig from "@/components/config/context-config";
export type { DialogProvide } from "./dialog-container";

type ModalProps = {
	title?: string;
	width?: string;
	maskClosable?: boolean;
	showCancel?: boolean;
	cancelText?: string;
	confirmText?: string;
	showConfirm?: boolean;
	bodyPadding?: string;
	showZanCun?: boolean;
	zancunText?: string;
	zancunType?: string;
	showApprove?: boolean;
	approveText?: string;
	approveType?: string;
	showNext?: boolean;
	nextNotRest?: string[];
	isNextSel?: boolean;
	destroyOnClose?: boolean;
	closeOnClickModal?: boolean;
	closeOnPressEscape?: boolean;
	noEvent?: boolean;
	done?: () => any | Promise<any>;
} & Partial<VNode>;
type createModel = (
	props: ModalProps,
	data: any,
	template: VNode | JSX.Element | ComponentOptions,
	callback?: (form: any) => void
) => any;
export const createModel: createModel = (props, data, template, callback) => {
	const container = document.createDocumentFragment();
	let instance: VNode | undefined;

	const unMountedModel = () => {
		if (instance && instance.el) {
			// 销毁弹框组件
			render(null, container as unknown as Element);
			instance = undefined;
			// 如果弹框显示
			if (props.showNext) {
				callback && callback(true);
			}
		}
	};

	const onModelClose = async () => {
		if (instance && instance.component) {
			instance.component.props.modelValue = false;
			instance.component.update();
		}
	};

	const wrapper = () => {
		return (
			<ContextConfig>
				<ElConfigProvider locale={zhCn}>
					<DialogContainer
						showCancel={props.showCancel}
						cancelText={props.cancelText}
						confirmText={props.confirmText}
						showConfirm={props.showConfirm}
						showNext={props.showNext}
						nextNotRest={props.nextNotRest}
						isNextSel={props.isNextSel}
						onCancel={onModelClose}
						onConfirm={onModelConfirm}
						bodyPadding={props.bodyPadding}
					>
						{h(template, { ...data })}
					</DialogContainer>
				</ElConfigProvider>
			</ContextConfig>
		);
	};

	const onModelConfirm = async (form: any) => {
		// 等待弹框动画关闭完成
		await onModelClose();
		if (callback) {
			callback(form);
		}
	};

	instance = h(
		ElDialog,
		{
			title: props.title,
			modelValue: true,
			width: props.width,
			destroyOnClose: true,
			closeOnClickModal: props.closeOnClickModal,
			closeOnPressEscape: props.closeOnPressEscape,
			onClosed: unMountedModel
		},

		{
			default: wrapper
		}
	);
	render(instance, container as unknown as Element);
	document.body.appendChild(container);
};

type createModelAsync = <T>(
	props: ModalProps,
	data: any,
	template: VNode | JSX.Element | ComponentOptions
) => Promise<T>;
export const createModelAsync: createModelAsync = (props, data, template) => {
	return new Promise((resolve) => {
		createModel(props, data, template, (form: any) => {
			resolve(form);
		});
	});
};

type createDrawer = (
	props: ModalProps,
	data: any,
	template: VNode | JSX.Element | ComponentOptions,
	callback?: (form: any) => void
) => any;
export const createDrawer: createModel = (props, data, template, callback) => {
	const container = document.createDocumentFragment();
	let instance: VNode | undefined;

	const unMountedModel = () => {
		if (instance && instance.el) {
			// 销毁弹框组件
			render(null, container as unknown as Element);
			instance = undefined;
			// 如果弹框显示
			if (props.showNext) {
				callback && callback(true);
			}
		}
	};

	const onModelClose = async () => {
		if (instance && instance.component) {
			instance.component.props.modelValue = false;
			instance.component.update();
			// await sleep(500);
		}
		console.log("!11");
	};

	const wrapper = () => {
		return (
			<ContextConfig>
				<ElConfigProvider locale={zhCn}>
					<DrawerContainer
						showCancel={props.showCancel}
						showConfirm={props.showConfirm}
						showNext={props.showNext}
						nextNotRest={props.nextNotRest}
						showZanCun={props.showZanCun}
						showApprove={props.showApprove}
						noEvent={props.noEvent}
						cancelText={props.cancelText}
						confirmText={props.confirmText}
						zancunText={props.zancunText}
						zancunType={props.zancunType}
						approveText={props.approveText}
						approveType={props.approveType}
						onCancel={onModelClose}
						onConfirm={onModelConfirm}
						onZancun={onModelConfirm}
						onApprove={onModelConfirm}
					>
						{h(template, { ...data })}
					</DrawerContainer>
				</ElConfigProvider>
			</ContextConfig>
		);
	};

	const onModelConfirm = async (form: any) => {
		// 等待弹框动画关闭完成
		await onModelClose();
		if (callback) {
			callback(form);
		}
	};

	instance = h(
		ElDrawer,
		{
			title: props.title,
			modelValue: true,
			size: props.width,
			destroyOnClose: true,
			onClosed: unMountedModel,
			closeOnClickModal: props.closeOnClickModal,
			closeOnPressEscape: props.closeOnPressEscape,
			class: "custom-drawer"
		},

		{
			default: wrapper
		}
	);
	render(instance, container as unknown as Element);
	document.body.appendChild(container);
};

type createDrawerAsync = <T>(
	props: ModalProps,
	data: any,
	template: VNode | JSX.Element | ComponentOptions
) => Promise<T>;
export const createDrawerAsync: createModelAsync = (props, data, template) => {
	return new Promise((resolve) => {
		createDrawer(props, data, template, (form: any) => {
			resolve(form);
		});
	});
};
