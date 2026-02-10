declare enum METHOD {
    GET = "GET",
    DELETE = "DELETE",
    POST = "POST",
    PUT = "PUT"
}
declare const TIME_OUT = 1000;
declare const HEADERS: {
    "Content-Type": string;
};
declare enum HEADER_TYPES {
    JSON = "application/json;charset=utf-8",
    FORMDATA = "multipart/form-data"
}
export { METHOD, TIME_OUT, HEADERS, HEADER_TYPES };
