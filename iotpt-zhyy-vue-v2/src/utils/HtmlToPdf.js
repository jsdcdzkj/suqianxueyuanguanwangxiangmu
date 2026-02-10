import { jsPDF } from "jspdf";
import html2canvas from "html2canvas";

export default class HtmlToPdf {
    containerHeight = 0;
    nodeHeightNum = 0;
    nodeList = [];
    nodeHeightList = [];
    drawCurrent = 0;
    images = [];
    container = null;
    onProgress = null;
    fileName = "";

    constructor(id, { onProgress, fileName }) {
        this.container = document.getElementById(id);
        this.containerHeight = this.container.getBoundingClientRect().height;
        this.container.scrollTop = 0;
        this.nodeList = [...this.container.childNodes];
        this.computedModuleList();
        this.onProgress = onProgress;
        this.fileName = fileName;
    }

    computedModuleList() {
        this.nodeHeightList = this.nodeList.map((item) => {
            this.nodeHeightNum += item.getBoundingClientRect().height;
            return item.getBoundingClientRect().height;
        });
    }

    async getCanvas(element) {
        return new Promise((resolve) => {
            window.html2canvas(element, {
                background: "#fff",
                onrendered: (canvas) => {
                    resolve(canvas);
                }
            });
        });
    }

    async drawImage() {
        for (let i = 0; i < this.nodeList.length; i++) {
            const element = this.nodeList[i];
            const canvas = await html2canvas(element, {
                scrollX: 0,
                scrollY: -window.scrollY
            });

            const imageData = canvas.toDataURL("image/jpeg", 1.0);
            this.images.push({
                img: imageData,
                width: canvas.width,
                height: canvas.height,
                index: this.drawCurrent
            });
            this.drawCurrent += 1;
            if (this.drawCurrent >= this.nodeList.length) {
                this.container.scrollTop = 0;
            }
            this.onProgress(((i + 1) / this.nodeList.length) * 50);
        }
        return this.images;
    }
    sleep() {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve();
            }, 1000);
        });
    }

    pageScroll() {
        if (this.container.scrollTop + this.containerHeight < this.container.scrollHeight) {
            this.container.scrollTop += this.nodeHeightList[this.drawCurrent];
        }
    }

    genPdf() {
        let position = 20;
        let pageAlHeight = 0;

        const pdf = new jsPDF({
            unit: "px",
            format: "a4"
        });

        const A4Origin = {
            width: pdf.internal.pageSize.getWidth(),
            height: pdf.internal.pageSize.getHeight()
        };

        this.images.forEach((item, index) => {
            const scale = 40 / A4Origin.height;
            let a4Width = (A4Origin.width / item.width) * item.width;
            let a4Height = (A4Origin.width / item.width) * item.height;

            if (a4Height > A4Origin.height) {
                a4Height = A4Origin.height;
            }

            pageAlHeight += a4Height;
            if (pageAlHeight > A4Origin.height) {
                pdf.addPage();
                pageAlHeight = a4Height;
                position = 20;
            }

            pdf.addImage(item.img, "JPEG", 20, position, a4Width - 40, a4Height * (1 - scale));
            position += a4Height;
            this.onProgress(50 + ((index + 1) / this.images.length) * 50);
        });
        return pdf.output("blob", { filename: this.fileName + ".pdf" });
    }
}

export const loadFile = (url, name = "xxx.pdf") => {
    const pdf = new jsPDF({
        unit: "px",
        format: "a4"
    });
    pdf.loadFile(url);
    pdf.save(name);
};
