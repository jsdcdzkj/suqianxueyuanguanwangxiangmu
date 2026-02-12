<script>
	export default {
		onLaunch: function() {
			console.log('App Launch')
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style lang="scss">
	/*每个页面公共css */
	@import "./uni_modules/vk-uview-ui/index.scss";

	.u-font-weight {
		font-weight: bold;
	}


	.btntype {
		width: 100%;
		height: 76rpx;
		color: #fff;
		font-size: 28rpx;
		line-height: 76rpx;
		text-align: center;
		border-radius: 16rpx;
		background-image: linear-gradient(to right, #1e95f4, #1e95f4);

		&.primary {
			color: #4f2271;
			background-image: linear-gradient(to right, #eecf9c, #e5c188);
		}

		&.danger {
			color: #fff;
			background-image: linear-gradient(to right, #f56c6c, #f56c6c);
		}

		&.normal {
			color: #333;
			background-image: linear-gradient(to right, #fff, #fff);
		}

		&.black {
			color: #fff;
			background-image: linear-gradient(to right, #000, #000);
		}

		&.success {
			color: #fff;
			background-image: linear-gradient(to right, #488829, #488829);
		}

		&.cancel {
			color: #fff;
			border: 1px solid rgba(255, 255, 255, 0.85);
			background: rgba($color: #fff, $alpha: 0.45);
		}

		&.baoxiang {
			color: #fff;
			border: 1px solid rgba(255, 255, 255, 0.65);
			background: rgba($color: #fff, $alpha: 0.12);
		}

		&.disabled {
			opacity: 0.5;
		}
	}

	.btntype[aria-disabled='true'] {
		color: #bbb !important;
	}
</style>