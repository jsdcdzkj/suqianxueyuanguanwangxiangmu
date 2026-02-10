var METHOD = /* @__PURE__ */ ((METHOD2) => {
  METHOD2["GET"] = "GET";
  METHOD2["DELETE"] = "DELETE";
  METHOD2["POST"] = "POST";
  METHOD2["PUT"] = "PUT";
  return METHOD2;
})(METHOD || {});
const TIME_OUT = 1e3;
const HEADERS = {
  "Content-Type": "application/json;charset=utf-8"
};
var HEADER_TYPES = /* @__PURE__ */ ((HEADER_TYPES2) => {
  HEADER_TYPES2["JSON"] = "application/json;charset=utf-8";
  HEADER_TYPES2["FORMDATA"] = "multipart/form-data";
  return HEADER_TYPES2;
})(HEADER_TYPES || {});
export { METHOD, TIME_OUT, HEADERS, HEADER_TYPES };
