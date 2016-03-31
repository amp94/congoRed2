"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = Caption;

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }

var _react = require("react");

var _react2 = _interopRequireDefault(_react);

function Caption(_ref) {
  var date = _ref.date;
  var locale = _ref.locale;
  var localeUtils = _ref.localeUtils;
  var onClick = _ref.onClick;

  return _react2["default"].createElement(
    "div",
    { className: "DayPicker-Caption", onClick: onClick },
    localeUtils.formatMonthTitle(date, locale)
  );
}

module.exports = exports["default"];
//# sourceMappingURL=Caption.js.map