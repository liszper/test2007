function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
import React, { useRef, useState } from 'react';
import { useFrame } from '@react-three/fiber';
export function Box(props) {
  const meshRef = useRef(null);
  const [hovered, setHover] = useState(false);
  const [active, setActive] = useState(false);
  useFrame((state, delta) => meshRef.current.rotation.x += delta);
  return /*#__PURE__*/React.createElement("mesh", _extends({}, props, {
    ref: meshRef,
    scale: active ? 1.5 : 1,
    onClick: event => setActive(!active),
    onPointerOver: event => setHover(true),
    onPointerOut: event => setHover(false)
  }), /*#__PURE__*/React.createElement("boxGeometry", {
    args: [1, 1, 1]
  }), /*#__PURE__*/React.createElement("meshStandardMaterial", {
    color: hovered ? 'hotpink' : 'orange'
  }));
}