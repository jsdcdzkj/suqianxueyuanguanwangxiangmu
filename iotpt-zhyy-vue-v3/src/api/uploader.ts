export enum UploadPath {
	pro = "/pro"
}

export const processFilesStruct = (backData: any[]) => {
	return backData.map((item: any) => {
		return {
			name: item.fileName,
			percentage: 100,
			status: "success",
			response: {
				code: 200,
				data: { ...item }
			}
		};
	});
};
