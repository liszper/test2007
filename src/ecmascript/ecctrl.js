import React, { useRef, useState } from 'react';
import { Canvas } from '@react-three/fiber';
import { Physics, RigidBody } from '@react-three/rapier';
import { Gltf, Environment, Fisheye, KeyboardControls } from '@react-three/drei';
import Controller from 'ecctrl';
export function App() {
  const keyboardMap = [{
    name: 'forward',
    keys: ['ArrowUp', 'KeyW']
  }, {
    name: 'backward',
    keys: ['ArrowDown', 'KeyS']
  }, {
    name: 'leftward',
    keys: ['ArrowLeft', 'KeyA']
  }, {
    name: 'rightward',
    keys: ['ArrowRight', 'KeyD']
  }, {
    name: 'jump',
    keys: ['Space']
  }, {
    name: 'run',
    keys: ['Shift']
  }];
  return /*#__PURE__*/React.createElement(Canvas, {
    shadows: true,
    onPointerDown: e => e.target.requestPointerLock()
  }, /*#__PURE__*/React.createElement(Fisheye, {
    zoom: 0.4
  }, /*#__PURE__*/React.createElement("directionalLight", {
    intensity: 0.7,
    castShadow: true,
    "shadow-bias": -0.0004,
    position: [-20, 20, 20]
  }, /*#__PURE__*/React.createElement("orthographicCamera", {
    attach: "shadow-camera",
    args: [-20, 20, 20, -20]
  })), /*#__PURE__*/React.createElement("ambientLight", {
    intensity: 0.2
  })));
}
export function AppOriginal() {
  const keyboardMap = [{
    name: 'forward',
    keys: ['ArrowUp', 'KeyW']
  }, {
    name: 'backward',
    keys: ['ArrowDown', 'KeyS']
  }, {
    name: 'leftward',
    keys: ['ArrowLeft', 'KeyA']
  }, {
    name: 'rightward',
    keys: ['ArrowRight', 'KeyD']
  }, {
    name: 'jump',
    keys: ['Space']
  }, {
    name: 'run',
    keys: ['Shift']
  }];
  return /*#__PURE__*/React.createElement(Canvas, {
    shadows: true,
    onPointerDown: e => e.target.requestPointerLock()
  }, /*#__PURE__*/React.createElement(Fisheye, {
    zoom: 0.4
  }, /*#__PURE__*/React.createElement(Environment, {
    files: "/night.hdr",
    ground: {
      scale: 100
    }
  }), /*#__PURE__*/React.createElement("directionalLight", {
    intensity: 0.7,
    castShadow: true,
    "shadow-bias": -0.0004,
    position: [-20, 20, 20]
  }, /*#__PURE__*/React.createElement("orthographicCamera", {
    attach: "shadow-camera",
    args: [-20, 20, 20, -20]
  })), /*#__PURE__*/React.createElement("ambientLight", {
    intensity: 0.2
  }), /*#__PURE__*/React.createElement(Physics, {
    timeStep: "vary"
  }, /*#__PURE__*/React.createElement(KeyboardControls, {
    map: keyboardMap
  }, /*#__PURE__*/React.createElement(Controller, {
    maxVelLimit: 5
  }, /*#__PURE__*/React.createElement(Gltf, {
    castShadow: true,
    receiveShadow: true,
    scale: 0.315,
    position: [0, -0.55, 0],
    src: "/ghost_w_tophat-transformed.glb"
  }))), /*#__PURE__*/React.createElement(RigidBody, {
    type: "fixed",
    colliders: "trimesh"
  }, /*#__PURE__*/React.createElement(Gltf, {
    castShadow: true,
    receiveShadow: true,
    rotation: [-Math.PI / 2, 0, 0],
    scale: 0.11,
    src: "/fantasy_game_inn2-transformed.glb"
  })))));
}