function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
import THREE from 'three';
import React, { useRef, useState, useEffect } from 'react';
import { Buffer } from 'buffer';
import { useFrame } from '@react-three/fiber';
window.Buffer = Buffer;
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
function Instances({
  count = 100000,
  temp = new THREE.Object3D()
}) {
  const instancedMeshRef = useRef();
  useEffect(() => {
    // Set positions
    for (let i = 0; i < count; i++) {
      temp.position.set(Math.random(), Math.random(), Math.random());
      temp.updateMatrix();
      instancedMeshRef.current.setMatrixAt(i, temp.matrix);
    }
    // Update the instance
    instancedMeshRef.current.instanceMatrix.needsUpdate = true;
  }, []);
  return /*#__PURE__*/React.createElement("instancedMesh", {
    ref: instancedMeshRef,
    args: [null, null, count]
  }, /*#__PURE__*/React.createElement("boxGeometry", null), /*#__PURE__*/React.createElement("meshPhongMaterial", null));
}