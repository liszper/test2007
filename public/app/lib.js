import "ws";
import * as i0 from "react-error-boundary";
import * as i1 from "react-dom";
import * as i2 from "@mantine/core";
import * as i3 from "wagmi";
import * as i4 from "@guildxyz/sdk";
import * as i5 from "ecctrl";
import * as i6 from "react-dom/client";
import * as i7 from "@tanstack/react-query";
import * as i8 from "@react-three/rapier";
import * as i9 from "three";
import * as i10 from "wagmi/chains";
import * as i11 from "wagmi/connectors";
import * as i12 from "@react-three/drei";
import "@mantine/hooks";
import * as i13 from "react";
import * as i14 from "@react-three/fiber";
import "@tabler/icons-react";
import * as i15 from "@airstack/airstack-react";

const ALL = {};

globalThis.shadow$bridge = function(name) {
  const ret = ALL[name];
  if (ret == undefined) {
    throw new Error("Dependency: " + name + " not provided by external JS!");
  } else {
    return ret;
  }
};

ALL["ws"] = {

};

ALL["react-error-boundary"] = {
  ErrorBoundary: i0.ErrorBoundary
};

ALL["react-dom"] = {
  findDOMNode: i1.findDOMNode,
  render: i1.render,
  unmountComponentAtNode: i1.unmountComponentAtNode
};

ALL["@mantine/core"] = {
  Button: i2.Button,
  Grid: i2.Grid,
  SimpleGrid: i2.SimpleGrid,
  Flex: i2.Flex,
  Badge: i2.Badge,
  MantineProvider: i2.MantineProvider,
  Stack: i2.Stack
};

ALL["wagmi"] = {
  useSignMessage: i3.useSignMessage,
  useDisconnect: i3.useDisconnect,
  useAccount: i3.useAccount,
  WagmiProvider: i3.WagmiProvider,
  http: i3.http,
  createConfig: i3.createConfig,
  useConnect: i3.useConnect,
  useEnsName: i3.useEnsName
};

ALL["@guildxyz/sdk"] = {
  createGuildClient: i4.createGuildClient,
  createSigner: i4.createSigner
};

ALL["ecctrl"] = {
  default: i5.default
};

ALL["react-dom/client"] = {
  createRoot: i6.createRoot,
  hydrateRoot: i6.hydrateRoot
};

ALL["@tanstack/react-query"] = {
  QueryClientProvider: i7.QueryClientProvider,
  QueryClient: i7.QueryClient
};

ALL["@react-three/rapier"] = {
  Physics: i8.Physics,
  RigidBody: i8.RigidBody
};

ALL["three"] = {
  Quaternion: i9.Quaternion,
  Vector3: i9.Vector3
};

ALL["wagmi/chains"] = {
  mainnet: i10.mainnet
};

ALL["wagmi/connectors"] = {
  walletConnect: i11.walletConnect,
  injected: i11.injected
};

ALL["@react-three/drei"] = {
  Html: i12.Html,
  Stars: i12.Stars,
  Gltf: i12.Gltf,
  KeyboardControls: i12.KeyboardControls
};

ALL["@mantine/hooks"] = {

};

ALL["react"] = {
  Children: i13.Children,
  useRef: i13.useRef,
  createElement: i13.createElement,
  Fragment: i13.Fragment,
  Suspense: i13.Suspense,
  Component: i13.Component,
  useEffect: i13.useEffect,
  useState: i13.useState,
  memo: i13.memo
};

ALL["@react-three/fiber"] = {
  Canvas: i14.Canvas,
  useFrame: i14.useFrame
};

ALL["@tabler/icons-react"] = {

};

ALL["@airstack/airstack-react"] = {
  AirstackProvider: i15.AirstackProvider
};
